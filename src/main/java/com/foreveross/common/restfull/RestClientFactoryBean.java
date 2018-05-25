/*******************************************************************************
 * Copyright (c) Feb 5, 2018 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.restfull;

import com.foreveross.common.restfull.RestClient.LoadBalancerRoundRobin;
import com.foreveross.common.restfull.RestClient.Options;
import com.foreveross.common.restfull.RestClient.Request;
import com.foreveross.common.restfull.RestClient.Request.Builder;
import com.foreveross.common.restfull.RestClient.Response;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.*;
import org.iff.infra.util.TypeConvertHelper.TypeConvert;
import org.springframework.beans.factory.FactoryBean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Feb 5, 2018
 */
public class RestClientFactoryBean implements FactoryBean<Object> {

    private static final Map<Method, MethodOperation> methodOperations = new HashMap<Method, MethodOperation>();
    private static final Map<Method, String> nullCharMap = new HashMap<Method, String>();

    private Class<?> type;

    private String name;

    private String url;

    private String path;

    private String restPrefix;

    /**
     * read config from: /src/main/resources/META-INF/restclient/*.properties, the only one rest interface configure.
     */
    private Map<String, String> restClientConf = new HashMap<String, String>();

    private LoadBalancerRoundRobin loadBalancer;

    /**
     * the proxy object to send rest request.
     */
    private Object proxyObject;

    public Object getObject() throws Exception {
        if (proxyObject == null) {
            proxyObject = Proxy.newProxyInstance(RestClient.class.getClassLoader(), new Class<?>[]{type},
                    new RestClientInvocationHandler(this));
        }
        return proxyObject;
    }

    public Class<?> getObjectType() {
        return this.type;
    }

    public boolean isSingleton() {
        return true;
    }

    public static void matchRestUri(Class<?> interfaceClass, Map<String, String> restClientConf) {
        Method[] methods = interfaceClass.getMethods();
        for (Method method : methods) {//列出所有接口方法，然后在配置中查找对应的配置
            String methodName = method.getName();
            Class<?>[] types = method.getParameterTypes();
            int i = 0;
            MethodOperation methodOperation = null;
            for (Entry<String, String> entry : restClientConf.entrySet()) {
                if (!methodName.equals(entry.getValue())) {
                    continue;
                }
                String methodPropName = entry.getKey();
                for (i = 0; i < types.length; i++) {
                    Class<?> type = types[i];
                    String typeName = getTypeSimpleName(type);
                    String key = methodPropName + ".arg" + i;
                    if (!typeName.equals(restClientConf.get(key))) {
                        break;
                    }
                }
                if (i != types.length) {//如果参数不等于类型个数，说明不匹配。
                    continue;
                }
                if (i == types.length) {//所有的参数都匹配了，还要看看是否还有更多的参数情况。
                    String key = methodPropName + ".arg" + i;
                    if (restClientConf.containsKey(key)) {//这个说明方法的参数比配置的参数还要多。
                        continue;
                    }
                    //如果没有更多的参数了，就说明已经匹配了。
                    String pathKey = methodPropName + ".path";
                    String path = restClientConf.get(pathKey);
                    //所有参数的列表
                    List<String[]> methodTypeFields = new ArrayList<String[/*0:参数类型名称，1:参数类型的字段*/]>();
                    for (i = 0; i < types.length; i++) {
                        String argKey = methodPropName + ".arg" + i;
                        String fieldKey = methodPropName + ".arg" + i + ".value";
                        methodTypeFields.add(new String[]{restClientConf.get(argKey),
                                StringUtils.defaultString(restClientConf.get(fieldKey))});
                    }
                    //拿到http method
                    String httpMethodKey = methodPropName + ".method";
                    String httpMethod = StringUtils.defaultIfBlank(restClientConf.get(httpMethodKey), "GET");
                    methodOperation = MethodOperation.create(method, path, httpMethod, methodTypeFields);
                    synchronized (methodOperations) {
                        methodOperations.put(method, methodOperation);
                    }
                    String nullCharKey = methodPropName.substring(0, methodPropName.indexOf(".method."))
                            + ".interface.nullChar";
                    String nullChar = StringUtils.defaultIfEmpty(restClientConf.get(nullCharKey), "-");
                    synchronized (nullCharMap) {
                        nullCharMap.put(method, nullChar);
                    }
                }
            }
            if (methodOperation == null) {
                Exceptions.runtime(FCS.get("Can't find method's rest config: {0}!", method.toGenericString()));
            }
        }
    }

