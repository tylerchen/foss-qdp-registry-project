/*******************************************************************************
 * Copyright (c) Aug 11, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.extension.monitor.application;

import java.util.List;
import java.util.Map;

/**
 * 初始化服务监控内容。
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 11, 2016
 */
public interface MonitorApplication {

	/**
	 * 添加Spring服务
	 * @param interfaces
	 * @return
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Aug 29, 2017
	 */
	void initSpringServiceMap(Class<?>[] interfaces);

	/**
	 * 返回Spring服务列表
	 * @return
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Sep 29, 2017
	 */
	Map<String, List<String>> findSpringService();
}
