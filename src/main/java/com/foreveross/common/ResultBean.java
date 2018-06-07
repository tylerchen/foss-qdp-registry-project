/*******************************************************************************
 * Copyright (c) Jul 19, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common;

import org.iff.infra.util.Assert;

import javax.xml.bind.annotation.XmlRootElement;
import java.beans.Transient;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ResultBean for return.
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Jul 19, 2016
 */
@XmlRootElement(name = "ResultBean")
@SuppressWarnings("serial")
public class ResultBean implements Serializable {
    private Map<String, Object> header = new LinkedHashMap<String, Object>();
    private Object body;

    /**
     * create Result Bean.
     */
    public ResultBean() {
        super();
    }

    /**
     * create Result Bean.
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Jul 19, 2016
     */
    public static ResultBean create() {
        return new ResultBean();
    }

    /**
     * create Result Bean.
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Jul 19, 2016
     */
    public static ResultBean success() {
        ResultBean result = create();
        result.addHeader("status", "success");
        return result;
    }

    /**
     * create Result Bean.
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Jul 19, 2016
     */
    public static ResultBean error() {
        ResultBean result = create();
        result.addHeader("status", "error");
        return result;
    }

    /**
     * test if is the error bean.
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Jul 19, 2016
     */
    @Transient
    public boolean isError() {
        Object status = header.get("status");
        return "error".equalsIgnoreCase(String.valueOf(status));
    }

    /**
     * test if is the success bean.
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Jul 19, 2016
     */
    @Transient
    public boolean isSuccess() {
        Object status = header.get("status");
        return "success".equalsIgnoreCase(String.valueOf(status));
    }

    /**
     * add header by key and value.
     *
     * @param key
     * @param value
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Jul 19, 2016
     */
    public ResultBean addHeader(String key, Object value) {
        Assert.notNull(key, "header key can't be null.");
        header.put(key, value);
        return this;
    }

    /**
     * remove header by key.
     *
     * @param key
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Jul 19, 2016
     */
    public ResultBean removeHeader(String key) {
        Assert.notNull(key, "header key can't be null.");
        header.remove(key);
        return this;
    }

    /**
     * set body.
     *
     * @param body
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Jul 19, 2016
     */
    public ResultBean setBody(Object body) {
        this.body = body;
        return this;
    }

    /**
     * return body.
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Jul 19, 2016
     */
    @SuppressWarnings("unchecked")
    public <T> T getBody() {
        return (T) body;
    }

    /**
     * return header map.
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Jul 19, 2016
     */
    public Map<String, Object> getHeader() {
        return header;
    }

    /**
     * set header.
     *
     * @param header
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Jul 19, 2016
     */
    protected ResultBean setHeader(Map<String, Object> header) {
        this.header = header;
        return this;
    }

}
