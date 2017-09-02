/**
 * Copyright (C), 2012-2016, 江苏中地集团有限公司
 * Author:   LG
 * Date:     2016年9月2日 上午8:38:45
 * Description: //模块目的、功能描述      
 */
package com.way.mobile.property.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * 功能描述：自定义资源文件解析类，将配置参数装配到map
 *
 * @ClassName PropertyConfigurerWithWhite
 * @Author：xinpei.xu
 * @Date：2017/08/16 19:55
 */
public class PropertyConfigurerWithWhite extends PropertyPlaceholderConfigurer {

    // 装配配置文件属性到内存
    private static Map<String, String> properties = new HashMap<String, String>();

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
            throws BeansException {
        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(DEFAULT_PLACEHOLDER_PREFIX,
                DEFAULT_PLACEHOLDER_SUFFIX, DEFAULT_VALUE_SEPARATOR, false);
        for (Entry<Object, Object> entry : props.entrySet()) {
            String stringKey = String.valueOf(entry.getKey());
            String stringValue = String.valueOf(entry.getValue());
            stringValue = helper.replacePlaceholders(stringValue, props);
            properties.put(stringKey, stringValue);
        }
        super.processProperties(beanFactoryToProcess, props);
    }

    /**
     * 功能描述: 获取全量配置属性<br>
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static Map<String, String> getProperties() {
        return properties;
    }

    /**
     * 功能描述: 获取指定配置属性<br>
     * 〈功能详细描述〉
     *
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static String getProperty(String key) {
        return properties.get(key);
    }
}
