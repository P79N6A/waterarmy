package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.PraiseInfo;
import org.apache.ibatis.annotations.Param;

public interface PraiseInfoMapper extends MyMapper<PraiseInfo> {
    void save(@Param("praiseInfo") PraiseInfo praiseInfo);
}
