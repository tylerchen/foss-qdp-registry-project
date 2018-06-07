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
import com.foreveross.common.restfull.UriManager;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.FCS;
import org.iff.infra.util.GsonHelper;
import org.iff.infra.util.XStreamHelper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Restful 入口。
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 9, 2015
 * auto generate by qdp.
 */
@RestController
@RequestMapping("/rest")
public class RestfulController extends BaseController {
    /**
     * 可以接收:application/x-www-form-urlencoded, xml-xstream, json等数据。输出数据根据Accept Header来决定，只支持xml-xstream, json，默认输出json。
     *
     * @param body        xml或json数据
     * @param accept      默认输出json，可以支持xml-xstream, json。
     * @param contentType 输入数据的格式，支持：application/x-www-form-urlencoded, xml-xstream, json。
     * @param request
     * @param response
     * @return 返回数据是以com.foreveross.common.ResultBean结构返回: {header: Map, body: Object}。
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Jul 19, 2016
     */
    @RequestMapping(value = "/**", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.APPLICATION_ATOM_XML_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE})
    public String rest(@RequestBody(required = false) String body,
                       @RequestHeader(name = "Accept", required = false) String accept,
                       @RequestHeader(name = "Content-Type", required = false) String contentType, HttpServletRequest request,
                       HttpServletResponse response) {
        try {
            String requestURI = request.getRequestURI();
            String reportPath = StringUtils.substringAfter(requestURI, "/rest/");

            //检测输入数据的类型。
            boolean isJson = StringUtils.contains(contentType, "application/json");
            boolean isXml = StringUtils.contains(contentType, "application/xml");
            boolean isForm = StringUtils.contains(contentType, "application/x-www-form-urlencoded")
                    || (!isJson && !isXml);

            //设置输出的格式：xml-xstream, json。
            Class<?> xmlOrJsonHelper = accept != null && !StringUtils.contains(accept, "text") && accept.indexOf("xml") > -1 ? XStreamHelper.class
                    : GsonHelper.class;

            //表单数据，及parameter数据。
            Map<String, Object> conditionParams = new LinkedHashMap<String, Object>();
            {
                Enumeration<String> names = request.getParameterNames();
                while (names.hasMoreElements()) {
                    String name = names.nextElement();
                    conditionParams.put(name, request.getParameter(name));
                }
            }

            //xml-xstream, json数据。
            Object data = null;
            if (isJson) {
                data = GsonHelper.toJson(body);
                if (data instanceof List) {
                    data = ((List<?>) data).toArray();
                } else {
                    data = new Object[]{data};
                }
            } else if (isXml) {
                data = XStreamHelper.fromXml(body);
                if (data != null && !data.getClass().isArray()) {
                    data = new Object[]{data};
                }
            }

            //转换为数据，对应方法调用的args。
            Object[] postData = data == null || isForm ? new Object[0] : (Object[]) data;

            Object o = UriManager.invoke(reportPath, request.getMethod(), conditionParams, postData);

            String value = invokeToString(o, conditionParams, xmlOrJsonHelper);
            return value;
        } catch (Exception ex) {
            return GsonHelper.toJsonString(
                    ResultBean.error().setBody(FCS.get("get json error: {0}", ex.getMessage()).toString()));
        }
    }

    private String invokeToString(Object value, Map<String, Object> conditionParams, Class<?> xmlOrJsonHelper) {
        {
            try {
                Object invoke = value;
                return toXmlOrJson(xmlOrJsonHelper, ResultBean.success().setBody(invoke));
            } catch (Throwable e) {
                e.printStackTrace();
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                return toXmlOrJson(xmlOrJsonHelper, ResultBean.error().setBody(sw.toString()));
            }
        }
    }

    private String toXmlOrJson(Class<?> xmlJsonHelper, ResultBean result) {
        if (XStreamHelper.class.isAssignableFrom(xmlJsonHelper)) {
            return XStreamHelper.toXml(result);
        } else if (GsonHelper.class.isAssignableFrom(xmlJsonHelper)) {
            return GsonHelper.toJsonString(result);
        }
        return null;
    }
}
