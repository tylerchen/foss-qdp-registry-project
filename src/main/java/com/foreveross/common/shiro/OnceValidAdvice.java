/*******************************************************************************
 * Copyright (c) Mar 21, 2018 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <pre>
 * 是否为OnceValidAdvice：boolean isOnceValidAdvice = Boolean.TRUE.equals(request.getAttribute(OnceValidAdvice.REQUEST_MARK));
 * 需要通过抛出异常来终止后续的验证。
 * 建议：在满足数据条件（数据已经存在）后，如果验证不通过，通过自行返回response信息及抛出异常来终止后续验证。
 * </pre>
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Mar 21, 2018
 */
public interface OnceValidAdvice {

    public static final String REQUEST_MARK = "OnceValidAdvice";

    boolean preHandle(ServletRequest request, ServletResponse response) throws Exception;
}
