package spencercjh.top.fastrun.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spencercjh.top.fastrun.common.constant.CommonConstant;
import spencercjh.top.fastrun.common.util.Json2Package;
import spencercjh.top.fastrun.common.util.ResultUtil;
import spencercjh.top.fastrun.common.util.UsernameUtil;
import spencercjh.top.fastrun.common.vo.Result;
import spencercjh.top.fastrun.entity.ParamData;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static spencercjh.top.fastrun.common.constant.CommonConstant.*;

/**
 * 高校体育攻击接口
 *
 * @author spencercjh
 */
@Log4j2
@RestController
@Api(description = "一键跑步接口")
public class RunController {

    @PostMapping("/run")
    @ApiOperation(value = "高校体育一键跑步API")
    public Result<Object> run(@ApiParam(name = "mobile", value = "手机号", type = "String", defaultValue = "15921916039")
                              @RequestParam @NonNull String mobile,
                              @ApiParam(name = "password", value = "密码", type = "String", defaultValue = "123456")
                              @RequestParam @NonNull String password,
                              @ApiParam(name = "startTime", value = "开始时间", type = "String", defaultValue = "2018-10-14 6:30:33")
                              @RequestParam @NonNull String startTime,
                              @ApiParam(name = "endTime", value = "结束时间", type = "String", defaultValue = "2018-10-14 6:00:39")
                              @RequestParam @NonNull String endTime,
                              @ApiParam(name = "distance", value = "距离(meter)", type = "Double", defaultValue = "3000")
                              @RequestParam @NonNull Double distance,
                              @ApiParam(name = "isGirl", value = "是否为女生", type = "Boolean", defaultValue = "false")
                              @RequestParam @NonNull Boolean isGirl,
                              @ApiParam(name = "frequency", value = "步频", type = "String", defaultValue = "101")
                              @RequestParam @NonNull String frequency,
                              @ApiParam(name = "pace", value = "配速(1公里耗时,eg:8'20'')", type = "String", defaultValue = "8'20''")
                              @RequestParam @NonNull String pace,
                              @ApiParam(name = "duration", value = "耗时(s)", type = "Long", defaultValue = "7200")
                              @RequestParam @NonNull Long duration) {
        if (!UsernameUtil.mobile(mobile)) {
            return new ResultUtil<>().setErrorMsg(501, "手机号格式有误");
        } else {
            ParamData paramData = new ParamData();
            try {
                login(paramData, mobile, password);
                runPage(paramData);
                saveRun(paramData, startTime, endTime, distance, isGirl, frequency, pace, duration);
            } catch (Exception | Error e) {
                e.printStackTrace();
                return paramData.getResult();
            }
            return paramData.getResult();
        }
    }

    /**
     * 登陆请求
     */
    private void login(ParamData paramData, String mobile, String password) throws Error, Exception {
        Map<String, String> loginBody = getLoginBody(mobile, password);
        paramData.setLoginUrl(paramData.getLoginUrl().replace("SIGN", loginBody.get(SIGN)).
                replace("DATA", loginBody.get(DATA)));
        log.info("loginUrl:" + paramData.getLoginUrl());
        HttpRequest loginRequest = HttpRequest.get(paramData.getLoginUrl());
        paramData.setRequestHeader(loginRequest, paramData, false, false, false);
        HttpResponse loginResponse = loginRequest.execute();
        String loginJsonResult = loginResponse.body();
        log.info("login result:" + loginJsonResult);
        JSONObject loginResponseResult = JSON.parseObject(loginJsonResult);
        paramData.setResult(new ResultUtil<>().setData(loginResponseResult));
        if (!loginResponseResult.getInteger(CODE).equals(CommonConstant.SUCCESS)) {
            throw new RuntimeException("无法正常跑步");
        }
        JSONObject loginResponseData = loginResponseResult.getJSONObject(DATA);
        paramData.setUtoken(loginResponseData.getString(UTOKEN));
        paramData.setUserId(loginResponseData.getString(USERID));
    }

    private Map<String, String> getLoginBody(String mobile, String password) throws Error, Exception {
        Map<String, String> data = new HashMap<>(4);
        data.put(INFO, INFO_VALUE);
        data.put(MOBILE, mobile);
        data.put(PASSWORD, password);
        data.put(TYPE_DEVICE, TYPE_DEVICE_VALUE);
        Map<String, String> body = new HashMap<>(2);
        String dataJson = JSON.toJSONString(data);
        body.put(SIGN, Json2Package.getSign(dataJson));
        body.put(DATA, Json2Package.getData(dataJson));
        log.info("login body:" + body);
        return body;
    }

