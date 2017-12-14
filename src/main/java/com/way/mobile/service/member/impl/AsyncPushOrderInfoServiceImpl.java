package com.way.mobile.service.member.impl;

import com.way.mobile.service.member.AsyncPushOrderInfoService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 功能描述：异步推送订单信息
 *
 * @Author：xinpei.xu
 */
@Service
public class AsyncPushOrderInfoServiceImpl implements AsyncPushOrderInfoService {

    /**
     * 异步推送订单信息
     * @param orderSn
     */
    @Override
    @Async
    public void pushOrderInfo(String orderSn) {
    }

}
