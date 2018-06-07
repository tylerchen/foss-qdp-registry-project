/*******************************************************************************
 * Copyright (c) 七月 14 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common.web;

import com.foreveross.common.ConstantBean;
import com.foreveross.common.ResultBean;
import com.foreveross.common.application.ApplicationInfoApplication;
import com.foreveross.common.application.ImageCaptchaApplication;
import com.foreveross.common.application.SystemApplication;
import com.foreveross.common.shiro.JWTTokenHelper;
import com.foreveross.common.shiro.ShiroUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.iff.infra.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 登录基础功能：登录、登出、验证码。
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 9, 2015
 * auto generate by qdp.
 */
@Controller
@RequestMapping("/system")
public class SystemController extends BaseController {

    @Inject
    ImageCaptchaApplication imageCaptchaApplication;

    @Inject
    @Named("systemApplication")
    SystemApplication systemApplication;


    @Inject
    ApplicationInfoApplication applicationInfoApplication;

    Boolean validCode = null;

    /**
     * 认证码验证
     *
     * @param request
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Apr 11, 2018
     */
    private boolean validateCode(HttpServletRequest request) {
        if (validCode == null) {
            String temp = ConstantBean.getProperty("auth.login.validcode.enable", "true").trim();
            validCode = !"false".equalsIgnoreCase(temp);
        }
        if (!validCode) {
            return true;
        }
        String code = request.getParameter("validCode");
        return imageCaptchaApplication.validateForID(request.getSession().getId(), code);
    }

    @ResponseBody
    @RequestMapping(path = "/login.do", method = RequestMethod.POST)
    public ResultBean login(ShiroUser user, HttpServletRequest request, HttpServletResponse response,
                            ModelMap modelMap) {
        // get login info if has login
        if (user == null || user.getLoginId() == null || user.getLoginPasswd() == null) {
            try {
                Subject subject = SecurityUtils.getSubject();
                if (subject == null || subject.getPrincipal() == null || !subject.isAuthenticated()) {
                    response.setStatus(401);
                    return error("Unauthorized");
                } else {
                    return success(subject.getSession().getAttribute("USER"));
                }
            } catch (Exception e) {
                response.setStatus(401);
                return error("Unauthorized");
            }
        }

        try {
            {/*认证码验证*/
                boolean valid = validateCode(request);
                if (!valid) {
                    return error("请输入正确的验证码！");
                }
            }
            {
                String loginPasswdEnc = user.getLoginPasswd();
                if (StringUtils.isBlank(loginPasswdEnc)) {
                    return error("无此帐户或登录密码错误！");
                }
                try {
                    String realPassword = RSAHelper.decrypt(loginPasswdEnc,
                            RSAHelper.getPrivateKeyFromBase64(ConstantBean.getProperty("rsa.key.private.base64")));
                    user.setLoginPasswd(realPassword);
                } catch (Exception e) {
                    return error("无此帐户或登录密码错误！");
                }
            }

            user = systemApplication.login(user);

            if (user == null) {
                return error("无此帐户或登录密码错误！");
            }

            ///////////////////shiro登陆////////////////////
            Subject currentUser = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(user.getLoginId(), user.getLoginPasswd());
            token.setRememberMe(true);
            Locale locale = null;
            {
                locale = (Locale) currentUser.getSession().getAttribute("locale");
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
            }
            {
                currentUser.getSession().stop();
            }
            {
                currentUser.login(token);
                Logger.info(user.getLoginId() + " is login.");
            }
            {
                if (locale != null) {
                    currentUser.getSession().setAttribute("locale", locale);
                    Cookie localeCookie = new Cookie("locale", locale.toString());
                    localeCookie.setMaxAge(60 * 60 * 24 * 365);
                    response.addCookie(localeCookie);
                }
            }
            {
                currentUser.getSession().setAttribute("USER", user);
            }
            return success(user);
        } catch (Exception e) {
            return error(e);
        }

    }

