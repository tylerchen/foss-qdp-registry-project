/*******************************************************************************
 * Copyright (c) 七月 14 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.extension.httpinvoker.web;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iff.infra.util.FCS;
import org.iff.infra.util.spring.SpringContextHelper;
import org.iff.infra.util.spring.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.common.web.BaseController;

/**
 * CommonWsController
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 9, 2015
 * auto generate by qdp.
 */
@Controller
@RequestMapping("/rpc/http")
public class HttpInvokerController extends BaseController {
	private static final org.iff.infra.util.Logger.Log Logger = org.iff.infra.util.Logger.get("HttpInvoker");
	/**
	 * using lock when register spring bean.
	 */
	private static final ReentrantLock lock = new ReentrantLock();

	/**
	 * invoke bean beanName by using http rpc invoker.
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Jul 19, 2016
	 */
	@RequestMapping(value = "/{beanName}", produces = "application/x-java-serialized-object;charset=UTF-8")
	@ResponseBody
	public void invoke(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("beanName") String beanName) throws IOException {

		/**
		 * 获得原始Bean。
		 */
		Object orginBean = null;
		try {
			orginBean = SpringContextHelper.getBean(beanName);
			if (orginBean == null) {
				throw new IOException(new NoSuchBeanDefinitionException(beanName,
						FCS.get("Bean not found by name [{0}] for rpc request!", beanName).toString()));
			}
		} catch (Exception e) {
			throw new IOException(new NoSuchBeanDefinitionException(beanName,
					FCS.get("Bean not found by name [{0}] for rpc request!", beanName).toString()));
		}

		/**
		 * rpcBeanName是动态注册的Bean名称，获得RPC Bean。
		 */
		String rpcBeanName = beanName + "_HttpInvokerServiceBean";
		Object httpInvokerBean = null;
		try {
			httpInvokerBean = SpringContextHelper.getBean(rpcBeanName);
		} catch (Exception e) {
		}
		/**
		 * 如果没有找到，获得RPC Bean，则使用Spring的机制动态注册一个。
		 */
		if (httpInvokerBean == null) {
			try {
				lock.lock();
				try {
					httpInvokerBean = SpringContextHelper.getBean(rpcBeanName);
				} catch (Exception e) {
				}
				if (httpInvokerBean == null) {
					Class<?>[] interfaces = orginBean.getClass().getInterfaces();
					//将applicationContext转换为ConfigurableApplicationContext  
					ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) SpringContextHelper
							.getApplicationContext();
					// 获取bean工厂并转换为DefaultListableBeanFactory  
					DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext
							.getBeanFactory();
					// 通过BeanDefinitionBuilder创建bean定义  
					BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
							.genericBeanDefinition(HttpInvokerServiceExporter.class);
					// 设置属性service,此属性引用已经定义的bean:rpcBeanName  
					beanDefinitionBuilder.addPropertyReference("service", beanName);
					// 设置属性serviceInterface,此属性引用已经定义的bean:rpcBeanName  
					beanDefinitionBuilder.addPropertyValue("serviceInterface", interfaces[0].getName());
					// 注册bean  
					defaultListableBeanFactory.registerBeanDefinition(rpcBeanName,
							beanDefinitionBuilder.getRawBeanDefinition());

					Logger.info(FCS.get("Register bean {rpcBeanName} success.", rpcBeanName));

					httpInvokerBean = SpringContextHelper.getBean(rpcBeanName);
				}
			} finally {
				lock.unlock();
			}
		}

		if (httpInvokerBean == null) {
			throw new IOException(new NoSuchBeanDefinitionException(beanName,
					FCS.get("RPC bean not found by name [{0}], maybe is not regist correctly, please check com.foreveross.extension.httpinvoker.web.HttpInvokerController.invoke(HttpServletRequest, HttpServletResponse, String)",
							beanName).toString()));
		}

		if (!(httpInvokerBean instanceof HttpRequestHandler)) {
			throw new IOException(
					FCS.get("RPC bean [{0}] is not instance of [{1}]", beanName, HttpRequestHandler.class.getName())
							.toString());
		}

		try {
			((HttpRequestHandler) httpInvokerBean).handleRequest(request, response);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
