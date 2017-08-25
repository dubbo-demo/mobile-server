package com.way.mobile.aop;

import com.way.common.exception.DataValidateException;
import com.way.common.log.WayLogger;
import com.way.common.result.AjaxResult;
import com.way.common.util.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * INFO: 开发监控： 显示服务调用时间
 * User: zhaokai
 * Date: 2016/10-31
 * Time: 9:19
 * Version: 1.0
 * History: <p>如果有修改过程，请记录</P>
 */
public class ApiAround {

    public Object handler(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch clock = new StopWatch();
        clock.start(); // 计时开始
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (httpServletRequest == null) {
            return joinPoint.proceed();
        }
        // 拦截的实体类
        Object target = joinPoint.getTarget();
        String targetName = target.getClass().getName();
        // 拦截的方法名称
        String methodName = joinPoint.getSignature().getName();
        //拦截的参数
        Object[] args = joinPoint.getArgs();
        try {
            WayLogger.info("request=>{}.{}", targetName, methodName);
            Object retVal = joinPoint.proceed();
            clock.stop(); // 计时结束
            WayLogger.info("response=>{}.{} params=>{} time=>{}ms", targetName, methodName, args, clock.getTotalTimeMillis());
            return retVal;
        } catch (DataValidateException d) {
            clock.stop(); // 计时结束
            WayLogger.error("response=>{}.{} params=>{} time=>{}ms error=>{}", targetName, methodName, args,
                    clock.getTotalTimeMillis(), ExceptionUtils.exceptionToString(d));
            return AjaxResult.failed(d.getMessage());
        } catch (Exception e) {
            clock.stop(); // 计时结束
            WayLogger.error("response=>{}.{} params=>{} time=>{}ms error=>{}", targetName, methodName, args,
                    clock.getTotalTimeMillis(), ExceptionUtils.exceptionToString(e));
            WayLogger.error(e.getMessage(), e);
            return AjaxResult.failed(e.getMessage());
        }
    }

}