    @ResponseBody
    @RequestMapping(path = "/login.jwt", method = RequestMethod.POST)
    public ResultBean jwtToken(ShiroUser user, HttpServletRequest request, HttpServletResponse response,
                               ModelMap modelMap) {
        // get login info if has login
        if (user == null || user.getLoginId() == null || user.getLoginPasswd() == null) {
            response.setStatus(401);
            return error("Unauthorized");
        }

        try {
            {
                String loginPasswdEnc = user.getLoginPasswd();
                if (StringUtils.isBlank(loginPasswdEnc)) {
                    return error("无此帐户或登录密码错误！");
                }
                try {
                    String realPassword = RSAHelper.decrypt(loginPasswdEnc,
                            RSAHelper.getPrivateKeyFromBase64(ConstantBean.getProperty("rsa.key.private.base64")));
                    user.setLoginPasswd(realPassword);
                } catch (Exception e) {
                    return error("无此帐户或登录密码错误！");
                }
            }

            user = systemApplication.login(user);

            if (user == null) {
                return error("无此帐户或登录密码错误！");
            }

            {
                /*禁止缓存*/
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(200);
                return success(JWTTokenHelper.encodeToken(user.getLoginId())).addHeader("Expires", 5 * 60 * 1000);
            }
        } catch (Exception e) {
            return error(e);
        }
    }

    @ResponseBody
    @RequestMapping("/logout.do")
    public ResultBean logout(ShiroUser user, HttpServletRequest request) {
        Subject currentUser = SecurityUtils.getSubject();
        try {
            request.getSession().removeAttribute("USER");
            request.getSession().invalidate();
            request.getSession(true);
            currentUser.logout();
        } catch (Exception e) {
            return error("Unauthorized");
        }
        return success("Logout success.");
    }

    @RequestMapping("/valid.png")
    public void validateImage(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        try {
            byte[] image = imageCaptchaApplication.getImageForID(request.getSession().getId());
            /*禁止图像缓存*/
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/png");
            /*将图像输出到Servlet输出流中*/
            OutputStream out = response.getOutputStream();
            out.write(image);
            out.flush();
            SocketHelper.closeWithoutError(out);
        } catch (Exception e) {
            error(e);
        }
    }

    @ResponseBody
    @RequestMapping("/encryptPassword.do")
    public ResultBean encryptPassword(HttpServletRequest request) {
        try {
            String parameter = request.getParameter("password");
            if (!StringUtils.isBlank(parameter)) {
                parameter = MD5Helper.secondSalt(MD5Helper.firstSalt(parameter));
            }
            return success(parameter);
        } catch (Exception e) {
            return error(e);
        }
    }

