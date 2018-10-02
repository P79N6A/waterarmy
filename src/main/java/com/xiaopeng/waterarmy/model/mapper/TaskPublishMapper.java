package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.TaskPublish;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TaskPublishMapper extends MyMapper<TaskPublish> {

    List<Map<String,Object>> getTaskPublishs(@Param("params") Map<String, String> params);

}
