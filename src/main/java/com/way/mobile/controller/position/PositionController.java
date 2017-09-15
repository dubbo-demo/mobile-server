package com.way.mobile.controller.position;

import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.common.util.Validater;
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
     * 根据手机号获取用户实时坐标
     */
    @RequestMapping(value = "/getRealtimePositionByPhoneNo", method = RequestMethod.POST)
    public ServiceResult<String> getRealtimePositionByPhoneNo(HttpServletRequest request, @ModelAttribute String phoneNo){
        ServiceResult<String> serviceResult = ServiceResult.newSuccess();
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

}
