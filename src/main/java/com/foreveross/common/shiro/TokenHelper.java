/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.iff.infra.util.StringHelper;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 用于JWT验证
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 28, 2017
 */
@Deprecated
//未实现
public class TokenHelper {

	private static final String SECRET = "XX#$%()(#*!()!KL<><MQLMNQNQJQK qdp>?N<:{LWPW";

	public static String encodeToken(String userPrincipal, long ttlMillis) {
		SignatureAlgorithm sigAlg = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET);
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
		long ttlMillis = 30 * 60 * 100;//过期时间30分钟
		Date now = new Date(nowMillis);
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET);
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
		String userPrincipal = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
				.parseClaimsJws(token).getBody().getSubject();
		return userPrincipal;
	}

}
