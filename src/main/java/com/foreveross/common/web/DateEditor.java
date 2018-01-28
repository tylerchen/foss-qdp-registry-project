/*******************************************************************************
 * Copyright (c) 七月 14 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common.web;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * SpringMVC日期转换类
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 9, 2015
 * auto generate by qdp.
 */
public class DateEditor extends PropertyEditorSupport {

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isNotBlank(text)) {
			try {
				setValue(DateUtils.parseDate(text, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd HH:mm:ss",
						"yyyy/MM/dd", "yyyyMMddHHmmss", "yyyyMMdd"));
			} catch (Exception e) {
				setValue(null);
			}
		} else {
			setValue(null);
		}
	}

	public String getAsText() throws IllegalArgumentException {
		Object value = getValue();
		if (value != null && value instanceof java.util.Date) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
		} else {
			return null;
		}
	}
}