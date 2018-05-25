/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.iff.infra.util.StringHelper;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

/**
 * 用于JWT验证
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 28, 2017
 */
public class JWTTokenHelper {

    private static final String SECRET = "FOSS-JWT";

    public static String encodeToken(String userPrincipal, long ttlMillis) {
        SignatureAlgorithm sigAlg = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        byte[] apiKeySecretBytes = SECRET.getBytes();
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, sigAlg.getJcaName());
        JwtBuilder builder = Jwts.builder().setId(StringHelper.uuid()).setIssuedAt(now).setIssuer("tylerchen-qdp")
                .setSubject(userPrincipal).signWith(sigAlg, signingKey);
        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    public static String encodeToken(String userPrincipal) {
        SignatureAlgorithm sigAlg = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        long ttlMillis = 5 * 60 * 1000;//过期时间5分钟
        Date now = new Date(nowMillis);
        byte[] apiKeySecretBytes = SECRET.getBytes();
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, sigAlg.getJcaName());
        JwtBuilder builder = Jwts.builder().setId(StringHelper.uuid()).setIssuedAt(now).setIssuer("tylerchen-qdp")
                .setSubject(userPrincipal).signWith(sigAlg, signingKey);
        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    public static String decodeToken(String token) {
        String userPrincipal = Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(token).getBody()
                .getSubject();
        return userPrincipal;
    }
}