    @RequestMapping("/info")
    public void info(@RequestBody(required = false) String body,
                     @RequestHeader(name = "Accept", required = false) String accept,
                     @RequestHeader(name = "Content-Type", required = false) String contentType, HttpServletRequest request,
                     HttpServletResponse response) {
        try {
            //检测输入数据的类型。
            boolean isJson = StringUtils.contains(contentType, "application/json");
            boolean isXml = StringUtils.contains(contentType, "application/xml");
            boolean isForm = StringUtils.contains(contentType, "application/x-www-form-urlencoded")
                    || (!isJson && !isXml);

            //设置输出的格式：xml-xstream, json。
            boolean isHtml = accept != null && StringUtils.contains(accept, "text/html");
            Class<?> xmlOrJsonHelper = accept != null && !StringUtils.contains(accept, "text") && accept.indexOf("xml") > -1 ? XStreamHelper.class
                    : GsonHelper.class;

            Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
            map.put("restConfig", applicationInfoApplication.restConfig());
            try {
                if (SecurityUtils.getSubject().isAuthenticated()) {
                    map.put("applicationConfig",
                            applicationInfoApplication.applicationConfig());
                    map.put("systemConfig",
                            applicationInfoApplication.systemConfig());
                    map.put("springBootConfig",
                            applicationInfoApplication.springBootConfig());
                }
            } catch (Exception e) {
            }
            if (isHtml) {
                response.setContentType("text/html;charset=UTF-8");
                String html = toInfoHtml(map);
                response.getWriter().print(html);
                SocketHelper.closeWithoutError(response.getWriter());
            } else if (isXml) {
                response.setContentType("application/json;charset=UTF-8");
                String json = GsonHelper.toJsonString(map);
                response.getWriter().print(json);
                SocketHelper.closeWithoutError(response.getWriter());
            } else if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                String json = GsonHelper.toJsonString(map);
                response.getWriter().print(json);
                SocketHelper.closeWithoutError(response.getWriter());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String toInfoHtml(Map<String, Map<String, String>> map) {
        String html = "" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "<meta http-equiv=\"x-ua-compatible\" content=\"IE=9\">\n" +
                "<meta charset=\"utf-8\">\n" +
                "<title>Application Info Page</title>\n" +
                "</head>\n" +
                "<body>\n";
        for (Map.Entry<String, Map<String, String>> pEntry : map.entrySet()) {
            String content = "";
            String pKey = pEntry.getKey();
            content += "<h1>" + pKey + "</h1>\n<hr />\n";
            Map<String, String> confMap = pEntry.getValue();
            Set<Map.Entry<String, String>> entries = confMap.entrySet();
            if (!"restConfig".equals(pKey)) {
                content += "<table><tbody>\n";
                for (Map.Entry<String, String> entry : entries) {
                    content += "<tr><td class=\"info-title\">" + entry.getKey() + "</td><td class=\"info-value\">" + entry.getValue() + "</td></tr>\n";
                }
                content += "</tbody></table>\n";
            } else {
                Set<String> keys = confMap.keySet();
                for (String key : keys) {
                    if (!(StringUtils.startsWith(key, "[GET]") || StringUtils.startsWith(key, "[POST]") || StringUtils.startsWith(key, "[PUT]") || StringUtils.startsWith(key, "[DELETE]"))) {
                        continue;
                    }
                    String restId = confMap.get(key);
                    content += "<table><thead><tr><th>" + key + "</th></tr></thead></table>";
                    content += "<table><thead><tr><th>Parameter</th><th>Type</th><th>Default Value</th></tr></thead>\n";
                    String paramPre = "[ARG:" + restId + "]";
                    content += "<tbody>";
                    for (String paramKey : keys) {
                        if (!StringUtils.startsWith(paramKey, paramPre)) {
                            continue;
                        }
                        String paramValue = confMap.get(paramKey);
                        String paramName = StringUtils.substringBefore(StringUtils.substringAfter(paramKey, "]"), "-");
                        String paramType = StringUtils.substringAfter(StringUtils.substringAfter(paramKey, "]"), "-");
                        content += "<tr><td class=\"info-title\">" + paramName + "</td><td>" + paramType + "</td><td>" + ("--NULL--".equals(paramValue) ? null : paramValue) + "</td>\n";
                    }
                    content += "</tbody></table>\n";
                }

            }
            html += content;
        }

        html += "</body></html>";
        html += "<style type=\"text/css\">\n";
        html += "table{width: 100%;border-collapse: collapse;border: 1px solid #ccc;}\n" +
                "table thead th {font-size: 12px;color: #333333;text-align: left;background-color: #ebeced;\n" +
                "/*background: url(table_top.jpg) repeat-x top center;*/\n" +
                "border: 1px solid #ccc; font-weight:bold;}\n" +
                "table tbody tr {background: #fff;font-size: 12px;color: #666666;}\n" +
                "table tbody tr.alt-row {background: #f2f7fc;}\n" +
                "table td{line-height:20px;text-align: left;padding:4px 10px 3px 10px;height: 18px;border: 1px solid #ccc;}\n" +
                ".info-title{background: #f4f5f7;padding-right: 10px;font-weight: bold;width: 100px;word-wrap: normal;white-space: nowrap;}";
        html += "</style>\n";

        return html;
    }
}
