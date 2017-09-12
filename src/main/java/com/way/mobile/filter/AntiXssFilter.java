package com.way.mobile.filter;

import com.way.common.log.WayLogger;
import com.way.common.result.AjaxResult;
import com.way.common.util.ResponseUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * 功能描述：
 *
 * @ClassName AntiXssFilter
 * @Author：xinpei.xu
 * @Date：2017/08/17 10:57
 */
public class AntiXssFilter implements Filter {

    public void destroy() {
    }

    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException, UnsupportedEncodingException {
        HttpServletRequest req = (HttpServletRequest) request;

        HttpServletResponse res = (HttpServletResponse) response;
        res.setContentType("application/json;charset=UTF-8");
        // 校验head参数
        if (existsXssHeadElement(req)) {
            ResponseUtils.renderJson(res, AjaxResult.failed("错误的参数"));
            return;
        }
        // 校验body参数
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (Object key : parameterMap.keySet()) {
            String[] val = (String[]) parameterMap.get(key);
            for (String v : val) {
                if (null != v) {
                    v = URLDecoder.decode(URLDecoder.decode(v, "utf-8"), "utf-8");
                }
                if (existsXssElement(v)) {
                    ResponseUtils.renderJson(res, AjaxResult.failed("错误的参数"));
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * @throws UnsupportedEncodingException
     * @Title: existsXssHeadElement
     * @Description: 校验header中的参数
     * @return: boolean
     */
    private boolean existsXssHeadElement(HttpServletRequest req) throws UnsupportedEncodingException {
        // 设备号
        String deviceNo = req.getHeader("deviceNo");
        if (null != deviceNo) {
            deviceNo = URLDecoder.decode(URLDecoder.decode(deviceNo, "utf-8"), "utf-8");
            if (existsXssElement(deviceNo)) {
                return true;
            }
        }
        // 终端
        String client = req.getHeader("client");
        if (null != client) {
            client = URLDecoder.decode(URLDecoder.decode(client, "utf-8"), "utf-8");
            if (existsXssElement(client)) {
                return true;
            }
        }
        // 渠道
        String channel = req.getHeader("channel");
        if (null != channel) {
            channel = URLDecoder.decode(URLDecoder.decode(channel, "utf-8"), "utf-8");
            if (existsXssElement(channel)) {
                return true;
            }
        }
        // app版本号
        String version = req.getHeader("version");
        if (null != version) {
            version = URLDecoder.decode(URLDecoder.decode(version, "utf-8"), "utf-8");
            if (existsXssElement(version)) {
                return true;
            }
        }
        // app版本号序号
        String build = req.getHeader("build");
        if (null != build) {
            build = URLDecoder.decode(URLDecoder.decode(build, "utf-8"), "utf-8");
            if (existsXssElement(build)) {
                return true;
            }
        }
        // ip
        String ip = req.getHeader("ip");
        if (null != ip) {
            ip = URLDecoder.decode(URLDecoder.decode(ip, "utf-8"), "utf-8");
            if (existsXssElement(ip)) {
                return true;
            }
        }
        return false;
    }

    public void init(FilterConfig arg0) throws ServletException {
    }

    /**
     * 黑名单
     * @param s
     * @return
     * @throws UnsupportedEncodingException
     */
    private boolean existsXssElement(String s)
            throws UnsupportedEncodingException {
        boolean res = false;
        s = s.toLowerCase();
        res = getIncludeSqlSpecialCharsFlag(s);
        if (!res) {
            res = getIncludeHtmlSpecialCharsFlag(s);
        }
        return res;
    }

    /**
     * 替换一些特殊字符
     *
     * @param
     * @return
     * @throws
     */
    private String replaceSpecialChars(String s) throws UnsupportedEncodingException {
        String specialChar = "";
        s = s.replaceAll("(\r|\n|\t|\f|'|\")", "");
        s = s.replaceAll("(\r|\n|\t|\f|'|\")", "");
        int specialCharsLen = specailCharArray.length;
        for (int i = 0; i < specialCharsLen; i++) {
            specialChar = specailCharArray[i];
            specialChar = URLDecoder.decode(URLDecoder.decode(specialChar, "utf-8"), "utf-8");
            s = s.replaceAll(specialChar, "");
        }
        return s;
    }

    /**
     * 判断是否包含html特殊字符
     *
     * @param
     * @return
     * @throws UnsupportedEncodingException
     */
    private boolean getIncludeHtmlSpecialCharsFlag(String s) throws UnsupportedEncodingException {
        boolean res = false;
//		s=replaceSpecialChars(s);
        //XSS黑名单
        if (s.indexOf("javascript:") != -1 || s.indexOf("document.cookie") != -1
                || s.indexOf("<script") != -1 || s.indexOf("<iframe") != -1
                || s.indexOf("\"><script>") != -1 || s.indexOf("\"<script>") != -1
                || s.indexOf("<img") != -1 || s.indexOf("onclick=") != -1
                || s.indexOf("<style") != -1 || s.indexOf(")//") != -1
                || s.indexOf("\">") != -1 || s.indexOf("<body") != -1
                || s.indexOf("/xss/") != -1 || s.indexOf("onfocus") != -1
                || s.indexOf("alert") != -1 //|| s.indexOf(";") != -1
                || s.indexOf("fromcharcode") != -1 || s.indexOf("eval") != -1
                || s.indexOf("<a") != -1 || s.indexOf("cookie") != -1
                || s.indexOf("document.write") != -1 ) {
            WayLogger.error("请求拦截可能存在SQL和脚本注入 ,请求参数{}：", s);
            res = true;
        }
        return res;
    }

    /**
     * 判断是否包含sql特殊字符
     *
     * @param
     * @return
     * @throws
     */
    private boolean getIncludeSqlSpecialCharsFlag(String s) {
        // 过滤掉的sql关键字，可以手动添加
        String badStr = "'|exec |execute |insert |select |delete |update |count|drop |master|truncate |"
                + "declare |sitename|net member|xp_cmdshell|like'%|like '%|insert |create |"
                + "table|grant|use |group_concat|column_name|"
                + "information_schema.columns|table_schema|union |where |order by |group by ";
        String[] badStrs = badStr.split("\\|");
        for (int i = 0; i < badStrs.length; i++) {
            if (s.indexOf(badStrs[i]) >= 0) {
                WayLogger.error("请求拦截可能存在SQL和脚本注入 ,请求参数{}：", s);
                return true;
            }
        }
        return false;
    }

    private String[] specailCharArray = {"%00", "%2B", "%25E3%2580%2580"};

}
