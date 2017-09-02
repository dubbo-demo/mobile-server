package com.way.mobile.aop;

import com.way.common.log.WayLogger;
import com.way.common.util.IpUtil;
import com.way.mobile.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * INFO: mobile统一用户请求日志
 * User: zhaokai
 * Date: 2016/10-31
 * Time: 9:19
 * Version: 1.0
 * History: <p>如果有修改过程，请记录</P>
 */
public class RequestLogAop extends BaseController {


    /**
     * @param src 源字符串
     * @return 字符串，将src的第一个字母转换为大写，src为空时返回null
     */
    private static String change(String src) {
        if (src != null) {
            StringBuffer sb = new StringBuffer(src);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            return sb.toString();
        } else {
            return null;
        }
    }

    public Object loggerUserRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        try {

            StopWatch clock = new StopWatch();
            clock.start(); // 计时开始
            Object retVal = joinPoint.proceed();
            clock.stop(); // 计时结束

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (request == null) {
                return retVal;
            }

            String sessionId = null;
            HttpSession session = request.getSession(false);
            // 设备号
            String deviceNo = request.getHeader("deviceNo");
            if (StringUtils.isNotBlank(deviceNo)) {
                deviceNo = URLDecoder.decode(URLDecoder.decode(deviceNo, "utf-8"), "utf-8");
            }
            // 终端
            String client = request.getHeader("client");
            if (StringUtils.isNotBlank(client)) {
                client = URLDecoder.decode(URLDecoder.decode(client, "utf-8"), "utf-8");
            }
            // 渠道
            String channel = request.getHeader("channel");
            if (StringUtils.isNotBlank(channel)) {
                channel = URLDecoder.decode(URLDecoder.decode(channel, "utf-8"), "utf-8");
            }
            // app版本号
            String version = request.getHeader("version");
            if (StringUtils.isNotBlank(version)) {
                version = URLDecoder.decode(URLDecoder.decode(version, "utf-8"), "utf-8");
            }
            // app版本号序号
            String build = request.getHeader("build");
            if (StringUtils.isNotBlank(build)) {
                build = URLDecoder.decode(URLDecoder.decode(build, "utf-8"), "utf-8");
            }

            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.putAll(request.getParameterMap());
            if (session != null) {
                sessionId = session.getId();
            }


            String username = super.getCurrentUsername();
            //此方法返回的是一个数组，数组中包括request以及ActionCofig等类对象
            Object[] args = joinPoint.getArgs();
            StringBuffer classUrl = new StringBuffer(request.getRequestURI());
            StringBuffer classvalue = new StringBuffer();

            try {
                if (args != null && args.length > 0) {
                    for (Object object : args) {
                        if (object == null) {
                            continue;
                        }
                        Class clazz = object.getClass();// 获取集合中的对象类型

                        if (HttpServletRequest.class == clazz || javax.servlet.http.HttpServletResponse.class == clazz
                                || clazz.toString().indexOf("org.springframework") >= 0) {
                            continue;
                        }

                        //传递的参数列表
                        if (LinkedHashMap.class == clazz) {
                            paramsMap.putAll((Map) object);
                            continue;
                        }

                        if (clazz.getDeclaredConstructors() != null && clazz.getDeclaredConstructors().length > 0) {
                            classvalue.append(clazz.getDeclaredConstructors()[0].getName() + "=");
                        }

                        if (String.class == clazz || Long.class == clazz || Boolean.class == clazz
                                || Double.class == clazz || Integer.class == clazz || Short.class == clazz
                                || Float.class == clazz) {
                            classvalue.append(object + ",");
                        } else {

                            Field[] fds = clazz.getDeclaredFields();// 获取他的字段数组
                            if (fds != null && fds.length > 0) {
                                classvalue.append("{");
                                for (Field field : fds) {// 遍历该数组
                                    if (field == null) {
                                        continue;
                                    }
                                    try {
                                        String fdname = field.getName();// 得到字段名，
                                        Method metd = clazz.getMethod("get" + change(fdname), null);// 根据字段名找到对应的get方法，null表示无参数
                                        Object name = metd.invoke(object, null);// 调用该字段的get方法
                                        if (name != null) {
                                            classvalue.append(fdname + ":" + name + ",");
                                        }
                                    } catch (NoSuchMethodException e) {
                                    } catch (SecurityException e) {
                                    } catch (IllegalAccessException e) {
                                    } catch (IllegalArgumentException e) {
                                    } catch (InvocationTargetException e) {
                                    }
                                }
                                classvalue = new StringBuffer(classvalue.substring(0, classvalue.length() - 1)).append("},");
                            }
                        }
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                WayLogger.debug("error:====inner error=====" + e.getMessage());
            }

            if (classvalue != null && classvalue.length() > 0) {
                classvalue = new StringBuffer(classvalue.substring(0, classvalue.length() - 1));
            }

            String requestIpAddr = IpUtil.getIpAddr(request);

            StringBuffer logStr = new StringBuffer(username + "|" + requestIpAddr + "|" + sessionId + "|"
                    + deviceNo + "|" + client + "|" + channel + "|" + version + "|" + build);
            logStr.append("|").append(classUrl).append("|").append(classvalue).append("|").append(clock.getTotalTimeMillis());

            WayLogger.debug(logStr.toString());
            return retVal;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
