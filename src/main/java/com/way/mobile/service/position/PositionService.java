package com.way.mobile.service.position;

import com.way.common.result.ServiceResult;

/**
 * 功能描述：定位Service
 *
 * @author xinpei.xu
 * @date 2017/08/28 20:56
 */
public interface PositionService {

    /**
     * 根据手机号获取用户实时坐标
     * @param phoneNo
     * @return
     */
    ServiceResult<String> getRealtimePositionByPhoneNo(String phoneNo);
}
