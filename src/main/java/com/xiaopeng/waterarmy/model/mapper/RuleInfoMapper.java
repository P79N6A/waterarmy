package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.RuleInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RuleInfoMapper extends MyMapper<RuleInfo> {

    List<Map<String,Object>> getRuleInfos(@Param("params") Map<String, Object> params);

    void save(@Param("ruleInfo") RuleInfo ruleInfo);

}
