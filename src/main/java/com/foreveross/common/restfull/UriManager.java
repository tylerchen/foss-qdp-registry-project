/*******************************************************************************
 * Copyright (c) Dec 18, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.restfull;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.*;
import org.iff.infra.util.TypeConvertHelper.TypeConvert;
import org.iff.infra.util.spring.SpringContextHelper;

import java.io.File;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * UriTemplate 管理器。Restful的路径解释、映射、匹配、调用。
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Dec 18, 2017
 */
@SuppressWarnings("unchecked")
public class UriManager {

    /**
     * <pre>
     * Map<String, Map<String, UriTemplate>>
     *       |             |          |----URI路径，调用方法，Bean名称。
     *       |             |----sencondIndex：二级搜索索引是，除去一级搜索索引部分，余下的路径再作索引，如：/AuthAccount/get/:id，索引路径为：:id。
     *       |----searchIndex：/{httpMethod}/{basePath}/{staticPath}/{paramCount}，把HTTP请求方法，类基础路径，方法的路径的静态部分，变量总数，作为一级搜索索引，如：/AuthAccount/get/:id，索引路径为：/GET/AuthAccount/get/1。
     * </pre>
     */
    private static final Map<String, Map<String, UriTemplate>> URI = new HashMap<String, Map<String, UriTemplate>>();

