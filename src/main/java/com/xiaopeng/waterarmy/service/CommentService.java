package com.xiaopeng.waterarmy.service;

import com.xiaopeng.waterarmy.common.message.JsonMessage;

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
public interface CommentService {

    JsonMessage conmment(String userName, String passWord, String comment);

}
