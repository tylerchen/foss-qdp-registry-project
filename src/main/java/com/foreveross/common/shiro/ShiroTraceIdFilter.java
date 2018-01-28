/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.iff.infra.util.FCS;
import org.iff.infra.util.HttpHelper;
import org.iff.infra.util.Logger;
import org.iff.infra.util.StringHelper;

/**
 * 设置跟踪ID，通过这个ID可以跟踪一个请求。
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 11, 2016
 */
public class ShiroTraceIdFilter extends AdviceFilter {

	protected boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String url = "";
		String ip = "";
		String traceId = "";
		String sessionId = "";
		String loginId = "";
		Subject subject = SecurityUtils.getSubject();
		Session session = null;
		{
			url = StringUtils.removeStart(request.getRequestURI(), request.getContextPath());
		}
		{
			ip = HttpHelper.getRemoteIpAddr(request);
		}
		{
			traceId = StringUtils.defaultIfBlank(request.getHeader("TRACE_ID"), Logger.getTraceId());
		}
		{
			session = subject.getSession(false);
		}
		{
			if (session != null) {
				sessionId = String.valueOf(session.getId());
			}
		}
		{
			if (session != null) {
				ShiroUser loginUser = (ShiroUser) session.getAttribute("USER");
				loginId = StringUtils.defaultIfBlank(loginUser == null ? "" : loginUser.getLoginId(), "ANON");
			}
		}
		{/*set the trace id*/

			String[] split = StringUtils.split(traceId, '/');
			traceId = StringHelper.concat(split.length > 0 ? split[0] : StringHelper.uuid(), "/", ip, "/", sessionId,
					"/", loginId);
			Logger.updateTraceId(traceId);
			response.addHeader("TRACE_ID", traceId);
			Logger.debug(FCS.get("ShiroTraceIdFilter.preHandle, uri: {0}", url));
		}
		return true;
	}

	protected void cleanup(ServletRequest request, ServletResponse response, Exception existing)
			throws ServletException, IOException {
		super.cleanup(request, response, existing);
	}
}