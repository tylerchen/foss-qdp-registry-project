/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import com.foreveross.common.application.AuthorizationApplication;
import com.foreveross.common.application.SystemApplication;

/**
 * 实现Realm：继承AuthorizingRealm，并重写认证授权方法
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 28, 2017
 */
@Named("monitorRealm")
public class MonitorRealm extends AuthorizingRealm {

	@Inject
	@Named("systemApplication")
	SystemApplication systemApplication;
	@Inject
	AuthorizationApplication authorizationApplication;

	public MonitorRealm() {
		super();
		setPermissionResolver(new UrlWildcardPermissionResolver());
	}

	/**
	 * 授权信息(拦截访问，进行授权)
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Object object = principals.fromRealm(getName()).iterator().next();
		Set<String> roleNames = authorizationApplication.findAuthRoleByLoginId(object.toString());
		Set<String> permissions = authorizationApplication.findAuthResourceByLoginId(object.toString());
		Set<Permission> objectPermissions = new HashSet<Permission>();

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
		info.setStringPermissions(permissions);
		info.setObjectPermissions(objectPermissions);
		return info;
	}

	/**
	 * 认证信息(登陆时)
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
			throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		ShiroUser user = systemApplication.getShiroUserByLoginId(token.getUsername());
		AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getLoginId(), user.getLoginPasswd(),
				getName());
		return authcInfo;
	}

	/** 
	 * 更新用户授权信息缓存. 
	 */
	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	/** 
	 * 清除所有用户授权信息缓存. 
	 */
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}
}
