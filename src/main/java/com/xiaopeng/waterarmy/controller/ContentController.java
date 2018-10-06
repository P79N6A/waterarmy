package com.xiaopeng.waterarmy.controller;

import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.model.dao.ContentInfoRepositories;
import com.xiaopeng.waterarmy.service.ContentService;
import com.xiaopeng.waterarmy.service.PlatFormService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * * 功能描述：内容管理
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
@RequestMapping("/content")
@Controller
public class ContentController {

    private static final String REPOSITORIES_INDEX_PAGE = "/content/content_info_repositories_list.html";
    private static final String CONTENT_INDEX_PAGE = "/content/content_info_list.html";

    @Autowired
    private ContentService contentService;

    @RequestMapping(value = "/repositories/index")
    public ModelAndView repositoriesIndex() {
        ModelAndView view = new ModelAndView(REPOSITORIES_INDEX_PAGE);
        return view;
    }

    @RequestMapping(value="/repositories/search/{pageNo}",method = RequestMethod.POST)
    @ResponseBody
    public PageInfo<Map<String,Object>> repositoriesSearch(@RequestParam Map<String,Object> params
            , @PathVariable("pageNo")Integer pageNo){
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize=10;
        if (!ObjectUtils.isEmpty(MapUtils.getString(params,"pageSize"))){
            pageSize = MapUtils.getInteger(params,"pageSize");
        }
        return contentService.repositoriesPage(pageNo, pageSize, params);
    }

    @RequestMapping(value = "/repositories/addRepositories", method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage addRepositories(ContentInfoRepositories repositories) {
        return  contentService.addRepositories(repositories);
    }

    @RequestMapping(value = "/repositories/updateRepositories", method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage updateRepositories(@RequestParam Map<String,Object> params) {
        return  contentService.updateRepositories(params);
    }

    @RequestMapping(value = "/index")
    public ModelAndView index(String type) {
        ModelAndView view = new ModelAndView(CONTENT_INDEX_PAGE);
        view.addObject("type", type);
        return view;
    }

    @RequestMapping(value="/search/{pageNo}",method = RequestMethod.POST)
    @ResponseBody
    public PageInfo<Map<String,Object>> search(@RequestParam Map<String,Object> params
            , @PathVariable("pageNo")Integer pageNo){
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize=10;
        if (!ObjectUtils.isEmpty(MapUtils.getString(params,"pageSize"))){
            pageSize = MapUtils.getInteger(params,"pageSize");
        }
        return contentService.page(pageNo, pageSize, params);
    }

    @RequestMapping(value = "/queryContentInfo", method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage queryContentInfo(String contentRepositoriesType) {
        return  contentService.queryContentInfo(contentRepositoriesType);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage delete(Long id) {
        return  contentService.delete(id);
    }

    @RequestMapping(value = "/importData", method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage importData(@RequestParam("file") MultipartFile file, String type) {//String type  Map<String,Object> form
        return  contentService.importData(file, type);
    }


}
