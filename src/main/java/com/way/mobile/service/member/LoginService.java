package com.way.mobile.service.member;

import com.way.common.exception.DataValidateException;
import com.way.common.result.ServiceResult;
import com.way.member.member.dto.MemberDto;

/**
 * 
 * @ClassName: LoginService
 * @Description: 登录Service
 * @author xinpei.xu
 * @date 2017/08/17 22:37
 *
 */
public interface LoginService {
	
	/**
	 * @Title: checkLoginFailTimes
	 * @Description: 校验用户登录失败次数是否大于允许登录的最大值
	 * @return: int
	 * @throws DataValidateException 
	 */
	public int checkLoginFailTimes(Object ob, String key) throws DataValidateException;
	
	/**
	 * @Title: register
	 * @Description: 登录
	 * @return: int
	 * @throws DataValidateException 
	 */
	public ServiceResult<MemberDto> login(MemberDto memberDto) throws DataValidateException;

	/**
	 * 退出应用
	 * @param token
	 */
    public void logout(String token);
}
