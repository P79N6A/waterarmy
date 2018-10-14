package com.xiaopeng.waterarmy.model.mapper;

import com.xiaopeng.waterarmy.common.mapper.MyMapper;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dao.AccountIPInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AccountIPInfoMapper extends MyMapper<AccountIPInfo> {

    Map<String, Object> getAccountIPInfo(@Param("ip") String ip, @Param("platform") String platform
            , @Param("userName") String userName);

    void save(@Param("accountIPInfo") AccountIPInfo accountIPInfo);


}