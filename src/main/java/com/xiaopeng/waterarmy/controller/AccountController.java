package com.xiaopeng.waterarmy.controller;

import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
@RequestMapping("/account")
@Controller
public class AccountController {

    private static final String INDEX_PAGE = "/account/account_list.html";

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView(INDEX_PAGE);
        return view;
    }

    @RequestMapping(value = "/search")
    @ResponseBody
    public JsonMessage getAccounts() {
        return accountService.getAccounts();
    }

}
