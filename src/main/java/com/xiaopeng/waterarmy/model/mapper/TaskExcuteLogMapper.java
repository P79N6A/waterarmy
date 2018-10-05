package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.TaskExcuteLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TaskExcuteLogMapper extends MyMapper<TaskExcuteLog> {

    List<Map<String,Object>> getTaskExcuteLogs(@Param("params") Map<String, Object> params);

    void save(@Param("taskExcuteLog") Map<String, Object> taskExcuteLog);

}
