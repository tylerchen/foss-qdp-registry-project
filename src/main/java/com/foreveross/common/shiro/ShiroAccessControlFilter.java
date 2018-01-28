/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.iff.infra.util.FCS;
import org.iff.infra.util.GsonHelper;
import org.iff.infra.util.I18nHelper;
import org.iff.infra.util.Logger;
import org.iff.infra.util.ThreadLocalHelper;

import com.foreveross.common.ResultBean;
import com.foreveross.extension.log.LogHelper;

/**
 * Shiro登录验证过滤器
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 11, 2016
 */
public class ShiroAccessControlFilter extends AdviceFilter {

	private static List<String> skipUrls = Arrays.asList("", "/", "/index.html", "/system/login.do",
			"/system/logout.do", "/system/valid.png", "/common/accessDeny.html", "/common/errors.html");

	protected boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String url = StringUtils.removeStart(request.getRequestURI(), request.getContextPath());
		Subject subject = SecurityUtils.getSubject();
		ShiroUser loginUser = null;
		{
			Logger.debug(FCS.get("ShiroAccessControlFilter.preHandle, uri: {0}", url));
			LogHelper.accessLog(loginUser != null ? loginUser.getLoginId() : null, request.getRemoteAddr(), url, "URL",
					new Date());
		}
		{
			try {
				Locale locale = null;
				if (locale == null) {
					if (subject.getPrincipal() != null && subject.isAuthenticated()) {
						locale = (Locale) subject.getSession().getAttribute("locale");
					}
				}
				if (locale == null) {
					Cookie[] cookies = request.getCookies();
					for (Cookie c : cookies) {
						if ("locale".equals(c.getName())) {
							String lang = c.getValue();
							if (StringUtils.isNotBlank(lang)) {
								int indexOf = lang.indexOf('_');
								if (indexOf > -1) {
									locale = new Locale(lang.substring(0, indexOf), lang.substring(indexOf + 1));
								}
							}
						}
					}
				}
				if (locale == null) {
					locale = ThreadLocalHelper.get("locale");
				}
				if (locale != null) {
					ThreadLocalHelper.set("locale", locale);
					ThreadLocalHelper.set("I18N", I18nHelper.get(null, locale));
				}
			} catch (Exception e) {
			}
		}
		if (skipUrls.contains(url) || url.startsWith("/resource/") || url.endsWith(".html")) {
			return true;
		}
		{
			if (subject.getPrincipal() == null || !subject.isAuthenticated()) {
				response.reset();
				response.setStatus(401);
				response.getWriter().write(GsonHelper.toJsonString(ResultBean.error().setBody("Unauthorized")));
				return false;
			}
		}

		ShiroUser user = (ShiroUser) subject.getSession().getAttribute("USER");
		if (user == null) {
			response.reset();
			response.setStatus(401);
			response.getWriter().write(GsonHelper.toJsonString(ResultBean.error().setBody("Unauthorized")));
			return false;
		}
		{//设置当前用户到线程领域
			ThreadLocalHelper.set("accountId", user.getId());
			ThreadLocalHelper.set("LoginEmail", user.getLoginId());
			ThreadLocalHelper.set("loginId", user.getLoginId());
		}
		// 如果是超级用户
		if (subject.hasRole("ADMIN")) {
			Logger.debug("==ADMIN is Logging==");
			return true;
		}

		{//开启shiro鉴权
			if (!subject.isPermitted(url)) {
				response.reset();
				response.setStatus(401);
				response.getWriter().write(GsonHelper.toJsonString(ResultBean.error().setBody("Unauthorized")));
				return false;
			}
		}
		return true;
	}

	protected void cleanup(ServletRequest request, ServletResponse response, Exception existing)
			throws ServletException, IOException {
		ThreadLocalHelper.remove();
		super.cleanup(request, response, existing);
	}

}