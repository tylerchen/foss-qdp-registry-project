/*******************************************************************************
 * Copyright (c) Oct 11, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.extension.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.EventBusHelper;
import org.iff.infra.util.IdHelper;

/**
 * 日志持久化内容。
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Oct 11, 2017
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class LogHelper {

	private static int MAX_COUNT = 1024;
	private static final List<Object[]>[] logs = new List[] { /*accessLog*/new ArrayList<Object[]>(MAX_COUNT),
			/*operationLog*/new ArrayList<Object[]>(MAX_COUNT) };
	private static final long[] lastUpdateTime = new long[] { /*accessLog*/ System.currentTimeMillis(),
			/*operationLog*/System.currentTimeMillis() };
	private static final ReentrantLock lock = new ReentrantLock();

	public static void accessLog(String user, String source, String target, String type, Date createTime) {
		///** 主键 **/String id;/** 访问者 **/String user;/** 来源 **/String source;/** 目标 **/String target;/** 创建时间 **/Date createTime;/** 修改时间 **/Date updateTime;/** 类型 **/String type;
		Object[] vo = new Object[] {
				/** 主键 **/
				IdHelper.uuid(),
				/** 访问者 **/
				trim(user, 100),
				/** 来源 **/
				trim(source, 100),
				/** 目标 **/
				trim(target, 2048),
				/** 创建时间 **/
				createTime == null ? new Date() : createTime,
				/** 修改时间 **/
				new Date(),
				/** 类型 **/
				trim(type, 40) };
		logs[0].add(vo);
		accessLogSave();
	}

	public static void accessLogSave() {
		if (logs[0].size() > MAX_COUNT - 100 || System.currentTimeMillis() - lastUpdateTime[0] > 60000) {
			try {
				lock.lock();
				if (logs[0].size() > MAX_COUNT - 100 || System.currentTimeMillis() - lastUpdateTime[0] > 60000) {
					List data = logs[0];
					logs[0] = new ArrayList(MAX_COUNT);
					lastUpdateTime[0] = System.currentTimeMillis();
					EventBusHelper.me().asyncEvent("LOG_SAVE_LogAccess", data);
				}
			} finally {
				lock.unlock();
			}
		}
	}

	public static void operationLog(String operator, String target, Date startTime, Date endTime, int costTime,
			String type, Date createTime, String result) {
		///** 主键 **/String id; /** 类型 **/String type; /** 操作者 **/String operator; /** 目标 **/String target; /** 开始时间 **/Date startTime; /** 结束时间 **/Date endTime; /** 耗时 **/Integer costTime; /** 创建时间 **/Date createTime; /** 修改时间 **/Date updateTime;/** 结果 **/String result;
		Object[] vo = new Object[] {
				/** 主键 **/
				IdHelper.uuid(),
				/** 类型 **/
				trim(type, 200),
				/** 操作者 **/
				trim(operator, 200),
				/** 目标 **/
				trim(target, 1000),
				/** 开始时间 **/
				startTime == null ? new Date() : startTime,
				/** 结束时间 **/
				endTime == null ? new Date() : endTime,
				/** 耗时 **/
				costTime,
				/** 创建时间 **/
				createTime == null ? new Date() : createTime,
				/** 修改时间 **/
				new Date(),
				/** 结果 **/
				trim(result, 2000) };
		logs[1].add(vo);
		operationLogSave();
	}

	public static void operationLogSave() {
		if (logs[1].size() > MAX_COUNT - 100 || System.currentTimeMillis() - lastUpdateTime[1] > 60000) {
			try {
				lock.lock();
				if (logs[1].size() > MAX_COUNT - 100 || System.currentTimeMillis() - lastUpdateTime[1] > 60000) {
					List data = logs[1];
					logs[1] = new ArrayList(MAX_COUNT);
					lastUpdateTime[1] = System.currentTimeMillis();
					EventBusHelper.me().asyncEvent("LOG_SAVE_LogOperation", data);
				}
			} finally {
				lock.unlock();
			}
		}
	}

	public static String trim(String str, int len) {
		str = StringUtils.defaultIfBlank(str, "UNKOWN");
		return StringUtils.left(str, len);
	}
}
