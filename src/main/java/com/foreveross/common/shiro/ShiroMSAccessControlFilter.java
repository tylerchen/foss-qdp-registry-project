/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import com.foreveross.common.ResultBean;
import com.foreveross.common.application.ApplicationInfoApplication;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.iff.infra.util.Assert;
import org.iff.infra.util.Exceptions;
import org.iff.infra.util.HttpHelper;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <pre>
 * Shiro ZUUL验证过滤器，Real Server 需要先在 Eureka 注册，
 * 然后通过Header[mstoken]取得EncryptDecryptUtil.deflate2Base62Encrypt值进行解码后进行 IP 验证。
 * 编码：/system/encrypeDecrypt?target=userName
 * </pre>
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 11, 2016
 */
public class ShiroMSAccessControlFilter extends AdviceFilter implements OnceValidAdvice {

    private static final org.iff.infra.util.Logger.Log Logger = org.iff.infra.util.Logger.get("FOSS.SHIRO");

    @Inject
    ApplicationInfoApplication applicationInfoApplication;

    public boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String url = StringUtils.removeStart(request.getRequestURI(), request.getContextPath());
        //是否为OnceValidAdvice。
        boolean isOnceValidAdvice = Boolean.TRUE.equals(request.getAttribute(OnceValidAdvice.REQUEST_MARK));

        String ip = HttpHelper.getRemoteIpAddr(request);
        /**
         * 验证： 如果Header[zuul]采用了约定的加密方式。
         */
        String zuulValue = request.getHeader("mstoken");

        if (StringUtils.isBlank(zuulValue)) {
            return false;
        }

        try {//开启shiro鉴权
            zuulValue = zuulValue != null && zuulValue.indexOf(' ') > -1 ? zuulValue.substring(zuulValue.indexOf(' ')).trim() : zuulValue;
            //String remoteIp = ZuulTokenHelper.mark(EncryptDecryptUtil.deflate2Base62Decrypt(zuulValue));
            Assert.state(ZuulTokenHelper.validate(zuulValue, "instance@admin.com"), "Shiro zuul auth failed, remote ip not register in eureka!");
            return true;
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