/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import com.foreveross.common.util.EncryptDecryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.NumberHelper;

import java.util.Date;

/**
 * 用于ZUUL的 Header 生成。
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 28, 2017
 */
public class ZuulTokenHelper {

    /**
     * 获得 User Name。
     *
     * @param dencoded
     * @return
     */
    public static String userName(String dencoded) {
        int index = StringUtils.indexOf(dencoded, ',');
        if (index < 0) {
            return null;
        }
        return dencoded.substring(0, index);
    }

    /**
     * 获得日期。
     *
     * @param dencoded
     * @return
     */
    public static Date date(String dencoded) {
        int index = StringUtils.indexOf(dencoded, ',');
        if (index < 0) {
            return null;
        }
        long time = NumberHelper.getLong(dencoded.substring(index + 1), 0);
        return time == 0 ? null : new Date(time);
    }

    /**
     * 获得标志mark，可以是 IP，或其他额外的信息。
     *
     * @param dencoded
     * @return
     */
    public static String mark(String dencoded) {
        int index = StringUtils.indexOf(dencoded, ',');
        int index2 = StringUtils.lastIndexOf(dencoded, ',');
        if (index < 0 && index == index2) {
            return null;
        }
        return dencoded.substring(index2 + 1);
    }

    /**
     * 验证 ZUUL 的 Header。
     *
     * @param encodeStr
     * @param userName
     * @param offsetMs
     * @return
     */
    public static boolean validate(String encodeStr, String userName, long offsetMs) {
        String decoded = EncryptDecryptUtil.deflate2Base62Encrypt(encodeStr);
        String decodeUserName = userName(decoded);
        Date decodeDate = date(decoded);
        if (decodeUserName != null && decodeDate != null && decodeUserName.equals(userName) && (System.currentTimeMillis() - decodeDate.getTime()) < offsetMs) {
            return true;
        }
        return false;
    }

    /**
     * 验证 ZUUL 的 Header。
     *
     * @param encodeStr
     * @param userName
     * @return
     */
    public static boolean validate(String encodeStr, String userName) {
        String decoded = EncryptDecryptUtil.deflate2Base62Decrypt(encodeStr);
        String decodeUserName = userName(decoded);
        if (decodeUserName != null && decodeUserName.equals(userName)) {
            return true;
        }
        return false;
    }
}
