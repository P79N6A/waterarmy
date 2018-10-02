package com.xiaopeng.waterarmy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.enums.PlantFormModuleEnum;
import com.xiaopeng.waterarmy.common.enums.PlatformStatusEnum;
import com.xiaopeng.waterarmy.common.enums.TaskTypeEnum;
import com.xiaopeng.waterarmy.model.mapper.PlatFormMapper;
import com.xiaopeng.waterarmy.service.PlatFormService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

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
public class PlatFormServiceImpl implements PlatFormService {

    @Autowired
    private PlatFormMapper platFormMapper;

    @Override
    public PageInfo<Map<String,Object>> page(Integer pageNo, Integer pageSize, Map<String,String> params){
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = platFormMapper.getPlatForms(params);
        for (Map<String,Object> result: results) {
            String module = MapUtils.getString(result,"module");
            if (!ObjectUtils.isEmpty(module)) {
                result.put("moduleDesc", PlantFormModuleEnum.getDesc(module));
            }
            String taskType = MapUtils.getString(result,"taskType");
            if (!ObjectUtils.isEmpty(taskType)) {
                result.put("taskTypeDesc", TaskTypeEnum.getDesc(taskType));
            }
            Integer status = MapUtils.getInteger(result,"status");
            if (!ObjectUtils.isEmpty(status)) {
                result.put("statusDesc", PlatformStatusEnum.getDesc(status));
            }
        }
        return new PageInfo<>(results);
    }

}
