package com.way.mobile.service.member;

import com.way.common.result.ServiceResult;
import com.way.member.member.dto.MemberDto;

/**
 * @ClassName: MemberService
 * @Description: 用户信息Service
 * @author: xinpei.xu
 * @date: 2017/08/21 19:42
 *
 */
public interface MemberService {

    /**
     * 校验邀请人手机号是否存在
     * @param phoneNo
     * @return
     */
    ServiceResult<String> checkPhone(String phoneNo);

    /**
     * 根据手机号搜索用户
     * @param phoneNo
     * @return
     */
    ServiceResult<MemberDto> searchUserByPhoneNo(String phoneNo);
}
