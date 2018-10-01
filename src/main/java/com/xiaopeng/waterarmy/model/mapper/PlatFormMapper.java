package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dao.PlatformConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PlatFormMapper extends MyMapper<PlatformConfig> {

    List<Map<String,Object>> getPlatForms(@Param("params") Map<String, String> params);

}
