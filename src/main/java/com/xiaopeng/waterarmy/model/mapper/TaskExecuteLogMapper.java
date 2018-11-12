package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.TaskExecuteLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TaskExecuteLogMapper extends MyMapper<TaskExecuteLog> {

    List<Map<String,Object>> getTaskExecuteLogs(@Param("params") Map<String, Object> params);

    void save(@Param("taskExecuteLog") Map<String, Object> taskExecuteLog);

    List<Map<String,Object>> getTaskExecuteCount(@Param("params") Map<String, Object> params);

}
