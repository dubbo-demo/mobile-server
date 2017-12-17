package com.way.mobile.service.member;

import com.way.base.beeCloud.dto.BeeCloudMessageDetailDto;

/**
 * 异步处理BeeCloud回参信息
 */
public interface AsyncPushBeeCloudMessageService {

    /**
     * 异步推送BeeCloud回参信息
     * @param message_detail
     */
    void pushBeeCloudMessage(BeeCloudMessageDetailDto message_detail);
}