    public static void main(String[] args) {
        try {
            //parseInterface(AuthAccountBsApplication.class);
            Properties properties = new Properties();
            properties.load(new StringReader(FileUtils.readFileToString(
                    new File(
                            "/Users/zhaochen/dev/workspace/cocoa/foss-qdp-project-v4/src/main/resources/META-INF/restful/rest.properties"),
                    "UTF-8")));
            Map<String, String> map = new HashMap<String, String>();
            for (Entry<Object, Object> entry : properties.entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                    map.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
            parseProperties(map);
            invoke("/AuthAccount", "POST", MapHelper.toMap("id", "id"), new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过属性文件来解释RESTFUL路径。
     *
     * @param props
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Dec 22, 2017
     */
    public static void parseProperties(Map<String, String> props) {
        if (props == null) {
            return;
        }
        List<String> restList = new ArrayList<String>();
        Map<String, String> classMap = new LinkedHashMap<String, String>();
        Map<String, String> methodMap = new LinkedHashMap<String, String>();
        Map<String, String> argMap = new LinkedHashMap<String, String>();
        {//先做第一次分类
            for (Entry<String, String> entry : props.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.endsWith(".restful.tc") && "true".equals(value)) {
                    restList.add(StringUtils.remove(key, ".restful.tc"));
                } else {
                    if (key.indexOf(".interface") > 0) {
                        classMap.put(key, value);
                    } else if (key.indexOf(".method.") > 0) {
                        if (key.indexOf(".arg") > 0) {
                            argMap.put(key, value);
                        } else {
                            methodMap.put(key, value);
                        }
                    }
                }
            }
        }
        for (String restName : restList) {
            String prefix = restName;
            String inf = classMap.get(prefix + ".interface");
            final String path = classMap.get(prefix + ".interface.path");
            final String beanName = classMap.get(prefix + ".interface.beanName");
            final String nullChar = classMap.get(prefix + ".interface.nullChar");
            BS.Path classPath = new BS.Path() {
                public Class<? extends Annotation> annotationType() {
                    return BS.Path.class;
                }

                public String[] path() {
                    return StringUtils.split(path, ',');
                }

                public BS.Method method() {
                    return BS.Method.GET;
                }

                public String nullChar() {
                    return StringUtils.defaultIfEmpty(nullChar, "-");
                }

                public String beanName() {
                    return beanName;
                }
            };
            String methodStartPrefix = prefix + ".method.";
            for (Entry<String, String> methodEntry : methodMap.entrySet()) {
                if (!(methodEntry.getKey().startsWith(methodStartPrefix)
                        && StringUtils.countMatches(methodEntry.getKey(), ".") == 2)) {
                    continue;
                }
                String methodPrefix = methodEntry.getKey();
                String methodName = methodMap.get(methodPrefix);
                final BS.Method requestMethod = BS.Method.get(methodMap.get(methodPrefix + ".method"));
                final String[] methodPath = StringUtils.split(methodMap.get(methodPrefix + ".path"), ',');
                BS.Param param = null;
                List<String> argsTypes = new ArrayList<String>();
                List<BS.Param> argAnnotations = new ArrayList<BS.Param>();
                for (int i = 0; i < 100; i++) {
                    String arg = argMap.get(methodPrefix + ".arg" + i);
                    if (StringUtils.isBlank(arg)) {
                        break;
                    }
                    argsTypes.add(arg);
                    final String argProps = argMap.get(methodPrefix + ".arg" + i + ".props");
                    String fieldStartPrefix = methodPrefix + ".arg" + i + ".field.";
                    final List<BS.Prop> fieldList = new ArrayList<BS.Prop>();
                    for (Entry<String, String> fieldEntry : argMap.entrySet()) {
                        if (!(fieldEntry.getKey().startsWith(fieldStartPrefix)
                                && StringUtils.countMatches(methodEntry.getKey(), ".") == 5)) {
                            continue;
                        }
                        final String field = fieldEntry.getValue();
                        final String defaultValue = argMap.get(field + ".defaultValue");
                        if (StringUtils.isNotBlank(field)) {
                            fieldList.add(new BS.Prop() {
                                public Class<? extends Annotation> annotationType() {
                                    return BS.Prop.class;
                                }

                                public String value() {
                                    return field;
                                }

                                public String defaultValue() {
                                    return StringUtils.defaultString(defaultValue);
                                }
                            });
                        }
                    } //END-for-props
                    param = new BS.Param() {
                        public Class<? extends Annotation> annotationType() {
                            return BS.Param.class;
                        }

                        public String[] value() {
                            return StringUtils.split(argProps, ',');
                        }

                        public BS.Prop[] props() {
                            return fieldList.toArray(new BS.Prop[0]);
                        }
                    };
                    argAnnotations.add(param);
                } //END-for-args
                Method mm = null;
                try {
                    Class<?> clazz = Class.forName(inf);
                    String method = methodName;
                    for (Class<?> superClass = clazz; superClass != null
                            && superClass != Object.class; superClass = superClass.getSuperclass()) {
                        Method[] methods = superClass.getDeclaredMethods();
                        for (Method m : methods) {
                            if (!m.getName().equals(method)) {
                                continue;
                            }
                            Class<?>[] types = m.getParameterTypes();
                            if (types.length != argsTypes.size()) {
                                continue;
                            }
                            int i = 0;
                            for (; i < types.length; i++) {
                                String argType = argsTypes.get(i);
                                if (types[i].isArray()
                                        && (types[i].getComponentType().getSimpleName() + "[]").equals(argType)) {
                                    continue;
                                } else if (types[i].getSimpleName().equals(argType)
                                        || types[i].getName().equals(argType)) {
                                    continue;
                                } else {
                                    break;
                                }
                            }
                            if (i == types.length) {
                                mm = m;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mm == null) {
                    Exceptions.runtime("Can't find the metohd " + methodName);
                }
                Annotation[][] annotations = new Annotation[argsTypes.size()][];
                for (int i = 0; i < argsTypes.size(); i++) {
                    annotations[i] = new Annotation[]{argAnnotations.get(i)};
                }
                BS.Path mmPath = new BS.Path() {
                    public Class<? extends Annotation> annotationType() {
                        return BS.Path.class;
                    }

                    public String[] path() {
                        return methodPath;
                    }

                    public BS.Method method() {
                        return requestMethod == null ? BS.Method.GET : requestMethod;
                    }

                    public String nullChar() {
                        return "-";
                    }

                    public String beanName() {
                        return "";
                    }
                };
                parse(mm, annotations, mmPath, classPath);
            }
        }
    }

    /**
     * 把一个接口上的注解直接解释成Restful的路径。
     *
     * @param clazz
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Dec 20, 2017
     */
    public static void parseInterface(Class<?> clazz) {
        Assert.notNull(clazz, "Interface can't be null!");
        try {
            BS.Path classPath = clazz.getAnnotation(BS.Path.class);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method m : methods) {
                BS.Path methodPath = m.getAnnotation(BS.Path.class);
                if (methodPath != null) {
                    parse(m, methodPath, classPath);
                }
            }
        } catch (Exception e) {
            Exceptions.runtime(FCS.get("Parse interface error: {int} !", clazz.getName()), e);
        }
    }

    /**
     * 根据请求的URI及请求方法，匹配UriTemplate，注意要去掉Restful的Context前缀。
     *
     * @param uri
     * @param requestMethodName
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Dec 20, 2017
     */
    public static UriTemplate match(String uri, String requestMethodName) {
        List<String> list = UriTemplate.genSearchIndex(uri, requestMethodName);
        for (String s : list) {
            Map<String, UriTemplate> map = URI.get(s);
            if (map == null) {
                continue;
            }
            if (map.size() == 1) {
                return map.entrySet().iterator().next().getValue();
            }
            for (Entry<String, UriTemplate> entry : map.entrySet()) {
                if (entry.getValue().match(uri)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 解释方法的Restful路径。
     *
     * @param method
     * @param methodPath
     * @param classPath
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Dec 20, 2017
     */
    public static void parse(Method method, BS.Path methodPath, BS.Path classPath) {
        UriTemplate.parse(method, methodPath, classPath);
    }

    /**
     * 解释方法的Restful路径。
     *
     * @param method
     * @param methodPath
     * @param classPath
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Dec 20, 2017
     */
    public static void parse(Method method, Annotation[][] annotations, BS.Path methodPath, BS.Path classPath) {
        UriTemplate.parse(method, annotations, methodPath, classPath);
    }

    /**
     * 解释方法的Restful路径。
     *
     * @param method
     * @param methodPath
     * @param classPath
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Dec 20, 2017
     */
    public static void parse(MyMethod method, BS.Path methodPath, BS.Path classPath) {
        UriTemplate.parse(method, methodPath, classPath);
    }

    /**
     * Restful直接调用。
     *
     * @param uri
     * @param requestMethod
     * @param conditionParams
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Dec 20, 2017
     */
    public static <T> T invoke(String uri, String requestMethod, Map<String, Object> conditionParams, Object[] datas) {
        // Spring bean调用的参数
        List<Object> args = new ArrayList<Object>();
        String beanName = null;
        try {
            // 匹配URI Template
            UriTemplate tpl = match(uri, requestMethod);
            Assert.notNull(tpl, "Can't find the rest path: " + uri);
            // URI路径参数解释
            Map<String, String> map = tpl.pathParams(uri);
            // 方法的参数及注解
            Multimap<String, Map<String, String>> params = tpl.getMethodParams();
            requestMethod = requestMethod.toUpperCase();
            if (requestMethod.equals("GET") //
                    || requestMethod.equals("OPTIONS") //
                    || requestMethod.equals("DELETE") // Permitted as spec is ambiguous.
                    || requestMethod.equals("PROPFIND") // (WebDAV) without body: request <allprop/>
                    || requestMethod.equals("MKCOL") // (WebDAV) may contain a body, but behaviour is unspecified
                    || requestMethod.equals("LOCK")) // (WebDAV) body: create lock, without body: refresh lock
            {
                formDataToMethodArguments(requestMethod, conditionParams, args, tpl, map, params);
            }
            if (requestMethod.equals("POST") //
                    || requestMethod.equals("PUT") //
                    || requestMethod.equals("PATCH")//
                    || requestMethod.equals("PROPPATCH") // WebDAV
                    || requestMethod.equals("REPORT")) // CalDAV/CardDAV (defined in WebDAV Versioning)
            {
                if (datas != null && datas.length > 0) {
                    postDataToMethodArguments(datas, args, map, params);
                } else {
                    formDataToMethodArguments(requestMethod, conditionParams, args, tpl, map, params);
                }
            }
            // 拿到Spring的Bean对象
            beanName = tpl.getClassBeanName();
            Object bean = SpringContextHelper.getBean(beanName);
            Assert.notNull(bean, FCS.get("Spring bean is null, bean name: ", beanName));
            return tpl.invoke(bean, args.toArray());
        } catch (Exception e) {
            Exceptions.runtime(FCS.get("Invoke restful error {err}, uri: {uri}, bean: {bean}, args: \n{args}!",
                    e.getMessage(), uri, beanName, XStreamHelper.toXml(args)), e);
        }
        return null;
    }

    private static void postDataToMethodArguments(Object[] datas, List<Object> args, Map<String, String> map,
                                                  Multimap<String, Map<String, String>> params) throws ClassNotFoundException {
        int index = 0;
        for (Entry<String, Map<String, String>> param : params.entries()) {
            Object arg = datas.length > index ? datas[index] : null;
            index++;
            String key = param.getKey();
            // 获得类型转换，如是基本Java数据类型及数组，可以通过类型转换器直接转换
            TypeConvert convert = TypeConvertHelper.me().get(key);
            if (convert.getName().equalsIgnoreCase("null")) {// 没有对应的类型转换器，可能这个是一个VO类型
                //使用POVO拷贝，转换成VO对象。
                args.add(POVOCopyHelper.copyTo(arg, Class.forName(key)));
            } else if (!param.getValue().isEmpty()) {//如果方法的参数注解不为空，就进行基本类型转换
                String value = map.get(param.getValue().keySet().iterator().next());
                if (convert.getName().startsWith("[L")) {//如果是一个数组，默认以逗号分割
                    args.add(convert.convert(key, StringUtils.split(value, ','), String[].class, null));
                } else {
                    args.add(convert.convert(key, value, String.class, null));
                }
            } else {
                args.add(null);
            }
        }
    }

    private static void formDataToMethodArguments(String requestMethod, Map<String, Object> conditionParams,
                                                  List<Object> args, UriTemplate tpl, Map<String, String> map, Multimap<String, Map<String, String>> params)
            throws ClassNotFoundException {
        for (Entry<String, Map<String, String>> param : params.entries()) {
            String key = param.getKey();
            // 获得类型转换，如是基本Java数据类型及数组，可以通过类型转换器直接转换
            TypeConvert convert = TypeConvertHelper.me().get(key);
            if (convert.getName().equalsIgnoreCase("null")) {// 没有对应的类型转换器，可能这个是一个VO类型
                Map<String, Object> values = new HashMap<String, Object>();
                // 如果方法的参数没有注解，那么默认路径上所有的参数都是VO的属性。
                if (param.getValue().isEmpty()) {
                    values.putAll(map);
                    BS.Method bsMehtod = BS.Method.get(requestMethod);
                    if (bsMehtod == BS.Method.POST || bsMehtod == BS.Method.PUT) {
                        values.putAll(conditionParams);
                    }
                }
                // 如果方法的参数有注解，那么先把注解的默认值添加上，再用路径的值来覆盖。
                else {
                    // 添加所有默认值，没有默认值的都为空。
                    values.putAll(param.getValue());
                    for (Entry<String, String> field : param.getValue().entrySet()) {
                        Object value = null;
                        // 如果路径中没有对应的值，就从表单中取
                        if (!map.containsKey(field.getKey())) {
                            conditionParams.get(field.getKey());
                        }
                        // 如果路径中有对应的值
                        else {
                            value = map.get(field.getKey());
                        }
                        // 如果值不为nullChar，就采用
                        if (value != null && !tpl.getNullChar().equals(value)) {
                            values.put(field.getKey(), value);
                        }
                    }
                }
                //使用POVO拷贝，转换成VO对象。
                args.add(POVOCopyHelper.copyTo(values, Class.forName(key)));
            } else if (!param.getValue().isEmpty()) {//如果方法的参数注解不为空，就进行基本类型转换
                String value = map.get(param.getValue().keySet().iterator().next());
                if (convert.getName().startsWith("[L")) {//如果是一个数组，默认以逗号分割
                    args.add(convert.convert(key, StringUtils.split(value, ','), String[].class, null));
                } else {
                    args.add(convert.convert(key, value, String.class, null));
                }
            } else {
                args.add(null);
            }
        }
    }

    public static Map<String, Map<String, UriTemplate>> getUriTemplates() {
        return URI;
    }

    public static class UriTemplate {

        /**
         * searchIndex={httpMethod}/{basePath}/{staticPath}/{paramCount}
         */
        private String searchIndex;
        private String sencondIndex;
        private String classPath;
        private String classBeanName;
        private String nullChar;
        private String methodPath;
        private String methodMethod;
        private MyMethod method;
        private Multimap<String, Map<String, String>> methodParams;// paramType:{fieldName:defaultValue}

        public static void parse(Method method, BS.Path methodPath, BS.Path classPath) {
            parse(MyMethod.create(method), methodPath, classPath);
        }

        public static void parse(Method method, Annotation[][] annotations, BS.Path methodPath, BS.Path classPath) {
            parse(MyMethod.create(method, annotations), methodPath, classPath);
        }

        public static void parse(MyMethod method, BS.Path methodPath, BS.Path classPath) {
            Assert.notNull(method, "Method can't be null.");
            Assert.notNull(methodPath, "BS.Path for method can't be null.");
            Assert.notNull(classPath, "BS.Path for class can't be null.");
            Assert.notEmpty(classPath.path(), "BS.Path.path for class can't be null.");
            Assert.notBlank(classPath.beanName(), "BS.Path.beanName for class can't be null.");
            Assert.notEmpty(methodPath.path(), "BS.Path.path for method can't be null.");
            for (String mpath : methodPath.path()) {
                UriTemplate tpl = new UriTemplate();
                tpl.classPath = classPath.path()[0];
                tpl.nullChar = StringUtils.defaultString(classPath.nullChar(), "-");
                tpl.classBeanName = classPath.beanName();
                tpl.methodPath = mpath;
                tpl.methodMethod = methodPath.method().name();
                tpl.method = method;
                //把HTTP请求方法，类基础路径，方法的路径的静态部分，变量总数，作为一级搜索索引。
                tpl.searchIndex = FCS.get("/{httpMethod}/{basePath}/{staticPath}/{paramCount}", tpl.methodMethod,
                        tpl.classPath, (tpl.methodPath.indexOf(':') > 0
                                ? tpl.methodPath.substring(0, tpl.methodPath.indexOf(':')) : tpl.methodPath),
                        StringUtils.countMatches(tpl.methodPath, ":")).toString();
                tpl.searchIndex = StringUtils.replace(tpl.searchIndex, "//", "/");
                tpl.searchIndex = StringUtils.replace(tpl.searchIndex, "//", "/");
                {//把方法的参数注解都解释出来。
                    tpl.methodParams = LinkedHashMultimap.create();
                    Annotation[][] annotations = method.getParameterAnnotations();
                    Class<?>[] types = method.getParameterTypes();
                    for (int i = 0; i < annotations.length; i++) {// argument index
                        Annotation[] annos = annotations[i];
                        Class<?> clazz = types[i];
                        Map<String, String> map = new LinkedHashMap<String, String>();
                        for (Annotation anno : annos) {// argument annotations
                            if (BS.Param.class.isInstance(anno)) {
                                BS.Param param = (BS.Param) anno;
                                if (param.value() != null) {
                                    for (String value : param.value()) {
                                        map.put(value, null);
                                    }
                                }
                                if (param.props() != null) {
                                    for (BS.Prop prop : param.props()) {
                                        map.put(prop.value(),
                                                StringUtils.isEmpty(prop.defaultValue()) ? null : prop.defaultValue());
                                    }
                                }
                            }
                        }
                        tpl.methodParams.put(clazz.getName(), map);
                    }
                }
                synchronized (URI) {
                    Map<String, UriTemplate> map = URI.get(tpl.searchIndex);
                    if (map == null) {
                        map = new HashMap<String, UriTemplate>();
                        URI.put(tpl.searchIndex, map);
                    }
                    //二级搜索索引是，除去一级搜索索引部分，余下的路径再作索引。
                    String[] ps = StringUtils.split(tpl.methodPath, '/');
                    StringBuilder sb = new StringBuilder();
                    for (String p : ps) {
                        if (p.length() > 0) {
                            if (p.startsWith(":")) {
                                sb.append(p).append('/');
                            } else if (sb.length() > 0) {//把方法路径的静态部分去除，直到变量名称才开始。
                                sb.append(p).append('/');
                            }
                        }
                    }
                    {
                        String key = sb.toString();
                        key = key.startsWith("/") ? key.substring(1) : key;
                        key = key.endsWith("/") ? key.substring(0, key.length() - 1) : key;
                        tpl.sencondIndex = key;
                        //检查是否有重要的索引，如果有，说明路径有重复。
                        if (map.containsKey(key)) {
                            Exceptions.runtime(FCS.get("Rest path {path}={tpl} has existed!", key, map.get(key)));
                        }
                        map.put(key, tpl);
                    }
                }
            } //END-FOR
        }

        public static List<String> genSearchIndex(String uri, String method) {
            Assert.notBlank(uri, "URI can't be blank.");
            Assert.notBlank(method, "Method can't be blank.");
            String trim = StringUtils.trim(uri);
            {
                trim = StringUtils.replace(trim, "//", "/");
                trim = trim.startsWith("/") ? trim.substring(1) : trim;
                trim = trim.endsWith("/") ? trim.substring(0, trim.length() - 1) : trim;
            }
            List<String> list = new ArrayList<String>();
            {
                StringBuilder sb = new StringBuilder().append('/').append(BS.Method.get(method)).append('/');
                int start = sb.length(), pos = start, counter = 0;
                sb.append(trim);
                //添加全路径
                list.add(sb.toString() + "/0");
                while ((pos = sb.lastIndexOf("/")) > start) {// from start
                    sb.setLength(pos);
                    //添加参数不为0的路径
                    list.add(sb.toString() + "/" + (++counter));//TO: /http-method/basePath/staticPath/counter.
                }
            }
            return list;
        }

        public boolean match(String uri) {
            if (StringUtils.isEmpty(uri)) {
                return false;
            }
            String path = uri;
            {//把路径清理干净，除去第一次索引路径。
                path = StringUtils.replace(path, "//", "/");
                path = path.startsWith("/") ? path.substring(1) : path;
                path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
                String tmp = searchIndex.substring(searchIndex.indexOf('/', 1), searchIndex.lastIndexOf('/'));
                if (uri.equals(tmp)) {
                    return true;
                }
                if (path.startsWith(tmp) && path.charAt(tmp.length()) == '/') {
                    path = path.substring(tmp.length() + 1);
                }
            }
            String[] ps = StringUtils.split(path, "/");
            String[] ss = StringUtils.split(searchIndex, "/");
            if (ps.length < 1 || ss.length < 1 || ps.length != ss.length) {
                return false;
            }
            for (int i = 0; i < ss.length; i++) {
                if (ss[i].startsWith(":")) {
                    continue;
                }
                if (!ss[i].equals(ps[i])) {
                    return false;
                }
            }
            return true;
        }

        public Map<String, String> pathParams(String uri) {
            Map<String, String> map = new LinkedHashMap<String, String>();
            if (StringUtils.isEmpty(uri)) {
                return map;
            }
            String path = uri;
            {//把路径清理干净，除去第一次索引路径。
                path = StringUtils.replace(path, "//", "/");
                path = path.startsWith("/") ? path.substring(1) : path;
                path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
                String tmp = searchIndex.substring(searchIndex.indexOf('/', 1) + 1, searchIndex.lastIndexOf('/'));
                if (path.equals(tmp)) {
                    return map;
                }
                if (path.startsWith(tmp) && path.charAt(tmp.length()) == '/') {
                    path = path.substring(tmp.length() + 1);
                }
            }
            String[] ps = StringUtils.split(path, "/");
            String[] ss = StringUtils.split(sencondIndex, "/");
            if (ps.length < 1 || ss.length < 1 || ps.length != ss.length) {
                return map;
            }
            for (int i = 0; i < ss.length; i++) {
                if (ss[i].startsWith(":")) {
                    map.put(ss[i].substring(1), ps[i]);
                }
            }
            return map;
        }

        public <T> T invoke(Object bean, Object... args) {
            Assert.notNull(bean, "Bean for invoke can't be null.");
            try {
                return (T) method.invoke(bean, args);
            } catch (Exception e) {
                Exceptions.runtime(FCS.get("Invoke {method} from bean {bean} error!", method, bean.toString()), e);
            }
            return null;
        }

        public String getSearchIndex() {
            return searchIndex;
        }

        public void setSearchIndex(String searchIndex) {
            this.searchIndex = searchIndex;
        }

        public String getSencondIndex() {
            return sencondIndex;
        }

        public void setSencondIndex(String sencondIndex) {
            this.sencondIndex = sencondIndex;
        }

        public String getClassPath() {
            return classPath;
        }

        public void setClassPath(String classPath) {
            this.classPath = classPath;
        }

        public String getClassBeanName() {
            return classBeanName;
        }

        public void setClassBeanName(String classBeanName) {
            this.classBeanName = classBeanName;
        }

        public String getNullChar() {
            return nullChar;
        }

        public void setNullChar(String nullChar) {
            this.nullChar = nullChar;
        }

        public String getMethodPath() {
            return methodPath;
        }

        public void setMethodPath(String methodPath) {
            this.methodPath = methodPath;
        }

        public String getMethodMethod() {
            return methodMethod;
        }

        public void setMethodMethod(String methodMethod) {
            this.methodMethod = methodMethod;
        }

        public MyMethod getMethod() {
            return method;
        }

        public void setMethod(MyMethod method) {
            this.method = method;
        }

        public Multimap<String, Map<String, String>> getMethodParams() {
            return methodParams;
        }

        public void setMethodParams(Multimap<String, Map<String, String>> methodParams) {
            this.methodParams = methodParams;
        }

        public String toString() {
            return "UriTempate [searchIndex=" + searchIndex + ", sencondIndex=" + sencondIndex + ", classPath="
                    + classPath + ", classBeanName=" + classBeanName + ", nullChar=" + nullChar + ", methodPath="
                    + methodPath + ", methodMethod=" + methodMethod + ", method=" + method + ", methodParams="
                    + methodParams + "]";
        }
    }

    public static class MyMethod {
        private Method method;
        private Annotation[][] annotations;

        public static MyMethod create(Method m, Annotation[][] annotations) {
            Assert.notNull(m);
            Assert.notNull(annotations);
            MyMethod mm = new MyMethod();
            mm.method = m;
            mm.annotations = annotations;
            return mm;
        }

        public static MyMethod create(Method m) {
            Assert.notNull(m);
            MyMethod mm = new MyMethod();
            mm.method = m;
            mm.annotations = m.getParameterAnnotations();
            return mm;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Annotation[][] getParameterAnnotations() {
            return annotations;
        }

        public Class<?>[] getParameterTypes() {
            return method.getParameterTypes();
        }

        public Object invoke(Object obj, Object... args) {
            try {
                return method.invoke(obj, args);
            } catch (Exception e) {
                Exceptions.runtime("method invoke error: " + method.toString(), e);
            }
            return null;
        }

        public Annotation[][] getAnnotations() {
            return annotations;
        }

        public void setAnnotations(Annotation[][] annotations) {
            this.annotations = annotations;
        }

        public String toString() {
            return method.toString();
        }

    }
}
