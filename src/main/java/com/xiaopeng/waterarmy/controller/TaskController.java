package com.xiaopeng.waterarmy.controller;

import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.service.PlatFormService;
import com.xiaopeng.waterarmy.service.TaskService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * * 功能描述：任务管理（包括链接，评论内容，规则，任务）
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
@RequestMapping("/task")
@Controller
public class TaskController {

    private static final String CONTENT_INFO_INDEX_PAGE = "/task/content_info_list.html";
    private static final String LINK_INFO_INDEX_PAGE = "/task/link_info_list.html";
    private static final String TASK_INFO_INDEX_PAGE = "/task/task_info_list.html";
    private static final String RULE_INFO_INDEX_PAGE = "/task/rule_info_list.html";

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/contentinfo/index")
    public ModelAndView contentInfoIndex() {
        ModelAndView view = new ModelAndView(CONTENT_INFO_INDEX_PAGE);
        return view;
    }

    @RequestMapping(value = "/linkinfo/index")
    public ModelAndView linkInfoIndex() {
        ModelAndView view = new ModelAndView(LINK_INFO_INDEX_PAGE);
        return view;
    }

    @RequestMapping(value = "/taskinfo/index")
    public ModelAndView taskInfoIndex() {
        ModelAndView view = new ModelAndView(TASK_INFO_INDEX_PAGE);
        return view;
    }

    @RequestMapping(value = "/ruleinfo/index")
    public ModelAndView ruleInfoIndex() {
        ModelAndView view = new ModelAndView(RULE_INFO_INDEX_PAGE);
        return view;
    }

    @RequestMapping(value="/contentinfo/search/{pageNo}",method = RequestMethod.POST)
    @ResponseBody
    public PageInfo<Map<String,Object>> contentInfoSearch(@RequestParam Map<String,String> params
            , @PathVariable("pageNo")Integer pageNo){
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize=10;
        if (!ObjectUtils.isEmpty(MapUtils.getString(params,"pageSize"))){
            pageSize = MapUtils.getInteger(params,"pageSize");
        }
        return taskService.contentInfoPage(pageNo, pageSize, params);
    }

    @RequestMapping(value="/linkInfo/search/{pageNo}",method = RequestMethod.POST)
    @ResponseBody
    public PageInfo<Map<String,Object>> linkInfoSearch(@RequestParam Map<String,String> params
            , @PathVariable("pageNo")Integer pageNo){
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize=10;
        if (!ObjectUtils.isEmpty(MapUtils.getString(params,"pageSize"))){
            pageSize = MapUtils.getInteger(params,"pageSize");
        }
        return taskService.linkInfoPage(pageNo, pageSize, params);
    }

    @RequestMapping(value="/ruleinfo/search/{pageNo}",method = RequestMethod.POST)
    @ResponseBody
    public PageInfo<Map<String,Object>> ruleInfoSearch(@RequestParam Map<String,String> params
            , @PathVariable("pageNo")Integer pageNo){
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize=10;
        if (!ObjectUtils.isEmpty(MapUtils.getString(params,"pageSize"))){
            pageSize = MapUtils.getInteger(params,"pageSize");
        }
        return taskService.ruleInfoPage(pageNo, pageSize, params);
    }

    @RequestMapping(value="/taskinfo/search/{pageNo}",method = RequestMethod.POST)
    @ResponseBody
    public PageInfo<Map<String,Object>> taskInfoSearch(@RequestParam Map<String,String> params
            , @PathVariable("pageNo")Integer pageNo){
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize=10;
        if (!ObjectUtils.isEmpty(MapUtils.getString(params,"pageSize"))){
            pageSize = MapUtils.getInteger(params,"pageSize");
        }
        return taskService.taskInfoPage(pageNo, pageSize, params);
    }

}
