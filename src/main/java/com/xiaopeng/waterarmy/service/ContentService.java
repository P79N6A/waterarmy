package com.xiaopeng.waterarmy.service;

import com.github.pagehelper.PageInfo;

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
     * 链接管理分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param params
     * @return
     */
    PageInfo<Map<String,Object>> page(Integer pageNo, Integer pageSize, Map<String, Object> params);

}