    public static String getTypeSimpleName(Class<?> clazz) {
        if (clazz.isArray()) {
            return clazz.getComponentType().getSimpleName() + "[]";
        }
        return clazz.getSimpleName();
    }

    private final class RestClientInvocationHandler implements InvocationHandler {
        private RestClientFactoryBean bean;

        public RestClientInvocationHandler(RestClientFactoryBean restClientFactoryBean) {
            this.bean = restClientFactoryBean;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if ("toString".equals(methodName)) {
                return bean.toString();
            }
            if ("equals".equals(methodName)) {
                return bean.equals(args[0]);
            }
            if ("hashCode".equals(methodName)) {
                return bean.hashCode();
            }
            if ("getClass".equals(methodName)) {
                return bean.getType();
            }
            if ("notify".equals(methodName)) {
                return null;
            }
            if ("notifyAll".equals(methodName)) {
                return null;
            }
            if ("wait".equals(methodName)) {
                return null;
            }
            MethodOperation operation = methodOperations.get(method);
            Assert.notNull(operation, "MethodOperation should not be null!");
            List<Map<String, Method>> argNameGetterMaps = operation.getArgNameGetterMaps();
            Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
            for (int i = 0; i < argNameGetterMaps.size(); i++) {
                Map<String, Method> argNameGetterMap = argNameGetterMaps.get(i);
                for (Entry<String, Method> entry : argNameGetterMap.entrySet()) {
                    String argName = entry.getKey();
                    Method getter = entry.getValue();
                    Object invoke = args[i];
                    if (getter != null) {
                        invoke = getter.invoke(args[i], new Object[0]);
                    }
                    Object object = invoke;
                    if (object == null) {
                    } else if (object instanceof Date) {
                        object = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(object);
                    } else if (object.getClass().isArray() && object.getClass().getComponentType() == String.class) {
                        object = StringUtils.join((String[]) object, ",");
                    } else {
                        object = String.valueOf(object);
                    }
                    valueMap.put(argName, object == null ? nullCharMap.get(method) : String.valueOf(object));
                }
            }
            String newPath = null;
            if (!valueMap.isEmpty()) {
                boolean match = true;
                for (String path : operation.getPaths()) {
                    // 1.看看变量的个数是否相同
                    if (StringUtils.countMatches(path, "/:") != valueMap.size()) {
                        continue;
                    }
                    // 2.再看变量名称是否相同
                    List<String> pathSplit = Arrays.asList(StringUtils.split(path, '/'));
                    for (Entry<String, Object> entry : valueMap.entrySet()) {
                        if (!pathSplit.contains(":" + entry.getKey())) {
                            match = false;
                            break;
                        }
                    }
                    // 3.如果相同就替换值
                    if (match) {
                        for (int i = 0; i < pathSplit.size(); i++) {
                            String key = pathSplit.get(i);
                            if (!key.startsWith(":")) {
                                continue;
                            }
                            Object object = valueMap.get(key.substring(1));
                            String value = object.toString();
                            pathSplit.set(i, value);
                        }
                    }
                    // 4.生成新路径
                    newPath = StringUtils.join(pathSplit, '/');
                }
            } else {
                for (String path : operation.getPaths()) {
                    if (StringUtils.countMatches(path, "/:") == 0) {
                        newPath = path;
                        break;
                    }
                }
            }
            RestClient client = new OkHttpRestClient();
            String urlSubfix = StringHelper.pathConcat(path, restPrefix, newPath);
            String serverUrl = loadBalancer.next().getId();
            String url2 = (serverUrl.endsWith("/") ? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl) + "/"
                    + (urlSubfix.startsWith("/") ? urlSubfix.substring(1) : urlSubfix);
            String httpMethod = operation.getHttpMethod().toUpperCase();
            Class<?> returnType = operation.getMethod().getReturnType();
            byte[] body = null;
            if (httpMethod.equals("POST") //
                    || httpMethod.equals("PUT") //
                    || httpMethod.equals("PATCH")//
                    || httpMethod.equals("PROPPATCH") // WebDAV
                    || httpMethod.equals("REPORT")) // CalDAV/CardDAV (defined in WebDAV Versioning)
            {
                body = GsonHelper.toJsonString(args[0]).getBytes("UTF-8");
            }
            {
                Logger.debug("[0] Create request for method : " + method.toGenericString());
                Logger.debug("[1] Request url               : " + url2);
                Logger.debug("[2] Request method            : " + httpMethod);
                Logger.debug("[3] Return type               : " + returnType);
            }
            Builder builder = Request.builder().method(httpMethod, body).url(url2).resultType(returnType)
                    .header("zuul", HttpHelper.ipsMd5()).header("Content-Type", "application/json")
                    .header("Accept", "application/json; charset=UTF-8").charset("UTF-8");
            Request request = builder.build();
            Response response = client.execute(request, new Options());
            return response.toObject();
        }
    }

