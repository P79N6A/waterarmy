package com.xiaopeng.waterarmy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.model.mapper.ContentInfoMapper;
import com.xiaopeng.waterarmy.model.mapper.LinkInfoMapper;
import com.xiaopeng.waterarmy.model.mapper.RuleInfoMapper;
import com.xiaopeng.waterarmy.model.mapper.TaskInfoMapper;
import com.xiaopeng.waterarmy.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class TaskServiceImpl implements TaskService {

    @Autowired
    private ContentInfoMapper contentInfoMapper;

    @Autowired
    private LinkInfoMapper linkInfoMapper;

    @Autowired
    private RuleInfoMapper ruleInfoMapper;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Override
    public PageInfo<Map<String,Object>> contentInfoPage(Integer pageNo, Integer pageSize, Map<String,String> params){
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = contentInfoMapper.getContentInfos(params);
        for (Map<String,Object> result: results) {
        }
        return new PageInfo<>(results);
    }

    @Override
    public PageInfo<Map<String, Object>> linkInfoPage(Integer pageNo, Integer pageSize, Map<String, String> params) {
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = linkInfoMapper.getLinkInfos(params);
        for (Map<String,Object> result: results) {
        }
        return new PageInfo<>(results);
    }

    @Override
    public PageInfo<Map<String, Object>> ruleInfoPage(Integer pageNo, Integer pageSize, Map<String, String> params) {
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = ruleInfoMapper.getRuleInfos(params);
        for (Map<String,Object> result: results) {
        }
        return new PageInfo<>(results);
    }

    @Override
    public PageInfo<Map<String, Object>> taskInfoPage(Integer pageNo, Integer pageSize, Map<String, String> params) {
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = taskInfoMapper.getTaskInfos(params);
        for (Map<String,Object> result: results) {
        }
        return new PageInfo<>(results);
    }
}
