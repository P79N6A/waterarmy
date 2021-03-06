package com.xiaopeng.waterarmy.job;

import com.alibaba.fastjson.JSON;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.*;
import com.xiaopeng.waterarmy.common.util.NumUtil;
import com.xiaopeng.waterarmy.common.util.ZhiMaProxyIpUtil;
import com.xiaopeng.waterarmy.handle.HandlerDispatcher;
import com.xiaopeng.waterarmy.handle.param.Content;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dao.AccountIPInfo;
import com.xiaopeng.waterarmy.model.dao.AccountTaskLog;
import com.xiaopeng.waterarmy.model.dao.ContentInfo;
import com.xiaopeng.waterarmy.model.dto.ProxyHttpConfig;
import com.xiaopeng.waterarmy.model.mapper.AccountTaskLogMapper;
import com.xiaopeng.waterarmy.service.AccountService;
import com.xiaopeng.waterarmy.service.ContentService;
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
import java.text.SimpleDateFormat;
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

    @Autowired
    private AccountTaskLogMapper accountTaskLogMapper;

    @Scheduled(fixedRate = 6000)//5000
    public void execute() {
        //logger.info("定时评论啦，现在时间：" + dateFormat.format(new Date()));
        List<Map<String, Object>> tasks = taskService.getExecutableTaskInfos(TaskTypeEnum.COMMENT.getName());
        for (Map<String, Object> task : tasks) {
            //获取需要评论的平台对应的用户列表
            String platform = MapUtils.getString(task, "platform");
            List<Account> accounts = accountService.getAccountsByPlatform(platform);
            //获取发帖内容库内容信息
            List<ContentInfo> contentInfos
                    = contentService.querysRepositorieContents(String.valueOf(task.get("contentRepositoriesType"))
                    , String.valueOf(task.get("contentRepositoriesName")));
            if (!ObjectUtils.isEmpty(accounts) && !ObjectUtils.isEmpty(contentInfos)) {
                //String publicIP = IPUtil.getPublicIP();
                //随机获取待执行评论任务用户id
                //Integer commentAccountNum = NumUtil.getRandomNum(accounts.size());
                //优先用执行任务最少的账号执行任务
                //Account commentAccount = accounts.get(0);//commentAccountNum
                Account commentAccount = getExecutor(accounts);
                if (!ObjectUtils.isEmpty(commentAccount)) {
                    //accounts.get(0);//getAccountByIP(accounts, platform, publicIP);
                    //随机获取内容库评论内容id
                    Integer commentContentNum = NumUtil.getRandomNum(contentInfos.size());
                    ContentInfo commentContent = contentInfos.get(commentContentNum);
                    //获取评论上下文
                    RequestContext context = createCommentContext(task, commentAccount, commentContent);
                    //执行评论任务
                    if (!ObjectUtils.isEmpty(context)) {
                        boolean isSucceed = commentTask(context, task, commentAccount, commentContent, platform);//, publicIP
                        if (!isSucceed) {
                            commentTask(context, task, commentAccount, commentContent, platform);//, publicIP
                        }
                        if (!ObjectUtils.isEmpty(context.getProxyHttpConfig())) {
                            ProxyHttpConfig zhimaProxyIp = context.getProxyHttpConfig();
                            zhimaProxyIp.setUsed(true);
                        }
                    } else {
                        logger.error("获取评论上下文为空! task：{}", JSON.toJSONString(task));
                    }
                } else {
                    logger.error("评论失败，该平台用户量: {}, 没有合适的用户! task：{} "
                            , accounts.size(), JSON.toJSONString(task));
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
    private boolean commentTask(RequestContext context, Map<String, Object> task
            , Account commentAccount, ContentInfo commentContent, String platform) {//, String publicIP
        boolean isSucceed = false;
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
            accountService.updateTaskCount(commentAccount.getUserName());
            if (!ObjectUtils.isEmpty(context.getProxyHttpConfig())) {
                String publicIP = context.getProxyHttpConfig().getProxyHost();
                Map<String, Object> info
                        = accountService.getAccountIPInfo(publicIP
                        , platform, commentAccount.getUserName());
                if (ObjectUtils.isEmpty(info)) {
                    AccountIPInfo accountIPInfo = new AccountIPInfo();
                    accountIPInfo.setIp(publicIP);
                    accountIPInfo.setPlatform(platform);
                    accountIPInfo.setUserName(commentAccount.getUserName());
                    accountService.saveAccountIPInfo(accountIPInfo);
                }
                saveAccountTaskLog(commentAccount.getUserName(), platform
                        , commentContent.getContent(), publicIP, context.getPrefixUrl()
                        , TaskTypeEnum.COMMENT.getName());
            }
            isSucceed = true;
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
        return isSucceed;
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

            //设置子任务类型
            String module = String.valueOf(task.get("module"));
            if (PlatformEnum.YICHE.getName().equals(platform)) {
                if (PlatFormModuleEnum.CHEJIAHAO.getName().equals(module)) {
                    requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHENEWSCOMMENT);
                } else if (PlatFormModuleEnum.NEWS.getName().equals(module)) {
                    requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHENEWSCOMMENT);
                } else if (PlatFormModuleEnum.KOUBEI.getName().equals(module)) {
                    requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHEKOUBEICOMMENT);
                }
            } else if (PlatformEnum.PCAUTO.getName().equals(platform)) {
                if (PlatFormModuleEnum.NEWS.getName().equals(module)) {
                    requestContext.setHandleEntryType(TaskEntryTypeEnum.TAIPINGYANGNEWSCOMMENT);
                } else if (PlatFormModuleEnum.CAROWNER_COMMENT.getName().equals(module)) {
                    requestContext.setHandleEntryType(TaskEntryTypeEnum.TAIPINGYANGCHEZHUCOMMENT);
                }
            } else if (PlatformEnum.XCAR.getName().equals(platform)) {
                if (PlatFormModuleEnum.NEWS.getName().equals(module)) {
                    requestContext.setHandleEntryType(TaskEntryTypeEnum.AIKANEWSCOMMENT);
                }
            } else if (PlatformEnum.QCTT.getName().equals(platform)) {
                if (PlatFormModuleEnum.VIDEO_COMMENT.getName().equals(module)) {
                    requestContext.setHandleEntryType(TaskEntryTypeEnum.QICHEVIDEOCOMMENT);
                }
            } else if (PlatformEnum.AUTOHOME.getName().equals(platform)) {
                if (PlatFormModuleEnum.CHEJIAHAO.getName().equals(module)) {
                    requestContext.setHandleEntryType(TaskEntryTypeEnum.AUTOHOMECHEJIAHAOCOMMENT);
                } else if (PlatFormModuleEnum.KOUBEI.getName().equals(module)) {
                    requestContext.setHandleEntryType(TaskEntryTypeEnum.AUTOHOMEKOUBEICOMMENT);
                }
            }
            ProxyHttpConfig zhimaProxyIp = ZhiMaProxyIpUtil.getZhimaProxyIp();
            requestContext.setProxyHttpConfig(zhimaProxyIp);
            return requestContext;
        } catch (Exception e) {
            logger.error("获取评论上下文失败, ", e);
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

//    private Account getAccountByIP(List<Account> accounts, String platform, String publicIP) {
//        for (Account acc: accounts) {
//            if (!ObjectUtils.isEmpty(publicIP)) {
//                Map<String, Object> accountIPInfo
//                        = accountService.getAccountIPInfo(publicIP, platform, null);
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