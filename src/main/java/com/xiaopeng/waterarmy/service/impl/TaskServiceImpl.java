package com.xiaopeng.waterarmy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.xiaopeng.waterarmy.common.enums.*;
import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.common.util.DateUtil;
import com.xiaopeng.waterarmy.handle.HandlerDispatcher;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dao.TaskInfo;
import com.xiaopeng.waterarmy.model.mapper.*;
import com.xiaopeng.waterarmy.service.TaskService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * * 功能描述：
 * <p> 版权所有：
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
@Service
public class TaskServiceImpl implements TaskService {

    private static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TaskPublishMapper taskPublishMapper;

    @Autowired
    private ContentInfoMapper contentInfoMapper;

    @Autowired
    private LinkInfoMapper linkInfoMapper;

    @Autowired
    private RuleInfoMapper ruleInfoMapper;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private TaskExcuteLogMapper taskExcuteLogMapper;

    @Autowired
    private HandlerDispatcher handlerDispatcher;

    @Override
    public PageInfo<Map<String, Object>> taskPublishPage(Integer pageNo, Integer pageSize, Map<String, Object> params) {
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = taskPublishMapper.getTaskPublishs(params);
        setResults(results, true);
        return new PageInfo<>(results);
    }

    @Override
    public PageInfo<Map<String, Object>> taskInfoPage(Integer pageNo, Integer pageSize, Map<String, Object> params) {

        handlerDispatcher.dispatch(null);

        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = taskInfoMapper.getTaskInfos(params);
        setResults(results, false);
        return new PageInfo<>(results);
    }

    @Override
    public PageInfo<Map<String, Object>> taskExcuteLogPage(Integer pageNo, Integer pageSize, Map<String, Object> params) {
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = taskExcuteLogMapper.getTaskExcuteLogs(params);
        for (Map<String,Object> result: results) {
            Integer excuteStatus = MapUtils.getInteger(result,"excuteStatus");
            if (!ObjectUtils.isEmpty(excuteStatus)) {
                result.put("excuteStatusDesc", ExcuteStatusEnum.getDesc(excuteStatus));
            }
        }
        return new PageInfo<>(results);
    }

    @Override
    public JsonMessage recoveryTask(Long taskId) {
        return null;
    }

    @Override
    public JsonMessage stopTask(Long taskId) {
        return null;
    }

    @Override
    public JsonMessage publishTask(Map<String, Object> params) {
        JsonMessage message = JsonMessage.init();
        params.put("finishCount", 0);
        params.put("status", TaskStatusEnum.DOING.getIndex());
        params.put("creator", "xiaoa");
        params.put("updater", "xiaoa");
        String content = MapUtils.getString(params, "content");
        String[] linkInfos = content.split("\n");
        List<Map<String, Object>> links = new ArrayList<>();
        for (String info: linkInfos) {
            String link = info.substring(0, info.indexOf(","));
            System.out.println(info.substring(info.indexOf(",") + 1));
            Integer executeCount = Integer.parseInt(info.substring(info.indexOf(",") + 1));
            Map<String, Object> param = new HashMap<>();
            param.put("link", link);
            param.put("executeCount", executeCount);
            links.add(param);
        }
        for (Map<String, Object> link: links) {
            params.put("link", link.get("link"));
            params.put("executeCount", link.get("executeCount"));
            taskInfoMapper.save(params);
        }

        message.success(CodeEnum.SUCCESS).setMsg("发布任务成功!");
        return message;
    }

    @Override
    public JsonMessage getTaskDetail(Long taskId) {
        return null;
    }

    private void setResults(List<Map<String,Object>> results, boolean isTaskPublish) {
        for (Map<String,Object> result: results) {
            String platform = MapUtils.getString(result,"platform");
            if (!ObjectUtils.isEmpty(platform)) {
                result.put("platformDesc", PlatformEnum.getDesc(platform));
            }
            String module = MapUtils.getString(result,"module");
            if (!ObjectUtils.isEmpty(module)) {
                result.put("moduleDesc", PlatFormModuleEnum.getDesc(module));
            }
            String taskType = MapUtils.getString(result,"taskType");
            if (!ObjectUtils.isEmpty(taskType)) {
                result.put("taskTypeDesc", TaskTypeEnum.getDesc(taskType));
            }
            Integer status = MapUtils.getInteger(result,"status");
            if (isTaskPublish) {
                if (!ObjectUtils.isEmpty(status)) {
                    result.put("statusDesc", PlatformStatusEnum.getDesc(status));
                }
                Integer executableCount = 0;
                if (!ObjectUtils.isEmpty(taskType) && !ObjectUtils.isEmpty(platform)) {
                    executableCount = getExecutableCountByTaskType(taskType, platform);
                }
                result.put("executableCount", executableCount);
            } else {
                if (!ObjectUtils.isEmpty(status)) {
                    result.put("statusDesc", TaskStatusEnum.getDesc(status));
                }
                Integer executeCount =  MapUtils.getInteger(result,"execute_count");
                Integer finishCount =  MapUtils.getInteger(result,"finish_count");
                if (!ObjectUtils.isEmpty(executeCount) && !ObjectUtils.isEmpty(finishCount)
                        && executeCount > 0 && (executeCount - finishCount == 0)) {

                }
            }
        }
    }

    /**
     *
     * 获取可执行次数
     *
     * @param taskType
     * @param platform
     * @return
     */
    private Integer getExecutableCountByTaskType(String taskType, String platform) {
        Integer executableCount = 0;
        List<Account> accounts = accountMapper.getAccountsByPlatform(platform);
        Date createStartDate = DateUtil.getStartTime();
        Date createEndDate = DateUtil.getNowEndTime();
        Map<String, Object> params = new HashMap<>();
        params.put("createStartDate", createStartDate);
        params.put("createEndDate", createEndDate);
        params.put("platform", platform);
        params.put("taskType", taskType);
        List<Map<String,Object>> tasks = taskPublishMapper.getTaskPublishs(params);
        Integer accountSize = !ObjectUtils.isEmpty(accounts) ? accounts.size() : 0;
        Integer taskSize =  !ObjectUtils.isEmpty(tasks) ? tasks.size() : 0;
        if (TaskTypeEnum.POSIED.getName().equals(taskType)) {
            if (accountSize - taskSize > 0) {
                executableCount = accountSize - taskSize;
            }
        } else if (TaskTypeEnum.COMMENT.getName().equals(taskType)){
            if (accountSize - taskSize > 0) {
                executableCount = accountSize * 3 - taskSize;
            }
        } else {
            executableCount = -1;
        }
        return executableCount;
    }

}
