/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.application.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.GsonHelper;
import org.iff.infra.util.Logger;
import org.iff.infra.util.MapHelper;
import org.iff.infra.util.mybatis.plugin.Page;

import com.foreveross.common.ConstantBean;
import com.foreveross.common.application.AuthorizationApplication;
import com.foreveross.common.shiro.ShiroUser;

/**
 * 授权
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 28, 2017
 */
@Named("simpleAuthorizationApplication")
@SuppressWarnings("unchecked")
public class SimpleAuthorizationApplicationImpl implements AuthorizationApplication {

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

	public boolean isAdminByAccountId(String id) {
		if (StringUtils.isBlank(id)) {
			return false;
		}
		{
			loadAccount();
		}
		Map<?, ?> map = getAccountById(id);
		if (map == null) {
			return false;
		}
		Object x = map.get("roles");
		if (x == null) {
			return false;
		}
		if (x instanceof List) {
			return ((List<?>) x).contains("ADMIN");
		}
		if (x.getClass().isArray()) {
			return ArrayUtils.contains((Object[]) x, "ADMIN");
		}
		return false;
	}

	public boolean isAdminByLoginId(String loginId) {
		if (StringUtils.isBlank(loginId)) {
			return false;
		}
		{
			loadAccount();
		}
		Map<?, ?> map = accounts.get(loginId);
		if (map == null) {
			return false;
		}
		Object x = map.get("roles");
		if (x == null) {
			return false;
		}
		if (x instanceof List) {
			return ((List<?>) x).contains("ADMIN");
		}
		if (x.getClass().isArray()) {
			return ArrayUtils.contains((Object[]) x, "ADMIN");
		}
		return false;
	}

	public Page<?> pageFindAuthResourceByAccountIdMap(String id) {
		if (StringUtils.isBlank(id)) {
			return Page.notNullPage(null);
		}
		{
			loadAccount();
		}
		if (isAdminByAccountId(id)) {
			return Page.offsetPage(0, 10000, Arrays.asList(MapHelper.toMap("code", "*")));
		}
		Map<?, ?> map = getAccountById(id);
		if (map == null) {
			return Page.notNullPage(null);
		}
		Object x = map.get("resources");
		if (x == null) {
			return Page.notNullPage(null);
		}
		List<Map<?, ?>> resources = new ArrayList<Map<?, ?>>();
		if (x instanceof List) {
			for (Object o : (List<?>) x) {
				resources.add(MapHelper.toMap("code", o));
			}
		} else if (x.getClass().isArray()) {
			for (Object o : (Object[]) x) {
				resources.add(MapHelper.toMap("code", o));
			}
		}
		return Page.offsetPage(0, resources.size(), resources);
	}

	public Page<?> pageFindAuthResourceByLoginIdMap(String loginId) {
		if (StringUtils.isBlank(loginId)) {
			return Page.notNullPage(null);
		}
		{
			loadAccount();
		}
		if (isAdminByLoginId(loginId)) {
			return Page.offsetPage(0, 10000, Arrays.asList(MapHelper.toMap("code", "*")));
		}
		Map<?, ?> map = accounts.get(loginId);
		if (map == null) {
			return Page.notNullPage(null);
		}
		Object x = map.get("resources");
		if (x == null) {
			return Page.notNullPage(null);
		}
		List<Map<?, ?>> resources = new ArrayList<Map<?, ?>>();
		if (x instanceof List) {
			for (Object o : (List<?>) x) {
				resources.add(MapHelper.toMap("code", o));
			}
		} else if (x.getClass().isArray()) {
			for (Object o : (Object[]) x) {
				resources.add(MapHelper.toMap("code", o));
			}
		}
		return Page.offsetPage(0, resources.size(), resources);
	}

	public Set<String> findAuthResourceByAccountId(String id) {
		Page<?> page = pageFindAuthResourceByAccountIdMap(id);
		Set<String> value = new HashSet<String>();
		for (Map<String, Object> resource : (List<Map<String, Object>>) page.getRows()) {
			value.add((String) resource.get("code"));
		}
		return value;
	}

	public Set<String> findAuthResourceByLoginId(String loginId) {
		Page<?> page = pageFindAuthResourceByLoginIdMap(loginId);
		Set<String> value = new HashSet<String>();
		for (Map<String, Object> resource : (List<Map<String, Object>>) page.getRows()) {
			value.add((String) resource.get("code"));
		}
		return value;
	}

	public Page<?> pageFindAuthRoleByAccountIdMap(String id) {
		if (StringUtils.isBlank(id)) {
			return Page.notNullPage(null);
		}
		{
			loadAccount();
		}
		Map<?, ?> map = getAccountById(id);
		if (map == null) {
			return Page.notNullPage(null);
		}
		Object x = map.get("roles");
		if (x == null) {
			return Page.notNullPage(null);
		}
		List<Map<?, ?>> roles = new ArrayList<Map<?, ?>>();
		if (x instanceof List) {
			for (Object o : (List<?>) x) {
				roles.add(MapHelper.toMap("id", o, "name", o, "code", o));
			}
		} else if (x.getClass().isArray()) {
			for (Object o : (Object[]) x) {
				roles.add(MapHelper.toMap("id", o, "name", o, "code", o));
			}
		}
		return Page.offsetPage(0, roles.size(), roles);
	}

