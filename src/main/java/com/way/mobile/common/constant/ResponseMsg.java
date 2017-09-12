package com.way.mobile.common.constant;

/**
 * @ClassName: ResponseMsg
 * @Description: 响应信息术语
 * @author: xinpei.xu
 * @date: 2017/07/08 19:18
 */
public interface ResponseMsg {
	
	String SUCCESS = "成功";
	String REGISTER_SUCCESS = "注册成功";
	String REGISTER_FAILUE = "注册失败";
	String LOGIN_SUCCESS = "登录成功";
	String LOGIN_FAILUE = "登录失败";
	String PASSWORD_MODIFY_SUCCESS = "密码修改成功";
	String PASSWORD_MODIFY_FAILUE = "密码修改失败";
	String PASSWORD_RESET_SUCCESS = "重置密码成功";
	String PASSWORD_RESET_FAILUE = "重置密码失败";
	String LOGIN_FREQUENT = "你最近登录过于频繁，请休息一下再来登录吧！";
	
	String MOBILE_EXIST = "手机号已被注册"; 
	String MOBILE_NOT_EXIST = "手机号未注册";
	String MOBILE_EMPTY = "手机号码为空";
	String MOBILE_VALIDATE = "手机号码格式有误";
	String SEND_CODE_MINUTE = "1分钟内已经发送过验证码";
	String SEND_CODE_FALIUE = "验证码发送失败";
	String SMS_CODE_OUTTIME = "短信验证码已失效";
	String SMS_CODE_ERROR = "短信验证码不正确";
	String IMG_CODE_OUTTIME = "图片验证码已失效";
	String IMG_CODE_ERROR = "图片验证码不正确";
	
	String MOBILE_OR_PASSWORD_ERROR = "手机号或密码错误";
	String USER_OUTTIME = "账号已过期，请重新登录";
	String TRAN_PASSWORD_ERROR = "旧密码不正确";
	String PASSWORD_EMPTY = "密码不能为空";
	
	
	
	String FAILURE = "失败";
	String UNKNOW = "未知的错误类型";
	String TOKEN_NOT_MATCH = "token不匹配";
	String INPUT_NULL = "输入为空";
	String UNLOGIN = "未登录";
	String LOGIN_OUTTIME = "登录时间超过设置长度";
	String LOGIN_MANY_TIMES = "登录次数太多";
	
	String USER_EXIST = "用户名已经存在";
	String USERNAME_VALIDATE = "用户名格式不正确"; 
	String ACCOUNTSTOP = "您的账户被停用"; 
	String SIDNO_NOT_EXIST = "验证出错,身份证号码不存在!"; 
	String SIDNO_IS_EMPTY = "接口返回的身份证验证信息为空!"; 
	String SIDNO_FAILE = "身份证号码验证出错!"; 
	String SIDNO_NAME_NOEQUAL = "姓名与身份证号码不一致"; 
	String SIDNO_NOT_MATCH ="身份证不一致";
	String USER_NAME_ILLEGAL ="用户名不一致";
	String USER_LOGGED_IN = "用户已经登录";
	String MEMBER_NULL = "查询的用户为空";
	String REALNAME_NULL = "真实姓名不存在";
	String AU_NULL = "实名认证未通过";
	String CARD_EXITS = "银卡卡已被绑定";
	String ACCOUNTNOEXITS = "您的账户不存在"; 
	String ACCOUNTMONEY = "项目余额不足";
	
	String EMAIL_VALIDATE = "邮箱格式不正确";
	String MOBILE_ERROR = "手机号码与之前发送验证码的号码不一致";
	String MOBILE_EMAIL_VALIDATE = "手机号码或者邮箱格式有误";
	String MOBILE_EMAIL_NOMATCH = "手机号或者邮箱和注册的不一样";
	
	String NEWTRAN_PASSWORD_ERROR = "新密码与原密码一致，请重新输入！"; 
	String PASSWORD_NO_PATTERN = "密码格式不正确"; 
	String USER_PASSWD_VALIDATE = "密码不正确";
	String TRANSPASSWD_NULL = "交易密码为空";
	String TRANSACTIONPWD_NOEQUAL = "交易密码不相等";
	String USER_NOT_EXITS = "用户名不存在";
	
}
