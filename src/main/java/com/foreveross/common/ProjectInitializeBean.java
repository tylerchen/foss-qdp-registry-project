/*******************************************************************************
 * Copyright (c) 七月 14 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.BeanHelper;
import org.iff.infra.util.CacheHelper;
import org.iff.infra.util.EhcacheHelper;
import org.iff.infra.util.I18nHelper;
import org.iff.infra.util.PropertiesHelper;
import org.iff.infra.util.spring.SpringContextHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import com.foreveross.common.application.SystemApplication;
import com.foreveross.common.restfull.UriManager;
import com.foreveross.extension.monitor.application.MonitorApplication;

import net.sf.ehcache.CacheManager;

/**
 * 项目初始化需要做的内容。
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 9, 2015
 * auto generate by qdp.
 */
public class ProjectInitializeBean implements InitializingBean, ApplicationListener<ContextRefreshedEvent> {

	private static boolean hasInit = false;
	private static boolean hasRefresh = false;

	public synchronized void afterPropertiesSet() throws Exception {
		if (!hasInit) {
			{//使用POVOCopy，生成Groovy代码。
				BeanHelper.setUsePOVOCopyHelper(true);
			}
			{//加载I18N
				I18nHelper.loadDefualtMessages("classpath://META-INF/i18n");
			}
			{//加载系统的配置
				Map<String, String> map = PropertiesHelper
						.loadPropertyFiles(new String[] { "classpath://META-INF/config" });
				ConstantBean.setProperties(map);
			}
			{//加载系统的配置
				Map<String, String> map = PropertiesHelper
						.loadPropertyFiles(new String[] { "classpath://META-INF/restful" });
				UriManager.parseProperties(map);
			}
			{//加载系统属性
				Map<String, String> map = new HashMap<String, String>();
				Properties properties = System.getProperties();
				for (Entry<Object, Object> entry : properties.entrySet()) {
					if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
						map.put((String) entry.getKey(), (String) entry.getValue());
					}
				}
				ConstantBean.setProperties(map);
			}
			hasInit = true;
		}
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!hasRefresh) {
			{//加载数据库I18N
				((SystemApplication) SpringContextHelper.getBean("systemApplication")).initI18n();
			}
			{//设置默认的缓存
				EhcacheHelper.init((CacheManager) SpringContextHelper.getBean("cacheManager"));
				CacheHelper.init(new CacheHelper.EhCacheCacheable());
				//CacheHelper.init(new CacheHelper.DisabledCacheable());
			}
			try {
				MonitorApplication monitorApplication = SpringContextHelper.getBean(MonitorApplication.class);
				ApplicationContext applicationContext = event.getApplicationContext();
				Map<String, DefaultListableBeanFactory> beanNames = new HashMap<String, DefaultListableBeanFactory>();
				ConfigurableApplicationContext xml = (ConfigurableApplicationContext) applicationContext;
				DefaultListableBeanFactory bf = (DefaultListableBeanFactory) xml.getBeanFactory();
				while (bf != null) {
					String[] names = bf.getBeanDefinitionNames();
					for (String name : names) {
						if (name.endsWith("Application")) {
							beanNames.put(name, bf);
						}
					}
					bf = (DefaultListableBeanFactory) bf.getParentBeanFactory();
				}
				for (Entry<String, DefaultListableBeanFactory> entry : beanNames.entrySet()) {
					try {
						String beanName = entry.getKey();
						Class<?> loadClass = entry.getValue().getType(beanName);
						if (loadClass != null) {
							String className = null;
							className = (String) entry.getValue().getBeanDefinition(beanName).getPropertyValues()
									.get("interfaceName");
							if (StringUtils.isBlank(className)) {
								className = entry.getValue().getBeanDefinition(beanName).getBeanClassName();
							}
							loadClass = xml.getClassLoader().loadClass(className);
						}
						if (loadClass.isInterface()) {//如果只是代理类，一般就是接口
							monitorApplication.initSpringServiceMap(new Class<?>[] { loadClass });
						} else {//如果是实现类，一般要拿到其接口
							Class<?>[] interfaces = loadClass.getInterfaces();
							monitorApplication.initSpringServiceMap(interfaces);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
