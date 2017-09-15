package com.way.mobile.service.position.impl;

import com.way.common.result.ServiceResult;
import com.way.member.position.service.PositionInfoService;
import com.way.mobile.service.position.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能描述：定位ServiceImpl
 *
 * @Author：xinpei.xu
 * @Date：2017/08/28 21:03
 */
@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    private PositionInfoService positionInfoService;

    /**
     * 根据手机号获取用户实时坐标
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<String> getRealtimePositionByPhoneNo(String phoneNo) {



        return null;
    }
}
