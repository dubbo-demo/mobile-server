package com.way.mobile.service.member.impl;

import com.way.common.result.ServiceResult;
import com.way.member.member.dto.MemberDto;
import com.way.member.member.service.MemberInfoService;
import com.way.mobile.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: MemberServiceImpl
 * @Description: 用户信息ServiceImpl
 * @author: xinpei.xu
 * @date: 2017/08/21 19:45
 *
 */
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberInfoService memberInfoService;

    /**
     * 校验邀请人手机号是否存在
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<String> checkPhone(String phoneNo) {
        ServiceResult<String> serviceResult = ServiceResult.newSuccess();
        // 根据手机号查出用户信息
        ServiceResult<MemberDto> memberDto = memberInfoService.queryMemberInfo(phoneNo);
        if(null == memberDto.getData() ){
            serviceResult.setCode(ServiceResult.ERROR_CODE);
            serviceResult.setMessage("校验邀请人手机号不存在");
        }
        return serviceResult;
    }

    /**
     * 根据手机号搜索用户
     * @param phoneNo
     * @return
     */
    @Override
    public ServiceResult<MemberDto> searchUserByPhoneNo(String phoneNo) {
        return memberInfoService.searchUserByPhoneNo(phoneNo);
    }
}
