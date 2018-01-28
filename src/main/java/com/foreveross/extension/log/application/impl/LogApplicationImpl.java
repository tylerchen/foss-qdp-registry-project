/*******************************************************************************
 * Copyright (c) Oct 11, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.extension.log.application.impl;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.iff.infra.util.EventBusHelper;
import org.iff.infra.util.GsonHelper;
import org.iff.infra.util.MapHelper;
import org.iff.infra.util.NumberHelper;
import org.iff.infra.util.mybatis.service.Dao;
import org.springframework.beans.factory.InitializingBean;

import com.foreveross.common.ConstantBean;
import com.foreveross.extension.log.LogHelper;
import com.foreveross.extension.log.application.LogApplication;

/**
 * 日志持久化。
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Oct 11, 2017
 */
@Named("logApplication")
public class LogApplicationImpl implements LogApplication, InitializingBean {

	private static final org.iff.infra.util.Logger.Log Logger = org.iff.infra.util.Logger.get("LOG");

	/**
	 * 保存日志，1分钟保存一次。
	 */
	private static Timer logSaveTimer = null;
	/**
	 * 清除日志，凌晨0-1点执行。
	 */
	private static Timer logCleanTimer = null;

	public static void startLogSaveTimer() {
		if (logSaveTimer != null) {
			return;
		}
		logSaveTimer = new Timer("LOG_SAVE", true);
		logSaveTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				try {
					LogHelper.accessLogSave();
				} catch (Throwable t) {
				}
				try {
					LogHelper.operationLogSave();
				} catch (Throwable t) {
				}
			}
		}, 60 * 1000, 60 * 1000);
		Logger.info("Timer-LOG_SAVE started.");
	}

	public static void startLogCleanTimer() {
		if (logCleanTimer != null) {
			return;
		}
		logCleanTimer = new Timer("LOG_CLEAN", true);
		/**
		 * 把时间设置到明天凌晨1~2点间，为什么不设置固定时间？因为分布式下不想多个节点同时做一件事，而形成并发。
		 */
		Date startTime = DateUtils.setHours(DateUtils.addDays(new Date(), 1), 1);
		/**
		 * 周期为一天。
		 */
		int period = 24 * 60 * 60 * 1000;
		/**
		 * 默认只保留10天内日志，可以配置system.properties。
		 */
		final int keepDays = NumberHelper.getInt(ConstantBean.getProperty("system.log.keep.days", "10"), 10);
		logCleanTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				try {
					EventBusHelper.me().asyncEvent("LOG_CLEAN_LogAccess", keepDays);
					EventBusHelper.me().asyncEvent("LOG_CLEAN_LogOperation", keepDays);
				} catch (Throwable t) {
				}
			}
		}, startTime, period);
		Logger.info("Timer-LOG_CLEAN started.");
	}

	public void afterPropertiesSet() throws Exception {
		EventBusHelper.me().regist("LOG_SAVE_LogAccess", new EventBusHelper.EventProcess() {
			public void listen(String eventPath, Object data) {
				if (data == null || !(data instanceof List)) {
					return;
				}
				try {
					List<Object[]> list = (List<Object[]>) data;
					saveAccessLog(list);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

			public String getName() {
				return "LOG_SAVE_LogAccess";
			}
		});
		Logger.info("EventBusHelper-regist-LOG_SAVE_LogAccess.");
		EventBusHelper.me().regist("LOG_SAVE_LogOperation", new EventBusHelper.EventProcess() {
			public void listen(String eventPath, Object data) {
				if (data == null || !(data instanceof List)) {
					return;
				}
				try {
					List<Object[]> list = (List<Object[]>) data;
					saveOperationLog(list);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

			public String getName() {
				return "LOG_SAVE_LogOperation";
			}
		});
		Logger.info("EventBusHelper-regist-LOG_SAVE_LogOperation.");
		EventBusHelper.me().regist("LOG_CLEAN_LogAccess", new EventBusHelper.EventProcess() {
			public void listen(String eventPath, Object data) {
				if (data == null || !(data instanceof Number)) {
					return;
				}
				try {
					removeDaysBeforeAccessLogs(((Number) data).intValue());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

			public String getName() {
				return "LOG_CLEAN_LogAccess";
			}
		});
		Logger.info("EventBusHelper-regist-LOG_CLEAN_LogAccess.");
		EventBusHelper.me().regist("LOG_CLEAN_LogOperation", new EventBusHelper.EventProcess() {
			public void listen(String eventPath, Object data) {
				if (data == null || !(data instanceof Number)) {
					return;
				}
				try {
					removeDaysBeforeOperationLogs(((Number) data).intValue());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

			public String getName() {
				return "LOG_CLEAN_LogOperation";
			}
		});
		Logger.info("EventBusHelper-regist-LOG_CLEAN_LogOperation.");
		startLogSaveTimer();
		startLogCleanTimer();
	}

	public void saveAccessLog(List<Object[]> list) {
		///** 主键 **/String id;/** 访问者 **/String user;/** 来源 **/String source;/** 目标 **/String target;/** 创建时间 **/Date createTime;/** 修改时间 **/Date updateTime;/** 类型 **/String type;
		//String sql = "INSERT INTO LOG_ACCESS( ID, USER, SOURCE, TARGET, CREATE_TIME, UPDATE_TIME, TYPE ) VALUES (?, ?, ?, ?, ?, ?, ?)";
		//jdbcTemplate.batchUpdate(sql, list);
		try {
			if (CollectionUtils.isNotEmpty(list)) {
				Logger.info(GsonHelper.toJsonString(list));
			}
		} catch (Exception e) {
		}
	}

	public void saveOperationLog(List<Object[]> list) {
		///** 主键 **/String id; /** 类型 **/String type; /** 操作者 **/String operator; /** 目标 **/String target; /** 开始时间 **/Date startTime; /** 结束时间 **/Date endTime; /** 耗时 **/Integer costTime; /** 创建时间 **/Date createTime; /** 修改时间 **/Date updateTime;/** 结果 **/String result;
		//String sql = "INSERT INTO LOG_OPERATION ( ID, TYPE, OPERATOR, TARGET, START_TIME, END_TIME, COST_TIME, CREATE_TIME, UPDATE_TIME, RESULT ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		//jdbcTemplate.batchUpdate(sql, list);
		try {
			if (CollectionUtils.isNotEmpty(list)) {
				Logger.info(GsonHelper.toJsonString(list));
			}
		} catch (Exception e) {
		}
	}

	public void remove30DaysBeforeAccessLogs() {
		removeDaysBeforeAccessLogs(30);
	}

	public void remove60DaysBeforeAccessLogs() {
		removeDaysBeforeAccessLogs(60);
	}

	public void remove90DaysBeforeAccessLogs() {
		removeDaysBeforeAccessLogs(90);
	}

	public void removeDaysBeforeAccessLogs(int days) {
		if (days < 1 || days > 10000) {
			return;
		}
		Dao.remove("SystemLog.deleteLogAccessDaysBeforeLogs", MapHelper.toMap("day", days));
	}

	public void remove30DaysBeforeOperationLogs() {
		removeDaysBeforeOperationLogs(30);
	}

	public void remove60DaysBeforeOperationLogs() {
		removeDaysBeforeOperationLogs(60);
	}

	public void remove90DaysBeforeOperationLogs() {
		removeDaysBeforeOperationLogs(90);
	}

	public void removeDaysBeforeOperationLogs(int days) {
		if (days < 1 || days > 10000) {
			return;
		}
		Dao.remove("SystemLog.deleteLogOperationDaysBeforeLogs", MapHelper.toMap("day", days));
	}

}
