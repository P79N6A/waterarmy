package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.ContentInfo;
import com.xiaopeng.waterarmy.model.dao.ContentInfoRepositories;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ContentInfoMapper extends MyMapper<ContentInfo> {

    List<Map<String, Object>> getContentInfos(@Param("params") Map<String, Object> params);

    List<ContentInfo> querysByRepositoriesType(@Param("type") String type);

    void save(@Param("contentInfo") ContentInfo contentInfo);

    void updateType(@Param("contentInfoRepositories") ContentInfoRepositories contentInfoRepositories);

    void  deleteById(@Param("id") Long id);

}
