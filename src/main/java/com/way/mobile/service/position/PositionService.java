package com.way.mobile.service.position;

import com.way.common.result.ServiceResult;
import com.way.member.position.dto.PositionInfoDto;

/**
 * 功能描述：定位Service
 *
 * @author xinpei.xu
 * @date 2017/08/28 20:56
 */
public interface PositionService {

    /**
     * 上传坐标
     * @param positionInfoDto
     * @return
     */
    ServiceResult<String> uploadPosition(PositionInfoDto positionInfoDto);

    /**
     * 根据手机号获取用户实时坐标
     * @param phoneNo
     * @return
     */
    ServiceResult<Object> getRealtimePositionByPhoneNo(String phoneNo);

    /**
     * 获取退出前查看的用户实时坐标
     * @param phoneNo
     * @return
     */
    ServiceResult<Object> getPositionsBeforeExit(String phoneNo);

    /**
     * 根据组ID获取用户实时坐标
     * @param phoneNo
     * @param groupId
     * @return
     */
    ServiceResult<Object> getRealtimePositionByGroupId(String phoneNo, String groupId);
}
