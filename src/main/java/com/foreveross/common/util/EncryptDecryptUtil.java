/*******************************************************************************
 * Copyright (c) 2018-06-25 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common.util;

import com.foreveross.common.ConstantBean;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * EncryptDecryptUtil
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since 2018-06-25
 * auto generate by qdp.
 */
public class EncryptDecryptUtil {
    private static DeflateParameters deflateParameters = JsonHelper.toObject(DeflateParameters.class, "{\"zlibHeader\":false}");

    public static void main(String[] args) {
        System.out.println(deflate2Base62Encrypt("eureka@admin.com"));
    }

    /**
     * 编码: Deflate -> Base64 -> +MD5。
     *
     * @param userName
     * @return
     */
    public static String deflate2Base62Encrypt(String userName, String mark) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DeflateCompressorOutputStream os = new DeflateCompressorOutputStream(out, deflateParameters);
            String content = userName + "," + System.currentTimeMillis() + "," + mark;
            os.write(content.getBytes());
            os.finish();
            StreamHelper.closeWithoutError(os);
            String outStr = BaseCryptHelper.encodeBase62(out.toByteArray()).toString();
            return MD5Helper.string2MD5(outStr) + outStr;
        } catch (Exception e) {
            Logger.warn("Encrype-Decrypt deflate to base64 encrypt error!", e);
        }
        return null;
    }

    /**
     * 编码: Deflate -> Base64 -> +MD5。
     *
     * @param userName
     * @return
     */
    public static String deflate2Base62Encrypt(String userName) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DeflateCompressorOutputStream os = new DeflateCompressorOutputStream(out, deflateParameters);
            String content = userName + "," + System.currentTimeMillis();
            os.write(content.getBytes());
            os.finish();
            StreamHelper.closeWithoutError(os);
            String outStr = BaseCryptHelper.encodeBase62(out.toByteArray()).toString();
            return MD5Helper.string2MD5(outStr) + outStr;
        } catch (Exception e) {
            Logger.warn("Encrype-Decrypt deflate to base64 encrypt error!", e);
        }
        return null;
    }

    /**
     * 解码: -MD5 -> Base64 -> Deflate 。
     *
     * @param encodeStr
     * @return
     */
    public static String deflate2Base62Decrypt(String encodeStr) {
        String content = encodeStr;
        if (StringUtils.isEmpty(content) || content.length() < 32) {
            return null;
        }
        content = content.substring(32);
        if (!StringUtils.startsWith(encodeStr, MD5Helper.string2MD5(content))) {
            return null;
        }
        try {
            DeflateCompressorInputStream is = new DeflateCompressorInputStream(new ByteArrayInputStream(BaseCryptHelper.decodeBase62(content)), deflateParameters);
            content = StreamHelper.getContent(is, false);
            return content;
        } catch (Exception e) {
            Logger.warn("Encrype-Decrypt deflate to base64 decrypt error!", e);
        }
        return null;
    }

    /**
     * 解码: RSA。
     *
     * @param parameter
     * @return
     */
    public static String rsaDecrypt(String parameter) {
        try {
            if (!StringUtils.isBlank(parameter)) {
                return RSAHelper.decrypt(parameter,
                        RSAHelper.getPrivateKeyFromBase64(ConstantBean.getProperty("rsa.key.private.base64")));
            }
        } catch (Exception e) {
            Logger.warn("Encrype-Decrypt RSA decrypt error!", e);
        }
        return null;
    }

    /**
     * 编码: RSA。
     *
     * @param parameter
     * @return
     */
    public static String rsaEncrypt(String parameter) {
        try {
            if (!StringUtils.isBlank(parameter)) {
                return RSAHelper.encrypt(parameter,
                        RSAHelper.getPublicKeyFromBase64(ConstantBean.getProperty("rsa.key.private.base64")));
            }
        } catch (Exception e) {
            Logger.warn("Encrype-Decrypt RSA encrypt error!", e);
        }
        return null;
    }

    /**
     * 编码: firstSalt。
     *
     * @param parameter
     * @return
     */
    public static String firstSalt(String parameter) {
        try {
            if (!StringUtils.isBlank(parameter)) {
                return MD5Helper.firstSalt(parameter);
            }
        } catch (Exception e) {
            Logger.warn("Encrype-Decrypt firstSalt encrypt error!", e);
        }
        return null;
    }


    /**
     * 编码: secondSalt。
     *
     * @param parameter
     * @return
     */
    public static String secondSalt(String parameter) {
        try {
            if (!StringUtils.isBlank(parameter)) {
                return MD5Helper.secondSalt(MD5Helper.firstSalt(parameter));
            }
        } catch (Exception e) {
            Logger.warn("Encrype-Decrypt secondSalt encrypt error!", e);
        }
        return null;
    }
}
