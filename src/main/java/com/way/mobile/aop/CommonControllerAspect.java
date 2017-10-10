package com.way.mobile.aop;

import com.way.common.log.WayLogger;
import com.way.common.redis.utils.NoShardedRedisCacheUtil;
import com.way.common.result.ServiceResult;
import com.way.common.spring.Configuration;
import com.way.common.util.Validater;
import com.way.mobile.common.po.LoginTokenInfo;
import com.way.mobile.common.util.PropertyConfig;
import com.way.mobile.common.util.TokenJedisUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述：通用切面
 * 			目前支持判断用户是否登陆以及版本升级
 *
 * @ClassName CommonControllerAspect
 * @Author：xinpei.xu
 * @Date：2017/08/17 15:06
 */
@Component
@Aspect
public class CommonControllerAspect {

    @Autowired
    private PropertyConfig config;

    @Autowired
    public NoShardedRedisCacheUtil noShardedRedisCacheUtil;

//    @Autowired
//    private VersionConfService versionConfService;

    private static Configuration configuration;

    public static final String expression = "execution(* com.way.mobile.*.controller.*.*(..)) and within(@org.springframework.stereotype.Controller *)";

    @PostConstruct
    public static void loadCodeDef() {
        configuration = new Configuration("validationMessages.properties");
		WayLogger.debug("加载与app交互的编码及说明:" + configuration.getProperties());
    }

    private static final String TIPMESSAGE = "您的请求正在处理中，请稍后重试";

    @Pointcut(expression)
    public void pointCutController() {
    }

    @Around("pointCutController()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object[] params = pjp.getArgs();
        String newToken = null;
        String memberId = null;
        HttpServletRequest paramRequest = null;
        if (null != params && params.length > 0) {
        	// 文件上传相关请求
            if (params[0] instanceof MultipartHttpServletRequest) {
//                Map<String, String> map = new HashMap<>();
                MultipartHttpServletRequest mulReq = (DefaultMultipartHttpServletRequest) params[0];
                // 请求对象参数
                paramRequest = mulReq;
                // 获取token
                newToken = mulReq.getParameter("token");
                if (null == newToken) {
					WayLogger.debug("token是空");

//                    map.put(IResponseCode.RETURN_CODE_STR, IResponseCode.TOKEN_EXPIRED_OVERTIME);
//                    map.put(IResponseCode.RETURN_INFO_STR, );
                    return ServiceResult.newFailure(4, "账号已过期，请重新登录");
                }

                if (newToken.length() < 30) {
					WayLogger.debug("token是空");
//                    map.put(IResponseCode.RETURN_CODE_STR, IResponseCode.FAIL);
//                    map.put(IResponseCode.RETURN_INFO_STR, "无效的token");
                    return ServiceResult.newFailure(1, "无效的token");
                }
                // 验证token
                LoginTokenInfo tokenInfo = TokenJedisUtils.getTokenInfo(newToken);
                if (tokenInfo != null) {
                    if (tokenInfo.getStatus() == 1) {
//                        map.put(IResponseCode.RETURN_CODE_STR, IResponseCode.TOKEN_EXPIRED_OTHERLOGIN);
//						map.put(IResponseCode.RETURN_INFO_STR, "该账户已在其他设备登录，请注意安全");
                        return ServiceResult.newFailure(2, "该账户已在其他设备登录，请注意安全");
                    }
                } else {
//                    map.put(IResponseCode.RETURN_CODE_STR, IResponseCode.TOKEN_EXPIRED_OVERTIME);
//                    map.put(IResponseCode.RETURN_INFO_STR, "账号已过期，请重新登录");
                    return ServiceResult.newFailure(4, "账号已过期，请重新登录");
                }
                memberId = tokenInfo.getMemberId();
                mulReq.setAttribute("memberId", memberId);
                // 校验文件大小
				ServiceResult serviceResult = verificationFile(mulReq);
                if(StringUtils.equals(map.get(IResponseCode.RETURN_CODE_STR),IResponseCode.FAIL)){
                	 return serviceResult;
                }
            } else if (params[0] instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) params[0];
                // 请求对象参数
                paramRequest = request;
                // 获取token
                newToken = request.getParameter("token");
                if (null != newToken) {
                    memberId = TokenJedisUtils.getMemberIdByToken(newToken);
                }
            }

            if (null != newToken && null != memberId) {
                TokenJedisUtils.expireTokenInfo(newToken, memberId, config.getTokenExpire());
            }
        }
        
