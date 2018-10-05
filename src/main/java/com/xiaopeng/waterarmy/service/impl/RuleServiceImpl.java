package com.xiaopeng.waterarmy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.enums.ContentRepositoriesEnum;
import com.xiaopeng.waterarmy.common.enums.PVStayTimeEnum;
import com.xiaopeng.waterarmy.common.enums.RandomSelectContentEnum;
import com.xiaopeng.waterarmy.common.enums.RandomSelectLinkEnum;
import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.model.dao.RuleInfo;
import com.xiaopeng.waterarmy.model.mapper.RuleInfoMapper;
import com.xiaopeng.waterarmy.service.RuleService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * * 功能描述：
 * <p> 版权所有：
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0uleService
 * @since 1.0.0
 * create on: 2018/9/20
 */
@Service
public class RuleServiceImpl implements RuleService {

    private static Logger logger = LoggerFactory.getLogger(RuleServiceImpl.class);

    @Autowired
    private RuleInfoMapper ruleInfoMapper;

    @Override
    public PageInfo<Map<String,Object>> page(Integer pageNo, Integer pageSize, Map<String,Object> params){
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = ruleInfoMapper.getRuleInfos(params);
        for (Map<String,Object> result: results) {
            Integer pvStayTime = MapUtils.getInteger(result,"pvStayTime");
            if (!ObjectUtils.isEmpty(pvStayTime)) {
                result.put("pvStayTimeDesc", PVStayTimeEnum.getDesc(pvStayTime));
            }
            Integer isRandomSelectLink = MapUtils.getInteger(result,"isRandomSelectLink");
            if (!ObjectUtils.isEmpty(isRandomSelectLink)) {
                result.put("isRandomSelectLinkDesc", RandomSelectLinkEnum.getDesc(isRandomSelectLink));
            }
            Integer isRandomSelectContent = MapUtils.getInteger(result,"isRandomSelectContent");
            if (!ObjectUtils.isEmpty(isRandomSelectContent)) {
                result.put("isRandomSelectContentDesc", RandomSelectContentEnum.getDesc(isRandomSelectContent));
            }
        }
        return new PageInfo<>(results);
    }

    @Override
    public JsonMessage addRule(Map<String, Object> params) {
        JsonMessage message = JsonMessage.init();
        RuleInfo info = new RuleInfo();
        info.setName(MapUtils.getString(params, "name"));
        info.setIsRandomSelectLink(MapUtils.getInteger(params, "isRandomSelectLink"));
        info.setIsRandomSelectContent(MapUtils.getInteger(params, "isRandomSelectContent"));
        info.setStartTimeInterval(MapUtils.getInteger(params, "startTimeInterval"));
        info.setEndTimeInterval(MapUtils.getInteger(params, "endTimeInterval"));
        info.setPvStayTime(MapUtils.getInteger(params, "pvStayTime"));
        info.setCreateTime(new Date());
        info.setUpdateTime(new Date());
        info.setCreator("xiaoa");
        info.setUpdater("xiaoa");
        ruleInfoMapper.save(info);
        message.success(CodeEnum.SUCCESS).setMsg("新增规则成功!");
        return message;
    }

    @Override
    public JsonMessage getRules() {
        JsonMessage message = JsonMessage.init();
        List<Map<String,Object>> rules = ruleInfoMapper.getRuleInfos(null);
        message.setData(rules);
        message.success(CodeEnum.SUCCESS).setMsg("获取规则列表成功!");
        return message;
    }
}
