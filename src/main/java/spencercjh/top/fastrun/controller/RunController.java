package spencercjh.top.fastrun.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spencercjh.top.fastrun.service.FastRunService;
import spencercjh.top.fastrun.common.util.ResultUtil;
import spencercjh.top.fastrun.common.util.UsernameUtil;
import spencercjh.top.fastrun.common.vo.Result;
import spencercjh.top.fastrun.entity.ParamData;


/**
 * 高校体育攻击接口
 *
 * @author spencercjh
 */
@Log4j2
@RestController
@Api(description = "一键跑步接口")
public class RunController {

    private final FastRunService fastRunService;

    @Autowired
    public RunController(FastRunService fastRunService) {
        this.fastRunService = fastRunService;
    }

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
                fastRunService.login(paramData, mobile, password);
                fastRunService.runPage(paramData);
                fastRunService.saveRun(paramData, startTime, endTime, distance, isGirl, frequency, pace, duration);
            } catch (Exception | Error e) {
                e.printStackTrace();
                return paramData.getResult();
            }
            return paramData.getResult();
        }
    }


}