	public Page<?> pageFindAuthRoleByLoginIdMap(String loginId) {
		if (StringUtils.isBlank(loginId)) {
			return Page.notNullPage(null);
		}
		{
			loadAccount();
		}
		Map<?, ?> map = accounts.get(loginId);
		if (map == null) {
			return Page.notNullPage(null);
		}
		Object x = map.get("roles");
		if (x == null) {
			return Page.notNullPage(null);
		}
		List<Map<?, ?>> roles = new ArrayList<Map<?, ?>>();
		if (x instanceof List) {
			for (Object o : (List<?>) x) {
				roles.add(MapHelper.toMap("id", o, "name", o, "code", o));
			}
		} else if (x.getClass().isArray()) {
			for (Object o : (Object[]) x) {
				roles.add(MapHelper.toMap("id", o, "name", o, "code", o));
			}
		}
		return Page.offsetPage(0, roles.size(), roles);
	}

	public Set<String> findAuthRoleByAccountId(String id) {
		Page<?> page = pageFindAuthRoleByAccountIdMap(id);
		Set<String> value = new HashSet<String>();
		for (Map<String, Object> resource : (List<Map<String, Object>>) page.getRows()) {
			value.add((String) resource.get("code"));
		}
		return value;
	}

	public Set<String> findAuthRoleByLoginId(String loginId) {
		Page<?> page = pageFindAuthRoleByLoginIdMap(loginId);
		Set<String> value = new HashSet<String>();
		for (Map<String, Object> resource : (List<Map<String, Object>>) page.getRows()) {
			value.add((String) resource.get("code"));
		}
		return value;
	}

	public Page<?> pageFindAuthOrganizationByAccountIdMap(String id) {
		if (StringUtils.isBlank(id)) {
			return Page.notNullPage(null);
		}
		{
			loadAccount();
		}
		Map<?, ?> map = getAccountById(id);
		if (map == null) {
			return Page.notNullPage(null);
		}
		Object x = map.get("organizations");
		if (x == null) {
			return Page.notNullPage(null);
		}
		List<Map<?, ?>> organizations = new ArrayList<Map<?, ?>>();
		if (x instanceof List) {
			for (Object o : (List<?>) x) {
				organizations.add(MapHelper.toMap("id", o, "name", o, "code", o));
			}
		} else if (x.getClass().isArray()) {
			for (Object o : (Object[]) x) {
				organizations.add(MapHelper.toMap("id", o, "name", o, "code", o));
			}
		}
		return Page.offsetPage(0, organizations.size(), organizations);
	}

	public Page<?> pageFindAuthOrganizationByLoginIdMap(String loginId) {
		if (StringUtils.isBlank(loginId)) {
			return Page.notNullPage(null);
		}
		{
			loadAccount();
		}
		Map<?, ?> map = accounts.get(loginId);
		if (map == null) {
			return Page.notNullPage(null);
		}
		Object x = map.get("organizations");
		if (x == null) {
			return Page.notNullPage(null);
		}
		List<Map<?, ?>> organizations = new ArrayList<Map<?, ?>>();
		if (x instanceof List) {
			for (Object o : (List<?>) x) {
				organizations.add(MapHelper.toMap("id", o, "name", o, "code", o));
			}
		} else if (x.getClass().isArray()) {
			for (Object o : (Object[]) x) {
				organizations.add(MapHelper.toMap("id", o, "name", o, "code", o));
			}
		}
		return Page.offsetPage(0, organizations.size(), organizations);
	}

	public Page<?> pageFindAuthMenuByAccountIdMap(String id) {
		if (StringUtils.isBlank(id)) {
			return Page.notNullPage(null);
		}
		{
			loadAccount();
		}
		Map<?, ?> map = getAccountById(id);
		if (map == null) {
			return Page.notNullPage(null);
		}
		Object x = map.get("menus");
		if (x == null) {
			return Page.notNullPage(null);
		}
		List<Map<?, ?>> menus = new ArrayList<Map<?, ?>>();
		if (x instanceof List) {
			for (Object o : (List<?>) x) {
				menus.add((Map<?, ?>) o);
			}
		} else if (x.getClass().isArray()) {
			for (Object o : (Object[]) x) {
				menus.add((Map<?, ?>) o);
			}
		}
		return Page.offsetPage(0, menus.size(), menus);
	}

	public Page<?> pageFindAuthMenuByLoginIdMap(String loginId) {
		if (StringUtils.isBlank(loginId)) {
			return Page.notNullPage(null);
		}
		{
			loadAccount();
		}
		Map<?, ?> map = accounts.get(loginId);
		if (map == null) {
			return Page.notNullPage(null);
		}
		Object x = map.get("menus");
		if (x == null) {
			return Page.notNullPage(null);
		}
		List<Map<?, ?>> menus = new ArrayList<Map<?, ?>>();
		if (x instanceof List) {
			for (Object o : (List<?>) x) {
				menus.add((Map<?, ?>) o);
			}
		} else if (x.getClass().isArray()) {
			for (Object o : (Object[]) x) {
				menus.add((Map<?, ?>) o);
			}
		}
		return Page.offsetPage(0, menus.size(), menus);
	}

}
