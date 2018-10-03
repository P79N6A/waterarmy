package com.xiaopeng.waterarmy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.enums.AccountLevelEnum;
import com.xiaopeng.waterarmy.common.enums.ExcelDataTypeEnum;
import com.xiaopeng.waterarmy.common.enums.PlatformEnum;
import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.common.util.ExcelUtil;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dao.LinkInfo;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
import com.xiaopeng.waterarmy.service.AccountService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * * 功能描述：
 * <p> 版权所有：
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public PageInfo<Map<String,Object>> page(Integer pageNo, Integer pageSize, Map<String,Object> params){
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = accountMapper.getAccounts(params);
        for (Map<String,Object> result: results) {
            Integer level = MapUtils.getInteger(result,"level");
            if (!ObjectUtils.isEmpty(level)) {
                result.put("levelDesc", AccountLevelEnum.getDesc(level));
            }
            String platform = MapUtils.getString(result,"platform");
            if (!ObjectUtils.isEmpty(platform)) {
                result.put("platformDesc", PlatformEnum.getDesc(platform));
            }
        }
        return new PageInfo<>(results);
    }

    @Override
    public JsonMessage getAccountByUserName(String userName) {
        JsonMessage message = JsonMessage.init();
        Account account = accountMapper.getAccountByUserName(userName);
        message.setData(account);
        message.success(CodeEnum.SUCCESS);
        return message;
    }

    @Override
    public JsonMessage importData(MultipartFile file) {
        JsonMessage message = JsonMessage.init();
        List<Object> datas = ExcelUtil.importData(file, ExcelDataTypeEnum.ACCOUNT.getName());
        for (Object data: datas) {
            Account info = (Account) data;
            info.setUUID(UUID.randomUUID().toString());
            info.setLevel(AccountLevelEnum.PRIMARY.getIndex());
            info.setTaskCount(0);
            info.setCreateTime(new Date());
            info.setUpdateTime(new Date());
            info.setCreator("xiaoa");
            info.setUpdater("xiaoa");
            accountMapper.save(info);
        }
        message.success(CodeEnum.SUCCESS).setMsg("导入账号数据成功!");
        return message;
    }


}
