package spencercjh.top.fastrun.schedule;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spencercjh.top.fastrun.serviceimpl.FastRunServiceImpl;
import spencercjh.top.fastrun.entity.ParamData;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static spencercjh.top.fastrun.common.constant.CommonConstant.*;

/**
 * @author spencercjh
 */
@Component
@Log4j2
public class FastRunSchedule {

    private final FastRunServiceImpl fastRunServiceImpl;

    @Autowired
    public FastRunSchedule(FastRunServiceImpl fastRunServiceImpl) {
        this.fastRunServiceImpl = fastRunServiceImpl;
    }

    /***
     * 每天早上8点钟执行一次
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void fastRunEveryDay() {
        ParamData paramData = new ParamData();
        try {
            fastRunServiceImpl.login(paramData, MOBILE_VALUE, PASSWORD_VALUE);
            fastRunServiceImpl.runPage(paramData);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, HOUR_VALUE);
            calendar.set(Calendar.MINUTE, MINUTE_VALUE_1);
            calendar.set(Calendar.SECOND, SECOND_VALUE);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startTime = dateFormat.format(calendar.getTime());
            calendar.set(Calendar.MINUTE, MINUTE_VALUE_2);
            String endTime = dateFormat.format(calendar.getTime());
            fastRunServiceImpl.saveRun(paramData, startTime, endTime, DISTANCE, ISGIRL, FREQUENCY, PACE, DURATION_VALUE);
        } catch (Exception | Error e) {
            e.printStackTrace();
            log.error(paramData.getResult());
        }
        log.info(paramData.getResult());
    }
}
