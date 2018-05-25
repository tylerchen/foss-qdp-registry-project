/*******************************************************************************
 * Copyright (c) Oct 15, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.util;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SpringMVC请求出现异常的拦截处理。
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Oct 15, 2017
 */
public class SimpleMappingExceptionResolver
        extends org.springframework.web.servlet.handler.SimpleMappingExceptionResolver {

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                              Exception ex) {
        if (ex != null) {
            ex.printStackTrace();
        }
        return super.doResolveException(request, response, handler, ex);
    }
}
