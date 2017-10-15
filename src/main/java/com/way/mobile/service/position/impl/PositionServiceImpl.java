package com.way.mobile.service.position.impl;

import com.way.common.result.ServiceResult;
import com.way.member.friend.dto.FriendsInfoDto;
import com.way.member.friend.service.FriendsInfoService;
import com.way.member.position.dto.PositionInfoDto;
import com.way.member.position.service.PositionInfoService;
import com.way.mobile.service.position.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private FriendsInfoService friendsInfoService;

    /**
     * 上传坐标
     * @param positionInfoDto
     * @return
     */
    @Override
    public ServiceResult<String> uploadPosition(PositionInfoDto positionInfoDto) {
        // 判断用户是否开启增值服务
        Boolean valueAddedService = true;
        // 开启增值服务直接保存
        if(valueAddedService){
            positionInfoService.savePosition(positionInfoDto);
        }else{
            // 根据手机号获取用户实时坐标
            ServiceResult<PositionInfoDto> dto = positionInfoService.getRealtimePositionByPhoneNo(positionInfoDto.getPhoneNo());
            // 有记录更新
            if(null != dto.getData()){
                // 更新用户坐标
                positionInfoService.updatePosition(positionInfoDto, dto.getData().getId());
            }else{
                // 没有记录新建
                positionInfoService.savePosition(positionInfoDto);
            }
        }
        return ServiceResult.newSuccess();
    }

    /**
     * 根据手机号获取用户实时坐标
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> getRealtimePositionByPhoneNo(String phoneNo) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        // 根据手机号获取用户实时坐标
        ServiceResult<PositionInfoDto> positionInfoDto = positionInfoService.getRealtimePositionByPhoneNo(phoneNo);
        Map<String, List<PositionInfoDto>> map = new HashMap<String, List<PositionInfoDto>>();
        List<PositionInfoDto> list = new ArrayList<PositionInfoDto>();
        list.add(positionInfoDto.getData());
        map.put("positions", list);
        serviceResult.setData(map);
        return serviceResult;
    }

    /**
     * 获取退出前查看的用户实时坐标
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> getPositionsBeforeExit(String phoneNo) {
        // 查出退出前查看的好友信息
        List<FriendsInfoDto> friendsInfoDtos = friendsInfoService.getFriendsInfoBeforeExit(phoneNo);

        return null;
    }
}
