package com.way.mobile.common.patchca;

import com.way.common.cache.RedisRootNameSpace;
import com.way.common.redis.CacheService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: RedisPatchcaStore
 * @Description: 使用redis存储验证码
 * @author: xinpei.xu
 * @date: 2017/08/17 20:52
 */
public class RedisPatchcaStore {
	private static Logger logger = LoggerFactory.getLogger(RedisPatchcaStore.class);
	
	/** 图片验证码保存redis中的key的header */
	private final static String REDIS_PATCHCA_CODE_HEADER = "patchca_id:";

	/**
	 * @Title: getImgCode
	 * @Description: 验证码保存到redis中
	 * @return: void
	 */
	public static void getImgCode(final HttpServletRequest request, final HttpServletResponse response, final String deviceNo) {
		String imgCode = PatchcaImageCode.getImgCode(request, response, deviceNo);
		// 验证码放在redis中
		CacheService.StringKey.set(REDIS_PATCHCA_CODE_HEADER + deviceNo, imgCode, RedisRootNameSpace.UnitEnum.FIFTEEN_MIN);
	}
	
	/**
	 * @Title: validateImgCode
	 * @Description: 校验图片验证码
	 * @return: boolean
	 */
	public static boolean validateImgCode(final String deviceNo, final String imgCode) {
		if (StringUtils.isEmpty(deviceNo) || StringUtils.isEmpty(imgCode)) {
			logger.error("参数为空，deviceNo："+deviceNo+",imgCode:"+imgCode);
			return false;
		}
		String code = CacheService.StringKey.getObject(REDIS_PATCHCA_CODE_HEADER + deviceNo, String.class);
		CacheService.KeyBase.delete(REDIS_PATCHCA_CODE_HEADER + deviceNo);
		return imgCode.equalsIgnoreCase(code);
	}
}
