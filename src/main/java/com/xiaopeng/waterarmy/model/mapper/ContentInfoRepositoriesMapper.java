package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.ContentInfo;
import com.xiaopeng.waterarmy.model.dao.ContentInfoRepositories;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ContentInfoRepositoriesMapper extends MyMapper<ContentInfoRepositories> {

    List<Map<String, Object>> getContentInfoRepositories(@Param("params") Map<String, Object> params);

    List<Map<String, Object>> queryRepositoriesByType(@Param("type") String type);

    void save(@Param("contentInfoRepositories") ContentInfoRepositories contentInfoRepositories);

    void update(@Param("contentInfoRepositories") ContentInfoRepositories contentInfoRepositories);

}
