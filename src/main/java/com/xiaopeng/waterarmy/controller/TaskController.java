package com.xiaopeng.waterarmy.controller;

import com.github.pagehelper.PageInfo;
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

    private static final String TASK_INFO_INDEX_PAGE = "/task/task_info_list.html";

    private static final String TASK_INFO_DETAIL_PAGE = "/task/task_info_detail.html";

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/index")
    public ModelAndView taskInfoIndex() {
        ModelAndView view = new ModelAndView(TASK_INFO_INDEX_PAGE);
        return view;
    }

    @RequestMapping(value = "/detail")
    public ModelAndView taskInfoDetail(Integer id) {
        ModelAndView view = new ModelAndView(TASK_INFO_DETAIL_PAGE);
        return view;
    }

    @RequestMapping(value="/search/{pageNo}",method = RequestMethod.POST)
    @ResponseBody
    public PageInfo<Map<String,Object>> search(@RequestParam Map<String,String> params
            , @PathVariable("pageNo")Integer pageNo){
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize=10;
        if (!ObjectUtils.isEmpty(MapUtils.getString(params,"pageSize"))){
            pageSize = MapUtils.getInteger(params,"pageSize");
        }
        return taskService.page(pageNo, pageSize, params);
    }

}
