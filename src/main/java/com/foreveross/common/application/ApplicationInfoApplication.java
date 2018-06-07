/*******************************************************************************
 * Copyright (c) 2018-06-03 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common.application;

import java.util.Map;

/**
 * ApplicationInfoApplication
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since 2018-06-03
 * auto generate by qdp.
 */
public interface ApplicationInfoApplication {

    /**
     * 返回应用的配置。
     *
     * @return
     */
    Map<String, String> applicationConfig();

    /**
     * 返回系统的属性。
     *
     * @return
     */
    Map<String, String> systemConfig();

    /**
     * <pre>
     *     返回 REST 配置。
     *     返回的数据格式为，层级已经展开为平坦的数据格式：
     *     {
     *         [GET|POST|PUT|DELETE]restPath: restId,
     *         [TYPE:restId]paramType: typeId,
     *         [ARG:typeId]prop: defaultValue
     *     }
     * </pre>
     *
     * @return
     */
    Map<String, String> restConfig();

    /**
     * 返回 SpringBoot 的配置。
     *
     * @return
     */
    Map<String, String> springBootConfig();
}
