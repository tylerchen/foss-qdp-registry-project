/*******************************************************************************
 * Copyright (c) Jan 22, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * Shiro登录后用户信息
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Jan 22, 2016
 */
@SuppressWarnings("serial")
public class ShiroUser implements Serializable {
    /**
     * 主键
     **/
    private String id;
    /**
     * 登录Id
     **/
    private String loginId;
    /**
     * 用户密码
     **/
    private String loginPasswd;
    /**
     * 状态
     **/
    private String status;
    /**
     * 类型
     **/
    private String type;
    /**
     * 用户
     **/
    private String userId;
    /**
     * 最后登录
     **/
    private Date lastLogin;
    /**
     * 尝试次数
     **/
    private Integer loginTryTimes;
    /**
     * 描述
     **/
    private String description;
    /**
     * 修改时间
     **/
    private Date updateTime;
    /**
     * 创建时间
     **/
    private Date createTime;
    /**
     * 角色
     **/
    private String[] roles;
    /**
     * 资源
     **/
    private String[] resources;
    /**
     * 用户名称
     **/
    private String userIdName;

    public ShiroUser() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginPasswd() {
        return loginPasswd;
    }

    public void setLoginPasswd(String loginPasswd) {
        this.loginPasswd = loginPasswd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getLoginTryTimes() {
        return loginTryTimes;
    }

    public void setLoginTryTimes(Integer loginTryTimes) {
        this.loginTryTimes = loginTryTimes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String[] getResources() {
        return resources;
    }

    public void setResources(String[] resources) {
        this.resources = resources;
    }

    public String getUserIdName() {
        return userIdName;
    }

    public void setUserIdName(String userIdName) {
        this.userIdName = userIdName;
    }

    @Override
    public String toString() {
        return "ShiroUser [id=" + id + ", loginId=" + loginId + ", loginPasswd=" + loginPasswd + ", status=" + status
                + ", type=" + type + ", userId=" + userId + ", lastLogin=" + lastLogin + ", loginTryTimes="
                + loginTryTimes + ", description=" + description + ", updateTime=" + updateTime + ", createTime="
                + createTime + ", roles=" + Arrays.toString(roles) + ", resources=" + Arrays.toString(resources)
                + ", userIdName=" + userIdName + ", getId()=" + getId() + ", getLoginId()=" + getLoginId()
                + ", getLoginPasswd()=" + getLoginPasswd() + ", getStatus()=" + getStatus() + ", getType()=" + getType()
                + ", getUserId()=" + getUserId() + ", getLastLogin()=" + getLastLogin() + ", getLoginTryTimes()="
                + getLoginTryTimes() + ", getDescription()=" + getDescription() + ", getUpdateTime()=" + getUpdateTime()
                + ", getCreateTime()=" + getCreateTime() + ", getRoles()=" + Arrays.toString(getRoles())
                + ", getResources()=" + Arrays.toString(getResources()) + ", getUserIdName()=" + getUserIdName()
                + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
                + "]";
    }
}
