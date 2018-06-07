/*******************************************************************************
 * Copyright (c) Oct 13, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.application;

import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.MapHelper;
import org.mvel2.MVEL;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 授权标签，按执行顺序进行判断。
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Oct 13, 2017
 */
@SuppressWarnings("unchecked")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Authorization {
    String RANGE_SELF = "SELF";
    String RANGE_DEPARTMENT = "DEPARTMENT";
    String RANGE_ORGANIZATION = "ORGANIZATION";

    /**
     * 是否匿名访问，执行顺序：【01】
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 13, 2017
     */
    boolean anonymous() default false;

    /**
     * 角色名称列表，执行顺序：【02】
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 13, 2017
     */
    String[] roleNames() default {};

    /**
     * 正则匹配角色：Pattern，执行顺序：【03】
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 13, 2017
     */
    String regex() default "";

    /**
     * 访问数据范围，未实现，执行顺序：【04】
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 13, 2017
     */
    String[] range() default {};

    /**
     * 使用MVEL脚本方式，执行顺序：【05】
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Oct 13, 2017
     */
    String mvelScript() default "";

    class Util {
        public static boolean hasAuthorization(Authorization authorization, String beanName, boolean canAnonymous,
                                               List<String> roles) {
            {//是否匿名访问，执行顺序：【01】
                if (canAnonymous) {
                    if (authorization == null) {
                        //没有Authorization标签的Bean不能匿名访问。
                        return false;
                    } else if (authorization.anonymous()) {
                        //匿名只能访问anonymous设置为true的Bean。
                        return true;
                    }
                } else if (authorization == null) {
                    return true;
                }
            }
            boolean hasPass = true;
            {//角色名称列表，执行顺序：【02】
                String[] roleNames = authorization.roleNames();
                if (roleNames != null && roleNames.length > 0 && roles != null && hasPass) {
                    hasPass = false;
                    for (String role : roleNames) {
                        if (roles.contains(role)) {
                            hasPass = true;
                            break;
                        }
                    }
                }
            }
            {//正则匹配角色：Pattern，执行顺序：【03】
                String regex = authorization.regex();
                if (StringUtils.isNotBlank(regex) && roles != null && hasPass) {
                    Pattern p = Pattern.compile(regex);
                    hasPass = false;
                    for (String role : roles) {
                        if (p.matcher(role).matches()) {
                            hasPass = true;
                            break;
                        }
                    }
                }
            }
            {// 访问数据范围，未实现，执行顺序：【04】
            }
            {// 使用MVEL脚本方式，执行顺序：【05】
                String mvel = authorization.mvelScript();
                if (StringUtils.isNotBlank(mvel) && hasPass) {
                    hasPass = false;
                    Object eval = MVEL.eval(mvel,
                            MapHelper.toMap("authorization", authorization, "beanName", beanName, "roles", roles));
                    hasPass = eval != null && (eval instanceof Boolean) && (Boolean) eval;
                }
            }
            return hasPass;
        }
    }
}
