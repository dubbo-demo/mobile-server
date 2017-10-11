package com.way.mobile.ehcache.service;

import java.util.List;
import java.util.Map;

/**
 * 客户端版本服务<br>
 * 〈功能详细描述〉
 *
 * @author xinpei.xu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface VersionConfService {

    /**
     * 
     * 功能描述: 刷新版本配置信息<br>
     * 〈功能详细描述〉
     *
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public void refreshVersionConf();

    /**
     * 
     * 功能描述: 获取ios版本配置信息<br>
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public List<Map<String, Object>> getIosVersionConf();

    /**
     * 
     * 功能描述: 获取android版本配置信息<br>
     * 〈功能详细描述〉
     *
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public List<Map<String, Object>> getAndroidVersionConf();
}
