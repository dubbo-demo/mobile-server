package com.way.mobile.controller.position;

import com.alibaba.fastjson.JSON;
import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.member.position.dto.PositionInfoDto;
import com.way.mobile.service.member.MemberService;
import com.way.mobile.service.position.PositionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 功能描述：定位Controller
 *
 * @author xinpei.xu
 * @date 2017/08/28 20:45
 */
@Controller
public class PositionController {

    @Autowired
    private PositionService positionService;

    @Autowired
    private MemberService memberService;

    /**
     * 上传坐标
     */
    @RequestMapping(value = "/uploadPosition", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<String> uploadPosition(HttpServletRequest request, @ModelAttribute PositionInfoDto positionInfoDto){
        ServiceResult<String> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }

            // 根据手机号查用户邀请码
//            ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(phoneNo);
            positionInfoDto.setInvitationCode(invitationCode);

            // 上传坐标
            serviceResult = positionService.uploadPosition(positionInfoDto);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "传坐标失败," + "请求参数：" + positionInfoDto);
        } finally {
            WayLogger.access("传坐标：/uploadPosition.do,参数：" + positionInfoDto);
        }
        return serviceResult;
    }

    /**
     * 根据手机号获取用户实时坐标
     */
    @RequestMapping(value = "/getRealtimePositionByPhoneNo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getRealTimePositionByPhoneNo(HttpServletRequest request, String positionInfoDtos){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if(StringUtils.isBlank(positionInfoDtos)){
                return ServiceResult.newFailure("必传参数不能为空");
            }
            List<PositionInfoDto> list = JSON.parseArray(positionInfoDtos, PositionInfoDto.class);
            if(CollectionUtils.isEmpty(list)){
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 根据手机号获取用户实时坐标
            serviceResult = positionService.getRealTimePositionByPhoneNo(invitationCode, list);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "根据手机号获取用户实时坐标失败," + "请求参数：positionInfoDto:" + positionInfoDtos);
        } finally {
            WayLogger.access("根据手机号获取用户实时坐标：/getRealtimePositionByPhoneNo.do,参数：positionInfoDto:" + positionInfoDtos);
        }
        return serviceResult;
    }

    /**
     * 获取退出前查看的用户
     * @param request
     * @return
     */
    @RequestMapping(value = "/getUserViewBeforeExit", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getUserViewBeforeExit(HttpServletRequest request){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 获取退出前查看的用户实时坐标
            serviceResult = positionService.getUserViewBeforeExit(invitationCode);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "获取退出前查看的用户实时坐标失败," + "请求参数：" + invitationCode);
        } finally {
            WayLogger.access("获取退出前查看的用户实时坐标：/getUserViewBeforeExit.do,参数：" + invitationCode);
        }
        return serviceResult;
    }

    /**
     * 根据组ID获取用户实时坐标
     * @param request
     * @return
     */
    @RequestMapping(value = "/getRealtimePositionByGroupId", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getRealtimePositionByGroupId(HttpServletRequest request, String groupId){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 获取退出前查看的用户实时坐标
            serviceResult = positionService.getRealtimePositionByGroupId(invitationCode, groupId);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "根据组ID获取用户实时坐标失败," + "请求参数：invitationCode：" + invitationCode + "，groupId：" + groupId);
        } finally {
            WayLogger.access("根据组ID获取用户实时坐标：/getRealtimePositionByGroupId.do,参数：invitationCode：" + invitationCode + "，groupId：" + groupId);
        }
        return serviceResult;
    }

    /**
     * 查询用户历史轨迹坐标
     * @param request
     * @param friendInvitationCode
     * @param startTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "/getMemberHistoryPositions", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<Object> getMemberHistoryPositions(HttpServletRequest request, String friendInvitationCode, String startTime, String endTime){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String invitationCode = (String) request.getAttribute("invitationCode");
        try {
            // 校验token
            if (StringUtils.isBlank(invitationCode) || StringUtils.isBlank(friendInvitationCode) || StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查询用户历史轨迹坐标
            serviceResult = positionService.getMemberHistoryPositions(invitationCode, friendInvitationCode, startTime, endTime);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage(ServiceResult.ERROR_MSG);
            WayLogger.error(e, "查询用户历史轨迹坐标失败," + "请求参数：invitationCode：" + invitationCode + "，friendInvitationCode：" + friendInvitationCode
                    + "，startTime：" + startTime + "，endTime：" + endTime);
        } finally {
            WayLogger.access("查询用户历史轨迹坐标：/getMemberHistoryPositions.do,参数：invitationCode：" + invitationCode + "，friendInvitationCode：" + friendInvitationCode
                    + "，startTime：" + startTime + "，endTime：" + endTime);
        }
        return serviceResult;
    }
}
