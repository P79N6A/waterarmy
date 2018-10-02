package com.xiaopeng.waterarmy.controller;

import com.xiaopeng.waterarmy.common.enums.*;
import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * * 功能描述：枚举类的controller
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
@Controller
@RequestMapping("/enums")
public class EnumsController {

    /**
     * 功能描述: 获取用户等级枚举列表
     *
     * @author <a href="1206401391@qq.com">iason</a>
     * @date 2018年8月21日
     */
    @RequestMapping(value = "/getAccountLevels", method = RequestMethod.GET)
    @ResponseBody
    public JsonMessage getAccountLevels() {
        List<Map<String, Object>> lists = AccountLevelEnum.parseMap();
        JsonMessage message = JsonMessage.init().success(CodeEnum.SUCCESS);
        message.setMsg("获取用户等级枚举列表成功!");
        message.data(lists);
        return message;
    }
    /**
     * 功能描述: 获取板块枚举列表
     *
     * @author <a href="1206401391@qq.com">iason</a>
     * @date 2018年8月21日
     */
    @RequestMapping(value = "/getPlatFormModules", method = RequestMethod.GET)
    @ResponseBody
    public JsonMessage getPlatFormModules() {
        List<Map<String, Object>> lists = PlatFormModuleEnum.parseMap();
        JsonMessage message = JsonMessage.init().success(CodeEnum.SUCCESS);
        message.setMsg("获取板块枚举列表成功!");
        message.data(lists);
        return message;
    }

    /**
     * 功能描述: 获取平台枚举列表
     *
     * @author <a href="1206401391@qq.com">iason</a>
     * @date 2018年8月21日
     */
    @RequestMapping(value = "/getPlatforms", method = RequestMethod.GET)
    @ResponseBody
    public JsonMessage getPlatforms() {
        List<Map<String, Object>> lists = PlatformEnum.parseMap();
        JsonMessage message = JsonMessage.init().success(CodeEnum.SUCCESS);
        message.setMsg("获取平台枚举列表成功!");
        message.data(lists);
        return message;
    }

    /**
     * 功能描述: 获取平台状态枚举列表
     *
     * @author <a href="1206401391@qq.com">iason</a>
     * @date 2018年8月21日
     */
    @RequestMapping(value = "/getPlatformStatus", method = RequestMethod.GET)
    @ResponseBody
    public JsonMessage getPlatformStatus() {
        List<Map<String, Object>> lists = PlatformStatusEnum.parseMap();
        JsonMessage message = JsonMessage.init().success(CodeEnum.SUCCESS);
        message.setMsg("获取平台状态枚举列表成功!");
        message.data(lists);
        return message;
    }

    /**
     * 功能描述: 获取任务类型枚举列表
     *
     * @author <a href="1206401391@qq.com">iason</a>
     * @date 2018年8月21日
     */
    @RequestMapping(value = "/getTaskTypes", method = RequestMethod.GET)
    @ResponseBody
    public JsonMessage getTaskTypes() {
        List<Map<String, Object>> lists = TaskTypeEnum.parseMap();
        JsonMessage message = JsonMessage.init().success(CodeEnum.SUCCESS);
        message.setMsg("获取任务类型枚举列表成功!");
        message.data(lists);
        return message;
    }

    /**
     * 功能描述: 获取PV停留时间枚举列表
     *
     * @author <a href="1206401391@qq.com">iason</a>
     * @date 2018年8月21日
     */
    @RequestMapping(value = "/getPVStayTimes", method = RequestMethod.GET)
    @ResponseBody
    public JsonMessage getPVStayTimes() {
        List<Map<String, Object>> lists = PVStayTimeEnum.parseMap();
        JsonMessage message = JsonMessage.init().success(CodeEnum.SUCCESS);
        message.setMsg("获取PV停留时间枚举列表成功!");
        message.data(lists);
        return message;
    }

    /**
     * 功能描述: 获取任务状态枚举列表
     *
     * @author <a href="1206401391@qq.com">iason</a>
     * @date 2018年8月21日
     */
    @RequestMapping(value = "/getTaskStatus", method = RequestMethod.GET)
    @ResponseBody
    public JsonMessage getTaskStatus() {
        List<Map<String, Object>> lists = TaskStatusEnum.parseMap();
        JsonMessage message = JsonMessage.init().success(CodeEnum.SUCCESS);
        message.setMsg("获取任务状态枚举列表成功!");
        message.data(lists);
        return message;
    }


}