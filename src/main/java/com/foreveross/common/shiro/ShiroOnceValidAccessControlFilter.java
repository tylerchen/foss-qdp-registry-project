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
import org.apache.shiro.web.servlet.AdviceFilter;
import org.iff.infra.util.FCS;
import org.iff.infra.util.ThreadLocalHelper;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Shiro登录验证过滤器，当存在多种登录验证方式时，并只满足一种登录就可以访问，那么就可以使用这个Filter。
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 11, 2016
 */
public class ShiroOnceValidAccessControlFilter extends AdviceFilter implements OnceValidAdvice {

    private static final org.iff.infra.util.Logger.Log Logger = org.iff.infra.util.Logger.get("FOSS.SHIRO");

    private List<OnceValidAdvice> onceFilterChains = new ArrayList<OnceValidAdvice>();

    public boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String url = StringUtils.removeStart(request.getRequestURI(), request.getContextPath());
        if (ShiroHelper.skipUrl(url)) {
            return true;
        }
        {
            Logger.debug(FCS.get("Shiro filter " + getClass().getSimpleName() + " preHandle, uri: {0}", url));
            LogHelper.accessLog(null, request.getRemoteAddr(), url, "URL", new Date());
        }
        {
            request.setAttribute(OnceValidAdvice.REQUEST_MARK, true);
        }
        try {//会通过抛出异常来终止验证过程
            for (OnceValidAdvice filter : onceFilterChains) {
                if (filter.preHandle(request, response)) {
                    return true;
                }
            }
            if (!ShiroHelper.sendRedirect(response, url)) {
                ShiroHelper.retrun401(request, response, ResultBean.error().setBody("Unauthorized"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            //异常直接终止，需要在各个OnceValidAdvice中处理response。
        }
        return false;
    }

    protected void cleanup(ServletRequest request, ServletResponse response, Exception existing)
            throws ServletException, IOException {
        ThreadLocalHelper.remove();
        super.cleanup(request, response, existing);
    }

    public List<OnceValidAdvice> getOnceFilterChains() {
        return onceFilterChains;
    }

    public void setOnceFilterChains(List<OnceValidAdvice> onceFilterChains) {
        this.onceFilterChains = onceFilterChains;
    }
}