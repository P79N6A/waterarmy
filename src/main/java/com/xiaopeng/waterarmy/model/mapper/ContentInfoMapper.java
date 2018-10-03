package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dao.ContentInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ContentInfoMapper extends MyMapper<ContentInfo> {

    List<Map<String,Object>> getContentInfos(@Param("params") Map<String, Object> params);

}
