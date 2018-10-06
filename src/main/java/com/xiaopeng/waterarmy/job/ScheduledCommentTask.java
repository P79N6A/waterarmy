package com.xiaopeng.waterarmy.job;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.ExecuteStatusEnum;
import com.xiaopeng.waterarmy.common.enums.PlatformEnum;
import com.xiaopeng.waterarmy.common.enums.TaskTypeEnum;
import com.xiaopeng.waterarmy.handle.HandlerDispatcher;
import com.xiaopeng.waterarmy.handle.param.Content;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.service.AccountService;
import com.xiaopeng.waterarmy.service.ContentService;
import com.xiaopeng.waterarmy.service.TaskService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 定时评论帖子任务
 *
 * Created by iason on 2018/10/3.
 */
@Component
public class ScheduledCommentTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledCommentTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private TaskService taskService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private HandlerDispatcher handlerDispatcher;

    @Scheduled(fixedRate = 600000)//5000
    public void reportCurrentTime() {
        logger.info("定时评论啦，现在时间：" + dateFormat.format(new Date()));
        List<Map<String,Object>> tasks = taskService.getExecutableTaskInfos(TaskTypeEnum.COMMENT.getName());
    }

    private RequestContext createTestContext(Map<String,Object> task) {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("这个车很洋气~");//真心不错，我昨天试了一下，好想买
        requestContext.setContent(content);
        requestContext.setUserId(2L);
        requestContext.setUserLoginId("18482193356");
        requestContext.setHandleType(TaskTypeEnum.COMMENT);
        requestContext.setPlatform(PlatformEnum.PCAUTO);
        requestContext.setPrefixUrl("https://bbs.pcauto.com.cn/topic-17493073.html");
        return requestContext;
    }

}
