package com.way.mobile.controller.position;

import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.common.util.Validater;
import com.way.member.position.dto.PositionInfoDto;
import com.way.mobile.service.position.PositionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 上传坐标
     */
    @RequestMapping(value = "/uploadPosition", method = RequestMethod.POST)
    public ServiceResult<String> uploadPosition(HttpServletRequest request, @ModelAttribute PositionInfoDto positionInfoDto){
        ServiceResult<String> serviceResult = ServiceResult.newSuccess();
        String memberId = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(memberId)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 上传坐标
            serviceResult = positionService.uploadPosition(positionInfoDto);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
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
    public ServiceResult<Object> getRealtimePositionByPhoneNo(HttpServletRequest request, @ModelAttribute String phoneNo){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String memberId = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(memberId)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 校验手机号格式是否正确
            if (!Validater.isMobileNew(phoneNo)) {
                serviceResult.setCode(ServiceResult.ERROR_CODE);
                serviceResult.setMessage("手机号不正确");
                return serviceResult;
            }
            // 根据手机号获取用户实时坐标
            serviceResult = positionService.getRealtimePositionByPhoneNo(phoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "根据手机号获取用户实时坐标失败," + "请求参数：" + phoneNo);
        } finally {
            WayLogger.access("根据手机号获取用户实时坐标：/getRealtimePositionByPhoneNo.do,参数：" + phoneNo);
        }
        return serviceResult;
    }

    /**
     * 获取退出前查看的用户实时坐标
     * @param request
     * @return
     */
    @RequestMapping(value = "/getPositionsBeforeExit", method = RequestMethod.POST)
    public ServiceResult<Object> getPositionsBeforeExit(HttpServletRequest request){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 获取退出前查看的用户实时坐标
            serviceResult = positionService.getPositionsBeforeExit(phoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "获取退出前查看的用户实时坐标失败," + "请求参数：" + phoneNo);
        } finally {
            WayLogger.access("获取退出前查看的用户实时坐标：/getPositionsBeforeExit.do,参数：" + phoneNo);
        }
        return serviceResult;
    }

    /**
     * 根据组ID获取用户实时坐标
     * @param request
     * @return
     */
    @RequestMapping(value = "/getRealtimePositionByGroupId", method = RequestMethod.POST)
    public ServiceResult<Object> getRealtimePositionByGroupId(HttpServletRequest request, @ModelAttribute String groupId){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 获取退出前查看的用户实时坐标
            serviceResult = positionService.getRealtimePositionByGroupId(phoneNo, groupId);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "根据组ID获取用户实时坐标失败," + "请求参数：phoneNo：" + phoneNo + "，groupId：" + groupId);
        } finally {
            WayLogger.access("根据组ID获取用户实时坐标：/getRealtimePositionByGroupId.do,参数：phoneNo：" + phoneNo + "，groupId：" + groupId);
        }
        return serviceResult;
    }

}
