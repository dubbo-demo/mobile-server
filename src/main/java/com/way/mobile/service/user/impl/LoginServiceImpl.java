/**   
 * @Title: MemberServiceImpl.java 
 * @Package: com.myph.member.infor.service 
 * @company: 麦芽金服
 * @Description: 麦芽普惠APP会员资料相关处理类
 * @author 徐超
 * @date 2016年10月27日 下午14:05:00
 * @version V1.0   
 */
package com.way.mobile.service.user.impl;

import com.way.common.constant.Constants;
import com.way.common.constant.NumberConstants;
import com.way.common.exception.DataValidateException;
import com.way.common.redis.CacheService;
import com.way.common.result.ServiceResult;
import com.way.common.util.DateUtils;
import com.way.member.user.dto.MemberDto;
import com.way.member.user.dto.MemberLoginFailInfoDto;
import com.way.member.user.service.MemberService;
import com.way.mobile.common.constant.IConstantsConfig;
import com.way.mobile.common.po.LoginTokenInfo;
import com.way.mobile.common.util.PropertyConfig;
import com.way.mobile.common.util.TokenJedisUtils;
import com.way.mobile.service.user.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 
 * @ClassName: LoginServiceImpl
 * @Description: 登录ServiceImpl
 * @author: xinpei.xu
 * @date: 2017/08/19 19:48
 *
 */
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private PropertyConfig propertyConfig;
	
	private static final String loginFailMsg = "手机号或密码错误，今日还有times次输入机会，您可以找回密码";
	
	@Autowired
	private MemberService memberService;

	/**
	 * @Title: checkLoginFailTimes
	 * @Description: 校验用户登录失败次数是否大于允许登录的最大值
	 * @return: int
	 * @throws DataValidateException 
	 */
	public int checkLoginFailTimes(Object ob, String key) throws DataValidateException {
		int tempFailTimes = 0;// 临时记录登录失败次数
		if (null != ob) { // 已经存在该用户登录失败的信息
			MemberLoginFailInfoDto loginFailVO = (MemberLoginFailInfoDto) ob;
			if (propertyConfig.getPermitLoginTimes() == loginFailVO.getLoginFailTimes()) { // 登录失败次数达到允许最大登录次数
				Date lastLoginFailTime = DateUtils.timeStamp2Date("" + loginFailVO.getLastLoginFailTime());
				if (propertyConfig.getLoginFailIntervalUnit() == IConstantsConfig.DATE_TIME_UNIT_DAY) { // 如果时间间隔的单位为天
					long subDay = DateUtils.getSubDays(lastLoginFailTime);
					if (subDay >= propertyConfig.getLoginFailInterval()) { // 时间间隔达到最大间隔日期
						CacheService.KeyBase.delete(key);
					} else { // 本次登录的时间间隔小于允许登录的时间间隔
						throw new DataValidateException("该账户已被锁定，您可以找回密码，次日自动解锁。");
					}
				} else { // 否则，默认时间间隔的单位为小时
					long subHours = DateUtils.getSubHours(lastLoginFailTime);
					if (subHours < propertyConfig.getLoginFailInterval()) { // 本次登录
						throw new DataValidateException("该账户已被锁定，您可以找回密码，次日自动解锁。");
					} else { // 达到时间间隔，登录失败次数清零，视为第一次登录
						CacheService.KeyBase.delete(key);
					}
				}
			} else { // 登录失败次数未达到允许最大登录次数
				tempFailTimes = loginFailVO.getLoginFailTimes();
			}
		}
		return tempFailTimes;
	}

	/**
	 * @Title: login
	 * @Description: 登录处理
	 * @return: ServiceResult
	 * @throws DataValidateException 
	 */
	public ServiceResult<MemberDto> login(MemberDto memberDto) throws DataValidateException {
		// 登录失败的KEY键
		String key = IConstantsConfig.JEDIS_HEADER_LOGIN_FAIL + memberDto.getPhone();
		Object ob = CacheService.StringKey.getObject(key, Object.class);
		// 临时记录登录失败次数
		int tempFailTimes = checkLoginFailTimes(ob, key);
		/** 调用户中心登录接口验证用户信息， 返回用户信息 */
		ServiceResult<MemberDto> result = memberService.queryMemberInfo(memberDto.getPhone());
		MemberDto resultDto = result.getData();
		if(resultDto == null){
			result.setCode(ServiceResult.ERROR_CODE);
			result.setMessage("手机号未注册");
			return result;
		}else{
			if(!resultDto.getPassword().equals(memberDto.getPassword())){
				result.setCode(ServiceResult.ERROR_CODE);
				result.setMessage("手机号或密码错误");
				return result;
			}
			if (ServiceResult.SUCCESS_CODE == result.getCode()) {
				memberDto.setMemberId(resultDto.getMemberId());
				// 登录成功记入日志
				memberService.saveMemberLoginInfo(memberDto);
				// 登录成功，则清零登录失败次数，视为第一次登录
				if (null != ob) 
					CacheService.KeyBase.delete(key);
				// 登录成功返回token
				String oldToken = TokenJedisUtils.getTokenByMemberId(resultDto.getMemberId() + "");
				if (null != oldToken) {// 存在旧token，说明是在另一台设备上登录，将旧token状态置为1
					LoginTokenInfo tokenInfo = TokenJedisUtils.getTokenInfo(oldToken);
					if (null != tokenInfo) {
						tokenInfo.setStatus(1);
						TokenJedisUtils.resetTokenInfoExpire(oldToken, tokenInfo, (int) TokenJedisUtils.getTokenSurplusExpire(oldToken));
					}
				}
				String newToken = TokenJedisUtils.putTokenInfoExpire(resultDto.getMemberId() + "");
				resultDto.setToken(newToken);
				// 是否是老用户 0:线下,1:App
				if(resultDto.getMemberSource() == NumberConstants.NUM_ZERO){
					resultDto.setMemberType(Constants.NO);
				} else{
					resultDto.setMemberType(Constants.YES);
					String isSubmitForApproval = Constants.NO;
					// 初始化资料明细完成步骤
					memberService.initDataCompletValues(memberDto.getMemberId(), isSubmitForApproval);
				}
			} else if (ServiceResult.ERROR_CODE == result.getCode()) { 
				// 密码错误登录失败，登录失败次数+1，最后登录失败时间改为当前时间
				MemberLoginFailInfoDto loginFailVO = new MemberLoginFailInfoDto();
				loginFailVO.setLastLoginFailTime(new Date().getTime());
				loginFailVO.setLoginFailTimes(tempFailTimes+1);
				CacheService.StringKey.set(key, loginFailVO);
				
				int subTimes = propertyConfig.getPermitLoginTimes() - tempFailTimes-1;
				result.setCode(ServiceResult.ERROR_CODE);
				result.setMessage(loginFailMsg.replace("times", ""+subTimes));
			}
		}
		return result;
	}
	
	/**
	 * 退出应用
	 */
	@Override
	public void logout(String token) {
	    TokenJedisUtils.removeTokenInfo(token);	    
	}
}
