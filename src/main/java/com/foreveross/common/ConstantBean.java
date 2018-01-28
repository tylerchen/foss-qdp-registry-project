/*******************************************************************************
 * Copyright (c) 七月 14 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common;

import java.util.HashMap;
import java.util.Map;

import org.iff.infra.util.Logger;

/**
 * 常量定义，将系统所有常量定义在此
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 9, 2015
 * auto generate by qdp.
 */
public final class ConstantBean {

	/**
	 * 返回結果
	 */
	public final static String SUCCESS = "0";//成功
	public final static String ERROR = "1";//删除失败
	public final static String SYSERR = "-1";//系统错误

	public final static String SYS_DEF = "SYS_DEF";//默认”类型1“系统参数
	public static final String SYS_ISVALIDATE = "SYS_ISVALIDATE";//“是否有效”
	public final static String SYS_AUTHER_ROLE_ACCOUNT = "ACCOUNT";//账号角色
	public final static String SYS_AUTHER_ROLE_ORGANIZATION = "ORGANIZATION";//组织机构角色

	public final static String ROOT_MENUE = "0";//顶级菜单父节点

	/**
	 * system properties.
	 */
	public final static Map<String, String> systemProperties = new HashMap<String, String>();

	/**
	 * setting system properties.
	 * @param properties
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Apr 28, 2016
	 */
	public static void setProperties(Map<String, String> properties) {
		if (properties != null && !properties.isEmpty()) {
			systemProperties.putAll(properties);
		}
		Logger.info("System properties: ConstantBean.props contains elements count: " + systemProperties.size());
	}

	/**
	 * get system properties
	 * @param key
	 * @return
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Apr 28, 2016
	 */
	public static String getProperty(String key) {
		return getProperty(key, null);
	}

	/**
	 * get system property by key, if the value is null, will return the default value.
	 * @param key
	 * @param defaultValue
	 * @return
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Apr 28, 2016
	 */
	public static String getProperty(String key, String defaultValue) {
		String value = systemProperties.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * clean all system property.
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Apr 28, 2016
	 */
	public static void cleanAllProperties() {
		systemProperties.clear();
	}
}
