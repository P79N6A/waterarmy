package com.xiaopeng.waterarmy.job;

import com.alibaba.fastjson.JSON;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.*;
import com.xiaopeng.waterarmy.handle.HandlerDispatcher;
import com.xiaopeng.waterarmy.handle.param.Content;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.service.TaskService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时评论点赞帖子任务
 * <p>
 * Created by iason on 2018/10/3.
 */
@Component
public class ScheduledCommentLikeTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledCommentLikeTask.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private HandlerDispatcher handlerDispatcher;

    @Scheduled(fixedRate = 6000)//5000
    public void reportCurrentTime() {
        List<Map<String, Object>> tasks = taskService.getExecutableTaskInfos(TaskTypeEnum.LIKE.getName());
        for (Map<String, Object> task : tasks) {
            //获取需要评论点赞的平台对应的用户列表
            String platform = MapUtils.getString(task, "platform");
            //获取评论点赞上下文
            RequestContext context = createCommentLikeContext(task);
            //执行评论点赞任务
            if (!ObjectUtils.isEmpty(context)) {
                commentLikeTask(context, task);
            } else {
                logger.error("获取评论点赞上下文为空! task：{}", JSON.toJSONString(task));
            }
        }
    }

    /**
     * 评论点赞
     *
     * @param context
     * @param task
     */
    private void commentLikeTask(RequestContext context, Map<String, Object> task) {
        Result<HandlerResultDTO> handlerResult = handlerDispatcher.dispatch(context);
        Map<String, Object> taskExecuteLog = new HashMap<>();
        BigInteger id = (BigInteger) task.get("id");
        Long taskInfoId = id.longValue();
        taskExecuteLog.put("taskInfoId", taskInfoId);
        taskExecuteLog.put("executor", "xiaoa");
        if (handlerResult.getSuccess()) {
            taskExecuteLog.put("executeStatus", ExecuteStatusEnum.SUCCEED.getIndex());
            taskService.updateFinishCount(taskInfoId);
        } else {
            taskExecuteLog.put("executeStatus", ExecuteStatusEnum.FAIL.getIndex());
            logger.error("评论点赞失败，handlerResult: {}", JSON.toJSONString(handlerResult));
        }
        try {
            taskExecuteLog.put("handlerResult", JSON.toJSONString(handlerResult));
            taskService.saveTaskExecuteLog(taskExecuteLog);
        } catch (Exception e) {
            logger.error("保存执行评论点赞log失败, ", e);
        }
    }

    /**
     * 获取评论点赞上下文
     *
     * @param task
     * @return
     */
    private RequestContext createCommentLikeContext(Map<String, Object> task) {
        RequestContext requestContext = null;
        try {
            requestContext = new RequestContext();
            Content content = new Content();
            String likeContent = MapUtils.getString(task, "likeContent");
            content.setText(likeContent);
            requestContext.setContent(content);
            requestContext.setHandleType(TaskTypeEnum.LIKE);
            String platform = MapUtils.getString(task, "platform");
            requestContext.setPlatform(PlatformEnum.getEnum(platform));
            String link = MapUtils.getString(task, "link");
            requestContext.setPrefixUrl(link);

            //设置子任务类型
            String module = String.valueOf(task.get("module"));
            if (PlatformEnum.YICHE.getName().equals(platform)) {
                if (PlatFormModuleEnum.CHEJIAHAO.getName().equals(module)) {
                    requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHECOMMENTPRAISE);
                }
            } else if (PlatformEnum.PCAUTO.getName().equals(platform)) {
                if (PlatFormModuleEnum.NEWS.getName().equals(module)) {
                    requestContext.setHandleEntryType(TaskEntryTypeEnum.TAIPINGYANGNEWSCOMMENTPRAISE);
                }
            } else if (PlatformEnum.XCAR.getName().equals(platform)) {
                if (PlatFormModuleEnum.NEWS.getName().equals(module)) {
                    requestContext.setHandleEntryType(TaskEntryTypeEnum.AIKANEWSCOMMENTPRAISE);
                }
            }
            return requestContext;
        } catch (Exception e) {
            logger.error("获取评论点赞上下文失败, ", e);
        }
        return requestContext;
    }

}