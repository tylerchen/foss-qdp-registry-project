/*******************************************************************************
 * Copyright (c) Dec 25, 2015 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import org.apache.shiro.authz.Permission;

import java.io.Serializable;

/**
 * 使用星号【*】匹配。
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Dec 25, 2015
 */
@SuppressWarnings("serial")
public class UrlWildcardPermission implements Permission, Serializable {

    /**
     * is has wildcard then split to cards value
     **/
    private boolean hasWildcard = false;
    /**
     * is permission is a valid string, not empty string
     **/
    private boolean valid = false;
    /**
     * <pre>
     * is starts with "*"
     * because "a/*".split("\\*") == "*a/".split("\\*");
     * so we need to handle this situation.
     * </pre>
     **/
    private boolean starStart = false;
    /**
     * then wild card split string
     **/
    private String[] cards = new String[0];
    /**
     * the permission string
     **/
    private String permission;

    public UrlWildcardPermission() {
    }

    public UrlWildcardPermission(String permission) {
        setPermission(permission);
    }

    public void checkPermission() {
        if (permission != null && permission.length() > 0) {
            valid = true;
            if (permission.indexOf('*') > -1) {
                starStart = permission.startsWith("*");
                hasWildcard = true;
                cards = permission.split("\\*");
            }
        }
    }

    public boolean implies(Permission p) {
        if (p != null && p instanceof UrlWildcardPermission) {
            UrlWildcardPermission uwp = (UrlWildcardPermission) p;
            if (uwp.isValid()) {
                if (hasWildcard) {
                    return wildCardMatch(uwp.getPermission());
                } else {
                    return permission.equals(uwp.getPermission());
                }
            }
        }
        return false;
    }

    public boolean isHasWildcard() {
        return hasWildcard;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isStarStart() {
        return starStart;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
        checkPermission();
    }

    /**
     * test the text match the wild char "*"
     *
     * @param text    example: helloworld
     * @param pattern example: hell*world
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since 2015-4-8
     */
    private boolean wildCardMatch(String text) {
        if (text == null || text.length() < 1) {
            return false;
        }
        for (int i = 0; i < cards.length; i++) {
            String card = cards[i];
            int idx = -1;
            if (i == 0 && starStart) {// if starts with "*" then test the indexOf.
                idx = text.indexOf(card);
            } else {// if not starts with "*", must match string start.
                idx = text.startsWith(card) ? 0 : -1;
            }
            if (idx == -1) {// not match
                return false;
            }
            text = text.substring(idx + card.length());
        }
        return true;
    }

    public String toString() {
        return valid ? permission : "";
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((permission == null) ? 0 : permission.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UrlWildcardPermission other = (UrlWildcardPermission) obj;
        if (permission == null) {
            if (other.permission != null) {
                return false;
            }
        } else if (!permission.equals(other.permission)) {
            return false;
        }
        return true;
    }

}
