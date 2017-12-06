package com.way.mobile.service.position.impl;

import com.way.common.constant.Constants;
import com.way.common.result.ServiceResult;
import com.way.member.friend.dto.FriendsInfoDto;
import com.way.member.friend.dto.GroupInfoDto;
import com.way.member.friend.service.FriendsInfoService;
import com.way.member.member.dto.MemberDto;
import com.way.member.position.dto.PositionInfoDto;
import com.way.member.position.service.PositionInfoService;
import com.way.mobile.service.member.MemberService;
import com.way.mobile.service.position.PositionService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

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

    @Autowired
    private MemberService memberService;

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
            ServiceResult<PositionInfoDto> dto = positionInfoService.getRealtimePositionByPhoneNo(positionInfoDto.getPhoneNo(), null);
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
     * @param positionInfoDtos
     * @return
     */
    @Override
    public ServiceResult<Object> getRealtimePositionByPhoneNo(String phoneNo, List<PositionInfoDto> positionInfoDtos) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        Map<String, List<PositionInfoDto>> map = new HashMap<String, List<PositionInfoDto>>();
        List<PositionInfoDto> list = new ArrayList<PositionInfoDto>();
        List<String> friendPhoneNos = new ArrayList<String>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String week = String.valueOf(cal.get(Calendar.DAY_OF_WEEK) - 1);
        String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String minute = String.format("%02d", cal.get(Calendar.MINUTE));
        String now = hour.concat(":").concat(minute);
        for(PositionInfoDto dto : positionInfoDtos){
            // 校验手机号
            if (StringUtils.isBlank(dto.getPhoneNo())) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查询是否被好友授权可见
            FriendsInfoDto friendsInfoDto = friendsInfoService.checkIsAuthorizedVisible(phoneNo, dto.getPhoneNo());
            if(null != friendsInfoDto){
                friendPhoneNos.add(dto.getPhoneNo());
                // 根据被授权开始时间、被授权结束时间、被授权星期计算出是否被授权可见
                String[] authorizedWeeks = friendsInfoDto.getAuthorizedWeeks().split(",");
                if(friendsInfoDto.getIsAuthorizedVisible() == 1 && ArrayUtils.contains(authorizedWeeks,week) &&
                        now.compareTo(friendsInfoDto.getAuthorizedAccreditStartTime()) >= 0 && now.compareTo(friendsInfoDto.getAuthorizedAccreditStartTime()) < 0){
                    // 根据手机号获取用户实时坐标
                    ServiceResult<PositionInfoDto> positionInfoDto = positionInfoService.getRealtimePositionByPhoneNo(dto.getPhoneNo(), dto.getModifyTime());
                    if(null != positionInfoDto.getData()){
                        list.add(positionInfoDto.getData());
                    }
                }else{
                    PositionInfoDto positionInfoDto = new PositionInfoDto();
                    positionInfoDto.setPhoneNo(dto.getPhoneNo());
                    positionInfoDto.setIsAccreditVisible("2");
                    list.add(positionInfoDto);
                }
            }
        }
        map.put("positions", list);
        serviceResult.setData(map);
        if(CollectionUtils.isEmpty(friendPhoneNos)){
            return ServiceResult.newFailure("必传参数不能为空");
        }
        // 标记好友退出前查看为是：1
        friendsInfoService.updateIsCheckBeforeExitByFriendPhoneNos(phoneNo, friendPhoneNos, Constants.YES_INT);
        return serviceResult;
    }

    /**
     * 获取退出前查看的用户
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<Object> getUserViewBeforeExit(String phoneNo) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        List<PositionInfoDto> list = new ArrayList<PositionInfoDto>();
        Map<String, Object> data = new HashMap<String, Object>();
        // 非组成员好友
        List<FriendsInfoDto> notGroupFriendslist = new ArrayList<FriendsInfoDto>();
        List<GroupInfoDto> groupInfoDtos = new ArrayList<GroupInfoDto>();
        Set<String> groupIds = new TreeSet<String>();
        // 查出退出前查看的好友信息
        List<FriendsInfoDto> friendsInfoDtos = friendsInfoService.getFriendsInfoBeforeExit(phoneNo);

        for(FriendsInfoDto dto : friendsInfoDtos){
            if(StringUtils.isBlank(dto.getGroupId())){
                notGroupFriendslist.add(dto);
            }else{
                // 收集组信息
                groupIds.add(dto.getGroupId());
            }
        }
        data.put("friends", notGroupFriendslist);
        friendsInfoDtos.removeAll(notGroupFriendslist);
        for(String groupId : groupIds){
            GroupInfoDto groupInfoDto = new GroupInfoDto();
            List<FriendsInfoDto> groupFriendslist = new ArrayList<FriendsInfoDto>();
            for(FriendsInfoDto dto : friendsInfoDtos){
                if(groupId.equals(dto.getGroupId())){
                    groupFriendslist.add(dto);
                }
            }
            groupInfoDto.setFriends(groupFriendslist);
            groupInfoDtos.add(groupInfoDto);
        }
        data.put("groups", groupInfoDtos);
        serviceResult.setData(data);
        serviceResult.setData(data);
        return serviceResult;
    }

    /**
     * 根据组ID获取用户实时坐标
     * @param phoneNo
     * @param groupId
     * @return
     */
    @Override
    public ServiceResult<Object> getRealtimePositionByGroupId(String phoneNo, String groupId) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        // 标记组好友退出前查看为是：1
        friendsInfoService.updateIsCheckBeforeExitByGroupId(phoneNo, groupId, Constants.YES_INT);
        return serviceResult;
    }

    /**
     * 查询用户历史轨迹坐标
     * @param phoneNo
     * @param friendPhoneNo
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public ServiceResult<Object> getMemberHistoryPositions(String phoneNo, String friendPhoneNo, String startTime, String endTime) {
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        Date now = new Date();
        // 查询用户信息
        ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(phoneNo);
        // 判断用户是否开通增值服务
        if("2".equals(memberDto.getData().getValueAddedService()) || null == memberDto.getData().getValueAddedServiceStartTime()
                || null == memberDto.getData().getValueAddedServiceEndTime() || now.after(memberDto.getData().getValueAddedServiceStartTime())
                ||memberDto.getData().getValueAddedServiceEndTime().after(now)){
            return ServiceResult.newFailure("您没有开通增值服务");
        }
        // 如果被查询用户不是自己
        if(!phoneNo.equals(friendPhoneNo)){
            // 判断该用户是否为好友
            ServiceResult<FriendsInfoDto> friendsInfoDto = friendsInfoService.getFriendInfo(phoneNo, friendPhoneNo);
            if(null == friendsInfoDto.getData()){
                return ServiceResult.newFailure("该用户不是您的好友");
            }
            // 判断用户是否开通增值服务
            ServiceResult<MemberDto> searchMemberDto = memberService.getMemberInfo(phoneNo);
            // 判断用户是否开通增值服务
            if(null== searchMemberDto.getData() ||"2".equals(searchMemberDto.getData().getValueAddedService())
                    || null == searchMemberDto.getData().getValueAddedServiceStartTime() || null == searchMemberDto.getData().getValueAddedServiceEndTime()
                    || now.after(searchMemberDto.getData().getValueAddedServiceStartTime())
                    ||searchMemberDto.getData().getValueAddedServiceEndTime().after(now)){
                return ServiceResult.newFailure("该用户没有开通增值服务");
            }
            // 查询好友轨迹
            List<PositionInfoDto> positionInfoDtos = positionInfoService.getMemberHistoryPositions(friendPhoneNo, startTime, endTime);
            serviceResult.setData(positionInfoDtos);
        }else{
            // 查询自己轨迹
            List<PositionInfoDto> positionInfoDtos =  positionInfoService.getMemberHistoryPositions(phoneNo, startTime, endTime);
            serviceResult.setData(positionInfoDtos);
        }
        return serviceResult;
    }
}
