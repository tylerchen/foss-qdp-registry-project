/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.application.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.foreveross.common.ConstantBean;
import com.foreveross.common.application.SystemApplication;
import com.foreveross.common.shiro.ShiroUser;

/**
 * 系统初始化及登录
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 28, 2017
 */
@Named("systemApplication")
public class SystemApplicationImpl implements SystemApplication {

	@Inject
	@Named("simpleSystemApplication")
	SystemApplication simpleSystemApplication;
	@Inject
	@Named("defaultSystemApplication")
	@Autowired(required = false)
	SystemApplication defaultSystemApplication;

	private Boolean enableSimple = null;

	public Boolean getEnableSimple() {
		if (enableSimple == null) {
			enableSimple = StringUtils.equalsIgnoreCase("true", ConstantBean.getProperty("auth.simpleauth.enable"));
		}
		return enableSimple;
	}

	public int initI18n() {
		if (getEnableSimple()) {
			return simpleSystemApplication.initI18n();
		}
		return defaultSystemApplication.initI18n();
	}

	public ShiroUser login(ShiroUser user) {
		if (getEnableSimple()) {
			return simpleSystemApplication.login(user);
		}
		return defaultSystemApplication.login(user);
	}

	public ShiroUser getShiroUserByLoginId(String loginId) {
		if (getEnableSimple()) {
			return simpleSystemApplication.getShiroUserByLoginId(loginId);
		}
		return defaultSystemApplication.getShiroUserByLoginId(loginId);
	}

}
