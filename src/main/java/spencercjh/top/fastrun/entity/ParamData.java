package spencercjh.top.fastrun.entity;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import spencercjh.top.fastrun.common.vo.Result;

import static spencercjh.top.fastrun.common.constant.CommonConstant.*;

/**
 * @author spencercjh
 */
@SuppressWarnings("SpellCheckingInspection")
@Data
public class ParamData {
    private String utoken = "";
    private String cookie = "";
    private String userId = "";
    private String runPageId = "";
    private String runId = "";
    private JSONObject mustPassNodeOne = new JSONObject();
    private JSONArray passNodes = new JSONArray();
    private Result<Object> result;
    /**
     * 请求URL
     */
    private String loginUrl = "http://gxhttp.chinacloudapp.cn/api/reg/login?sign=SIGN&data=DATA";
    private String runPageUrl = "http://gxhttp.chinacloudapp.cn/api/run/runPage?sign=SIGN&data=DATA";
    private String saveRunUrl = "http://gxhttp.chinacloudapp.cn/api/run/saveRunV2";
    private String runDetailUrl = "http://gxhttp.chinacloudapp.cn/api/center/runDetailV2?sign=SIGN&data=DATA";

    /**
     * 设置Header
     *
     * @param httpRequest   请求
     * @param paramData     信息对象
     * @param needSetCookie 是否需要设置Cookie
     * @param needSetUToken 是否需要设置utoken
     * @param isSaveRun     是否是saveRun请求
     */
    public void setRequestHeader(HttpRequest httpRequest, ParamData paramData, boolean needSetCookie,
                                 boolean needSetUToken, boolean isSaveRun) {
        httpRequest.header(ACCEPT_LANGUAGE, ACCEPT_LANGUAGE_VALUE);
        httpRequest.header(USER_AGENT, USER_AGENT_VALUE);
        httpRequest.header(VERSION_CODE, VERSION_CODE_VALUE);
        httpRequest.header(VERSION_NAME, VERSION_NAME_VALUE);
        httpRequest.header(PLATFORM, PLATFORM_VALUE);
        httpRequest.header(XXVERSIONXX, XXVERSIONXX_VALUE);
        httpRequest.header(UUID, UUID_VALUE);
        httpRequest.header(UTOKEN, paramData.utoken);
        httpRequest.header(SECRET, SECRET_VALUE);
        httpRequest.header(HOST, HOST_VALUE);
        httpRequest.header(CONNECTION, CONNECTION_VALUE);
        httpRequest.header(ACCEPT_ENCODING, ACCEPT_ENCODING_VALUE);
        if (needSetCookie) {
            httpRequest.header(COOKIE, paramData.cookie);
        }
        if (needSetUToken) {
            httpRequest.header(UTOKEN, paramData.utoken);
        }
        if (isSaveRun) {
            httpRequest.header(CONTENT_TYPE, CONTENT_TYPE_VALUE);
            httpRequest.header(CONTENT_LENGTH, CONTENT_LENGTH_VALUE);
        }
    }
}
