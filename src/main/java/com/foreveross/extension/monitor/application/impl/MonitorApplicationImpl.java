/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.extension.monitor.application.impl;

import com.foreveross.extension.monitor.application.MonitorApplication;

import javax.inject.Named;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 监控
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 28, 2017
 */
@Named("monitorApplication")
public class MonitorApplicationImpl implements MonitorApplication {

    Map<String, List<String>> springServiceMap = new LinkedHashMap<String, List<String>>();

    public void initSpringServiceMap(Class<?>[] interfaces) {
        if (interfaces == null || interfaces.length < 1) {
            return;
        }
        for (Class<?> intf : interfaces) {//TODO extends interface
            String className = intf.getName();
            if (!className.endsWith("Application") || className.endsWith("RsApplication")) {
                continue;
            }
            Method[] methods = intf.getDeclaredMethods();
            List<String> list = springServiceMap.get(className);
            if (list == null) {
                list = new ArrayList<String>();
                springServiceMap.put(className, list);
            }
            for (Method m : methods) {
                StringBuilder name = new StringBuilder(128);
                {
                    name.append("public ").append(m.getReturnType().getSimpleName()).append(" ").append(m.getName())
                            .append("(");
                    for (Class<?> c : m.getParameterTypes()) {
                        name.append(c.getSimpleName()).append(",");
                    }
                    if (name.charAt(name.length() - 1) == ',') {
                        name.setCharAt(name.length() - 1, ')');
                    } else {
                        name.append(')');
                    }
                }
                String cleanName = name.toString();
                if (!list.contains(cleanName)) {
                    list.add(cleanName);
                }
            }
        }
    }

    public Map<String, List<String>> findSpringService() {
        return springServiceMap;
    }
}
