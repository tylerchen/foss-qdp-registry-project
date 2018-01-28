/*******************************************************************************
 * Copyright (c) Aug 11, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.application;

import com.foreveross.common.shiro.ShiroUser;

/**
 * 系统初始化及登录
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 11, 2016
 */
public interface SystemApplication {

	/**
	 * @return
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Aug 29, 2017
	 */
	int initI18n();

	/**
	 * @param user
	 * @return
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Aug 29, 2017
	 */
	ShiroUser login(ShiroUser user);

	/**
	 * @param loginId
	 * @return
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Aug 29, 2017
	 */
	ShiroUser getShiroUserByLoginId(String loginId);

}
