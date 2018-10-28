package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import com.xiaopeng.waterarmy.model.dao.CommentLikeLog;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface CommentLikeLogMapper extends MyMapper<CommentLikeLog> {

    void save(@Param("commentLikeLog") CommentLikeLog commentLikeLog);

    CommentLikeLog getContentLikeLog(@Param("params") Map<String, Object> params);
}
