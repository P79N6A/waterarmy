package com.xiaopeng.waterarmy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.enums.ExcelDataTypeEnum;
import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.common.util.ExcelUtil;
import com.xiaopeng.waterarmy.model.dao.LinkInfo;
import com.xiaopeng.waterarmy.model.mapper.LinkInfoMapper;
import com.xiaopeng.waterarmy.service.LinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Override
    public JsonMessage importData(MultipartFile file) {
        JsonMessage message = JsonMessage.init();
        List<Object> datas = ExcelUtil.importData(file, ExcelDataTypeEnum.LINK.getName());
        for (Object data: datas) {
            LinkInfo info = (LinkInfo) data;
            info.setName("test");
            info.setCreateTime(new Date());
            info.setUpdateTime(new Date());
            info.setCount(0);
            info.setCreator("xiaoa");
            info.setUpdater("xiaoa");
            linkInfoMapper.save(info);
        }
        message.success(CodeEnum.SUCCESS).setMsg("导入链接数据成功!");
        return message;
    }
}
