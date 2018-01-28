/*******************************************************************************
 * Copyright (c) 2017-10-11 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.extension.log.application;

/**
 * LogAccess Application.
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since 2017-10-11
 * @version 1.0.0
 * auto generate by qdp v3.0.
 */
public interface LogApplication {

	/**
	 * 保留最近30天的访问日志。
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Oct 17, 2017
	 */
	void remove30DaysBeforeAccessLogs();

	/**
	 * 保留最近60天的访问日志。
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Oct 17, 2017
	 */
	void remove60DaysBeforeAccessLogs();

	/**
	 * 保留最近90天的访问日志。
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Oct 17, 2017
	 */
	void remove90DaysBeforeAccessLogs();

	/**
	 * 指定保留多少天的访问日志。
	 * @param days
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Oct 17, 2017
	 */
	void removeDaysBeforeAccessLogs(int days);

	/**
	 * 保留最近30天的操作日志。
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Oct 17, 2017
	 */
	void remove30DaysBeforeOperationLogs();

	/**
	 * 保留最近60天的操作日志。
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Oct 17, 2017
	 */
	void remove60DaysBeforeOperationLogs();

	/**
	 * 保留最近90天的操作日志。
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Oct 17, 2017
	 */
	void remove90DaysBeforeOperationLogs();

	/**
	 * 指定保留多少天的操作日志。
	 * @param days
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Oct 17, 2017
	 */
	void removeDaysBeforeOperationLogs(int days);
}
