/*******************************************************************************
 * Copyright (c) 七月 14 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common;

import com.foreveross.common.application.SystemApplication;
import com.foreveross.common.restfull.RestClientUtil;
import com.foreveross.common.restfull.UriManager;
import com.foreveross.extension.monitor.application.MonitorApplication;
import net.sf.ehcache.CacheManager;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.*;
import org.iff.infra.util.log.LogKafkaHelper;
import org.iff.infra.util.spring.SpringContextHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.*;
import java.util.Map.Entry;

/**
 * 项目初始化需要做的内容。
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 9, 2015
 * auto generate by qdp.
 */
public class ProjectInitializeBean
        implements InitializingBean, ApplicationListener<ContextRefreshedEvent>, BeanFactoryPostProcessor {

    private static boolean hasInit = false;
    private static boolean hasRefresh = false;

    /**
     * 【Bean未开始加载】在Bean开始初始化前就加载一些配置或进行预处理。
     * (non-Javadoc)
     *
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @see BeanFactoryPostProcessor#postProcessBeanFactory(ConfigurableListableBeanFactory)
     * @since Mar 20, 2018
     */
    public synchronized void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (!hasInit && (hasInit = true)) {
            {//使用POVOCopy，生成Groovy代码。
                BeanHelper.setUsePOVOCopyHelper(true);
            }
            {//加载I18N
                I18nHelper.loadDefualtMessages("classpath://META-INF/i18n");
            }
            {//加载系统的配置
                Map<String, String> map = PropertiesHelper
                        .loadPropertyFiles(new String[]{"classpath://META-INF/config"});
                ConstantBean.setProperties(map);
            }
            {//加载 restful 的配置
                Map<String, String> map = PropertiesHelper
                        .loadPropertyFiles(new String[]{"classpath://META-INF/restful"});
                UriManager.parseProperties(map);
            }
            {//加载系统属性
                Map<String, String> map = new LinkedHashMap<String, String>();
                Properties properties = System.getProperties();
                for (Entry<Object, Object> entry : properties.entrySet()) {
                    if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                        map.put((String) entry.getKey(), (String) entry.getValue());
                    }
                }
                ConstantBean.setProperties(map);
            }
        }
    }

    /**
     * 【Bean开始加载但未完成】在Bean开始初始化就进行预处理。
     * (non-Javadoc)
     *
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @see InitializingBean#afterPropertiesSet()
     * @since Mar 20, 2018
     */
    public void afterPropertiesSet() {

    }

    /**
     * 【Bean已经初始化完成】所有Bean加载完成后，会调用这个方法，也就是说这个方法执行时所有的Bean已经初始化完成了。
     * (non-Javadoc)
     *
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @see ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     * @since Mar 20, 2018
     */
    @SuppressWarnings("resource")
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!hasRefresh && (hasRefresh = true)) {
            {//启动 Kafka 日志
                String brokers = ConstantBean.getProperty("log.kafka.brokers");
                String topic = ConstantBean.getProperty("log.kafka.topic");
                if (StringUtils.isNotBlank(brokers) && StringUtils.isNotBlank(topic)) {
                    LogKafkaHelper.init(brokers, topic);
                    LogKafkaHelper.start();
                }
            }
            {//加载数据库I18N
                ((SystemApplication) SpringContextHelper.getBean("systemApplication")).initI18n();
            }
            {//设置默认的缓存
                EhcacheHelper.init((CacheManager) SpringContextHelper.getBean("cacheManager"));
                CacheHelper.init(new CacheHelper.EhCacheCacheable());
                //CacheHelper.init(new CacheHelper.DisabledCacheable());
            }
            {//加载 restful client 的配置
                Map<String, String> map = PropertiesHelper
                        .loadPropertyFiles(new String[]{"classpath://META-INF/restclient"});
                RestClientUtil.parseProperties(map);
            }
            {//初始化Shiro配置
                //((ShiroChainDefinitionManager) SpringContextHelper.getBean("shiroChainDefinitionManager"))
                //		.reCreateFilterChains();
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
                List<Class<?>> initSpringService = new ArrayList<Class<?>>();
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
                            initSpringService.add(loadClass);
                        } else {//如果是实现类，一般要拿到其接口
                            Class<?>[] interfaces = loadClass.getInterfaces();
                            initSpringService.addAll(Arrays.asList(interfaces));
                        }
                    } catch (Exception e) {
                    }
                }
                if (initSpringService.size() > 0) {
                    monitorApplication.initSpringServiceMap(initSpringService.toArray(new Class<?>[0]));
                }
            } catch (Exception e) {
            }
        }
    }
}
