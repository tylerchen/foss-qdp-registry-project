/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import com.foreveross.common.ResultBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
 * JSON Web Token (JWT) is an open standard (RFC 7519) that defines a compact and
 * self-contained way for securely transmitting information between parties as a JSON object.
 * This information can be verified and trusted because it is digitally signed.
 * JWTs can be signed using a secret (with the HMAC algorithm) or a public/private key pair using RSA
 * </pre>
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 28, 2017
 */
public class ShiroJwtAccessControlFilter extends AdviceFilter implements OnceValidAdvice {

    private static final org.iff.infra.util.Logger.Log Logger = org.iff.infra.util.Logger.get("FOSS.SHIRO");

    public boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String url = StringUtils.removeStart(request.getRequestURI(), request.getContextPath());
        //是否为OnceValidAdvice。
        boolean isOnceValidAdvice = Boolean.TRUE.equals(request.getAttribute(OnceValidAdvice.REQUEST_MARK));
        String ip = HttpHelper.getRemoteIpAddr(request);

        String jwtToken = request.getHeader("Authorization");

        if (!StringUtils.startsWith(jwtToken, "Bearer ")) {
            jwtToken = request.getParameter("_jwt");
            if (StringUtils.isBlank(jwtToken)) {
                return false;
            }
        } else {
            jwtToken = jwtToken.substring(jwtToken.indexOf(" ")).trim();
        }

        try {//开启shiro鉴权
            Subject subject = SecurityUtils.getSubject();
            subject.login(new JWTToken(jwtToken));
            //Shiro鉴权不通过，如果要终止后续的验证，需要自行返回错误信息并抛出异常
            Assert.state(ShiroHelper.isPermitted(subject, url));
            Logger.debug(FCS.get("Shiro jwt auth success, ip: {0}", ip));
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
        //ThreadLocalHelper.remove();
        super.cleanup(request, response, existing);
    }
}