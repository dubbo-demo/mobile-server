package com.way.mobile.filter;

import com.way.common.log.WayLogger;
import com.way.common.result.ServiceResult;
import com.way.common.util.ResponseUtils;
import com.way.mobile.common.po.LoginTokenInfo;
import com.way.mobile.common.util.TokenJedisUtils;
import com.way.mobile.property.config.PropertyConfigurerWithWhite;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName: MobileLoginFilter
 * @Description: app请求filter
 * @author: xinpei.xu
 * @date: 2017/08/17 15:06
 */
public class MobileLoginFilter extends HttpServlet implements Filter {
	private static final long serialVersionUID = 1L;

	public void destroy() {
	}

	public void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain filterChain)
			throws IOException, ServletException {
		String contentType = sRequest.getContentType();
		HttpServletRequest request = (HttpServletRequest) sRequest;
		HttpServletResponse response = (HttpServletResponse) sResponse;
		String token = request.getParameter("token");
		request.setCharacterEncoding("utf-8");
		response.setContentType("application/json;charset=UTF-8");
		// 获取链接数组
		String[] pathArr = request.getRequestURI().split("/");
		// 访问链接
		String pathInfoUrl = pathArr[pathArr.length - 1];
		// 校验是否为文件上传-文件上传不做校验
		if (null != contentType && contentType.startsWith("multipart/form-data")) {// 文件上传相关请求
			WayLogger.debug("校验是否为白名单请求........有文件类型参数请求");
		} else {
			// 不是文件上传
			WayLogger.debug("校验是否为白名单请求.........普通参数请求");
			Object o = PropertyConfigurerWithWhite.getProperty(pathInfoUrl);
			WayLogger.debug("token:" + token + ",pathInfoUrl:" + pathInfoUrl);
			// 不在白名单中的请求需要token
			if (null == o && StringUtils.isBlank(token)) {
				WayLogger.debug("非白名单请求，token不能为空");
				ResponseUtils.toJsonOrJsonP(request, response, ServiceResult.ERROR_CODE, "token不能为空");
				return;
			} else if (null == o && !StringUtils.isBlank(token)) {
				// 白名单请求需要token
				if (token.length() < 30) {
					ResponseUtils.toJsonOrJsonP(request, response, ServiceResult.ERROR_CODE, "无效的token");
					return;
				}
				LoginTokenInfo tokenInfo = TokenJedisUtils.getTokenInfo(token);
				// 验证token
				if (tokenInfo == null) {// 帐号已过期
					ResponseUtils.toJsonOrJsonP(request, response, ServiceResult.TOKEN_EXPIRED_OVERTIME, "帐号已过期，请重新登录");
					return;
				}
				if (tokenInfo.getStatus() == 1) { // 该账户已在其他设备登录
					ResponseUtils.toJsonOrJsonP(request, response, ServiceResult.TOKEN_EXPIRED_OTHERLOGIN, "该账户已在其他设备登录，请注意安全");
					return;
				}
				request.setAttribute("invitationCode", tokenInfo.getInvitationCode());
				TokenJedisUtils.expireTokenInfo(token,tokenInfo.getInvitationCode());
			}
		}
		filterChain.doFilter(request, response);
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

}
