package com.xiaopeng.waterarmy.job;

import com.alibaba.fastjson.JSON;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.ContentRepositoriesEnum;
import com.xiaopeng.waterarmy.common.enums.ExecuteStatusEnum;
import com.xiaopeng.waterarmy.common.enums.PlatformEnum;
import com.xiaopeng.waterarmy.common.enums.TaskTypeEnum;
import com.xiaopeng.waterarmy.common.util.NumUtil;
import com.xiaopeng.waterarmy.handle.HandlerDispatcher;
import com.xiaopeng.waterarmy.handle.param.Content;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dao.ContentInfo;
import com.xiaopeng.waterarmy.service.AccountService;
import com.xiaopeng.waterarmy.service.ContentService;
import com.xiaopeng.waterarmy.service.TaskService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时执行发帖帖子任务
 * <p>
 * Created by iason on 2018/10/3.
 */
//@Component
public class ScheduledPublishTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledPublishTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        logger.info("定时发帖啦，现在时间：" + dateFormat.format(new Date()));
        List<Map<String, Object>> tasks = taskService.getExecutableTaskInfos(TaskTypeEnum.POSIED.getName());
        for (Map<String, Object> task : tasks) {
            //获取需要发帖的平台对应的用户列表
            String platform = MapUtils.getString(task, "platform");
            List<Account> accounts = accountService.getAccountsByPlatform(platform);
            //获取发帖内容库内容信息
            List<ContentInfo> contentInfos
                    = contentService.querysByRepositoriesType(ContentRepositoriesEnum.POSIED.getName());
            if (!ObjectUtils.isEmpty(accounts) && !ObjectUtils.isEmpty(contentInfos)) {
                //随机获取待执行发帖任务用户id
                Integer publishAccountNum = NumUtil.getRandomNum(accounts.size());
                Account publishAccount = accounts.get(publishAccountNum);
                //随机获取内容库发帖内容id
                Integer publishContentNum = NumUtil.getRandomNum(contentInfos.size());
                ContentInfo publishContent = contentInfos.get(publishContentNum);
                //获取发帖上下文
                RequestContext context = createPublishTaskContext(task, publishAccount, publishContent);
                //执行发帖任务
                if (!ObjectUtils.isEmpty(context)) {
                    publishTask(context, task, publishAccount, publishContent);
                } else {
                    logger.error("获取发帖上下文为空! task：{}", JSON.toJSONString(task));
                }
            } else {
                logger.error("发帖失败，平台 {} 用户列表为空，发帖内容库为空! task：{} "
                        , platform, JSON.toJSONString(task));
            }
        }
    }

    /**
     * 发帖
     *
     * @param context
     * @param task
     * @param publishAccount
     */
    private void publishTask(RequestContext context, Map<String, Object> task
            , Account publishAccount, ContentInfo publishContent) {
        Result<HandlerResultDTO> handlerResult = handlerDispatcher.dispatch(context);
        Map<String, Object> taskExecuteLog = new HashMap<>();
        BigInteger id = (BigInteger) task.get("id");
        Long taskInfoId = id.longValue();
        taskExecuteLog.put("taskInfoId", taskInfoId);
        taskExecuteLog.put("contentInfoId", publishContent.getId());
        taskExecuteLog.put("executor", publishAccount.getUserName());
        if (handlerResult.getSuccess()) {
            taskExecuteLog.put("executeStatus", ExecuteStatusEnum.SUCCEED.getIndex());
            taskService.updateFinishCount(taskInfoId);
        } else {
            taskExecuteLog.put("executeStatus", ExecuteStatusEnum.FAIL.getIndex());
            logger.error("发帖失败，handlerResult: {}", JSON.toJSONString(handlerResult));
        }
        try {
            taskExecuteLog.put("handlerResult", JSON.toJSONString(handlerResult));
            taskService.saveTaskExecuteLog(taskExecuteLog);
        } catch (Exception e) {
            logger.error("保存执行log失败, ", e);
        }
    }

    /**
     * 获取发帖上下文
     *
     * @param task
     * @param publishAccount
     * @param publishContent
     * @return
     */
    private RequestContext createPublishTaskContext(Map<String, Object> task
            , Account publishAccount, ContentInfo publishContent) {
        RequestContext requestContext = null;
        try {
            requestContext = new RequestContext();
            Content content = new Content();
            content.setText(publishContent.getContent());
            //"出来工作也有些年头了，一个人在外面的确不容易，每天上班搭公交，每次回家坐长途" +
            //"，真的很累，驾照考出来都两年多了"
            content.setTitle(publishContent.getTitle());//"这车什么时候量产上市"
            requestContext.setContent(content);
            requestContext.setUserId(publishAccount.getId());//4L
            requestContext.setUserLoginId(publishAccount.getUserName());//"18383849422"
            requestContext.setHandleType(TaskTypeEnum.POSIED);
            String platform = MapUtils.getString(task, "platform");
            requestContext.setPlatform(PlatformEnum.getEnum(platform));//PlatformEnum.YICHE
            String link = MapUtils.getString(task, "link");
            requestContext.setPrefixUrl(link);//"http://baa.bitauto.com/langdong/"
        } catch (Exception e) {
            logger.error("获取发帖上下文失败, ", e);
        }
        return requestContext;
    }

}
