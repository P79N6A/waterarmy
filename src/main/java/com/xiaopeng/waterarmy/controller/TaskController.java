package com.xiaopeng.waterarmy.controller;

import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
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

    private static final String TASK_PUBLISH_INDEX_PAGE = "/task/task_publish_list.html";

    private static final String TASK_PUBLISH_DETAIL_PAGE = "/task/task_publish_detail.html";

    private static final String TASK_INFO_INDEX_PAGE = "/task/task_info_list.html";

    private static final String TASK_INFO_DETAIL_PAGE = "/task/task_info_detail.html";

    private static final String TASK_EXECUTE_LOG_INDEX_PAGE = "/task/task_execute_log_list.html";

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/publish/index")
    public ModelAndView taskPublishIndex() {
        ModelAndView view = new ModelAndView(TASK_PUBLISH_INDEX_PAGE);
        return view;
    }

    @RequestMapping(value = "/publish/detail")
    public ModelAndView taskPublishDetail(Integer taskPublishId) {
        ModelAndView view = new ModelAndView(TASK_PUBLISH_DETAIL_PAGE);
        view.addObject("taskPublishId", taskPublishId);
        return view;
    }

    /**
     * 分页查询发布任务列表
     *
     * @param params
     * @param pageNo
     * @return
     */
    @RequestMapping(value="/publish/search/{pageNo}",method = RequestMethod.POST)
    @ResponseBody
    public PageInfo<Map<String,Object>> taskPublishSearch(@RequestParam Map<String,Object> params
            , @PathVariable("pageNo")Integer pageNo){
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize=10;
        if (!ObjectUtils.isEmpty(MapUtils.getString(params,"pageSize"))){
            pageSize = MapUtils.getInteger(params,"pageSize");
        }
        return taskService.taskPublishPage(pageNo, pageSize, params);
    }

    /**
     * 发布任务
     *
     * @param params
     * @return
     */
    @RequestMapping(value="/publish/publishTask",method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage publishTask(@RequestParam Map<String,Object> params) {
        return taskService.publishTask(params);
    }

    @RequestMapping(value = "/publish/add", method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage add(@RequestParam Map<String,Object> params) {
        return  taskService.addPublishTask(params);
    }

    @RequestMapping(value = "/publish/update", method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage update(@RequestParam Map<String,Object> params) {
        return  taskService.updatePublishTask(params);
    }


    @RequestMapping(value = "/info/index")
    public ModelAndView taskInfoIndex() {
        ModelAndView view = new ModelAndView(TASK_INFO_INDEX_PAGE);
        return view;
    }

    @RequestMapping(value = "/info/detail")
    public ModelAndView taskInfoDetail(Integer id) {
        ModelAndView view = new ModelAndView(TASK_INFO_DETAIL_PAGE);
        return view;
    }

    @RequestMapping(value="/info/search/{pageNo}",method = RequestMethod.POST)
    @ResponseBody
    public PageInfo<Map<String,Object>> taskInfoSearch(@RequestParam Map<String,Object> params
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

    @RequestMapping(value = "/executelog/index")
    public ModelAndView taskexecutelogIndex() {
        ModelAndView view = new ModelAndView(TASK_EXECUTE_LOG_INDEX_PAGE);
        return view;
    }

    @RequestMapping(value="/executelog/search/{pageNo}",method = RequestMethod.POST)
    @ResponseBody
    public PageInfo<Map<String,Object>> taskExecuteLogSearch(@RequestParam Map<String,Object> params
            , @PathVariable("pageNo")Integer pageNo){
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize=10;
        if (!ObjectUtils.isEmpty(MapUtils.getString(params,"pageSize"))){
            pageSize = MapUtils.getInteger(params,"pageSize");
        }
        return taskService.taskExecuteLogPage(pageNo, pageSize, params);
    }

    @RequestMapping(value = "/recoveryTask")
    @ResponseBody
    public JsonMessage recoveryTask(@RequestParam Map<String,Object> params) {
        Long id = Long.parseLong(String.valueOf(params.get("id")));
        return taskService.recoveryTask(id);
    }

    @RequestMapping(value = "/stopTask")
    @ResponseBody
    public JsonMessage stopTask(@RequestParam Map<String,Object> params) {
        Long id = Long.parseLong(String.valueOf(params.get("id")));
        return taskService.stopTask(id);
    }


}
