/*******************************************************************************
 * Copyright (c) Feb 2, 2018 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.restfull;

import com.foreveross.common.restfull.RestClient.LoadBalancerRoundRobin;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.Exceptions;
import org.iff.infra.util.Logger;
import org.iff.infra.util.spring.SpringContextHelper;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Feb 2, 2018
 */
public class RestClientUtil {

    /**
     * 解释rest client的配置。
     *
     * @param map
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Feb 22, 2018
     */
    public static void parseProperties(Map<String, String> props) {
        if (props == null) {
            return;
        }
        List<String> restList = new ArrayList<String>();
        Map<String, Map<String, String>> classRestConfMap = new LinkedHashMap<String, Map<String, String>>();
        Map<String, String> globalConfigMap = new LinkedHashMap<String, String>();
        {//先做第一次分类
            for (Entry<String, String> entry : props.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.endsWith(".restful.tc") && "true".equals(value)) {
                    restList.add(StringUtils.remove(key, ".restful.tc"));
                } else if (key.startsWith("rest.client.")) {
                    globalConfigMap.put(key, value);
                }
            }
        }
        {//第二次处理把同一个rest client配置放到一起，并且解释“*.restful.url”的值
            for (String restName : restList) {
                String restNameDot = restName + ".";
                Map<String, String> restConfMap = new LinkedHashMap<String, String>();
                for (Entry<String, String> entry : props.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (!key.startsWith(restNameDot)) {
                        continue;
                    }
                    restConfMap.put(key, value);
                }
                String restUrlKey = restName + ".restful.url";
                String restUrl = restConfMap.get(restUrlKey);
                if (restUrl.startsWith("{{") && restUrl.endsWith("}}")) {
                    restUrl = StringUtils.remove(StringUtils.remove(restUrl, "{{"), "}}");
                    restUrl = props.get(restUrl);
                    restConfMap.put(restUrlKey, restUrl);
                }
                classRestConfMap.put(restName, restConfMap);
            }
        }
        {//第三次处理，把解释完成的配置，注册成Spring Bean。
            for (Entry<String, Map<String, String>> entry : classRestConfMap.entrySet()) {
                String interfaceKey = entry.getKey() + ".interface";
                String interfaceName = entry.getValue().get(interfaceKey);
                String urlKey = entry.getKey() + ".restful.url";
                String url = entry.getValue().get(urlKey);
                String pathKey = entry.getKey() + ".restful.path";
                String path = StringUtils.defaultString(entry.getValue().get(pathKey));
                String restPrefixKey = entry.getKey() + ".interface.path";
                String restPrefix = entry.getValue().get(restPrefixKey);
                try {
                    Class<?> interfaceClass = Class.forName(interfaceName);
                    RestClientFactoryBean.matchRestUri(interfaceClass, entry.getValue());
                    Logger.info("Start to regist bean: " + interfaceName);
                    registerRestClient(interfaceClass, url, path, restPrefix, entry.getValue());
                } catch (ClassNotFoundException e) {
                    Exceptions.runtime("Can't load class by name: " + interfaceName, e);
                }
            }
        }
    }

    public static void registerRestClient(Class<?> interfaceClass, String url, String path, String restPrefix,
                                          Map<String, String> restClientConf) {
        //将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) SpringContextHelper
                .getApplicationContext();
        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext
                .getBeanFactory();
        // 通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(RestClientFactoryBean.class);
        String beanName = StringUtils.uncapitalize(interfaceClass.getSimpleName()) + "TCRestClient";
        // 属性引用
        // definition.addPropertyReference("service", alias);
        definition.addPropertyValue("type", interfaceClass);
        definition.addPropertyValue("name", beanName);
        definition.addPropertyValue("url", url);
        definition.addPropertyValue("path", path);
        definition.addPropertyValue("restPrefix", restPrefix);
        definition.addPropertyValue("restClientConf", restClientConf);
        definition.addPropertyValue("loadBalancer", new LoadBalancerRoundRobin(StringUtils.split(url, ",")));
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        definition.getBeanDefinition().setPrimary(true);
        // 注册bean
        defaultListableBeanFactory.registerBeanDefinition(beanName, definition.getRawBeanDefinition());
        Logger.info("Success to regist bean: " + interfaceClass.getName() + ", as " + beanName);
    }
}
