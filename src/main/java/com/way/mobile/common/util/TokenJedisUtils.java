package com.way.mobile.common.util;

import com.way.common.cache.RedisRootNameSpace;
import com.way.common.redis.CacheService;
import com.way.common.util.BeanUtils;
import com.way.mobile.common.po.LoginTokenInfo;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: TokenJedisUtils
 * @Description:
 * @author: xinpei.xu
 * @date: 2017/08/17 20:21
 */
public class TokenJedisUtils {

	/**
	 * @param memberId
	 * @Title: putTokenInfoExpire
	 * @Description: 将用户的token信息保存到redis中，并设置有效时长
	 * @return: String
	 */
	public static String putTokenInfoExpire(String memberId) {
		String token = null;
		if (!StringUtils.isEmpty(memberId)) {
			token = BeanUtils.getUUID();
			LoginTokenInfo tokenInfo = new LoginTokenInfo();
			tokenInfo.setMemberId(memberId);
			tokenInfo.setStatus(0);
			CacheService.StringKey.set(token, tokenInfo);
			CacheService.StringKey.set(memberId, token);
		}
		return token;
	}
	
	/**
	 * @Title: expireTokenInfo
	 * @Description: 重新设置token的有效时长 
	 * @return: void
	 */
	public static void expireTokenInfo(String token, String memberId) {
		if (!StringUtils.isEmpty(memberId)) {
			LoginTokenInfo tokenInfo = new LoginTokenInfo();
			tokenInfo.setMemberId(memberId);
			tokenInfo.setStatus(0);
//			CacheService.StringKey.set(token, tokenInfo, RedisRootNameSpace.UnitEnum.THIRTY_MIN);
//			CacheService.StringKey.set(memberId, token, RedisRootNameSpace.UnitEnum.THIRTY_MIN);
			CacheService.StringKey.set(token, tokenInfo);
			CacheService.StringKey.set(memberId, token);
		}
	}
	
	/**
	 * @Title: removeTokenInfo
	 * @Description: 删除token信息
	 * @return: void
	 */
	public static void removeTokenInfo(String token) {
		if (!StringUtils.isEmpty(token)) {
			LoginTokenInfo tokenInfo = CacheService.StringKey.getObject(token, LoginTokenInfo.class);
			if (tokenInfo != null) {
				CacheService.KeyBase.delete(token);
				CacheService.KeyBase.delete(tokenInfo.getMemberId());
			}
		}
	}
	
	/**
	 * @Title: getTokenInfo
	 * @Description: 获取token对应的信息
	 * @return: LoginTokenInfo
	 */
	public static LoginTokenInfo getTokenInfo(String token) {
		if (!StringUtils.isEmpty(token)) {
			LoginTokenInfo tokenInfo = CacheService.StringKey.getObject(token, LoginTokenInfo.class);
			if (tokenInfo != null) 
				return tokenInfo;
		}
		return null;
	}

	/**
	 * @Title: getTokenByMemberId
	 * @Description: 根据用户ID获取token
	 * @return: String
	 */
	public static String getTokenByMemberId(String memberId) {
		return CacheService.StringKey.getObject(memberId, String.class);
	}
	
	/**
	 * @Title: getMemberIdByToken
	 * @Description: 根据token获取用户ID
	 * @return: String
	 */
	public static String getMemberIdByToken(String token) {
		String memberId = null;
		LoginTokenInfo tokenInfo = CacheService.StringKey.getObject(token, LoginTokenInfo.class);
		if (null != tokenInfo)
			memberId = tokenInfo.getMemberId();
		return memberId;
	}
	
	/**
	 * @Title: getTokenSurplusExpire
	 * @Description: 获得token在Redis中剩余失效时间
	 * @return: long
	 */
	public static long getTokenSurplusExpire(String token) {
		return CacheService.KeyBase.getExprise(token);
	}
	
	/**
	 * @Title: resetTokenInfoExpire
	 * @Description: 重新设置token
	 * @return: void
	 */
	public static void resetTokenInfoExpire(String oldToken, LoginTokenInfo tokenInfo, int tokenSurplusExpire) {
		CacheService.StringKey.set(oldToken, tokenInfo, RedisRootNameSpace.UnitEnum.THIRTY_MIN);
	}
}
