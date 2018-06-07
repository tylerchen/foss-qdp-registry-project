/*******************************************************************************
 * Copyright (c) 2018-06-03 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common.application.impl;

import com.foreveross.common.application.ApplicationInfoApplication;
import com.foreveross.common.restfull.UriManager;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.IdHelper;
import org.iff.infra.util.PropertiesHelper;
import org.iff.infra.util.TypeConvertHelper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import javax.inject.Named;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * ApplicationInfoApplication
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since 2018-06-03
 * auto generate by qdp.
 */
@Named("applicationInfoApplication")
public class ApplicationInfoApplicationImpl implements ApplicationInfoApplication, ApplicationContextAware {

    private ApplicationContext applicationContext = null;

    private Map<String, String> applicationConfigs = new TreeMap<String, String>();
    private Map<String, String> systemConfigs = new TreeMap<String, String>();
    /**
     * restConfigs={
     * [GET|POST|PUT|DELETE]restPath: restId,
     * [ARG:restId]prop-type: defaultValue
     * }
     */
    private Map<String, String> restConfigs = new TreeMap<String, String>();
    private Map<String, String> springBootConfigs = new TreeMap<String, String>();

    /**
     * 返回应用的配置。
     *
     * @return
     */
    public Map<String, String> applicationConfig() {
        if (applicationConfigs.isEmpty()) {
            //加载系统的配置
            synchronized (applicationConfigs) {
                Map<String, String> map = PropertiesHelper
                        .loadPropertyFiles(new String[]{"classpath://META-INF/config"});
                applicationConfigs.putAll(map);
            }
        }
        return new TreeMap<String, String>(applicationConfigs);
    }

    /**
     * 返回系统的属性。
     *
     * @return
     */
    public Map<String, String> systemConfig() {
        if (systemConfigs.isEmpty()) {
            //加载系统属性
            synchronized (systemConfigs) {
                Properties properties = System.getProperties();
                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                        systemConfigs.put((String) entry.getKey(), (String) entry.getValue());
                    }
                }
            }
        }
        return new TreeMap<String, String>(systemConfigs);
    }

    /**
     * <pre>
     *     返回 REST 配置。
     *     返回的数据格式为，层级已经展开为平坦的数据格式：
     *     {
     *         [GET|POST|PUT|DELETE]restPath: restId,
     *         [ARG:restId]prop-type: defaultValue
     *     }
     * </pre>
     *
     * @return
     */
    public Map<String, String> restConfig() {
        //restConfigs
        //[GET|POST|PUT|DELETE]restPath:restId
        //[ARG:restId]prop-type:defaultValue
        if (restConfigs.isEmpty()) {
            Map<String, Map<String, UriManager.UriTemplate>> uriTemplates = UriManager.getUriTemplates();
            for (Map.Entry<String, Map<String, UriManager.UriTemplate>> parent : uriTemplates.entrySet()) {
                String parentKey = parent.getKey();
                Map<String, UriManager.UriTemplate> parentValue = parent.getValue();
                for (Map.Entry<String, UriManager.UriTemplate> entry : parentValue.entrySet()) {
                    String key = entry.getKey();
                    UriManager.UriTemplate value = entry.getValue();
                    Multimap<String, Map<String, String>> methodParams = value.getMethodParams();
                    UriManager.MyMethod method = value.getMethod();
                    String nullChar = value.getNullChar();
                    String classPath = value.getClassPath();
                    String methodMethod = value.getMethodMethod();
                    String methodPath = value.getMethodPath();
                    //====
                    String restPath = "/" + classPath + "/" + methodPath;
                    {
                        restPath = StringUtils.replace(restPath, "//", "/");
                        restPath = StringUtils.replace(restPath, "//", "/");
                    }
                    String restId = IdHelper.uuid();
                    restConfigs.put("[" + methodMethod.toUpperCase() + "]" + restPath, restId);
                    for (Map.Entry<String, Map<String, String>> mpEntry : methodParams.entries()) {
                        String paramType = mpEntry.getKey();
                        descriptParamClass(paramType, mpEntry.getValue(), StringUtils.contains("POST|PUT", methodMethod.toUpperCase()), restConfigs, restId);
                    }
                }
            }
        }
        return new TreeMap<String, String>(restConfigs);
    }

    /**
     * 返回 SpringBoot 的配置。
     *
     * @return
     */
    public Map<String, String> springBootConfig() {
        if (springBootConfigs.isEmpty()) {
            Environment environment = applicationContext.getEnvironment();
            for (Iterator it = ((AbstractEnvironment) environment).getPropertySources().iterator(); it.hasNext(); ) {
                PropertySource propertySource = (PropertySource) it.next();
                if (propertySource instanceof MapPropertySource) {
                    for (Map.Entry<String, Object> entry : (((MapPropertySource) propertySource).getSource()).entrySet()) {
                        springBootConfigs.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString());
                    }
                }
            }
        }
        return new TreeMap<String, String>(springBootConfigs);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void descriptParamClass(String paramType, Map<String, String> propValue, boolean isPostOrPut, Map<String, String> config, String restId) {
        try {
            Class<?> aClass = Class.forName(paramType);
            TypeConvertHelper.TypeConvert convert = TypeConvertHelper.me().get(paramType);
            if (!(convert == null || "null".equals(convert.getName()))) {
                if (propValue.size() > 0) {
                    String packTypeName = aClass.isArray() ? (aClass.getComponentType().getSimpleName() + "[]") : aClass.getSimpleName();
                    String key = propValue.keySet().iterator().next();
                    String defaultValue = propValue.values().iterator().next();
                    config.put("[ARG:" + restId + "]" + key + "-" + packTypeName, defaultValue == null ? "--NULL--" : defaultValue);
                }
                return;
            }
            BeanInfo beanInfo = Introspector.getBeanInfo(aClass);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                if ((!isPostOrPut && !propValue.containsKey(pd.getName())) || pd.getWriteMethod() == null) {
                    continue;
                }
                String prop = pd.getName();
                Class<?> propertyType = pd.getPropertyType();
                String packTypeName = propertyType.isArray() ? (propertyType.getComponentType().getSimpleName() + "[]") : propertyType.getSimpleName();
                config.put("[ARG:" + restId + "]" + prop + "-" + packTypeName, "--NULL--");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
