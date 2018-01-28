/*******************************************************************************
 * Copyright (c) Aug 11, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.extension.monitor.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.iff.infra.util.Logger;
import org.iff.infra.util.Logger.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.http.util.IPAddress;
import com.alibaba.druid.support.http.util.IPRange;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.druid.util.Utils;
import com.foreveross.common.ConstantBean;
import com.foreveross.common.web.BaseController;
import com.foreveross.extension.monitor.JavaMelodyMonitor;

/**
 * 监控。
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 11, 2016
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController extends BaseController {

	private DruidMonitor monitor;

	@RequestMapping("/druid/**")
	public void druid(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			if (monitor == null) {
				synchronized (this) {
					monitor = new DruidMonitor(null);
					monitor.init();
				}
			}
			monitor.service(request, response);
		} catch (Exception e) {
			error(e);
		}
	}

	@RequestMapping("/javamelody")
	public void javamelody(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		try {
			JavaMelodyMonitor monitor2 = new JavaMelodyMonitor(request.getContextPath() + "/monitor/javamelody");
			monitor2.init(request, response);
			monitor2.doFilter(request, response);
		} catch (Exception e) {
			error(e);
		}
	}

	public class DruidMonitor {
		private Log LOG = Logger.get("DRUID");

		public static final String SESSION_USER_KEY = "druid-user";
		public static final String PARAM_NAME_USERNAME = "loginUsername";
		public static final String PARAM_NAME_PASSWORD = "loginPassword";

		protected String username = null;
		protected String password = null;

		protected List<IPRange> allowList = new ArrayList<IPRange>();
		protected List<IPRange> denyList = new ArrayList<IPRange>();

		protected final String resourcePath;

		protected String remoteAddressHeader = null;

		//////======
		private DruidStatService statService = DruidStatService.getInstance();

		/** web.xml中配置的jmx的连接地址 */
		private String jmxUrl = null;
		/** web.xml中配置的jmx的用户名 */
		private String jmxUsername = null;
		/** web.xml中配置的jmx的密码 */
		private String jmxPassword = null;
		private MBeanServerConnection conn = null;

		public DruidMonitor(String resourcePath) {
			this.resourcePath = org.apache.commons.lang3.StringUtils.defaultString(resourcePath,
					"support/http/resources");
		}

		public void init() throws ServletException {
			initAuthEnv();
			initService();
		}

		private void initAuthEnv() {
			String paramUserName = ConstantBean.getProperty("monitor.druid.username");
			if (!StringUtils.isEmpty(paramUserName)) {
				this.username = paramUserName;
			}

			String paramPassword = ConstantBean.getProperty("monitor.druid.password");
			if (!StringUtils.isEmpty(paramPassword)) {
				this.password = paramPassword;
			}

			String paramRemoteAddressHeader = ConstantBean.getProperty("monitor.druid.remoteip");
			if (!StringUtils.isEmpty(paramRemoteAddressHeader)) {
				this.remoteAddressHeader = paramRemoteAddressHeader;
			}

			try {
				String param = ConstantBean.getProperty("monitor.druid.allow");
				if (param != null && param.trim().length() != 0) {
					param = param.trim();
					String[] items = param.split(",");

					for (String item : items) {
						if (item == null || item.length() == 0) {
							continue;
						}

						IPRange ipRange = new IPRange(item);
						allowList.add(ipRange);
					}
				}
			} catch (Exception e) {
				String msg = "initParameter config error, allow : " + ConstantBean.getProperty("monitor.druid.allow");
				LOG.error(msg, e);
			}

			try {
				String param = ConstantBean.getProperty("monitor.druid.deny");
				if (param != null && param.trim().length() != 0) {
					param = param.trim();
					String[] items = param.split(",");

					for (String item : items) {
						if (item == null || item.length() == 0) {
							continue;
						}

						IPRange ipRange = new IPRange(item);
						denyList.add(ipRange);
					}
				}
			} catch (Exception e) {
				String msg = "initParameter config error, deny : " + ConstantBean.getProperty("monitor.druid.deny");
				LOG.error(msg, e);
			}
		}

		public boolean isPermittedRequest(String remoteAddress) {
			boolean ipV6 = remoteAddress != null && remoteAddress.indexOf(':') != -1;

			if (ipV6) {
				return "0:0:0:0:0:0:0:1".equals(remoteAddress) || (denyList.size() == 0 && allowList.size() == 0);
			}

			IPAddress ipAddress = new IPAddress(remoteAddress);

			for (IPRange range : denyList) {
				if (range.isIPAddressInRange(ipAddress)) {
					return false;
				}
			}

			if (allowList.size() > 0) {
				for (IPRange range : allowList) {
					if (range.isIPAddressInRange(ipAddress)) {
						return true;
					}
				}

				return false;
			}

			return true;
		}

		protected String getFilePath(String fileName) {
			return resourcePath + fileName;
		}

		protected void returnResourceFile(String fileName, String uri, HttpServletResponse response)
				throws ServletException, IOException {

			String filePath = getFilePath(fileName);

			if (filePath.endsWith(".html")) {
				response.setContentType("text/html; charset=utf-8");
			}
			if (fileName.endsWith(".jpg")) {
				byte[] bytes = Utils.readByteArrayFromResource(filePath);
				if (bytes != null) {
					response.getOutputStream().write(bytes);
				}

				return;
			}

			String text = Utils.readFromResource(filePath);
			if (text == null) {
				response.sendRedirect(uri + "/index.html");
				return;
			}
			if (fileName.endsWith(".css")) {
				response.setContentType("text/css;charset=utf-8");
			} else if (fileName.endsWith(".js")) {
				response.setContentType("text/javascript;charset=utf-8");
			}
			response.getWriter().write(text);
		}

		public void service(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			String contextPath = request.getContextPath();
			String servletPath = "/monitor/druid";
			String requestURI = request.getRequestURI();

			response.setCharacterEncoding("utf-8");

			if (contextPath == null) { // root context
				contextPath = "";
			}
			String uri = contextPath + servletPath;
			String path = requestURI.substring(contextPath.length() + servletPath.length());

			if (!isPermittedRequest(request)) {
				path = "/nopermit.html";
				returnResourceFile(path, uri, response);
				return;
			}

			if ("/submitLogin".equals(path)) {
				String usernameParam = request.getParameter(PARAM_NAME_USERNAME);
				String passwordParam = request.getParameter(PARAM_NAME_PASSWORD);
				if (username.equals(usernameParam) && password.equals(passwordParam)) {
					request.getSession().setAttribute(SESSION_USER_KEY, username);
					response.getWriter().print("success");
				} else {
					response.getWriter().print("error");
				}
				return;
			}

			if (isRequireAuth() //
					&& !ContainsUser(request)//
					&& !("/login.html".equals(path) //
							|| path.startsWith("/css")//
							|| path.startsWith("/js") //
							|| path.startsWith("/img"))) {
				if (contextPath.equals("") || contextPath.equals("/")) {
					response.sendRedirect("/druid/login.html");
				} else {
					if ("".equals(path)) {
						response.sendRedirect("druid/login.html");
					} else {
						response.sendRedirect("login.html");
					}
				}
				return;
			}

			if ("".equals(path)) {
				if (contextPath.equals("") || contextPath.equals("/")) {
					response.sendRedirect("/druid/index.html");
				} else {
					response.sendRedirect("druid/index.html");
				}
				return;
			}

			if ("/".equals(path)) {
				response.sendRedirect("index.html");
				return;
			}

			if (path.contains(".json")) {
				String fullUrl = path;
				if (request.getQueryString() != null && request.getQueryString().length() > 0) {
					fullUrl += "?" + request.getQueryString();
				}
				response.getWriter().print(process(fullUrl));
				return;
			}

			// find file in resources path
			returnResourceFile(path, uri, response);
		}

		public boolean ContainsUser(HttpServletRequest request) {
			HttpSession session = request.getSession(false);
			return session != null && session.getAttribute(SESSION_USER_KEY) != null;
		}

		public boolean isRequireAuth() {
			return this.username != null;
		}

		public boolean isPermittedRequest(HttpServletRequest request) {
			String remoteAddress = getRemoteAddress(request);
			return isPermittedRequest(remoteAddress);
		}

		protected String getRemoteAddress(HttpServletRequest request) {
			String remoteAddress = null;

			if (remoteAddressHeader != null) {
				remoteAddress = request.getHeader(remoteAddressHeader);
			}

			if (remoteAddress == null) {
				remoteAddress = request.getRemoteAddr();
			}

			return remoteAddress;
		}

		//////======
		private void initService() {
			try {
				String param = ConstantBean.getProperty("monitor.druid.resetEnable");
				if (param != null && param.trim().length() != 0) {
					param = param.trim();
					boolean resetEnable = Boolean.parseBoolean(param);
					statService.setResetEnable(resetEnable);
				}
			} catch (Exception e) {
				String msg = "initParameter config error, resetEnable : "
						+ ConstantBean.getProperty("monitor.druid.resetEnable");
				LOG.error(msg, e);
			}

			// 获取jmx的连接配置信息
			String param = ConstantBean.getProperty("monitor.druid.jmxUrl");
			if (param != null) {
				jmxUrl = param;
				jmxUsername = ConstantBean.getProperty("monitor.druid.jmxUsername");
				jmxPassword = ConstantBean.getProperty("monitor.druid.jmxPassword");
				try {
					initJmxConn();
				} catch (IOException e) {
					LOG.error("init jmx connection error", e);
				}
			}
		}

		/**
		 * 初始化jmx连接
		 * 
		 * @throws IOException
		 */
		private void initJmxConn() throws IOException {
			if (jmxUrl != null) {
				JMXServiceURL url = new JMXServiceURL(jmxUrl);
				Map<String, String[]> env = null;
				if (jmxUsername != null) {
					env = new HashMap<String, String[]>();
					String[] credentials = new String[] { jmxUsername, jmxPassword };
					env.put(JMXConnector.CREDENTIALS, credentials);
				}
				JMXConnector jmxc = JMXConnectorFactory.connect(url, env);
				conn = jmxc.getMBeanServerConnection();
			}
		}

		/**
		 * 根据指定的url来获取jmx服务返回的内容.
		 * 
		 * @param connetion jmx连接
		 * @param url url内容
		 * @return the jmx返回的内容
		 * @throws Exception the exception
		 */
		private String getJmxResult(MBeanServerConnection connetion, String url) throws Exception {
			ObjectName name = new ObjectName(DruidStatService.MBEAN_NAME);

			String result = (String) conn.invoke(name, "service", new String[] { url },
					new String[] { String.class.getName() });
			return result;
		}

		/**
		 * 程序首先判断是否存在jmx连接地址，如果不存在，则直接调用本地的duird服务； 如果存在，则调用远程jmx服务。在进行jmx通信，首先判断一下jmx连接是否已经建立成功，如果已经
		 * 建立成功，则直接进行通信，如果之前没有成功建立，则会尝试重新建立一遍。.
		 * 
		 * @param url 要连接的服务地址
		 * @return 调用服务后返回的json字符串
		 */
		protected String process(String url) {
			String resp = null;
			if (jmxUrl == null) {
				resp = statService.service(url);
			} else {
				if (conn == null) {// 连接在初始化时创建失败
					try {// 尝试重新连接
						initJmxConn();
					} catch (IOException e) {
						LOG.error("init jmx connection error", e);
						resp = DruidStatService.returnJSONResult(DruidStatService.RESULT_CODE_ERROR,
								"init jmx connection error" + e.getMessage());
					}
					if (conn != null) {// 连接成功
						try {
							resp = getJmxResult(conn, url);
						} catch (Exception e) {
							LOG.error("get jmx data error", e);
							resp = DruidStatService.returnJSONResult(DruidStatService.RESULT_CODE_ERROR,
									"get data error:" + e.getMessage());
						}
					}
				} else {// 连接成功
					try {
						resp = getJmxResult(conn, url);
					} catch (Exception e) {
						LOG.error("get jmx data error", e);
						resp = DruidStatService.returnJSONResult(DruidStatService.RESULT_CODE_ERROR,
								"get data error" + e.getMessage());
					}
				}
			}
			return resp;
		}
	}
}
