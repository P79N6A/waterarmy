package com.xiaopeng.waterarmy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.enums.ContentRepositoriesEnum;
import com.xiaopeng.waterarmy.common.enums.ExcelDataTypeEnum;
import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.common.util.ExcelUtil;
import com.xiaopeng.waterarmy.model.dao.ContentInfo;
import com.xiaopeng.waterarmy.model.dao.ContentInfoRepositories;
import com.xiaopeng.waterarmy.model.mapper.ContentInfoMapper;
import com.xiaopeng.waterarmy.model.mapper.ContentInfoRepositoriesMapper;
import com.xiaopeng.waterarmy.service.ContentService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * * 功能描述：内容库管理
 * <p> 版权所有：
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0uleService
 * @since 1.0.0
 * create on: 2018/9/20
 */
@Service
public class ContentServiceImpl implements ContentService {

    private static Logger logger = LoggerFactory.getLogger(ContentServiceImpl.class);

    @Autowired
    private ContentInfoRepositoriesMapper contentInfoRepositoriesMapper;

    @Autowired
    private ContentInfoMapper contentInfoMapper;

    @Override
    public PageInfo<Map<String, Object>> repositoriesPage(Integer pageNo, Integer pageSize, Map<String, Object> params) {
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String, Object>> results = contentInfoRepositoriesMapper.getContentInfoRepositories(params);
        for (Map<String, Object> result : results) {
            String type = MapUtils.getString(result, "type");
            if (!ObjectUtils.isEmpty(type)) {
                result.put("typeDesc", ContentRepositoriesEnum.getDesc(type));
            }
        }
        return new PageInfo<>(results);
    }

    @Override
    public PageInfo<Map<String, Object>> page(Integer pageNo, Integer pageSize, Map<String, Object> params) {
        PageHelper.startPage(pageNo, pageSize);
        List<Map<String, Object>> results = contentInfoMapper.getContentInfos(params);
        for (Map<String, Object> result : results) {
            String contentRepositoriesType = MapUtils.getString(result, "contentRepositoriesType");
            if (!ObjectUtils.isEmpty(contentRepositoriesType)) {
                result.put("contentRepositoriesTypeDesc", ContentRepositoriesEnum.getDesc(contentRepositoriesType));
            }
        }
        return new PageInfo<>(results);
    }

    @Override
    public JsonMessage addRepositories(ContentInfoRepositories repositories) {
        JsonMessage message = JsonMessage.init();
        repositories.setCount(0);
        repositories.setCreateTime(new Date());
        repositories.setUpdateTime(new Date());
        repositories.setCreator("xiaoa");
        repositories.setUpdater("xiaoa");
        contentInfoRepositoriesMapper.save(repositories);
        message.success(CodeEnum.SUCCESS).setMsg("新建内容库数据成功!");
        return message;
    }

    @Override
    public JsonMessage updateRepositories(Map<String, Object> params) {
        JsonMessage message = JsonMessage.init();
        ContentInfoRepositories repositories = new ContentInfoRepositories();
        repositories.setId(MapUtils.getLong(params, "id"));
        repositories.setName(MapUtils.getString(params, "name"));
        repositories.setType(MapUtils.getString(params, "type"));
        repositories.setUpdateTime(new Date());
        repositories.setUpdater("xiaoa");
        contentInfoRepositoriesMapper.update(repositories);
        message.success(CodeEnum.SUCCESS).setMsg("更新内容库数据成功!");
        return message;
    }

    @Override
    public JsonMessage updateRepositoriesType(Map<String, Object> params) {
        JsonMessage message = JsonMessage.init();
        ContentInfoRepositories repositories = new ContentInfoRepositories();
        repositories.setId(MapUtils.getLong(params, "id"));
        repositories.setType(MapUtils.getString(params, "type"));
        contentInfoRepositoriesMapper.update(repositories);
        message.success(CodeEnum.SUCCESS).setMsg("更新内容库数据成功!");
        return message;
    }

    @Override
    public JsonMessage queryContentInfo(String contentRepositoriesType) {
        JsonMessage message = JsonMessage.init();
        Map<String, Object> params = new HashMap<>();
        params.put("type", contentRepositoriesType);
        List<Map<String, Object>> infos = contentInfoMapper.getContentInfos(params);
        message.setData(infos);
        message.success(CodeEnum.SUCCESS).setMsg("获取内容列表成功！");
        return message;
    }

    @Override
    public List<ContentInfo> querysByRepositoriesType(String contentRepositoriesType) {
        List<ContentInfo> contentInfos = contentInfoMapper.querysByRepositoriesType(contentRepositoriesType);
        return contentInfos;
    }

    @Override
    public JsonMessage delete(Long id) {
        JsonMessage message = JsonMessage.init();
        try {
            contentInfoMapper.deleteById(id);
        } catch (Exception e) {
            logger.error("删除内容 id : {}失败, ", id, e);
            message.fail(CodeEnum.FAIL).setMsg("删除内容失败！");
            return message;
        }
        message.success(CodeEnum.SUCCESS).setMsg("删除内容成功！");
        return message;
    }

    @Override
    public JsonMessage importData(MultipartFile file, String type) {
        JsonMessage message = JsonMessage.init();
        List<Object> datas = ExcelUtil.importData(file, ExcelDataTypeEnum.CONTENT.getName());
        List<ContentInfo> infos = new ArrayList<>();
        for (Object data : datas) {
            ContentInfo info = (ContentInfo) data;
            info.setCreateTime(new Date());
            info.setUpdateTime(new Date());
            info.setContentRepositoriesType(ContentRepositoriesEnum.COMMENT.getName());
            info.setCreator("xiaoa");
            info.setUpdater("xiaoa");
            contentInfoMapper.save(info);
            infos.add(info);
        }
        message.success(CodeEnum.SUCCESS).setMsg("导入内容数据成功!");
        message.setData(infos);
        return message;
    }
}
