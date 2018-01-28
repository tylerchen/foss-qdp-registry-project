/*******************************************************************************
 * Copyright (c) Dec 25, 2015 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;

/**
 * 配置UrlWildcardPermission为默认的配置方式。
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Dec 25, 2015
 */
public class UrlWildcardPermissionResolver implements PermissionResolver {

	public Permission resolvePermission(String permissionString) {
		return new UrlWildcardPermission(permissionString);
	}

}
