package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.AccountTaskLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AccountTaskLogMapper extends MyMapper<AccountTaskLog> {

    void save(@Param("accountTaskLog") AccountTaskLog accountTaskLog);

    List<Map<String, Object>> getAccountTaskLogs(@Param("params") Map<String, Object> params);

}