		if (paramRequest != null) {
			// 兼容老版本
			String version = paramRequest.getHeader("version");
			if (StringUtils.isNotEmpty(version)) {
				version = version.replace(".", "");
				boolean isIntercept = false;
				if (Validater.isNumber(version)
						&& StringUtils.equals(paramRequest.getHeader("platform"), IConstantsConfig.PLATFORM_INFO_MAIYA)
						&& Integer.valueOf(version) >= 235) {
					isIntercept = true;
				}
				if (Validater.isNumber(version) && StringUtils.equals(paramRequest.getHeader("platform"),
						IConstantsConfig.PLATFORM_INFO_MAIYAFQ) && Integer.valueOf(version) > 10) {
					isIntercept = true;
				}
				if (StringUtils.equals(paramRequest.getHeader("platform"), IConstantsConfig.PLATFORM_INFO_ANXINHUA)) {
					isIntercept = true;
				}
				if (isIntercept) {
					// 校验版本升级
//					Map<String, Object> tmpMap = versionUpgrade(paramRequest);
					Map<String, Object> tmpMap = null;
					if (tmpMap != null && !tmpMap.isEmpty()) {
						return tmpMap;
					}
				}
			}
		}

        // 标识 当前请求是否需要防重 过滤
        boolean isMatch = false;
        // 判断当前请求是否 防重黑名单限制重复请求
        String methodName = pjp.getSignature().getName();
        Object value = PropertyConfigurerWithBlack.getProperty(methodName);
        if (null != value) {
            isMatch = true;
        }