    /**
     * 跑步界面请求
     */
    private void runPage(ParamData paramData) throws Error, Exception {
        Map<String, String> runPageBody = getRunPageBody(paramData.getUserId());
        paramData.setRunPageUrl(paramData.getRunPageUrl().replace("SIGN", runPageBody.get(SIGN)).
                replace("DATA", runPageBody.get(DATA)));
        log.info("runPageUrl:" + paramData.getRunPageUrl());
        HttpRequest runPageRequest = HttpRequest.get(paramData.getRunPageUrl());
        paramData.setRequestHeader(runPageRequest, paramData, true, true, false);
        String runPageJsonResult = runPageRequest.execute().body();
        log.info("run page result:" + runPageJsonResult);
        JSONObject runPageResponseResult = JSON.parseObject(runPageJsonResult);
        paramData.setResult(new ResultUtil<>().setData(runPageResponseResult));
        if (!runPageResponseResult.getInteger(CODE).equals(CommonConstant.SUCCESS)) {
            throw new RuntimeException("无法正常跑步");
        }
        JSONObject runPageResponseData = runPageResponseResult.getJSONObject(DATA);
        paramData.setRunPageId(runPageResponseData.getString(RUNPAGEID));
        JSONArray mustPassNodeJsonArray = runPageResponseData.getJSONArray(IBEACON);
        JSONObject mustPassNodeOne = mustPassNodeJsonArray.getJSONObject(0);
        paramData.setMustPassNodeOne(mustPassNodeOne);
        JSONArray passNodesJsonArray = runPageResponseData.getJSONArray(GPSINFO);
        paramData.setPassNodes(passNodesJsonArray);
    }

    private Map<String, String> getRunPageBody(String userId) throws Error, Exception {
        Map<String, String> data = new HashMap<>(4);
        data.put(INITLOCATION, INITLOCATION_VALUE);
        data.put(TYPE_RUN, TYPE_RUN_VALUE);
        data.put(USERID, userId);
        Map<String, String> body = new HashMap<>(2);
        String dataJson = JSON.toJSONString(data);
        body.put(SIGN, Json2Package.getSign(dataJson));
        body.put(DATA, Json2Package.getData(dataJson));
        log.info("run page body:" + body);
        return body;
    }

    /**
     * 上传跑步数据请求
     */
    private void saveRun(ParamData paramData, String startTime, String endTime, Double distance, boolean isGirl,
                         String frequency, String pace, Long duration) throws Error, Exception {
        Map<String, Object> saveRunBody = getSaveRunBody(paramData, paramData.getMustPassNodeOne(), paramData.getPassNodes(),
                startTime, endTime, distance, isGirl, frequency, pace, duration);
        String signAndData = JSON.toJSONString(saveRunBody);
        log.info("sign and data:" + signAndData);
        HttpRequest saveRunRequest = HttpRequest.post(paramData.getSaveRunUrl()).form(saveRunBody);
        paramData.setRequestHeader(saveRunRequest, paramData, true, true, true);
        log.info("saveRunUrl:" + paramData.getSaveRunUrl());
        HttpResponse saveRunResult = saveRunRequest.execute();
        String saveRunJsonResult = saveRunResult.body();
        log.info("save run result:" + saveRunJsonResult);
        JSONObject saveRunResponseResult = JSON.parseObject(saveRunJsonResult);
        paramData.setResult(new ResultUtil<>().setData(saveRunResponseResult));
    }

    private Map<String, Object> getSaveRunBody(ParamData paramData, JSONObject mustPassNodeOne, JSONArray passNodes, String startTime,
                                               String endTime, Double distance, boolean isGirl, String frequency,
                                               String pace, Long duration) throws Error, Exception {
        JSONArray newPassNodes = new JSONArray();
        for (int i = 0; i < passNodes.size(); ++i) {
            JSONObject singlePassNode = passNodes.getJSONObject(i);
            singlePassNode.put(SPEED, 0.0);
            newPassNodes.add(singlePassNode);
        }
        paramData.setPassNodes(newPassNodes);
        JSONObject position = mustPassNodeOne.getJSONObject(POSITION);
        position.put(SPEED, 0.0);
        mustPassNodeOne.put(POSITION, position);
        JSONArray bNode = new JSONArray();
        bNode.add(mustPassNodeOne);
        Map<String, Object> data = new HashMap<>(16);
        data.put(BUPIN, frequency);
        data.put(DURATION, String.valueOf(duration));
        data.put(END_TIME, endTime);
        data.put(FROMBP, FROMBP_VALUE);
        data.put(GOAL, (isGirl ? GOAL_VALUE_FEMALE : GOAL_VALUE_MALE));
        data.put(REAL, String.valueOf(distance));
        data.put(RUNPAGEID, paramData.getRunPageId());
        data.put(SPEED, pace);
        data.put(START_TIME, startTime);
        data.put(TYPE_RUN, TYPE_RUN_VALUE);
        data.put(USERID, paramData.getUserId());
        data.put(TOTAL_NUMBER, TOTAL_NUMBER_VALUE);
        String dataJsonString = JSON.toJSONString(data);
        JSONObject dataJsonObject = JSON.parseObject(dataJsonString);
        JSONObject myRun = JSON.parseObject(PREVIOUS_RUN);
        dataJsonObject.put(TNODE, passNodes);
        dataJsonObject.put(TRACK, myRun.get(TRACK));
        dataJsonObject.put(TREND, myRun.get(TREND));
        dataJsonObject.put(BNODE, bNode);
        String finalDataJson = JSON.toJSONString(dataJsonObject);
        Map<String, Object> body = new HashMap<>(2);
        body.put(SIGN, Json2Package.getSign(finalDataJson));
        body.put(DATA, finalDataJson);
        return body;
    }
}
