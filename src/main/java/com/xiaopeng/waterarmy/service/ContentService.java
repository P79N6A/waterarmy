package com.xiaopeng.waterarmy.service;

import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.model.dao.ContentInfo;
import com.xiaopeng.waterarmy.model.dao.ContentInfoRepositories;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * * 功能描述：链接管理
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
public interface ContentService {

    /**
     * 内容库管理分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param params
     * @return
     */
    PageInfo<Map<String,Object>> repositoriesPage(Integer pageNo, Integer pageSize, Map<String, Object> params);

    /**
     * 内容管理分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param params
     * @return
     */
    PageInfo<Map<String,Object>> page(Integer pageNo, Integer pageSize, Map<String, Object> params);

    JsonMessage addRepositories(ContentInfoRepositories repositories);

    JsonMessage updateRepositories(Map<String,Object> params);

    JsonMessage updateContentRepositoriesType(Map<String,Object> params);

    JsonMessage queryRepositoriesByType(String type);

    List<ContentInfo> querysByRepositoriesType(String contentRepositoriesType);

    List<ContentInfo> querysRepositorieContents(String contentRepositoriesType, String contentRepositoriesName);

    JsonMessage delete(Long id);

    JsonMessage importData(MultipartFile file, String type);

}
