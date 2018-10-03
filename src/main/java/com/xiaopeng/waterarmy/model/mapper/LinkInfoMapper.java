package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.LinkInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LinkInfoMapper extends MyMapper<LinkInfo> {

    List<Map<String,Object>> getLinkInfos(@Param("params") Map<String, Object> params);

    void save(@Param("linkInfo") LinkInfo linkInfo);

}
