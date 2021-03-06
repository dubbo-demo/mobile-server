package com.way.mobile.service.member.impl;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.way.base.beeCloud.dto.BeeCloudMessageDetailDto;
import com.way.base.beeCloud.service.BeeCloudMessageDetailService;
import com.way.common.result.ServiceResult;
import com.way.member.member.dto.MemberDto;
import com.way.member.order.dto.MemberOrderInfoDto;
import com.way.member.order.service.MemberOrderInfoService;
import com.way.member.recharge.dto.RechargeInfoDto;
import com.way.member.recharge.service.RechargeInfoService;
import com.way.mobile.service.member.AsyncPushBeeCloudMessageService;
import com.way.mobile.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 功能描述：异步推送订单信息
 *
 * @Author：xinpei.xu
 */
@Service
public class AsyncPushBeeCloudMessageServiceImpl implements AsyncPushBeeCloudMessageService {

    @Autowired
    private BeeCloudMessageDetailService beeCloudMessageDetailService;

    @Autowired
    private MemberOrderInfoService memberOrderInfoService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RechargeInfoService rechargeInfoService;

    /**
     * 异步推送BeeCloud回参信息
     * @param message_detail
     */
    @Override
    @Async
    public void pushBeeCloudMessage(BeeCloudMessageDetailDto message_detail) {
        // 根据内部交易号查出交易信息
        MemberOrderInfoDto memberOrderInfoDto = memberOrderInfoService.getOrderInfo(message_detail.getOut_trade_no());
        if(Double.valueOf(message_detail.getTotal_fee()).equals(memberOrderInfoDto.getAmount())){
            // 会员购买服务
            memberService.buyServiceByRecharge(memberOrderInfoDto.getPhoneNo(), memberOrderInfoDto.getType(), memberOrderInfoDto.getValidityDurationType());
            message_detail.setFlag(1);
            message_detail.setModifyTime(new Date());

            // 查询会员
            ServiceResult<MemberDto> memberDto = memberService.getMemberInfo(memberOrderInfoDto.getPhoneNo());

            RechargeInfoDto dto = new RechargeInfoDto();
            dto.setInvitationCode(memberDto.getData().getInvitationCode());
            dto.setAmount(memberOrderInfoDto.getAmount()/100);
            dto.setType(String.valueOf(memberOrderInfoDto.getType()));
            dto.setOrderNumber(message_detail.getOut_trade_no());
            dto.setCreateTime(new Date());
            dto.setModifyTime(new Date());
            // 更新充值记录表
            rechargeInfoService.addRechargeInfoDto(dto);

            // 更新BeeCloud回参信息
            beeCloudMessageDetailService.updateFlag(message_detail);
        }
    }

}
