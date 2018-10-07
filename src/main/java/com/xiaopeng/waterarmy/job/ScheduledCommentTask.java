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
 * 定时评论帖子任务
 * <p>
 * Created by iason on 2018/10/3.
 */
@Component
public class ScheduledCommentTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledCommentTask.class);

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
        logger.info("定时评论啦，现在时间：" + dateFormat.format(new Date()));
        List<Map<String, Object>> tasks = taskService.getExecutableTaskInfos(TaskTypeEnum.COMMENT.getName());
        for (Map<String, Object> task : tasks) {
            //获取需要评论的平台对应的用户列表
            String platform = MapUtils.getString(task, "platform");
            List<Account> accounts = accountService.getAccountsByPlatform(platform);
            //获取发帖内容库内容信息
            List<ContentInfo> contentInfos
                    = contentService.querysByRepositoriesType(ContentRepositoriesEnum.COMMENT.getName());
            if (!ObjectUtils.isEmpty(accounts) && !ObjectUtils.isEmpty(contentInfos)) {
                //随机获取待执行评论任务用户id
                Integer commentAccountNum = NumUtil.getRandomNum(accounts.size());
                Account commentAccount = accounts.get(commentAccountNum);
                //随机获取内容库评论内容id
                Integer commentContentNum = NumUtil.getRandomNum(contentInfos.size());
                ContentInfo commentContent = contentInfos.get(commentContentNum);
                //获取评论上下文
                RequestContext context = createCommentContext(task, commentAccount, commentContent);
                //执行评论任务
                if (!ObjectUtils.isEmpty(context)) {
                    commentTask(context, task, commentAccount, commentContent);
                } else {
                    logger.error("获取评论上下文为空! task：{}", JSON.toJSONString(task));
                }
            } else {
                logger.error("评论失败，平台 {} 用户列表为空，评论内容库为空! task：{} "
                        , platform, JSON.toJSONString(task));
            }
        }
    }

    /**
     * 评论
     *
     * @param context
     * @param task
     * @param commentAccount
     * @param commentContent
     */
    private void commentTask(RequestContext context, Map<String, Object> task
            , Account commentAccount, ContentInfo commentContent) {
        Result<HandlerResultDTO> handlerResult = handlerDispatcher.dispatch(context);
        Map<String, Object> taskExecuteLog = new HashMap<>();
        BigInteger id = (BigInteger) task.get("id");
        Long taskInfoId = id.longValue();
        taskExecuteLog.put("taskInfoId", taskInfoId);
        taskExecuteLog.put("contentInfoId", commentContent.getId());
        taskExecuteLog.put("executor", commentAccount.getUserName());
        if (handlerResult.getSuccess()) {
            taskExecuteLog.put("executeStatus", ExecuteStatusEnum.SUCCEED.getIndex());
            taskService.updateFinishCount(taskInfoId);
        } else {
            taskExecuteLog.put("executeStatus", ExecuteStatusEnum.FAIL.getIndex());
            logger.error("评论失败，handlerResult: {}", JSON.toJSONString(handlerResult));
        }
        try {
            taskExecuteLog.put("handlerResult", JSON.toJSONString(handlerResult));
            taskService.saveTaskExecuteLog(taskExecuteLog);
        } catch (Exception e) {
            logger.error("保存执行评论log失败, ", e);
        }
    }

    /**
     * 获取评论上下文
     *
     * @param task
     * @param commentAccount
     * @param commentContent
     * @return
     */
    private RequestContext createCommentContext(Map<String, Object> task
            , Account commentAccount, ContentInfo commentContent) {
        RequestContext requestContext = null;
        try {
            requestContext = new RequestContext();
            Content content = new Content();
            content.setText(commentContent.getContent()); //"这个车很洋气~" //真心不错，我昨天试了一下，好想买
            requestContext.setContent(content);
            requestContext.setUserId(commentAccount.getId());
            requestContext.setUserLoginId(commentAccount.getUserName());//"18482193356"
            requestContext.setHandleType(TaskTypeEnum.COMMENT);
            String platform = MapUtils.getString(task, "platform");
            requestContext.setPlatform(PlatformEnum.getEnum(platform));
            String link = MapUtils.getString(task, "link");
            requestContext.setPrefixUrl(link);//"https://bbs.pcauto.com.cn/topic-17493073.html"
            return requestContext;

        } catch (Exception e) {
            logger.error("获取评论上下文失败, ", e);
        }
        return requestContext;
    }

}
