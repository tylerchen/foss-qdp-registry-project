/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import com.foreveross.common.ResultBean;
import com.foreveross.extension.log.LogHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.iff.infra.util.Exceptions;
import org.iff.infra.util.FCS;
import org.iff.infra.util.I18nHelper;
import org.iff.infra.util.ThreadLocalHelper;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

/**
 * Shiro登录验证过滤器
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 11, 2016
 */
public class ShiroAccessControlFilter extends AdviceFilter implements OnceValidAdvice {

    private static final org.iff.infra.util.Logger.Log Logger = org.iff.infra.util.Logger.get("FOSS-SHIRO");

    public boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String url = StringUtils.removeStart(request.getRequestURI(), request.getContextPath());
        Subject subject = SecurityUtils.getSubject();
        ShiroUser loginUser = null;
        //是否为OnceValidAdvice。
        boolean isOnceValidAdvice = Boolean.TRUE.equals(request.getAttribute(OnceValidAdvice.REQUEST_MARK));
        {
            Logger.debug(FCS.get("Shiro ShiroAccessControlFilter.preHandle, uri: {0}", url));
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
        if (!isOnceValidAdvice && ShiroHelper.skipUrl(url)) {
            return true;
        }
        {
            if (subject.getPrincipal() == null || !subject.isAuthenticated()) {
                sendRedirectOrUnauthorizedJson(request, response, url, isOnceValidAdvice);
                return false;
            }
        }

        ShiroUser user = (ShiroUser) subject.getSession().getAttribute("USER");
        if (user == null) {
            sendRedirectOrUnauthorizedJson(request, response, url, isOnceValidAdvice);
            return false;
        }
        {//设置当前用户到线程领域
            ThreadLocalHelper.set("accountId", user.getId());
            ThreadLocalHelper.set("LoginEmail", user.getLoginId());
            ThreadLocalHelper.set("loginId", user.getLoginId());
        }
        // 如果是超级用户
        if (subject.hasRole("ADMIN")) {
            Logger.debug("Shiro ==ADMIN is Logging==");
            return true;
        }

        {//开启shiro鉴权
            if (!subject.isPermitted(url)) {//鉴权不通过就要返回401或重定向
                if (!ShiroHelper.sendRedirect(response, url)) {
                    ShiroHelper.retrun401(request, response, ResultBean.error().setBody("Unauthorized"));
                }
                if (isOnceValidAdvice) {
                    Exceptions.runtime("Shiro not permit, end OnceValidAdvice chain.", "FOSS-SHIRO-0100");
                }
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

    private void sendRedirectOrUnauthorizedJson(HttpServletRequest request, HttpServletResponse response, String url,
                                                boolean isOnceValidAdvice) throws IOException {
        if (!isOnceValidAdvice) {
            if (!ShiroHelper.sendRedirect(response, url)) {
                ShiroHelper.retrun401(request, response, ResultBean.error().setBody("Unauthorized"));
            }
        }
    }
}