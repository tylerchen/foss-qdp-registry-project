/*******************************************************************************
 * Copyright (c) Mar 21, 2018 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import com.foreveross.common.application.AuthorizationApplication;
import com.foreveross.common.application.SystemApplication;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Mar 21, 2018
 */
public class JWTRealm extends AuthorizingRealm {

    @Inject
    @Named("systemApplication")
    SystemApplication systemApplication;
    @Inject
    AuthorizationApplication authorizationApplication;

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Iterator<?> iterator = principals.fromRealm(getName()).iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        Object principal = iterator.next();
        String loginId = principal.toString();
        Set<String> roleNames = authorizationApplication.findAuthRoleByLoginId(loginId);
        Set<String> permissions = authorizationApplication.findAuthResourceByLoginId(loginId);
        Set<Permission> objectPermissions = new HashSet<Permission>();

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
        info.setStringPermissions(permissions);
        info.setObjectPermissions(objectPermissions);
        return info;
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JWTToken jwtToken = (JWTToken) token;
        String loginId = JWTTokenHelper.decodeToken((String) jwtToken.getPrincipal());
        ShiroUser user = systemApplication.getShiroUserByLoginId(loginId);
        jwtToken.setCredentials(user.getLoginPasswd());
        AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getLoginId(), jwtToken.getCredentials(),
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

    /**
     * 仅支持 JWTToken 验证。
     * (non-Javadoc)
     *
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @see org.apache.shiro.realm.AuthenticatingRealm#supports(AuthenticationToken)
     * @since Mar 21, 2018
     */
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

}