        String limitKey = "";
        // 标识 当前请求是否被过滤了 true：过滤  false：没有
        boolean isLimit = false;
        try {
            if (isMatch) {
                // 步骤redis的key
                limitKey = getLimitRedisKey(pjp, params, memberId);
                Object resObj = reSubmitLimit(pjp, limitKey);
                if (null != resObj) {
                    isLimit = true;
                    return resObj;
                }
            }
        } finally {
            // 改成有 第一个进入方法的拥有者来完成 清除key的动作
            if (isMatch && !isLimit) {
                noShardedRedisCacheUtil.del(limitKey);
            }
        }
    }
    
    
    /**
     * 功能描述: 版本升级<br>
     * 〈功能详细描述〉
     *
     * @param request
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
//	private Map<String, Object> versionUpgrade(HttpServletRequest request) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		// 当前客户端版本
//		String curVersion = request.getHeader("version");
//		// 终端系统 1：IOS 2：Android
//		String client = request.getHeader("client");
//		// 平台标识
//		String platform = request.getHeader("platform");
//		// 若是非强制升级，已提示，客户端本地缓存该标识，用于接口端判断是否需要再提示非强制升级
//		String nonForceFlag = request.getHeader("nonForceFlag");
//
//		if (StringUtils.isNotEmpty(curVersion) && StringUtils.isNotEmpty(client)) {
//			try {
//				// 从ehcache中获取版本配置信息
//				List<Map<String, Object>> confs = versionConfService.getIosVersionConf();
//				if (StringUtils.equals(NumberConstants.STR_TWO, client)) {
//					confs = versionConfService.getAndroidVersionConf();
//				}
//				// 有版本配置
//				if (null != confs && !confs.isEmpty()) {
//					// 1.获取强制升级的最新版本
//					VersionUpgrade forceConf = null;
//					VersionUpgrade newConf = null;
//					for (Map<String, Object> conf : confs) {
//						VersionUpgrade confVesion = (VersionUpgrade) BeanUtil.mapToObject(conf, VersionUpgrade.class);
//						if (StringUtils.equals(confVesion.getSplatform(), platform) && newConf == null) {
//							// 最新版本配置信息
//							newConf = confVesion;
//						}
//						if (confVesion.getbIsEnUpgrade() == NumberConstants.NUM_ONE
//								&& StringUtils.equals(confVesion.getSplatform(), platform)) {
//							forceConf = confVesion;
//							break;
//						}
//					}
//					// 当前版本配置
//					int curVerionValue = Integer.parseInt(curVersion.replaceAll("\\.", ""));
//					if (null != forceConf) {
//						int compareValue = Integer.parseInt(forceConf.getsVersionNo().replaceAll("\\.", ""));
//						// 当前版本低于强制版本，则升级
//						if (curVerionValue < compareValue) {
//							Map<String, String> msg = new HashMap<String, String>();
//							result.put(IResponseCode.RETURN_CODE_STR, "-9999");
//							msg.put("isupdate", NumberConstants.STR_ONE);
//							msg.put("ismandatory", StringUtils.EMPTY + newConf.getbIsEnUpgrade());
//							msg.put("comment", newConf.getsComment());
//							msg.put("appurl", newConf.getsDownloadAddr());
//							result.put(IResponseCode.RETURN_INFO_STR, JSON.toJSONString(msg));
//							return result;
//						}
//					}
//					// 当前版本已是最新强制升级版本，取版本列表中最新一条版本信息，根据非强制升级标识判断是否升级
//					if (StringUtils.isEmpty(nonForceFlag) && newConf != null) {
//						int compareValue = Integer.parseInt(newConf.getsVersionNo().replaceAll("\\.", ""));
//						if (curVerionValue < compareValue) {
//							Map<String, String> msg = new HashMap<String, String>();
//							result.put(IResponseCode.RETURN_CODE_STR, "-8888");
//							msg.put("isupdate", NumberConstants.STR_ONE);
//							msg.put("ismandatory", StringUtils.EMPTY + newConf.getbIsEnUpgrade());
//							msg.put("comment", newConf.getsComment());
//							msg.put("appurl", newConf.getsDownloadAddr());
//							result.put(IResponseCode.RETURN_INFO_STR, JSON.toJSONString(msg));
//							return result;
//						}
//					}
//				}
//			} catch (Exception e) {
//				WayLogger.error("CommonControllerAspect -> versionUpgrade", e.getMessage());
//			}
//		}
//		return result;
//	}
    
	private Map<String, String> verificationFile(MultipartHttpServletRequest mulReq) {
		Map<String, String> map = new HashMap<String, String>();
		Map<String, MultipartFile> fileMap = mulReq.getFileMap();
		// 遍历所有的上传文件
		for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
			MultipartFile multipartFile = entry.getValue();
			// 校验文件格式
			if (!multipartFile.getContentType().contains("image/png")
					&& !multipartFile.getContentType().contains("image/jpg")
					&& !multipartFile.getContentType().contains("image/jpeg")
					&& !multipartFile.getContentType().contains("video/mp4")) {
				map.put(IResponseCode.RETURN_CODE_STR, IResponseCode.FAIL);
		        map.put(IResponseCode.RETURN_INFO_STR, "上传文件格式需要是png,jpg,jpeg,video");
		        return map;
			}
			String key = noShardedRedisCacheUtil.key(RedisConstants.SYS_PARAM_CONFIG, IModuleParamConfig.MYLOAN_MOBILE,
					IModuleParamConfig.MYLOAN_MOBILE_MAX_FILE_SIZE);
			String fileSize = noShardedRedisCacheUtil.get(key);
			// 校验文件大小
			if (StringUtils.isNotBlank(fileSize) && multipartFile.getSize() > Double.valueOf(fileSize) * 1024) {
				map.put(IResponseCode.RETURN_CODE_STR, IResponseCode.FAIL);
		        map.put(IResponseCode.RETURN_INFO_STR, "上传图片大小请控制在" + Double.valueOf(fileSize) + "KB以内");
		        return map;
			}
		}
		return map;
	}

    /**
     * 防止重复提交
     *
     * @param pjp
     * @param key
     * @return
     */
    private Object reSubmitLimit(ProceedingJoinPoint pjp, String key) {

        String signature = pjp.getSignature().toLongString();
        String returnType = signature.split(" ")[1];

        long remainTime = noShardedRedisCacheUtil.ttl(key);
        if (remainTime > 0) {
            return retunrProcessObj(returnType, TIPMESSAGE);
        } else {
            //noShardedRedisCacheUtil.setex(key, methodName, config.getReqMethodIntervalUnit());
            //改成 setnx方式
            boolean setNxResult = noShardedRedisCacheUtil.setNewnx(key, "LOCK", config.getReqMethodIntervalUnit());
            if (!setNxResult) {
                return retunrProcessObj(returnType, TIPMESSAGE);
            }
        }
        return null;
    }

    /**
     * 根据参数 拼装KEY
     *
     * @param pjp
     * @param params
     * @param memberId
     * @return
     */
    private String getLimitRedisKey(ProceedingJoinPoint pjp, Object[] params, String memberId) {
        String deviceNo = "";
        String methodName = pjp.getSignature().getName();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        //memberid找不到 尝试获取请求头 deviceNo
        if (StringUtils.isBlank(memberId)) {
            deviceNo = request.getHeader("deviceNo");
        }

        //要防止一些特殊接口可能出现memberId 和deviceNo 都不存在的情况， 要规避这样的情况
        if (StringUtils.isBlank(memberId) && StringUtils.isBlank(deviceNo)) {
            String queryStr = request.getQueryString();
            if (StringUtils.isNotBlank(queryStr)) {
                memberId = String.valueOf(request.getQueryString().hashCode());
            } else {//随机 通常这个不会被执行  只是做冗余
                memberId = RandomStringUtils.randomNumeric(8);
            }
        }
        // 步骤redis的key
        String key = noShardedRedisCacheUtil.key(Constant.REQUEST_BLACKLIST + methodName,
                memberId, deviceNo);
        return key;
    }

    private Object retunrProcessObj(String returnType, String tipMsg) {
        Object resObj = null;
        if (returnType.contains("BaseResponse")) {
            resObj = new BaseResponse(IResponseCode.FAIL, tipMsg);
        } else if (returnType.contains("ResponseInfoBase")) {
            resObj = new ResponseInfoBase(IResponseCode.FAIL, tipMsg);
        } else if (returnType.contains("Map")) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put(IResponseCode.RETURN_CODE_STR, IResponseCode.FAIL);
            resultMap.put(IResponseCode.RETURN_INFO_STR, tipMsg);
            resObj = resultMap;
        } else if (returnType.contains("ResultParams")) {
            resObj = new ResultParams(IResponseCode.FAIL, tipMsg);
        }
        return resObj;

    }
    
	@SuppressWarnings("unchecked")
	private ResultParams getReturnCode(Object result) {
		ResultParams resultParam = new ResultParams();
		if (result instanceof BaseResponse) {
			BeanUtils.copyProperties(result, resultParam);
			String msg = getMsg(resultParam.getRetcode(), resultParam.getRetinfo());
			if (StringUtils.isNotEmpty(msg)) {
				resultParam.setRetcode(IResponseCode.FAIL);
				resultParam.setRetinfo(msg);
			}
		} else if (result instanceof ResponseInfoBase) {
			BeanUtils.copyProperties(result, resultParam);
			String msg = getMsg(resultParam.getRetcode(), resultParam.getRetinfo());
			if (StringUtils.isNotEmpty(msg)) {
				resultParam.setRetcode(IResponseCode.FAIL);
				resultParam.setRetinfo(msg);
			}
		} else if (result instanceof Map) {
			Map<String, Object> param = (Map<String, Object>) result;
			resultParam.setRetcode(String.valueOf(param.get(IResponseCode.RETURN_CODE_STR)));
			resultParam.setRetinfo(String.valueOf(param.get(IResponseCode.RETURN_INFO_STR)));
			String msg = getMsg(String.valueOf(param.get(IResponseCode.RETURN_CODE_STR)),
					String.valueOf(param.get(IResponseCode.RETURN_INFO_STR)));
			if (StringUtils.isNotEmpty(msg)) {
				resultParam.setRetcode(IResponseCode.FAIL);
				resultParam.setRetinfo(msg);
			}
		} else if (result instanceof ResultParams) {
			BeanUtils.copyProperties(result, resultParam);
			String msg = getMsg(resultParam.getRetcode(), resultParam.getRetinfo());
			if (StringUtils.isNotEmpty(msg)) {
				resultParam.setRetcode(IResponseCode.FAIL);
				resultParam.setRetinfo(msg);
			}
		}
		return resultParam;
	}

    @Before("pointCutController()")
    public void doBefore(JoinPoint jp) {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @AfterReturning(pointcut = "pointCutController()", returning = "result")
    public void doReturn(Object result) {
        if (result instanceof BaseResponse) {
            BaseResponse param = (BaseResponse) result;
            String msg = getMsg(param.getRetcode(),param.getRetinfo());
            if (StringUtils.isNotEmpty(msg)) {
                param.setRetcode(IResponseCode.FAIL);
                param.setRetinfo(msg);
            }
        } else if (result instanceof ResponseInfoBase) {
            ResponseInfoBase param = (ResponseInfoBase) result;
            String msg = getMsg(param.getRetcode(),param.getRetinfo());
            if (StringUtils.isNotEmpty(msg)) {
                param.setRetcode(IResponseCode.FAIL);
                param.setRetinfo(msg);
            }
        } else if (result instanceof Map) {
            Map param = (Map) result;
            String msg = getMsg(String.valueOf(param.get(IResponseCode.RETURN_CODE_STR)),
            		String.valueOf(param.get(IResponseCode.RETURN_INFO_STR)));
            if (StringUtils.isNotEmpty(msg)) {
                param.put(IResponseCode.RETURN_CODE_STR, IResponseCode.FAIL);
                param.put(IResponseCode.RETURN_INFO_STR, msg);
            }
        } else if (result instanceof ResultParams) {
            ResultParams param = (ResultParams) result;
            String msg = getMsg(param.getRetcode(),param.getRetinfo());
            if (StringUtils.isNotEmpty(msg)) {
                param.setRetcode(IResponseCode.FAIL);
                param.setRetinfo(msg);
            }
        }
		WayLogger.debug("【CommonControllerAspect】转换后的返回消息体:" + result);
    }

    /**
     * 获取返回码对应的说明内容
     *
     * @param code
     * @return
     */
    private String getMsg(String code,String message) {
        String msg = null;
        try {
            /**
             * 如果能转换成数字，说明不需要转换；否则需要找出对应的返回消息
             */
            Integer.parseInt(code);
        } catch (Exception e) {
            msg = configuration.getValue(code);
            // 为兼容老版本去除JXL0000手机认证成功的码
			if (StringUtils.isEmpty(msg) && !"JXL0000".equals(code) && !"JXL1000".equals(code)
					 && !"JXL0001".equals(code)) {
				/**
				 * 没有找到对应的错误码-提示语直接透传
				 */
				msg = message;
				WayLogger.debug("【CommonControllerAspect】【getMsg】未找到对应的错误码:" + code);
			}
        }
        return msg;

    }

    @After("pointCutController()")
    public void doAfter(JoinPoint jp) {
    }

    @AfterThrowing(pointcut = "pointCutController()", throwing = "ex")
    public void doThrowing(Exception ex) {
    }

    /**
     * @Title: processResult
     * @Description: 处理最终返回结果
     * @return: Object
     */
    @SuppressWarnings("unused")
    private Object processResult(Object object) {
        return object;
    }
}
