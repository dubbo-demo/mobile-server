package com.way.mobile.common.constant;

/**
 * @ClassName: IConstantsConfig
 * @Description: 配置参数需要的常量值
 * @author: xinpei.xu
 * @date: 2017/08/17 21:47
 */
public interface IConstantsConfig {
	
	/*************** redis header ***********************/
	/** redis header:登录失败 */
	String JEDIS_HEADER_LOGIN_FAIL = "LOGIN_FAIL:";
	/** redis header:重置密码失败 */
	String JEDIS_HEADER_RESET_PASSWORD_FAIL = "RESET_PASSWORD_FAIL:";
	/** redis header:借款失败 */
	String JEDIS_HEADER_LOAD_FAIL = "LOAD_FAIL:";
	/** redis header:注册短信验证码 */
	String JEDIS_HEADER_REGIST_CODE = "REGIST_CODE:";
	/** redis header:忘记密码短信验证码 */
	String JEDIS_HEADER_FORGET_PASSWORD_CODE = "FORGET_PASSWORD_CODE:";
	
	
	/*************** 时间间隔单位标记 ***********************/
	/** 时间单位标记：分钟 */
	int DATE_TIME_UNIT_MINUTE = 1;
	/** 时间单位标记：小时 */
	int DATE_TIME_UNIT_HOURS = 2;
	/** 时间单位标记：日 */
	int DATE_TIME_UNIT_DAY = 3;
	/** 时间单位标记：周 */
	int DATE_TIME_UNIT_WEEK = 4;
	/** 时间单位标记：月 */
	int DATE_TIME_UNIT_MONTH = 5;
	
}
