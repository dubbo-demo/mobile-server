package com.way.mobile.ehcache.service.impl;

import com.way.base.versionUpdate.dto.VersionUpdateDto;
import com.way.base.versionUpdate.service.VersionUpdateService;
import com.way.common.result.ServiceResult;
import com.way.mobile.common.util.PropertyConfig;
import com.way.mobile.ehcache.service.VersionConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * app版本升级<br>
 * 〈功能详细描述〉
 *
 * @author xinpei.xu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class VersionConfServiceImpl implements VersionConfService {

    private Cache versionCache;

    @Autowired
    private VersionUpdateService versionUpdateService;

    @Autowired
    private PropertyConfig config;

    /**
     * @return the versionCache
     */
    public Cache getVersionCache() {
		return versionCache;
	}
    
    /**
     * @param versionCache the versionCache to set
     */
	public void setVersionCache(Cache versionCache) {
		this.versionCache = versionCache;
	}

    /**
     * refreshVersionConf
     * @return
     */
	@Override
	public void refreshVersionConf() {
		versionCache.clear();
		// 版本升级IOS列表
		List<VersionUpdateDto> iosConfigs = new ArrayList<VersionUpdateDto>();
		// 版本升级安卓列表
        List<VersionUpdateDto> androidConfigs = new ArrayList<VersionUpdateDto>();
		// 查询版本升级列表
        ServiceResult<Map<String, Object>> serviceResult = versionUpdateService.versionUpdateList();
		if (null != serviceResult.getData()) {
			iosConfigs = (List<VersionUpdateDto>) serviceResult.getData().get("ios");
			androidConfigs = (List<VersionUpdateDto>) serviceResult.getData().get("android");
		}
		versionCache.put("iosVersionConf", iosConfigs);
		versionCache.put("androidVersionConf", androidConfigs);
	}

    /**
     * getIosVersionConf
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<VersionUpdateDto> getIosVersionConf() {
        ValueWrapper value = versionCache.get("iosVersionConf");
        return (List<VersionUpdateDto>) value.get();
    }

    /**
     * getAndroidVersionConf
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<VersionUpdateDto> getAndroidVersionConf() {
        ValueWrapper value = versionCache.get("androidVersionConf");
        return (List<VersionUpdateDto>) value.get();
    }
}
