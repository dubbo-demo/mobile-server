package com.way.mobile.service.user;

import com.way.common.exception.DataValidateException;
import com.way.common.result.ServiceResult;
import com.way.member.user.dto.MemberDto;
import com.way.member.user.dto.MemberResetPasswordDto;

/**
 * 
 * @ClassName: RegistService
 * @Description: 注册Service
 * @author: xinpei.xu
 * @date: 2017/08/17 22:18
 *
 */
public interface RegistService {
	
	/**
	 * @Title: sendCode
	 * @Description: 发送验证码
	 * @return: void
	 * @throws DataValidateException 
	 */
	public ServiceResult<String> sendCode(MemberDto memberDto) throws DataValidateException;
	
	/**
	 * @Title: regist
	 * @Description: 用户注册
	 * @return: void
	 * @throws DataValidateException 
	 */
	public ServiceResult<MemberDto> regist(MemberDto memberDto) throws DataValidateException;
	
	/**
	 * @Title: forgetPassword
	 * @Description: 忘记密码
	 * @return: void
	 * @throws DataValidateException 
	 */
	public ServiceResult<String> forgetPassword(MemberDto memberDto) throws DataValidateException;
	
	/**
	 * 重置密码
	 * @param memberResetPasswordDto
	 * @throws DataValidateException 
	 */
	public ServiceResult<String> resetPassword(MemberResetPasswordDto memberResetPasswordDto) throws DataValidateException;
	
	/**
	 * @Title: checkResetPasswordFailTimes
	 * @Description: 校验重置密码失败次数是否大于允许的最大值
	 * @return: int
	 * @throws DataValidateException 
	 */
	public int checkResetPasswordFailTimes(Object ob, String key) throws DataValidateException;
}
