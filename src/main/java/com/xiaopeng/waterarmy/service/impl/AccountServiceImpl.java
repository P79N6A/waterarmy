package com.xiaopeng.waterarmy.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.enums.AccountLevelEnum;
import com.xiaopeng.waterarmy.common.enums.ExcelDataTypeEnum;
import com.xiaopeng.waterarmy.common.enums.PlatformEnum;
import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.common.util.ExcelUtil;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dao.AccountIPInfo;
import com.xiaopeng.waterarmy.model.dao.LinkInfo;
import com.xiaopeng.waterarmy.model.mapper.AccountIPInfoMapper;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
import com.xiaopeng.waterarmy.service.AccountService;
import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

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

    @Autowired
    private AccountIPInfoMapper accountIPInfoMapper;

    @Override
    public PageInfo<Map<String,Object>> page(Integer pageNo, Integer pageSize, Map<String,Object> params){
        PageHelper.startPage(pageNo, pageSize);
        List<String> userNames = new ArrayList<>();
        String userName = MapUtils.getString(params,"userName");
        if (!ObjectUtils.isEmpty(userName)) {
            String[] names = userName.split(",");
            for (String n: names) {
                userNames.add(n);
            }
        }
        List<Map<String,Object>> results = accountMapper.getAccounts(params, userNames);
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
    public List<Account> getAccountsByPlatform(String platform) {
        List<Account> accounts = accountMapper.getAccountsByPlatform(platform);
        return accounts;
    }

    @Override
    public Account getAccountByUserName(String userName) {
        Account account = accountMapper.getAccountByUserName(userName);
        return account;
    }

    @Override
    public JsonMessage add(Map<String, Object> params) {
        JsonMessage message = JsonMessage.init();
        try {
            Account info = new Account();
            info.setUUID(UUID.randomUUID().toString());
            info.setUserName(MapUtils.getString(params, "userName"));
            info.setFullName(MapUtils.getString(params, "fullName"));
            info.setPassword(MapUtils.getString(params, "password"));
            info.setMobile(MapUtils.getString(params, "mobile"));
            info.setEmail(MapUtils.getString(params, "email"));
            info.setLevel(MapUtils.getInteger(params, "level"));
            info.setPlatform(MapUtils.getString(params, "platform"));
            info.setTaskCount(0);
            info.setCreateTime(new Date());
            info.setUpdateTime(new Date());
            info.setStatus(MapUtils.getInteger(params, "status"));
            info.setCreator("xiaoa");
            info.setUpdater("xiaoa");
            accountMapper.save(info);
        } catch (Exception e) {
            logger.error("新增账号 params : {}失败, ", JSON.toJSONString(params), e);
            message.fail(CodeEnum.FAIL).setMsg("新增账号失败！");
            return message;
        }
        message.success(CodeEnum.SUCCESS).setMsg("新增账号成功！");
        return message;
    }

    @Override
    public JsonMessage update(Map<String,Object> params) {
        JsonMessage message = JsonMessage.init();
        try {
            accountMapper.update(params);
        } catch (Exception e) {
            logger.error("更新账号 params : {}失败, ", JSON.toJSONString(params), e);
            message.fail(CodeEnum.FAIL).setMsg("更新账号失败！");
            return message;
        }
        message.success(CodeEnum.SUCCESS).setMsg("更新账号成功！");
        return message;
    }

    @Override
    public JsonMessage delete(String ids) {
        JsonMessage message = JsonMessage.init();
        try {
            String[] acccountIds = ids.split(",");
            for (String id: acccountIds) {
                accountMapper.deleteById(id);
            }
        } catch (Exception e) {
            logger.error("删除账号 ids : {}失败, ", ids, e);
            message.fail(CodeEnum.FAIL).setMsg("删除账号失败！");
            return message;
        }
        message.success(CodeEnum.SUCCESS).setMsg("删除账号成功！");
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

    @Override
    public JsonMessage updateTaskCount(String userName) {
        JsonMessage message = JsonMessage.init();
        try {
            accountMapper.updateTaskCount(userName);
        } catch (Exception e) {
            logger.error("更新用户: %s 累计有效任务次数失败, ", userName, e);
        }
        message.success(CodeEnum.SUCCESS).setMsg("累计有效任务次数成功!");
        return message;
    }

    public Map<String, Object> getAccountIPInfo(String ip, String platform, String userName) {
        Map<String, Object>  accountIPInfo = null;
        try {
            accountIPInfo = accountIPInfoMapper.getAccountIPInfo(ip, platform, userName);
        } catch (Exception e) {
            logger.error("getAccountIPInfo error userName: {}, ip: {}, platform: {} , "
                    , ip, platform, e);
        }
        return accountIPInfo;
    }

    public boolean saveAccountIPInfo(AccountIPInfo accountIPInfo) {
        try {
            accountIPInfoMapper.save(accountIPInfo);
        } catch (Exception e) {
            logger.error("save error accountIPInfo: {} , "
                    , JSON.toJSONString(accountIPInfo), e);
            return false;
        }
        return true;
    }

}