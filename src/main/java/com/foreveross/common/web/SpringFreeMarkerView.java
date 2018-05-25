/*******************************************************************************
 * Copyright (c) Jan 28, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.web;

import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.iff.infra.util.ActionHelper;
import org.iff.infra.util.I18nHelper;
import org.iff.infra.util.Logger;
import org.iff.infra.util.ThreadLocalHelper;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * try catch the freemarker template exception and redirect to error page.
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Jan 28, 2016
 */
public class SpringFreeMarkerView extends FreeMarkerView {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionHelper helper = ActionHelper.get();
        if (helper == null) {
            helper = ActionHelper.create(request, response);
        }
        {
            ((Map) model).put("request", request);
            try {
                Locale locale = null;
                if (locale == null) {
                    Subject subject = SecurityUtils.getSubject();
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
                if (locale != null) {
                    ThreadLocalHelper.set("locale", locale);
                    ThreadLocalHelper.set("I18N", I18nHelper.get(null, locale));
                }
            } catch (Exception e) {
            }
        }
        super.render(model, request, response);
    }

    protected void processTemplate(Template template, SimpleHash model, HttpServletResponse response)
            throws IOException, TemplateException {
        try {
            super.processTemplate(template, model, response);
        } catch (Exception e) {
            ActionHelper helper = ActionHelper.get();
            if (helper != null) {
                helper.redirect(helper.getRequest().getContextPath() + "/common/errors.html");
            }
            Logger.error("Render template error", e);
        }
    }
}
