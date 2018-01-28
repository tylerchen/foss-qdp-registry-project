/*******************************************************************************
 * Copyright (c) 七月 14 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.FCS;
import org.iff.infra.util.GsonHelper;
import org.iff.infra.util.XStreamHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.common.ResultBean;
import com.foreveross.common.restfull.UriManager;

/**
 * Restful 入口。
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 9, 2015
 * auto generate by qdp.
 */
@Controller
@RequestMapping("/rest")
public class RestfulController extends BaseController {
	/**
	 * invoke bean method and return xml result, only accept "params" parameter name and json value or xstream xml value.
	 * @param modelMap
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Jul 19, 2016
	 */
	@RequestMapping(value = "/**", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String rest(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String requestURI = request.getRequestURI();
			String reportPath = StringUtils.substringAfter(requestURI, "/rest/");

			Map<String, Object> conditionParams = new LinkedHashMap<String, Object>();
			{
				Enumeration<String> names = request.getParameterNames();
				while (names.hasMoreElements()) {
					String name = names.nextElement();
					conditionParams.put(name, request.getParameter(name));
				}
				conditionParams.putAll(modelMap);
			}

			Object o = UriManager.invoke(reportPath, request.getMethod(), conditionParams);

			String value = invokeToString(o, conditionParams, GsonHelper.class);
			return value;
		} catch (Exception ex) {
			return GsonHelper.toJsonString(
					ResultBean.error().setBody(FCS.get("get json error: {0}", ex.getMessage()).toString()));
		}
	}

	private String invokeToString(Object value, Map<String, Object> conditionParams, Class<?> xmlOrJsonHelper) {
		{
			try {
				Object invoke = value;
				return toXmlOrJson(xmlOrJsonHelper, ResultBean.success().setBody(invoke));
			} catch (Throwable e) {
				e.printStackTrace();
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				return toXmlOrJson(xmlOrJsonHelper, ResultBean.error().setBody(sw.toString()));
			}
		}
	}

	private String toXmlOrJson(Class<?> xmlJsonHelper, ResultBean result) {
		if (XStreamHelper.class.isAssignableFrom(xmlJsonHelper)) {
			return XStreamHelper.toXml(result);
		} else if (GsonHelper.class.isAssignableFrom(xmlJsonHelper)) {
			return GsonHelper.toJsonString(result);
		}
		return null;
	}
}
