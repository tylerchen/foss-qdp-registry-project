/*******************************************************************************
 * Copyright (c) 七月 14 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foreveross.common.application.Authorization;

/**
 * CommonWsController
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 9, 2015
 * auto generate by qdp.
 */
@Controller
@RequestMapping("/ws")
public class CommonWsController extends AbstractWsController {

	boolean canAccessBean(Authorization authorization, String beanName, List<String> roles) {
		return Authorization.Util.hasAuthorization(authorization, beanName, false, roles);
	}
}
