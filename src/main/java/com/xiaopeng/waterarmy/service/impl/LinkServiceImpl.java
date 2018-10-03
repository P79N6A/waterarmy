package com.xiaopeng.waterarmy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.model.mapper.LinkInfoMapper;
import com.xiaopeng.waterarmy.service.LinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @version 1.0.0uleService
 * @since 1.0.0
 * create on: 2018/9/20
 */
@Service
public class LinkServiceImpl implements LinkService {

    private static Logger logger = LoggerFactory.getLogger(LinkServiceImpl.class);

    @Autowired
    private LinkInfoMapper linkInfoMapper;

    @Override
    public PageInfo<Map<String,Object>> page(Integer pageNo, Integer pageSize, Map<String,Object> params){
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String,Object>> results = linkInfoMapper.getLinkInfos(params);
        return new PageInfo<>(results);
    }

}
