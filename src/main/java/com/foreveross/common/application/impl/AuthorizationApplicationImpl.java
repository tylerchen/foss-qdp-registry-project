/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.application.impl;

import com.foreveross.common.ConstantBean;
import com.foreveross.common.application.AuthorizationApplication;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.mybatis.plugin.Page;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Set;

/**
 * 授权
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 28, 2017
 */
@Named("authorizationApplication")
public class AuthorizationApplicationImpl implements AuthorizationApplication {
    @Inject
    @Named("simpleAuthorizationApplication")
    AuthorizationApplication simpleAuthorizationApplication;
    @Inject
    @Named("defaultAuthorizationApplication")
    @Autowired(required = false)
    AuthorizationApplication defaultAuthorizationApplication;

    private Boolean enableSimple = null;

    public Boolean getEnableSimple() {
        if (enableSimple == null) {
            enableSimple = StringUtils.equalsIgnoreCase("true", ConstantBean.getProperty("auth.simpleauth.enable"));
        }
        return enableSimple;
    }

    public boolean isAdminByAccountId(String id) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.isAdminByAccountId(id);
        }
        return defaultAuthorizationApplication.isAdminByAccountId(id);
    }

    public boolean isAdminByLoginId(String loginId) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.isAdminByLoginId(loginId);
        }
        return defaultAuthorizationApplication.isAdminByLoginId(loginId);
    }

    public Page<?> pageFindAuthResourceByAccountIdMap(String id) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.pageFindAuthResourceByAccountIdMap(id);
        }
        return defaultAuthorizationApplication.pageFindAuthResourceByAccountIdMap(id);
    }

    public Page<?> pageFindAuthResourceByLoginIdMap(String loginId) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.pageFindAuthResourceByLoginIdMap(loginId);
        }
        return defaultAuthorizationApplication.pageFindAuthResourceByLoginIdMap(loginId);
    }

    public Set<String> findAuthResourceByAccountId(String id) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.findAuthResourceByAccountId(id);
        }
        return defaultAuthorizationApplication.findAuthResourceByAccountId(id);
    }

    public Set<String> findAuthResourceByLoginId(String loginId) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.findAuthResourceByLoginId(loginId);
        }
        return defaultAuthorizationApplication.findAuthResourceByLoginId(loginId);
    }

    public Page<?> pageFindAuthRoleByAccountIdMap(String id) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.pageFindAuthRoleByAccountIdMap(id);
        }
        return defaultAuthorizationApplication.pageFindAuthRoleByAccountIdMap(id);
    }

    public Page<?> pageFindAuthRoleByLoginIdMap(String loginId) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.pageFindAuthRoleByLoginIdMap(loginId);
        }
        return defaultAuthorizationApplication.pageFindAuthRoleByLoginIdMap(loginId);
    }

    public Set<String> findAuthRoleByAccountId(String id) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.findAuthRoleByAccountId(id);
        }
        return defaultAuthorizationApplication.findAuthRoleByAccountId(id);
    }

    public Set<String> findAuthRoleByLoginId(String loginId) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.findAuthRoleByLoginId(loginId);
        }
        return defaultAuthorizationApplication.findAuthRoleByLoginId(loginId);
    }

    public Page<?> pageFindAuthOrganizationByAccountIdMap(String id) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.pageFindAuthOrganizationByAccountIdMap(id);
        }
        return defaultAuthorizationApplication.pageFindAuthOrganizationByAccountIdMap(id);
    }

    public Page<?> pageFindAuthOrganizationByLoginIdMap(String loginId) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.pageFindAuthOrganizationByLoginIdMap(loginId);
        }
        return defaultAuthorizationApplication.pageFindAuthOrganizationByLoginIdMap(loginId);
    }

    public Page<?> pageFindAuthMenuByAccountIdMap(String id) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.pageFindAuthMenuByAccountIdMap(id);
        }
        return defaultAuthorizationApplication.pageFindAuthMenuByAccountIdMap(id);
    }

    public Page<?> pageFindAuthMenuByLoginIdMap(String loginId) {
        if (getEnableSimple()) {
            return simpleAuthorizationApplication.pageFindAuthMenuByLoginIdMap(loginId);
        }
        return defaultAuthorizationApplication.pageFindAuthMenuByLoginIdMap(loginId);
    }
}
