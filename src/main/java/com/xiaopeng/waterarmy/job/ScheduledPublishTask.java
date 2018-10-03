package com.xiaopeng.waterarmy.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * 定时执行发帖帖子任务
 *
 * Created by iason on 2018/10/3.
 */
@Component
public class ScheduledPublishTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledPublishTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        //logger.info("定时发帖啦，现在时间：" + dateFormat.format(new Date()));
    }
}
