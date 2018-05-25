/*******************************************************************************
 * Copyright (c) 七月 14 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common.web;

import com.foreveross.common.ResultBean;
import com.foreveross.common.shiro.ShiroUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.iff.infra.util.I18nHelper;
import org.iff.infra.util.Logger;
import org.iff.infra.util.ThreadLocalHelper;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * Controller的基础类，所有的Controller必须要继承这个。
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 9, 2015
 * auto generate by qdp.
 */
public class BaseController {

    /**
     * 打印结果到response.
     *
     * @param response
     * @param result
     * @throws IOException
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 17, 2017
     */
    public void outPrint(HttpServletResponse response, String result) throws IOException {
        PrintWriter out = response.getWriter();
        out.print(result);
    }

    /**
     * 成功返回内容。
     *
     * @param data
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 17, 2017
     */
    protected ResultBean success(Object data) {
        return ResultBean.success().setBody(data);
    }

    /**
     * 错误返回内容。
     *
     * @param data
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 17, 2017
     */
    protected ResultBean error(Object data) {
        return ResultBean.error().setBody(data);
    }

    /**
     * 异常返回内容。
     *
     * @param t
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 17, 2017
     */
    protected ResultBean error(Throwable t) {
        Logger.debug("ERROR", t);
        return ResultBean.error().setBody(t);
    }

    /**
     * 返回I18N。
     *
     * @param code
     * @param defaultMessage
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 17, 2017
     */
    protected String getI18nMessage(String code, String defaultMessage) {
        I18nHelper i18n = ThreadLocalHelper.get("I18N");
        if (i18n == null) {
            Subject subject = SecurityUtils.getSubject();
            Locale locale = (Locale) subject.getSession().getAttribute("locale");
            i18n = I18nHelper.get(null, locale);
            ThreadLocalHelper.set("I18N", i18n);
        }
        return i18n.getMessage(code, defaultMessage);
    }

    /**
     * 获得登录用户。
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 17, 2017
     */
    protected ShiroUser getAccount() {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.getPrincipal() == null || !currentUser.isAuthenticated()) {
            return null;
        } else {
            return (ShiroUser) currentUser.getSession().getAttribute("USER");
        }
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(java.util.Date.class, new DateEditor());
    }
}
