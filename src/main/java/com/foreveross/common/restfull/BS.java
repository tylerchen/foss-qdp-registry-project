/*******************************************************************************
 * Copyright (c) Dec 18, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.restfull;

import java.lang.annotation.*;

/**
 * Base restful annotation support.
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Dec 18, 2017
 */
public interface BS {

    public static enum Method {
        /**
         * GET: 是幂等的，多次执行不会改变数据。
         */
        GET, //
        /**
         * POST: 创建对象。（产生URI）。
         */
        POST, //
        /**
         * PUT: 修改对象。（不产生URI）
         */
        PUT, //
        /**
         * DELETE: 删除对象。（减少URI）
         */
        DELETE;

        /**
         * convert method name to Method.
         *
         * @param method
         * @return
         * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
         * @since Dec 19, 2017
         */
        public static Method get(String method) {
            for (Method m : Method.values()) {
                if (m.name().equalsIgnoreCase(method)) {
                    return m;
                }
            }
            return null;
        }
    }

    public static enum Type {
        /**
         * XSTREAM: 输入数据类型为XSTREAM。
         */
        XSTREAM, //
        /**
         * JSON: 输入数据类型为JSON。
         */
        JSON
    }

    /**
     * 限制：路径参数中如果要转换为VO中属性时，VO属性是复杂类型的包括数据，无法进行转换（无法把String转换成数组或复杂类型）。
     * 用于类型及restful方法。
     *
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Dec 18, 2017
     */
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Path {

        String[] path()

                default {};

        Method method()

                default Method.GET;

        String nullChar()

                default "-";

        String beanName() default "";
    }

    /**
     * 定义对象的属性，用于@Param内，可以指定默认值，如果不指定默认值，可以使用@Param的value。
     *
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Dec 18, 2017
     */
    @Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Prop {

        String value();

        String defaultValue() default "";
    }

    /**
     * 定义参数
     *
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Dec 18, 2017
     */
    @Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Param {

        String[] value() default {};

        Prop[] props() default {};
    }
}
