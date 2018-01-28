/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.application.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.GsonHelper;
import org.iff.infra.util.Logger;

import com.foreveross.common.ConstantBean;
import com.foreveross.common.application.SystemApplication;
import com.foreveross.common.shiro.ShiroUser;

/**
 * 系统初始化及登录
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 28, 2017
 */
@Named("simpleSystemApplication")
public class SimpleSystemApplicationImpl implements SystemApplication {

	private Map<String, Map<?, ?>> accounts = new HashMap<String, Map<?, ?>>();

	private void loadAccount() {
		if (!accounts.isEmpty()) {
			return;
		}
		boolean enable = StringUtils.equalsIgnoreCase("true", ConstantBean.getProperty("auth.simpleauth.enable"));
		String json = ConstantBean.getProperty("auth.account.json");
		if (!enable) {
			Logger.warn("You MUST set authorization.properties auth.simpleauth.enable=true to enable simple auth.");
		} else {
			try {
				List<Map<?, ?>> account = GsonHelper.toJsonList(json);
				for (Map<?, ?> map : account) {
					accounts.put((String) map.get("loginId"), map);
				}
			} catch (Exception e) {
				Logger.warn("Format JSON auth.account.json error: " + json, e);
			}
		}
	}

	private Map<?, ?> getAccountById(String id) {
		for (Map<?, ?> map : accounts.values()) {
			if (StringUtils.equals((String) map.get("id"), id)) {
				return map;
			}
		}
		return null;
	}

	public int initI18n() {
		return 0;
	}

	public ShiroUser login(ShiroUser user) {
		if (user == null) {
			return null;
		}
		{
			loadAccount();
		}
		if (StringUtils.isNotBlank(user.getLoginId())) {
			ShiroUser user2 = getShiroUserByLoginId(user.getLoginId());
			if (user2 == null) {
				return null;
			}
			if (StringUtils.equals(user.getLoginPasswd(), user2.getLoginPasswd())) {
				return user2;
			}
		}
		if (StringUtils.isNotBlank(user.getId())) {
			Map<?, ?> map = getAccountById(user.getId());
			if (map == null) {
				return null;
			}
			ShiroUser user2 = copyToShiroUser(map);
			if (StringUtils.equals(user.getLoginPasswd(), user2.getLoginPasswd())) {
				return user2;
			}
		}
		return null;
	}

	public ShiroUser getShiroUserByLoginId(String loginId) {
		if (StringUtils.isBlank(loginId)) {
			return null;
		}
		{
			loadAccount();
		}
		Map<?, ?> map = accounts.get(loginId);
		if (map == null) {
			return null;
		}
		return copyToShiroUser(map);
	}

	ShiroUser copyToShiroUser(Map<?, ?> map) {
		ShiroUser user = new ShiroUser();
		user.setId((String) map.get("id"));
		user.setLoginId((String) map.get("loginId"));
		user.setLoginPasswd((String) map.get("loginPasswd"));
		{
			Object x = map.get("roles");
			if (x == null) {
				user.setRoles(new String[0]);
			}
			if (x instanceof List) {
				user.setRoles(((List<?>) x).toArray(new String[0]));
			}
			if (x.getClass().isArray()) {
				user.setRoles((String[]) x);
			}
		}
		{
			Object x = map.get("resources");
			if (x == null) {
				user.setResources(new String[0]);
			}
			if (x instanceof List) {
				user.setResources(((List<?>) x).toArray(new String[0]));
			}
			if (x.getClass().isArray()) {
				user.setResources((String[]) x);
			}
		}
		return user;
	}
}
