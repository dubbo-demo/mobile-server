package com.way.mobile.aop;

import com.way.base.versionUpdate.dto.VersionUpdateDto;
import com.way.common.constant.Constants;
import com.way.common.constant.IModuleParamConfig;
import com.way.common.constant.NumberConstants;
import com.way.common.constant.RedisConstants;
import com.way.common.log.WayLogger;
import com.way.common.redis.utils.NoShardedRedisCacheUtil;
import com.way.common.result.ServiceResult;
import com.way.common.spring.Configuration;
import com.way.common.util.Validater;
import com.way.member.member.dto.MemberDto;
import com.way.mobile.common.po.LoginTokenInfo;
import com.way.mobile.common.util.PropertyConfig;
import com.way.mobile.common.util.TokenJedisUtils;
import com.way.mobile.ehcache.service.VersionConfService;
import com.way.mobile.property.config.PropertyConfigurerWithWhite;
import com.way.mobile.service.member.MemberService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private VersionConfService versionConfService;

    @Autowired
    private MemberService memberService;

    private static Configuration configuration;

    public static final String expression = "execution(* com.way.mobile.controller..*.*(..)) and within(@org.springframework.stereotype.Controller *)";

    @PostConstruct
    public static void loadCodeDef() {
        configuration = new Configuration("config/validationMessages.properties");
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
        String invitationCode = null;
        HttpServletRequest paramRequest = null;
        String pathInfoUrl = null;
        if (null != params && params.length > 0) {
            if (params[0] instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) params[0];
                // 请求对象参数
                paramRequest = request;
                // 获取token
                newToken = paramRequest.getParameter("token");
                // 获取链接数组
                String[] pathArr = paramRequest.getRequestURI().split("/");
                // 访问链接
                pathInfoUrl = pathArr[pathArr.length - 1];
                WayLogger.debug("校验是否为白名单请求.........普通参数请求");
                Object o = PropertyConfigurerWithWhite.getProperty(pathInfoUrl);
                WayLogger.debug("token:" + newToken + ",pathInfoUrl:" + pathInfoUrl);
                // 不在白名单中的请求需要token
                if(null == o){
                    if (null == newToken) {
                        WayLogger.debug("token是空");
                        return ServiceResult.newFailure(Constants.INVALID, "token不能为空");
                    }

                    if (newToken.length() < 30) {
                        WayLogger.debug("无效的token");
                        return ServiceResult.newFailure(Constants.INVALID, "无效的token");
                    }
                    // 验证token
                    LoginTokenInfo tokenInfo = TokenJedisUtils.getTokenInfo(newToken);
                    if(tokenInfo == null){
                        return ServiceResult.newFailure(Constants.TOKEN_EXPIRED_OVERTIME, "账号已过期，请重新登录");
                    }
                    if(tokenInfo.getStatus() == Constants.INVALID){
                        return ServiceResult.newFailure(Constants.OKEN_EXPIRED_OTHERLOGIN, "该账户已在其他设备登录，请注意安全");
                    }
                    paramRequest.setAttribute("invitationCode", tokenInfo.getInvitationCode());
                    invitationCode = tokenInfo.getInvitationCode();
                }
            // 文件上传相关请求
            } else if (params[1] instanceof MultipartHttpServletRequest) {
                MultipartHttpServletRequest mulReq = (DefaultMultipartHttpServletRequest) params[1];
                // 请求对象参数
                paramRequest = mulReq;
                // 获取链接数组
                String[] pathArr = paramRequest.getRequestURI().split("/");
                // 访问链接
                pathInfoUrl = pathArr[pathArr.length - 1];
                // 获取token
                newToken = mulReq.getParameter("token");
                if (null == newToken) {
                    WayLogger.debug("token是空");
                    return ServiceResult.newFailure(Constants.INVALID, "token不能为空");
                }

                if (newToken.length() < 30) {
                    WayLogger.debug("无效的token");
                    return ServiceResult.newFailure(Constants.INVALID, "无效的token");
                }
                // 验证token
                LoginTokenInfo tokenInfo = TokenJedisUtils.getTokenInfo(newToken);
                if(tokenInfo == null){
                    return ServiceResult.newFailure(Constants.TOKEN_EXPIRED_OVERTIME, "账号已过期，请重新登录");
                }
                if(tokenInfo.getStatus() == Constants.INVALID){
                    return ServiceResult.newFailure(Constants.OKEN_EXPIRED_OTHERLOGIN, "该账户已在其他设备登录，请注意安全");
                }
                mulReq.setAttribute("invitationCode", tokenInfo.getInvitationCode());
                invitationCode = tokenInfo.getInvitationCode();
                // 校验文件大小
//                ServiceResult serviceResult = verificationFile(mulReq);
//                if(Constants.INVALID == serviceResult.getCode()){
//                    return serviceResult;
//                }
            }
//            if (null != newToken && null != memberId) {
//                TokenJedisUtils.expireTokenInfo(newToken, memberId);
//            }
        }
        
		if (paramRequest != null) {
            // 版本升级
			String version = paramRequest.getHeader("version");
			if (StringUtils.isNotBlank(version)) {
				version = version.replace(".", "");
				boolean isIntercept = false;
				if (Validater.isNumber(version)) {
					isIntercept = true;
				}
				if (isIntercept) {
					// 校验版本升级
                    ServiceResult serviceResult = versionUpgrade(paramRequest);
					if (null != serviceResult.getData()) {
						return serviceResult;
					}
				}
			}
			// 判断用户会员
            if(invitationCode != null && (!"buyMemberByRewardScore.do".equals(pathInfoUrl) && !"buyMemberByRecharge.do".equals(pathInfoUrl)
                && !"getMemberInfo.do".equals(pathInfoUrl) &&!"getOrderNumber.do".equals(pathInfoUrl))){
                // 查询用户信息
                ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(invitationCode);
                Date date = new Date();
                // 判断用户是否为会员
                if("1".equals(memberDto.getData().getMemberType())  || date.before(memberDto.getData().getMemberStartTime())
                        || date.after(memberDto.getData().getMemberEndTime())){
                    return ServiceResult.newFailure(Constants.MEMBERSHIP_EXPIRES, "您还不是会员，请先购买会员");
                }
            }
		}

        // 标识 当前请求是否需要防重 过滤
        boolean isMatch = false;
        // 判断当前请求是否 防重黑名单限制重复请求
        String methodName = pjp.getSignature().getName();
        Object value = PropertyConfigurerWithWhite.getProperty(methodName);
        if (null != value) {
            isMatch = true;
        }

        String limitKey = "";
        // 标识 当前请求是否被过滤了 true：过滤  false：没有
        boolean isLimit = false;
        try {
            if (isMatch) {
                // 步骤redis的key
                limitKey = getLimitRedisKey(pjp, params, invitationCode);
                ServiceResult serviceResult = reSubmitLimit(pjp, limitKey);
                if (null != serviceResult.getData()) {
                    isLimit = true;
                    return serviceResult;
                }
            }
            return pjp.proceed();
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
	private ServiceResult versionUpgrade(HttpServletRequest request) {
        ServiceResult serviceResult = ServiceResult.newSuccess();
		// 当前客户端版本
		String curVersion = request.getHeader("version");
		// 终端系统 1：IOS 2：Android
		String client = request.getHeader("client");
		// 若是非强制升级，已提示，客户端本地缓存该标识，用于接口端判断是否需要再提示非强制升级
		String noForceFlag = request.getHeader("noForceFlag");

		if (StringUtils.isNotEmpty(curVersion) && StringUtils.isNotEmpty(client)) {
			try {
				// 从ehcache中获取版本配置信息
				List<VersionUpdateDto> confs = versionConfService.getIosVersionConf();
				if (StringUtils.equals(NumberConstants.STR_TWO, client)) {
					confs = versionConfService.getAndroidVersionConf();
				}
				// 有版本配置
				if (null != confs && !confs.isEmpty()) {
					// 1.获取强制升级的最新版本
                    VersionUpdateDto forceConf = null;
                    VersionUpdateDto newConf = null;
					for (VersionUpdateDto confVesion : confs) {
						if (newConf == null) {
							// 最新版本配置信息
							newConf = confVesion;
						}
						if (confVesion.getMandatory() == NumberConstants.NUM_ONE) {
							forceConf = confVesion;
							break;
						}
					}
					// 当前版本配置
					int curVerionValue = Integer.parseInt(curVersion.replaceAll("\\.", ""));
					if (null != forceConf) {
						int compareValue = Integer.parseInt(forceConf.getVersionNo().replaceAll("\\.", ""));
						// 当前版本低于强制版本，则升级
						if (curVerionValue < compareValue) {
							Map<String, String> msg = new HashMap<String, String>();
                            serviceResult.setCode(Constants.VERSION_MANDATORY_UPGRADE);
							msg.put("isUpdate", NumberConstants.STR_ONE);
							msg.put("mandatory", StringUtils.EMPTY + newConf.getMandatory());
							msg.put("comment", newConf.getComment());
							msg.put("downLoadAddr", newConf.getDownLoadAddr());
                            serviceResult.setData(msg);
							return serviceResult;
						}
					}
					// 当前版本已是最新强制升级版本，取版本列表中最新一条版本信息，根据非强制升级标识判断是否升级
					if (StringUtils.isEmpty(noForceFlag) && newConf != null) {
						int compareValue = Integer.parseInt(newConf.getVersionNo().replaceAll("\\.", ""));
						if (curVerionValue < compareValue) {
							Map<String, String> msg = new HashMap<String, String>();
                            serviceResult.setCode(Constants.VERSION_UPGRADE);
							msg.put("isUpdate", NumberConstants.STR_ONE);
							msg.put("mandatory", StringUtils.EMPTY + newConf.getMandatory());
							msg.put("comment", newConf.getComment());
							msg.put("downLoadAddr", newConf.getDownLoadAddr());
                            serviceResult.setData(msg);
                            return serviceResult;
						}
					}
				}
			} catch (Exception e) {
				WayLogger.error("CommonControllerAspect -> versionUpgrade", e.getMessage());
			}
		}
		return serviceResult;
	}
    
	private ServiceResult verificationFile(MultipartHttpServletRequest mulReq) {
		Map<String, MultipartFile> fileMap = mulReq.getFileMap();
		// 遍历所有的上传文件
		for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
			MultipartFile multipartFile = entry.getValue();
			// 校验文件格式
			if (!multipartFile.getContentType().contains("image/png")
					&& !multipartFile.getContentType().contains("image/jpg")
					&& !multipartFile.getContentType().contains("image/jpeg")
					&& !multipartFile.getContentType().contains("video/mp4")) {
		        return ServiceResult.newFailure(Constants.INVALID, "上传文件格式需要是png,jpg,jpeg,video");
			}
			String key = noShardedRedisCacheUtil.key(RedisConstants.SYS_PARAM_CONFIG, IModuleParamConfig.WAY_MEMBER,
					IModuleParamConfig.WAY_MOBILE_MAX_FILE_SIZE);
			String fileSize = noShardedRedisCacheUtil.get(key);
			// 校验文件大小
			if (StringUtils.isNotBlank(fileSize) && multipartFile.getSize() > Double.valueOf(fileSize) * 1024) {
		        return ServiceResult.newFailure(Constants.INVALID, "上传图片大小请控制在" + Double.valueOf(fileSize) + "KB以内");
			}
		}
		return ServiceResult.newSuccess();
	}

    /**
     * 防止重复提交
     *
     * @param pjp
     * @param key
     * @return
     */
    private ServiceResult reSubmitLimit(ProceedingJoinPoint pjp, String key) {

        String signature = pjp.getSignature().toLongString();
        String returnType = signature.split(" ")[1];

        long remainTime = noShardedRedisCacheUtil.ttl(key);
        if (remainTime > 0) {
            return retunrProcessObj(returnType, TIPMESSAGE);
        } else {
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
     * @param invitationCode
     * @return
     */
    private String getLimitRedisKey(ProceedingJoinPoint pjp, Object[] params, String invitationCode) {
        String deviceNo = "";
        String methodName = pjp.getSignature().getName();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        //memberid找不到 尝试获取请求头 deviceNo
        if (StringUtils.isBlank(invitationCode)) {
            deviceNo = request.getHeader("deviceNo");
        }

        //要防止一些特殊接口可能出现memberId 和deviceNo 都不存在的情况， 要规避这样的情况
        if (StringUtils.isBlank(invitationCode) && StringUtils.isBlank(deviceNo)) {
            String queryStr = request.getQueryString();
            if (StringUtils.isNotBlank(queryStr)) {
                invitationCode = String.valueOf(request.getQueryString().hashCode());
            } else {//随机 通常这个不会被执行  只是做冗余
                invitationCode = RandomStringUtils.randomNumeric(8);
            }
        }
        // 步骤redis的key
        String key = noShardedRedisCacheUtil.key(RedisConstants.REQUEST_BLACKLIST + methodName, invitationCode, deviceNo);
        return key;
    }

    private ServiceResult retunrProcessObj(String returnType, String tipMsg) {
        ServiceResult serviceResult = null;
        if(returnType.contains("ServiceResult")){
            serviceResult = ServiceResult.newFailure(tipMsg);
        }
        return serviceResult;

    }
    
    @Before("pointCutController()")
    public void doBefore(JoinPoint jp) {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @AfterReturning(pointcut = "pointCutController()", returning = "result")
    public void doReturn(Object result) {
        if(result instanceof ServiceResult){
            ServiceResult param = (ServiceResult) result;
            String msg = getMsg(param.getCode().toString(), param.getMessage());
            if (StringUtils.isNotBlank(msg)) {
                param.setCode(Constants.INVALID);
                param.setMessage(msg);
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
			if (StringUtils.isBlank(msg)) {
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
    private Object processResult(Object object) {
        return object;
    }
}
