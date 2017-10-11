package com.way.mobile.redis;

import java.io.Serializable;

/**
 * 客户端版本服务<br>
 * 〈功能详细描述〉
 *
 * @author xinpei.xu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class InvokeMsg implements Serializable {

    private static final long serialVersionUID = -8862123804553584278L;

    /**
     * spring bean name
     */
    private String beanName;

    /**
     * 调用方法
     */
    private String methodName;

    /**
     * 参数
     */
    private Object[] params;

    /**
     * 参数类
     */
    private Class[] paramClasses;

    public InvokeMsg() {
        super();
    }

    public InvokeMsg(String beanName, String methodName, Object[] params) {
        this.beanName = beanName;
        this.methodName = methodName;
        this.params = params;
    }

    public Class[] getParamClasses() {
        return paramClasses;
    }

    public void setParamClasses(Class[] paramClasses) {
        this.paramClasses = paramClasses;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
