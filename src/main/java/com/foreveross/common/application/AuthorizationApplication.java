/*******************************************************************************
 * Copyright (c) Aug 11, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.application;

import org.iff.infra.util.mybatis.plugin.Page;

import java.util.Set;

/**
 * 授权
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 11, 2016
 */
public interface AuthorizationApplication {

    /**
     * 登录帐号ID，是否为管理员（角色代码：ADMIN）。
     *
     * @param id
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    boolean isAdminByAccountId(String id);

    /**
     * 登录帐号，是否为管理员（角色代码：ADMIN）。
     *
     * @param loginId
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    boolean isAdminByLoginId(String loginId);

    /**
     * 通过登录帐号ID获取资源。
     *
     * @param id
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    Page<?> pageFindAuthResourceByAccountIdMap(String id);

    /**
     * 通过登录帐号获取资源。
     *
     * @param loginId
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    Page<?> pageFindAuthResourceByLoginIdMap(String loginId);

    /**
     * 通过登录帐号ID获取资源列表，如果是管理员，返回“*”。
     *
     * @param id
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    Set<String> findAuthResourceByAccountId(String id);

    /**
     * 通过登录帐号获取资源列表，如果是管理员，返回“*”。
     *
     * @param loginId
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    Set<String> findAuthResourceByLoginId(String loginId);

    /**
     * 通过登录帐号ID获取角色。
     *
     * @param id
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    Page<?> pageFindAuthRoleByAccountIdMap(String id);

    /**
     * 通过登录帐号获取角色。
     *
     * @param loginId
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    Page<?> pageFindAuthRoleByLoginIdMap(String loginId);

    /**
     * 通过登录帐号ID获取角色列表，如果是管理员，返回“ADMIN”。
     *
     * @param id
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    Set<String> findAuthRoleByAccountId(String id);

    /**
     * 通过登录帐号获取角色列表，如果是管理员，返回“ADMIN”。
     *
     * @param loginId
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    Set<String> findAuthRoleByLoginId(String loginId);

    /**
     * 通过登录帐号ID获取组织。
     *
     * @param id
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    Page<?> pageFindAuthOrganizationByAccountIdMap(String id);

    /**
     * 通过登录帐号获取组织。
     *
     * @param loginId
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    Page<?> pageFindAuthOrganizationByLoginIdMap(String loginId);

    /**
     * <pre>
     * 通过登录帐号ID获取菜单。
     * 菜单来源：
     * 1、帐号 -> 角色 -> 菜单
     * 2、帐号 -> 用户 -> 组织 -> 角色 -> 菜单
     * 3、帐号 -> 用户 -> 组织 -> 菜单
     * 4、帐号 -> 用户 -> 组织 -> 根组织 -> 菜单
     * </pre>
     *
     * @param id
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    Page<?> pageFindAuthMenuByAccountIdMap(String id);

    /**
     * <pre>
     * 通过登录帐号获取菜单。
     * 菜单来源：
     * 1、帐号 -> 角色 -> 菜单
     * 2、帐号 -> 用户 -> 组织 -> 角色 -> 菜单
     * 3、帐号 -> 用户 -> 组织 -> 菜单
     * 4、帐号 -> 用户 -> 组织 -> 根组织 -> 菜单
     * </pre>
     *
     * @param loginId
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 9, 2017
     */
    Page<?> pageFindAuthMenuByLoginIdMap(String loginId);
}
