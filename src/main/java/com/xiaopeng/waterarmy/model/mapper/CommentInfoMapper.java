package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import org.apache.ibatis.annotations.Param;

public interface CommentInfoMapper extends MyMapper<CommentInfo> {
    void save(@Param("commentInfo") CommentInfo commentInfo);
}
