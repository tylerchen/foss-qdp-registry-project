/*******************************************************************************
 * Copyright (c) Jan 22, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import java.util.Set;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.iff.infra.util.CacheHelper;

/**
 * Shiro会话监听
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Jan 22, 2016
 */
@SuppressWarnings("unchecked")
public class ShiroSessionListener implements SessionListener {

	public void onStart(Session session) {//会话创建触发 已进入shiro的过滤连就触发这个方法  
		//System.out.println("会话创建：" + session.getId());
	}

	public void onStop(Session session) {//退出 
		Set<String> cacheKeys = (Set<String>) session.getAttribute("CACHE_KEY_SET");
		if (cacheKeys != null) {
			for (String str : cacheKeys) {
				CacheHelper.del(str);
			}
		}
		//System.out.println("退出会话：" + session.getId());
	}

	public void onExpiration(Session session) {//会话过期时触发
		Set<String> cacheKeys = (Set<String>) session.getAttribute("CACHE_KEY_SET");
		if (cacheKeys != null) {
			for (String str : cacheKeys) {
				CacheHelper.del(str);
			}
		}
		//System.out.println("会话过期：" + session.getId());
	}

}