/*******************************************************************************
 * Copyright (c) Mar 22, 2018 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import com.foreveross.common.ConstantBean;
import com.foreveross.common.ResultBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.Subject;
import org.iff.infra.util.FCS;
import org.iff.infra.util.GsonHelper;
import org.iff.infra.util.StringHelper;
import org.iff.infra.util.XStreamHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Mar 22, 2018
 */
public class ShiroHelper {

    private static final org.iff.infra.util.Logger.Log Logger = org.iff.infra.util.Logger.get("FOSS.SHIRO");

    private static final ReentrantLock LOCK = new ReentrantLock();

    protected ShiroHelper() {
    }

    public static void retrun401(HttpServletRequest request, HttpServletResponse response, ResultBean resultBean)
            throws IOException {
        String accept = request.getHeader("Accept");
        //检测输入数据的类型。
        boolean isXml = !StringUtils.contains(accept, "text") && StringUtils.contains(accept, "xml");

        response.reset();
        response.setStatus(401);
        response.setContentType(isXml ? "application/xml" : "application/json;charset=UTF-8");

        String result = isXml ? XStreamHelper.toXml(resultBean) : GsonHelper.toJsonString(resultBean);

        response.getWriter().write(result);
    }

    private static String skipUrl = null;
    private static String[] skipUrls = null;

    public static boolean skipUrl(String url) {
        if (skipUrl == null) {
            LOCK.lock();
            try {
                skipUrl = ConstantBean.getProperty("shiro.skipUrls", "").trim();
            } finally {
                LOCK.unlock();
            }
        }
        if (skipUrl.length() < 1) {
            return true;
        }
        if (skipUrls == null) {
            LOCK.lock();
            try {
                skipUrls = StringUtils.split(skipUrl, ';');
            } finally {
                LOCK.unlock();
            }
        }
        boolean match = false;
        for (String urlPattern : skipUrls) {
            if (urlPattern.indexOf('*') < 0) {
                if ((match = urlPattern.equals(url))) {
                    break;
                }
            } else if ((match = StringHelper.wildCardMatch(url, urlPattern))) {
                break;
            }
        }
        return match;
    }

    private static String unauthorizedUrl = null;
    private static String toUrl = null;
    private static String[] unauthorizedUrls = null;

    /**
     * 根据配置决定是否发送重定向，如果发送就返回true，否则就返回false。
     *
     * @param response
     * @param url
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Mar 21, 2018
     */
    public static boolean sendRedirect(HttpServletResponse response, String url) {
        if (unauthorizedUrl == null) {
            LOCK.lock();
            try {
                unauthorizedUrl = ConstantBean.getProperty("shiro.redirect.unauthorized.url", "").trim();
            } finally {
                LOCK.unlock();
            }
        }
        if (unauthorizedUrl.length() < 1) {
            return false;
        }
        if (toUrl == null) {
            LOCK.lock();
            try {
                toUrl = ConstantBean.getProperty("shiro.redirect.to.url", "").trim();
            } finally {
                LOCK.unlock();
            }
        }
        if (toUrl.length() < 1) {
            return false;
        }
        if (unauthorizedUrls == null) {
            LOCK.lock();
            try {
                unauthorizedUrls = StringUtils.split(unauthorizedUrl, ';');
            } finally {
                LOCK.unlock();
            }
        }
        boolean match = false;
        for (String redirectUrl : unauthorizedUrls) {
            if (redirectUrl.indexOf('*') < 0 && (match = redirectUrl.equals(url))) {
                break;
            } else if ((match = StringHelper.wildCardMatch(url, redirectUrl))) {
                break;
            }
        }
        if (match) {
            Logger.debug(FCS.get("Shiro unauthorized url configured and redirect: {0} to {1}.", url, toUrl));
            response.reset();
            response.setHeader(HttpHeaders.LOCATION, toUrl);
            response.setStatus(HttpStatus.FOUND.value());
            return true;
        } else {
            Logger.debug(FCS.get("Shiro unauthorized url configured and not match to redirect: {0} not match {1}.", url,
                    unauthorizedUrl));
        }
        return false;
    }

    public static boolean isAdmin(Subject subject) {
        return subject.hasRole("ADMIN");
    }

    public static boolean isPermitted(Subject subject, String url) {
        return subject.hasRole("ADMIN") || subject.isPermitted(url);
    }
}
