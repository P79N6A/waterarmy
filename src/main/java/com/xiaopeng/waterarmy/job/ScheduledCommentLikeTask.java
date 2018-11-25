package com.xiaopeng.waterarmy.job;

import com.alibaba.fastjson.JSON;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.*;
import com.xiaopeng.waterarmy.common.util.IPUtil;
import com.xiaopeng.waterarmy.common.util.ZhiMaProxyIpUtil;
import com.xiaopeng.waterarmy.handle.HandlerDispatcher;
import com.xiaopeng.waterarmy.handle.param.Content;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dao.AccountIPInfo;
import com.xiaopeng.waterarmy.model.dao.AccountTaskLog;
import com.xiaopeng.waterarmy.model.dao.CommentLikeLog;
import com.xiaopeng.waterarmy.model.dto.ProxyHttpConfig;
import com.xiaopeng.waterarmy.model.mapper.AccountTaskLogMapper;
import com.xiaopeng.waterarmy.model.mapper.CommentLikeLogMapper;
import com.xiaopeng.waterarmy.service.AccountService;
import com.xiaopeng.waterarmy.service.TaskService;
import org.apache.commons.collections.map.HashedMap;
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

    @Autowired
    private AccountService accountService;

    @Autowired
    private CommentLikeLogMapper commentLikeLogMapper;

    @Autowired
    private AccountTaskLogMapper accountTaskLogMapper;

    @Scheduled(fixedRate = 6000)//5000
    public void execute() {
        List<Map<String, Object>> tasks = taskService.getExecutableTaskInfos(TaskTypeEnum.LIKE.getName());
        for (Map<String, Object> task : tasks) {
            //获取需要评论点赞的平台对应的用户列表
            String platform = MapUtils.getString(task, "platform");
            List<Account> accounts = accountService.getAccountsByPlatform(platform);
            if (!ObjectUtils.isEmpty(accounts)) {
                //String publicIP = IPUtil.getPublicIP();
                Account commentLikeAccount = getExecutor(accounts);
                if (!ObjectUtils.isEmpty(commentLikeAccount)) {
                    //accounts.get(0);//getAccountByIP(accounts, platform, publicIP, task);
                    //获取评论点赞上下文
                    RequestContext context = createCommentLikeContext(task, commentLikeAccount);
                    //执行评论点赞任务
                    if (!ObjectUtils.isEmpty(context)) {
                        commentLikeTask(context, task, commentLikeAccount, platform);//, publicIP
                        if (!ObjectUtils.isEmpty(context.getProxyHttpConfig())) {
                            ProxyHttpConfig zhimaProxyIp = context.getProxyHttpConfig();
                            zhimaProxyIp.setUsed(true);
                        }
                    } else {
                        logger.error("获取评论点赞上下文为空! task：{}", JSON.toJSONString(task));
                    }
                } else {
                    logger.error("评论点赞失败，该平台用户量: {}, 没有合适的用户! task：{} "
                            , accounts.size(), JSON.toJSONString(task));
                }
            }
        }
    }

    /**
     * 评论点赞
     *
     * @param context
     * @param task
     */
    private void commentLikeTask(RequestContext context, Map<String, Object> task
            , Account commentLikeAccount, String platform) {//, String publicIP
        Result<HandlerResultDTO> handlerResult = handlerDispatcher.dispatch(context);
        Map<String, Object> taskExecuteLog = new HashMap<>();
        BigInteger id = (BigInteger) task.get("id");
        Long taskInfoId = id.longValue();
        taskExecuteLog.put("taskInfoId", taskInfoId);
        taskExecuteLog.put("executor", commentLikeAccount.getUserName());

        if (handlerResult.getSuccess()) {
            taskExecuteLog.put("executeStatus", ExecuteStatusEnum.SUCCEED.getIndex());
            taskService.updateFinishCount(taskInfoId);
            accountService.updateTaskCount(commentLikeAccount.getUserName());
            if (!ObjectUtils.isEmpty(context.getProxyHttpConfig())) {
                String publicIP = context.getProxyHttpConfig().getProxyHost();
                Map<String, Object> info
                        = accountService.getAccountIPInfo(publicIP, platform, commentLikeAccount.getUserName());
                if (ObjectUtils.isEmpty(info)) {
                    AccountIPInfo accountIPInfo = new AccountIPInfo();
                    accountIPInfo.setPlatform(platform);
                    accountIPInfo.setUserName(commentLikeAccount.getUserName());
                    accountIPInfo.setIp(publicIP);
                    accountService.saveAccountIPInfo(accountIPInfo);
                }
            }
            CommentLikeLog likeLog = new CommentLikeLog();
            likeLog.setUserName(commentLikeAccount.getUserName());
            likeLog.setPlatform(platform);
            likeLog.setLikeContent(context.getContent().getText());
            likeLog.setCommentLikeLink(context.getPrefixUrl());
            commentLikeLogMapper.save(likeLog);
            String publicIP = commentLikeAccount.getProxyHttpConfig().getProxyHost();
            saveAccountTaskLog(commentLikeAccount.getUserName(), platform
                    , "", publicIP, context.getPrefixUrl()
                    , TaskTypeEnum.LIKE.getName());
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
    private RequestContext createCommentLikeContext(Map<String, Object> task, Account commentLikeAccount) {
        RequestContext requestContext = null;
        try {
            requestContext = new RequestContext();
            Content content = new Content();
            String likeContent = MapUtils.getString(task, "likeContent");
            content.setText(likeContent);
            requestContext.setContent(content);
            requestContext.setUserId(commentLikeAccount.getId());
            requestContext.setUserLoginId(commentLikeAccount.getUserName());
            requestContext.setHandleType(TaskTypeEnum.LIKE);
            String platform = MapUtils.getString(task, "platform");
            requestContext.setPlatform(PlatformEnum.getEnum(platform));
            String link = MapUtils.getString(task, "link");
            requestContext.setPrefixUrl(link);

            Map requestParam = new HashMap();
            requestContext.setRequestParam(requestParam);

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
            ProxyHttpConfig zhimaProxyIp = ZhiMaProxyIpUtil.getZhimaProxyIp();
            requestContext.setProxyHttpConfig(zhimaProxyIp);
            return requestContext;
        } catch (Exception e) {
            logger.error("获取评论点赞上下文失败, ", e);
        }
        return requestContext;
    }

    /**
     * 获取可执行任务用户
     *
     * @param accounts
     * @return
     */
    private Account getExecutor(List<Account> accounts) {
        Account executor = null;
        for (Account account : accounts) {
            if (PlatformEnum.AUTOHOME.getName()
                    .equals(account.getPlatform())) {
                Map<String, Object> params = new HashedMap();
                params.put("userName", account.getUserName());
                params.put("platform", account.getPlatform());
                List<Map<String, Object>> logs = accountTaskLogMapper.getAccountTaskLogs(params);
                if (ObjectUtils.isEmpty(logs)) {
                    executor = account;
                    break;
                }
            } else {
                executor = account;
                break;
            }
        }
        return executor;
    }

    private void saveAccountTaskLog(String userName, String platform
            , String content, String publicIP, String link, String taskType) {
        AccountTaskLog accountTaskLog = new AccountTaskLog();
        accountTaskLog.setUserName(userName);
        accountTaskLog.setPlatform(platform);
        accountTaskLog.setTaskType(taskType);
        accountTaskLog.setLink(link);
        accountTaskLog.setContent(content);
        accountTaskLog.setIp(publicIP);
        accountTaskLogMapper.save(accountTaskLog);
    }

//    private Account getAccountByIP(List<Account> accounts, String platform, String pulicIP, Map<String, Object> task) {
//        for (Account acc: accounts) {
//            Map<String, Object> likeInfo = new HashMap<>();
//            likeInfo.put("userName", acc.getUserName());
//            likeInfo.put("platform", platform);
//            likeInfo.put("commentLikeLink", task.get("link"));
//            likeInfo.put("likeContent", task.get("likeContent"));
//            CommentLikeLog commentLikeLog = commentLikeLogMapper.getContentLikeLog(likeInfo);
//            if (!ObjectUtils.isEmpty(commentLikeLog)) {
//                continue;
//            }
//            if (!ObjectUtils.isEmpty(pulicIP)) {
//                Map<String, Object> accountIPInfo
//                        = accountService.getAccountIPInfo(pulicIP, platform, null);
//                if (!ObjectUtils.isEmpty(accountIPInfo)) {
//                    String publicIPUserName = String.valueOf(accountIPInfo.get("userName"));
//                    if (acc.getUserName().equals(publicIPUserName)) {
//                        return acc;
//                    } else {
//                        return accountService.getAccountByUserName(publicIPUserName);
//                    }
//                } else {
//                    return acc;
//                }
//            }
//        }
//        return accounts.get(0);
//    }

}