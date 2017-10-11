package com.way.mobile.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * 客户端版本服务<br>
 * 〈功能详细描述〉
 *
 * @author xinpei.xu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
public class InvokeService extends ApplicationObjectSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvokeService.class);

    /**
     * 执行对象
     *
     * @param msg 远程调用消息对象
     */
    public Object execute(InvokeMsg msg) {
        Object obj = getApplicationContext().getBean(msg.getBeanName());
        if (msg.getParamClasses() == null) {
            return invoke(obj, msg.getMethodName(), msg.getParams());
        } else {
            return invoke(obj.getClass(), obj, msg.getMethodName(), msg.getParamClasses(), msg.getParams());
        }
    }

    /**
     * Invokes accessible method of an object.
     *
     * @param c class that contains method
     * @param obj object to execute
     * @param method method to invoke
     * @param paramClasses classes of parameters
     * @param params parameters
     */
    private static Object invoke(Class c, Object obj, String method, Class[] paramClasses, Object[] params) {
        Method m;
        try {
            m = c.getMethod(method, paramClasses);
            return m.invoke(obj, params);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Invokes accessible method of an object without specifying parameter types.
     *
     * @param obj object
     * @param method method of an object
     * @param params method parameters
     */
    public static Object invoke(Object obj, String method, Object[] params) {
        Class[] paramClass = getClasses(params);
        return invoke(obj.getClass(), obj, method, paramClass, params);
    }

    /**
     * Returns classes from array of specified objects.
     */
    private static Class[] getClasses(Object... objects) {
        if (objects == null) {
            return new Class[0];
        }
        Class[] result = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                result[i] = objects[i].getClass();
            }
        }
        return result;
    }
}
