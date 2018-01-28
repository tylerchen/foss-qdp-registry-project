/*******************************************************************************
 * Copyright (c) Jan 28, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common;

import java.lang.reflect.Method;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.iff.infra.util.FCS;
import org.iff.infra.util.Logger;
import org.iff.infra.util.ThreadLocalHelper;

import com.foreveross.extension.log.LogHelper;

/**
 * Spring Bean调用拦截日志。
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Jan 28, 2016
 */
@Aspect
public class LoggerBean {
	private static final Logger.Log logger = Logger.get("TRACE");

	@Pointcut("@within(org.springframework.transaction.annotation.Transactional) || @within(javax.inject.Named)")
	public void logPointcut() {
	}

	@Around("logPointcut()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		long start = System.currentTimeMillis();
		MethodSignature ms = pjp.getSignature() instanceof MethodSignature ? (MethodSignature) pjp.getSignature()
				: null;
		String shortString = pjp.toShortString();
		if (ms != null) {
			Method method = ms.getMethod();
			logger.debug(FCS.get("Enter method {0}.{1}", method.getDeclaringClass().getName(), method.getName()));
		} else {
			logger.debug(FCS.get("Enter method {0}", shortString));
		}
		Throwable ex = null;
		try {
			return pjp.proceed();
		} catch (Throwable t) {
			ex = t;
			throw t;
		} finally {
			long end = System.currentTimeMillis();
			if (ms != null) {
				Method method = ms.getMethod();
				String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
				logger.debug(FCS.get("Enter method {0}", methodName));
				LogHelper.operationLog((String) ThreadLocalHelper.get("LoginEmail"), methodName, new Date(start),
						new Date(end), Long.valueOf(end - start).intValue(), "METHOD", new Date(),
						ex == null ? "OK" : ex.getMessage());
			} else {
				logger.debug(FCS.get("Exit method {0}, spend time {1}ms.", shortString,
						(System.currentTimeMillis() - start)));
				LogHelper.operationLog((String) ThreadLocalHelper.get("LoginEmail"), shortString, new Date(start),
						new Date(end), Long.valueOf(end - start).intValue(), "METHOD", new Date(),
						ex == null ? "OK" : ex.getMessage());
			}
		}
	}
}
