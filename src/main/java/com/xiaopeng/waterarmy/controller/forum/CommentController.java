package com.xiaopeng.waterarmy.controller.forum;

import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.service.CommentService;
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
 * @author <a href="wenlong.cwl@alibaba-inc.com">成文龙</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
@RequestMapping("/comment")
@Controller
public class CommentController {
    private static final String INDEX_PAGE = "/comment/comment.html";

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView(INDEX_PAGE);
        return view;
    }

    @RequestMapping(value = "/comment")
    @ResponseBody
    public JsonMessage comment(String userName, String passWord, String comment) {
        return commentService.conmment(userName, passWord, comment);
    }

}
