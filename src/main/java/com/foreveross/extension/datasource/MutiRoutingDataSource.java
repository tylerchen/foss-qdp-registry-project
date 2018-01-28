/*******************************************************************************
 * Copyright (c) Sep 10, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.extension.datasource;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * 多数据源路由
 * 	1、多数据源配置
 * 	配置第2个数据源
 * 	&lt;bean id="dataSource2"
 * 		class="org.springframework.jdbc.datasource.DriverManagerDataSource"&gt;
 * 		&lt;property name="driverClassName" value="com.mysql.jdbc.Driver" /&gt;
 * 		&lt;property name="url"
 * 			value="jdbc:mysql://localhost:3306/foodmart?useUnicode=true&amp;characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull&amp;useSSL=false" /&gt;
 * 		&lt;property name="username" value="iff" /&gt;
 * 		&lt;property name="password" value="iff" /&gt;
 * 	&lt;/bean&gt;
 * 	配置第2个事务管理器
 * 	&lt;bean id="transactionManager2"
 * 		class="org.springframework.jdbc.datasource.DataSourceTransactionManager"&gt;
 * 		&lt;property name="dataSource" ref="routingDataSource" /&gt;
 * 	&lt;/bean&gt;
 * 
 * 		配置动态数据源路由，根据事务管理器的名称来决定使用哪个数据源，事务名称及数据源对应下面配置，默认为：transactionManager（必须），
 * 		把其他数据源的引用都改为动态数据源，如：&lt;property name="dataSource" ref="routingDataSource" /&gt;
 * 		在使用非默认数据源需要在Application实现中配置事务管理器名称，如：
 * 		@Named("functionApplication")
 * 		@Transactional("transactionManager2")
 * 		public class FunctionApplicationImpl implements FunctionApplication { 
 * 
 * 	&lt;bean id="routingDataSource" class="com.foreveross.common.MutiRoutingDataSource"&gt;
 * 		&lt;property name="targetDataSources"&gt;
 * 			&lt;map key-type="java.lang.String"&gt;
 * 				&lt;entry key="transactionManager" value-ref="dataSource" /&gt;
 * 				&lt;entry key="transactionManager2" value-ref="dataSource2" /&gt;
 * 			&lt;/map&gt;
 * 		&lt;/property&gt;
 * 	&lt;/bean&gt;
 * </pre>
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Sep 10, 2016
 */
@Aspect
@Order(0)
public class MutiRoutingDataSource extends AbstractRoutingDataSource {

	private static final ThreadLocal<String> transactionName = new ThreadLocal<String>();

	@Pointcut("@within(org.springframework.transaction.annotation.Transactional)")
	public void getTransactoinNamePointcut() {
	}

	@Before("getTransactoinNamePointcut()")
	public void before(JoinPoint jp) {
		try {
			MethodInvocationProceedingJoinPoint mipj = (MethodInvocationProceedingJoinPoint) jp;
			Object target = mipj.getTarget();
			MethodSignature ms = (MethodSignature) mipj.getSignature();
			Method method = ms.getMethod();
			Transactional annotation = AnnotationUtils.findAnnotation(method, Transactional.class);
			if (annotation != null) {
				transactionName.set(annotation.value());
			} else {
				annotation = AnnotationUtils.findAnnotation(target.getClass(), Transactional.class);
				if (annotation != null) {
					transactionName.set(annotation.value());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Object determineCurrentLookupKey() {
		String str = transactionName.get();
		return StringUtils.defaultIfBlank(str, "transactionManager");
	}

	@Override
	public Connection getConnection() throws SQLException {
		return determineTargetDataSource().getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return determineTargetDataSource().getConnection(username, password);
	}
}
