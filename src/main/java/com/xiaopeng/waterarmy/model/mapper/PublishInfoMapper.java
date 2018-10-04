package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import com.xiaopeng.waterarmy.model.dao.PublishInfo;
import org.apache.ibatis.annotations.Param;

public interface PublishInfoMapper extends MyMapper<PublishInfo> {
    void save(@Param("publishInfo") PublishInfo publishInfo);
}
