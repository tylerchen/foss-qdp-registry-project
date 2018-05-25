/*******************************************************************************
 * Copyright (c) Nov 12, 2015 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.web;

import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.ActionHelper;
import org.iff.infra.util.StringHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <pre>
 * Use to redirect .html to freemarker view
 * @see /src/main/resources/META-INF/spring-web/spring-servlet.xml
 * </pre>
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Nov 12, 2015
 */
@Deprecated
//没有使用了，以前用于freemarker。
public class CustomUrlFilenameViewController extends BaseController implements Controller {

    // org.springframework.web.servlet.mvc.UrlFilenameViewController
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uri = StringUtils.removeStart(request.getRequestURI(), request.getContextPath());
        int hasDot = uri.lastIndexOf('.');
        if (hasDot > -1) {
            String viewName = uri.substring(0, hasDot);
            Map<String, Object> requestParameterToMap = ActionHelper.create(request, response).requestParameterToMap();
            String queryString = StringHelper.getNotNullValue(request.getQueryString());
            queryString = queryString.replaceAll("pageNo=([a-zA-Z0-9\\\u4e00-\\\u9fa5]+\\&?)", "");
            requestParameterToMap.put("requestQueryString", queryString);
            String requestUrl = StringHelper.concat(request.getRequestURI(),
                    StringUtils.isEmpty(queryString) ? "" : ("?" + queryString));
            requestParameterToMap.put("requestUrl", requestUrl);
            requestParameterToMap.put("requestURI", request.getRequestURI());
            return new ModelAndView(viewName, requestParameterToMap);
        }
        return null;
    }

}
