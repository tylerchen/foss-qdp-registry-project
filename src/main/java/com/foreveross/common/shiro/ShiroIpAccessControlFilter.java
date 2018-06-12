/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import com.foreveross.common.ConstantBean;
import com.foreveross.common.ResultBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.iff.infra.util.Exceptions;
import org.iff.infra.util.FCS;
import org.iff.infra.util.HttpHelper;
import org.iff.infra.util.StringHelper;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Shiro IP验证过滤器
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 11, 2016
 */
public class ShiroIpAccessControlFilter extends AdviceFilter implements OnceValidAdvice {

    private static final org.iff.infra.util.Logger.Log Logger = org.iff.infra.util.Logger.get("FOSS.SHIRO");

    private String accessIp = null;
    private String[] ips = null;

    public boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //String url = StringUtils.removeStart(request.getRequestURI(), request.getContextPath());
        //是否为OnceValidAdvice。
        boolean isOnceValidAdvice = Boolean.TRUE.equals(request.getAttribute(OnceValidAdvice.REQUEST_MARK));

        String ip = HttpHelper.getRemoteIpAddr(request);

        {/*check the match list.*/
            if (accessIp == null) {
                accessIp = ConstantBean.getProperty("access.ip", "").trim();
            }
            if (accessIp.length() < 1) {
                return false;
            }
            if (ips == null) {
                ips = StringUtils.split(accessIp, ',');
            }
            for (String aip : ips) {
                if (aip.indexOf('*') < 0 && aip.equals(ip)) {
                    Logger.debug(FCS.get("Shiro ip auth success, ip: {0}", ip));
                    return true;
                } else if (StringHelper.wildCardMatch(ip, aip.trim())) {
                    Logger.debug(FCS.get("Shiro ip auth success, ip: {0}", ip));
                    return true;
                }
            }
        }

        ShiroHelper.retrun401(request, response, ResultBean.error().setBody("Unauthorized"));
        if (isOnceValidAdvice) {
            Exceptions.runtime("Shiro not permit, end OnceValidAdvice chain.", "FOSS-SHIRO-0100");
        }
        return false;
    }

    protected void cleanup(ServletRequest request, ServletResponse response, Exception existing)
            throws ServletException, IOException {
        super.cleanup(request, response, existing);
    }

}