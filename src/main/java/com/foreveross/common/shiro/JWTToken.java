/*******************************************************************************
 * Copyright (c) Mar 21, 2018 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Mar 21, 2018
 */
@SuppressWarnings("serial")
public class JWTToken implements AuthenticationToken {

    // 密钥
    private Object principal;

    private Object credentials;

    public JWTToken() {
    }

    public JWTToken(String principal) {
        this.principal = principal;
    }

    public Object getPrincipal() {
        return principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    public Object getCredentials() {
        return credentials;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }

}
