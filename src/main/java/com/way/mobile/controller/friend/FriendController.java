package com.way.mobile.controller.friend;

import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.mobile.service.friend.FriendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能描述：好友Controller
 *
 * @author xinpei.xu
 * @date 2017/08/31 19:56
 */
@Controller
public class FriendController {

    @Autowired
    private FriendService friendService;

    /**
     * 获取首页好友以及组信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/getFriendsAndGroups", method = RequestMethod.POST)
    public ServiceResult<Object> getFriendsAndGroups(HttpServletRequest request){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 获取首页好友以及组信息
            serviceResult = friendService.getFriendsAndGroups(phoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "获取首页好友以及组信息失败," + "请求参数：" + phoneNo);
        } finally {
            WayLogger.access("获取首页好友以及组信息：/getFriendsAndGroups.do,参数：" + phoneNo);
        }
        return serviceResult;
    }

    /**
     * 取消查看好友实时坐标
     * @param request
     * @param friendPhoneNo
     * @return
     */
    @RequestMapping(value = "/cancelGetFriendPosition", method = RequestMethod.POST)
    public ServiceResult<Object> cancelGetFriendPosition(HttpServletRequest request, @ModelAttribute String friendPhoneNo){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 取消查看好友实时坐标
            serviceResult = friendService.cancelGetFriendPosition(phoneNo, friendPhoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "取消查看好友实时坐标失败," + "请求参数：phoneNo：" + phoneNo + "，friendPhoneNo：" + friendPhoneNo);
        } finally {
            WayLogger.access("取消查看好友实时坐标：/cancelGetFriendPosition.do,参数：phoneNo：" + phoneNo + "，friendPhoneNo：" + friendPhoneNo);
        }
        return serviceResult;
    }

    /**
     * 取消查看组好友实时坐标
     * @param request
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/cancelGetGroupPosition", method = RequestMethod.POST)
    public ServiceResult<Object> cancelGetGroupPosition(HttpServletRequest request, @ModelAttribute String groupId){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 取消查看组好友实时坐标
            serviceResult = friendService.cancelGetGroupPosition(phoneNo, groupId);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "取消查看组好友实时坐标失败," + "请求参数：phoneNo：" + phoneNo + "，groupId：" + groupId);
        } finally {
            WayLogger.access("取消查看组好友实时坐标：/cancelGetFriendPosition.do,参数：phoneNo：" + phoneNo + "，groupId：" + groupId);
        }
        return serviceResult;
    }

    /**
     * 查询手机联系人状态
     * @param request
     * @param friendPhoneNo
     * @return
     */
    @RequestMapping(value = "/getPhoneContactStatus", method = RequestMethod.POST)
    public ServiceResult<Object> getPhoneContactStatus(HttpServletRequest request, @ModelAttribute String friendPhoneNo){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查询手机联系人状态
            serviceResult = friendService.getPhoneContactStatus(phoneNo, friendPhoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "查询手机联系人状态失败," + "请求参数：phoneNo：" + phoneNo + "，friendPhoneNo：" + friendPhoneNo);
        } finally {
            WayLogger.access("查询手机联系人状态：/getPhoneContactStatus.do,参数：phoneNo：" + phoneNo + "，friendPhoneNo：" + friendPhoneNo);
        }
        return serviceResult;
    }

    /**
     * 查询好友列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/getFriendList", method = RequestMethod.POST)
    public ServiceResult<Object> getFriendList(HttpServletRequest request){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查询好友列表
            serviceResult = friendService.getFriendList(phoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "查询好友列表失败," + "请求参数：phoneNo：" + phoneNo);
        } finally {
            WayLogger.access("查询好友列表：/getFriendList.do,参数：phoneNo：" + phoneNo);
        }
        return serviceResult;
    }

    /**
     * 申请添加好友
     * @param request
     * @param friendPhoneNo
     * @param applyInfo
     * @return
     */
    @RequestMapping(value = "/applyForAddFriend", method = RequestMethod.POST)
    public ServiceResult<Object> applyForAddFriend(HttpServletRequest request, @ModelAttribute String friendPhoneNo, @ModelAttribute String applyInfo){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(friendPhoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(applyInfo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 申请添加好友
            serviceResult = friendService.applyForAddFriend(phoneNo, friendPhoneNo, applyInfo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "申请添加好友失败," + "请求参数：phoneNo：" + phoneNo);
        } finally {
            WayLogger.access("申请添加好友：/applyForAddFriend.do,参数：phoneNo：" + phoneNo);
        }
        return serviceResult;
    }

    /**
     * 获取被申请好友记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/getApplicationRecordOfFriend", method = RequestMethod.POST)
    public ServiceResult<Object> getApplicationRecordOfFriend(HttpServletRequest request){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 获取被申请好友记录
            serviceResult = friendService.getApplicationRecordOfFriend(phoneNo);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "获取被申请好友记录失败," + "请求参数：phoneNo：" + phoneNo);
        } finally {
            WayLogger.access("获取被申请好友记录：/getApplicationRecordOfFriend.do,参数：phoneNo：" + phoneNo);
        }
        return serviceResult;
    }

    /**
     * 同意/拒绝添加好友申请
     * @param request
     * @param friendPhoneNo
     * @param isApprove
     * @param applicationId
     * @return
     */
    @RequestMapping(value = "/agreeToAddFriend", method = RequestMethod.POST)
    public ServiceResult<Object> agreeToAddFriend(HttpServletRequest request, @ModelAttribute String friendPhoneNo, @ModelAttribute String isApprove, @ModelAttribute String applicationId){
        ServiceResult<Object> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(isApprove)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 同意/拒绝添加好友申请
            serviceResult = friendService.agreeToAddFriend(phoneNo, friendPhoneNo, isApprove);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "同意/拒绝添加好友申请失败," + "请求参数：phoneNo：" + phoneNo);
        } finally {
            WayLogger.access("同意/拒绝添加好友申请：/agreeToAddFriend.do,参数：phoneNo：" + phoneNo);
        }
        return serviceResult;
    }
}
