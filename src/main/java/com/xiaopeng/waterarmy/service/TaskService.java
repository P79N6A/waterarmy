package com.xiaopeng.waterarmy.service;

import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
public interface TaskService {

    /**
     * 任务发布列表分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param params
     * @return
     */
    PageInfo<Map<String,Object>> taskPublishPage(Integer pageNo, Integer pageSize, Map<String, Object> params);

    /**
     * 任务列表分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param params
     * @return
     */
    PageInfo<Map<String,Object>> taskInfoPage(Integer pageNo, Integer pageSize, Map<String, Object> params);

    /**
     * 任务执行记录分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param params
     * @return
     */
    PageInfo<Map<String,Object>> taskExcuteLogPage(Integer pageNo, Integer pageSize, Map<String, Object> params);

    /**
     * 保存任务执行记录
     *
     * @param params
     * @return
     */
    boolean saveTaskExcuteLog(Map<String, Object> params);

    /**
     * 获取任务详情
     *
     * @param taskId
     * @return
     */
    JsonMessage getTaskDetail(Long taskId);

    /**
     * 根据任务类型获取需要执行的任务
     *
     * @param taskType
     * @return
     */
    List<Map<String,Object>> getExecutableTaskInfos(String taskType);

    /**
     * 恢复任务
     *
     * @param taskId
     * @return
     */
    JsonMessage recoveryTask(Long taskId);

    /**
     * 停止任务
     *
     * @param taskId
     * @return
     */
    JsonMessage stopTask(Long taskId);

    /**
     * 发布任务
     *
     * @param params
     * @return
     */
    JsonMessage publishTask(@RequestParam Map<String,Object> params);

}