    public static class MethodOperation {
        /**
         * 接口方法
         **/
        private Method method;
        /**
         * 接口方法对应的rest路径
         **/
        private String[] paths;
        /**
         * 接口方法对应的rest http请求方法
         **/
        private String httpMethod;
        /**  **/
        private List<Map<String, Method>> argNameGetterMaps = new ArrayList<Map<String, Method>>();

        /**
         * 创建 MethodOperation
         *
         * @param method           被调用的方法
         * @param path             URI路径
         * @param methodTypeFields 方法类型简称及字段，结构：[[methodSimpleName, "field1,field2"]]
         * @return
         * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
         * @since Feb 3, 2018
         */
        public static MethodOperation create(Method method, String path, String httpMethod,
                                             List<String[/*0:参数类型名称，1:参数类型的字段*/]> methodTypeFields) {
            Assert.notNull(method, "Method can't be null!");
            Assert.notEmpty(path, "Path can't be empty!");
            Assert.notEmpty(httpMethod, "Http method can't be empty!");
            MethodOperation mo = new MethodOperation();
            mo.setMethod(method);
            mo.setPaths(StringUtils.split(path, ','));
            mo.setHttpMethod(httpMethod);
            if (methodTypeFields == null || methodTypeFields.isEmpty()) {
                return mo;
            }
            Class<?>[] types = method.getParameterTypes();
            int i = 0;
            for (String[] typeField : methodTypeFields) {
                String methodType = typeField[0];
                String[] methodFields = StringUtils.split(StringUtils.defaultString(typeField[1]), ',');
                Assert.notEmpty(methodType, "Method type [e.g.: XX.method.XX.arg0=SysI18nVO] can't be empty!");
                Map<String, Method> map = new HashMap<String, Method>();
                if (methodFields.length < 1) {//如果参数没有带字段，就不需要取该参数的字段。
                    mo.getArgNameGetterMaps().add(map);
                    continue;
                }
                //如果参数带字段，就把该字段对应的getter方法拿出来，方便到时取值。
                Class<?> type = types[i++];
                for (String field : methodFields) {
                    try {
                        TypeConvert convert = TypeConvertHelper.me().get(type.getName());
                        if ("null".equals(convert.getName())) {
                            PropertyDescriptor prop = new PropertyDescriptor(field, type);
                            Method readMethod = prop.getReadMethod();
                            map.put(field, readMethod);
                        } else {
                            map.put(field, null);
                        }
                    } catch (Exception e) {
                        Logger.debug(FCS.get("Can't new PropertyDescriptor from class: {0} and field: {1}!",
                                type.getName(), field), e);
                        map.put(field, null);
                    }
                }
                mo.getArgNameGetterMaps().add(map);
            }
            return mo;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public String[] getPaths() {
            return paths;
        }

        public void setPaths(String[] paths) {
            this.paths = paths;
        }

        public String getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
        }

        public List<Map<String, Method>> getArgNameGetterMaps() {
            return argNameGetterMaps;
        }

        public void setArgNameGetterMaps(List<Map<String, Method>> argNameGetterMaps) {
            this.argNameGetterMaps = argNameGetterMaps;
        }
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRestPrefix() {
        return restPrefix;
    }

    public void setRestPrefix(String restPrefix) {
        this.restPrefix = restPrefix;
    }

    public Map<String, String> getRestClientConf() {
        return restClientConf;
    }

    public void setRestClientConf(Map<String, String> restClientConf) {
        this.restClientConf = restClientConf;
    }

    public LoadBalancerRoundRobin getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(LoadBalancerRoundRobin loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public Object getProxyObject() {
        return proxyObject;
    }

    public void setProxyObject(Object proxyObject) {
        this.proxyObject = proxyObject;
    }

}
