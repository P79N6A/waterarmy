package com.xiaopeng.waterarmy.controller;

import com.github.pagehelper.PageInfo;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.model.dao.ContentInfoRepositories;
import com.xiaopeng.waterarmy.service.ContentService;
import com.xiaopeng.waterarmy.service.RuleService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/rule")
@Controller
public class RuleController {

    private static final String INDEX_PAGE = "/rule/rule_info_list.html";

    @Autowired
    private RuleService ruleService;

    @RequestMapping(value = "/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView(INDEX_PAGE);
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
        return ruleService.page(pageNo, pageSize, params);
    }

    @RequestMapping(value = "/getRules", method = RequestMethod.GET)
    @ResponseBody
    public JsonMessage getRules() {
        return  ruleService.getRules();
    }

    @RequestMapping(value = "/addRule", method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage addRule(@RequestParam Map<String,Object> params) {
        return  ruleService.addRule(params);
    }

}
