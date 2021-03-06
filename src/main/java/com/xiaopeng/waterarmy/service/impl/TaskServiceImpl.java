package com.xiaopeng.waterarmy.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.enums.*;
import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.common.util.DateUtil;
import com.xiaopeng.waterarmy.handle.HandlerDispatcher;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dao.TaskImageInfo;
import com.xiaopeng.waterarmy.model.dao.TaskPublish;
import com.xiaopeng.waterarmy.model.mapper.*;
import com.xiaopeng.waterarmy.service.TaskService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
    private TaskExecuteLogMapper taskExecuteLogMapper;

    @Autowired
    private HandlerDispatcher handlerDispatcher;

    @Autowired
    private TaskImageInfoMapper taskImageInfoMapper;

    @Override
    public PageInfo<Map<String, Object>> taskPublishPage(Integer pageNo, Integer pageSize, Map<String, Object> params) {
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = taskPublishMapper.getTaskPublishs(params);
        setResults(results, true);
        return new PageInfo<>(results);
    }

    @Override
    public PageInfo<Map<String, Object>> taskInfoPage(Integer pageNo, Integer pageSize, Map<String, Object> params) {
        //handlerDispatcher.dispatch(null);

        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = taskInfoMapper.getTaskInfos(params);
        setResults(results, false);
        return new PageInfo<>(results);
    }

    @Override
    public PageInfo<Map<String, Object>> taskExecuteLogPage(Integer pageNo, Integer pageSize, Map<String, Object> params) {
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = taskExecuteLogMapper.getTaskExecuteLogs(params);
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
            if (!ObjectUtils.isEmpty(module)) {
                result.put("taskTypeDesc", TaskTypeEnum.getDesc(taskType));
            }
            Integer executeStatus = MapUtils.getInteger(result,"executeStatus");
            if (!ObjectUtils.isEmpty(executeStatus)) {
                result.put("executeStatusDesc", ExecuteStatusEnum.getDesc(executeStatus));
            }
        }
        return new PageInfo<>(results);
    }

    @Override
    public boolean saveTaskExecuteLog(Map<String, Object> params) {
        try {
            if (ObjectUtils.isEmpty(params.get("executor"))) {
                params.put("executor", "xiaoa");
            }
            taskExecuteLogMapper.save(params);
        } catch (Exception e) {
            logger.error("saveTaskExecuteLog error, params, {}, ", e, JSON.toJSONString(params));
            return false;
        }
        return true;
    }

    @Override
    public boolean updateFinishCount(Long id) {
        try {
            taskInfoMapper.updateExecuteCount(id);
            taskInfoMapper.updateFinishStatus(id);
        } catch (Exception e) {
         logger.error("更新任务 id ,{}完成次数失败, ", id, e);
            return false;
        }
        return true;
    }

    @Override
    public List<Map<String,Object>> getExecutableTaskInfos(String taskType) {
        Map<String, Object> params = new HashMap<>();
        params.put("taskType", taskType);
        params.put("status", TaskStatusEnum.DOING.getIndex());
        List<Map<String,Object>> tasks = taskInfoMapper.getExecutableTaskInfos(params);
        return tasks;
    }

    @Override
    public JsonMessage recoveryTask(Long id) {
        JsonMessage message = JsonMessage.init();
        taskInfoMapper.updateStatus(id, TaskStatusEnum.DOING.getIndex());
        message.success(CodeEnum.SUCCESS).setMsg("恢复任务成功!");
        return message;
    }

    @Override
    public JsonMessage stopTask(Long id) {
        JsonMessage message = JsonMessage.init();
        taskInfoMapper.updateStatus(id, TaskStatusEnum.PAUSING.getIndex());
        message.success(CodeEnum.SUCCESS).setMsg("停止任务成功!");
        return message;
    }

    @Override
    public JsonMessage publishTask(Map<String, Object> params) {
        JsonMessage message = JsonMessage.init();
        Map<String, Object>  taskPublish = taskPublishMapper.getTaskPublishById(
                Long.parseLong(String.valueOf(params.get("taskPublishId"))));
        params.put("taskType", MapUtils.getString(taskPublish, "taskType"));
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
            param.put("executeCount", executeCount);
            param.put("executeCount", executeCount);
            links.add(param);
        }
        for (Map<String, Object> link: links) {
            params.put("link", link.get("link"));
            params.put("executeCount", link.get("executeCount"));
            params.put("id", null);
            taskInfoMapper.save(params);

            TaskImageInfo taskImageInfo = new TaskImageInfo();
            taskImageInfo.setFileName(String.valueOf(params.get("imageName")));
            taskImageInfo.setFilePath(String.valueOf(params.get("imagePath")));
            taskImageInfo.setTaskId(String.valueOf(params.get("id")));
            taskImageInfoMapper.save(taskImageInfo);
        }
        message.success(CodeEnum.SUCCESS).setMsg("发布任务成功!");
        return message;
    }

    @Override
    public JsonMessage addPublishTask(Map<String, Object> params) {
        JsonMessage message = JsonMessage.init();
        try {
            params.put("creator", "xiaoa");
            params.put("updater", "xiaoa");
            taskPublishMapper.save(params);
        } catch (Exception e) {
            logger.error("新增发帖配置 params : {}失败, ", JSON.toJSONString(params), e);
            message.fail(CodeEnum.FAIL).setMsg("新增发帖配置失败！");
            return message;
        }
        message.success(CodeEnum.SUCCESS).setMsg("新增发帖配置成功！");
        return message;
    }

    @Override
    public JsonMessage updatePublishTask(Map<String,Object> params) {
        JsonMessage message = JsonMessage.init();
        try {
            taskPublishMapper.update(params);
        } catch (Exception e) {
            logger.error("更新发帖配置 params : {}失败, ", JSON.toJSONString(params), e);
            message.fail(CodeEnum.FAIL).setMsg("更新发帖配置失败！");
            return message;
        }
        message.success(CodeEnum.SUCCESS).setMsg("更新发帖配置成功！");
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
            } else {
                if (!ObjectUtils.isEmpty(status)) {
                    result.put("statusDesc", TaskStatusEnum.getDesc(status));
                }
            }
            Integer executableCount = 0;
            if (!ObjectUtils.isEmpty(taskType) && !ObjectUtils.isEmpty(platform)) {
                executableCount = getExecutableCountByTaskType(taskType, platform);
            }
            if (executableCount == -1) {
                result.put("executableCount", "无限次");
            } else {
                result.put("executableCount", executableCount);
            }
        }
    }

    @Override
    public JsonMessage uploadTaskImg(MultipartFile file) {
        JsonMessage message = JsonMessage.init().success(CodeEnum.SUCCESS);
        String fileName = file.getOriginalFilename();
        List<InputStream> imgInputStreams = new ArrayList<>();
        Map<String, String> fileInfo = new HashMap<>();
        try {
            imgInputStreams.add(file.getInputStream());
            //String path = System.getProperty("java.class.path");
            String path
                    = ClassUtils.getDefaultClassLoader()
                    .getResource("").getPath() + "images/";
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
            inputStreamToFile(file.getInputStream(), path + fileName);
            fileInfo.put("imageName", fileName);
            fileInfo.put("imagePath", path + fileName);
            message.setData(fileInfo);
        } catch (IOException e) {
            logger.error("上传图片流失败, ", e);
        }
        //file.getInputStream();
        return message;
    }


    public static void inputStreamToFile(InputStream is, String fileName) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            int len = -1;
            in = new BufferedInputStream(is);
            out = new BufferedOutputStream(new FileOutputStream(fileName));
            byte[] b = new byte[1024];
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
        } catch (IOException e) {
            logger.error("写文件失败, ", e);
        } finally {
            if (!ObjectUtils.isEmpty(in)) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("关闭in失败, ", e);
                }
            }

            if (!ObjectUtils.isEmpty(in)) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("关闭out失败, ", e);
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
