package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.TaskImageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TaskImageInfoMapper extends MyMapper<TaskImageInfo> {

    List<Map<String, Object>> getTaskImageInfos(@Param("params") Map<String, Object> params);

    void save(@Param("taskImageInfo") TaskImageInfo taskImageInfo);

}
