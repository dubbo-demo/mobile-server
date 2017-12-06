package com.way.mobile.controller.rewardScore;

import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.member.rewardScore.dto.RewardScoreDto;
import com.way.mobile.service.member.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能描述：
 *
 * @Author：xinpei.xu
 * @Date：2017年12月06日 17:13
 */
@Controller
public class RewardScoreController {

    @Autowired
    private MemberService memberService;

    /**
     * 查看积分明细
     * @param request
     * @param pageNumber
     * @return
     */
    @RequestMapping(value = "/getRewardScoreDetail", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<RewardScoreDto> getRewardScoreDetail(HttpServletRequest request, String pageNumber){
        ServiceResult<RewardScoreDto> serviceResult = ServiceResult.newSuccess();
        String phoneNo = (String) request.getAttribute("phoneNo");
        try {
            // 校验token
            if (StringUtils.isBlank(phoneNo)) {
                return ServiceResult.newFailure("必传参数不能为空");
            }
            if (StringUtils.isBlank(pageNumber)) { // 第几页为空
                return ServiceResult.newFailure("必传参数不能为空");
            }
            // 查看积分明细
            serviceResult = memberService.getRewardScoreDetail(phoneNo, pageNumber);
        } catch (Exception e) {
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            WayLogger.error(e, "查看积分明细失败," + "请求参数：phoneNo：" + phoneNo + "pageNumber：" + pageNumber);
        } finally {
            WayLogger.access("查看积分明细：/getRewardScoreDetail.do,参数：phoneNo：" + phoneNo + "pageNumber：" + pageNumber);
        }
        return serviceResult;
    }
}
