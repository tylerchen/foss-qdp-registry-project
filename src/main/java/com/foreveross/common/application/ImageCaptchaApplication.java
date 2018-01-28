/*******************************************************************************
 * Copyright (c) Aug 11, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.application;

/**
 * 验证码
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 11, 2016
 */
public interface ImageCaptchaApplication {

	/**
	 * get image bytes.
	 * @param sessionId
	 * @return
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Sep 7, 2016
	 */
	byte[] getImageForID(String sessionId);

	/**
	 * validateForID
	 * @param sessionId
	 * @param validate
	 * @return
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Sep 7, 2016
	 */
	boolean validateForID(String sessionId, Object validate);
}
