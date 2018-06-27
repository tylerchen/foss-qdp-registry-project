/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import com.foreveross.common.ResultBean;
import com.foreveross.common.util.EncryptDecryptUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.iff.infra.util.Assert;
import org.iff.infra.util.Exceptions;
import org.iff.infra.util.FCS;
import org.iff.infra.util.HttpHelper;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <pre>
 * Shiro Eureka验证过滤器，通过验证Header中的Authorization值来验证密码。
 * </pre>
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 11, 2016
 */
public class ShiroEurekaAccessControlFilter extends AdviceFilter implements OnceValidAdvice {

    private static final org.iff.infra.util.Logger.Log Logger = org.iff.infra.util.Logger.get("FOSS.SHIRO");

    public boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String url = StringUtils.removeStart(request.getRequestURI(), request.getContextPath());
        //是否为OnceValidAdvice。
        boolean isOnceValidAdvice = Boolean.TRUE.equals(request.getAttribute(OnceValidAdvice.REQUEST_MARK));
        if (!StringUtils.equals(url, "/eureka") && !StringUtils.startsWith(url, "/eureka/")) {
            return false;
        }
        String ip = HttpHelper.getRemoteIpAddr(request);
        String token = request.getHeader("Authorization");

        try {
            Assert.isTrue(StringUtils.startsWith(token, "Basic "), "Shiro eureka Authorization Basic value not found!");
            token = token.substring(token.indexOf(" ")).trim();

            String decodeBase64 = new String(Base64.decodeBase64(token));
            Assert.isTrue(StringUtils.indexOf(decodeBase64, ':') > -1, "Shiro eureka Authorization Basic is invalid!");
            String userName = decodeBase64.substring(0, decodeBase64.indexOf(':'));
            String password = decodeBase64.substring(decodeBase64.indexOf(':') + 1);


            boolean valid = StringUtils.startsWith(EncryptDecryptUtil.deflate2Base62Decrypt(password), "eureka@admin.com,");
            Assert.isTrue(valid, "Shiro eureka Authorization Basic not pass!");
            Logger.debug(FCS.get("Shiro Author auth success, ip: {0}", ip));
            return valid;
        } catch (Exception e) {
            ShiroHelper.retrun401(request, response, ResultBean.error().setBody("Unauthorized"));
            if (isOnceValidAdvice) {
                Exceptions.runtime("Shiro not permit, end OnceValidAdvice chain.", "FOSS-SHIRO-0100");
            }
        }
        return false;
    }

    protected void cleanup(ServletRequest request, ServletResponse response, Exception existing)
            throws ServletException, IOException {
        super.cleanup(request, response, existing);
    }

}