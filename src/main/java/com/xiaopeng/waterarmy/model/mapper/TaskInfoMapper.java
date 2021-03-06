package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.TaskInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TaskInfoMapper extends MyMapper<TaskInfo> {

    List<Map<String, Object>> getTaskInfos(@Param("params") Map<String, Object> params);

    List<Map<String, Object>> getExecutableTaskInfos(@Param("params") Map<String, Object> params);

    void updateExecuteCount(@Param("id")  Long id);

    void updateStatus(@Param("id")  Long id, @Param("status")  Integer status);

    void updateFinishStatus(@Param("id")  Long id);

    void save(@Param("taskInfo") Map<String, Object> taskInfo);

}
