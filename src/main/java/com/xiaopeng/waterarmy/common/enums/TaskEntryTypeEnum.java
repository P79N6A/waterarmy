package com.xiaopeng.waterarmy.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * 功能描述：汽车网站平台枚举
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="1206401391@qq.com">iason</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
public enum TaskEntryTypeEnum {

    /**
     * 子任务
     */
    YICHENEWSCOMMENT ("YICHENEWSCOMMENT", "易车车家号评论"),
    YICHECOMMENTPRAISE ("YICHECOMMENTPRAISE", "易车车家号点赞"),
    YICHEKOUBEICOMMENT ("YICHEKOUBEICOMMENT", "易车口碑评论"),
    TAIPINGYANGNEWSCOMMENT ("TAIPINGYANGNEWSCOMMENT", "太平洋新闻评论"),
    TAIPINGYANGCHEZHUCOMMENT ("TAIPINGYANGCHEZHUCOMMENT", "太平洋车主点评评论"),
    TAIPINGYANGNEWSCOMMENTPRAISE ("TAIPINGYANGNEWSCOMMENTPRAISE", "太平洋新闻评论点赞"),
    AIKANEWSCOMMENT ("AIKANEWSCOMMENT", "爱卡新闻评论"),
    AIKANEWSCOMMENTPRAISE ("AIKANEWSCOMMENTPRAISE", "爱卡新闻评论点赞"),
    QICHEVIDEOCOMMENT ("QICHEVIDEOCOMMENT", "汽车头条视频评论"),
    AUTOHOMENEWSCOMMENT ("AUTOHOMENEWSCOMMENT", "汽车之家新闻评论"),
    AUTOHOMENEWSCOMMENTPRAISE ("AUTOHOMENEWSCOMMENTPRAISE", "汽车之家新闻评论点赞"),
    CHEJIAHAOCOMMENT ("CHEJIAHAOCOMMENT", "车家号评论"),
    CHEJIAHAOCOMMENTPRAISE ("CHEJIAHAOCOMMENTPRAISE", "车家号评论点赞"),
    CHEJIAHAOVIDEOPLAY ("CHEJIAHAOVIDEOPLAY", "车家号评论点赞"),
    AUTOHOMEKOUBEICOMMENT("AUTOHOMEKOUBEICOMMENT", "汽车之家口碑评论");

    private String name;
    private String desc;

    private TaskEntryTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static List<Map<String, Object>> parseMap() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (TaskEntryTypeEnum taskTypeEnum : TaskEntryTypeEnum.values()) {
            Map<String, Object> taskType = new HashMap<>();
            taskType.put("name", taskTypeEnum.getName());
            taskType.put("desc", taskTypeEnum.getDesc());
            lists.add(taskType);
        }
        return lists;
    }

    public static String getDesc(String name) {
        for (TaskEntryTypeEnum r : TaskEntryTypeEnum.values()) {
            if (r.getName().equals(name)) {
                return r.desc;
            }
        }
        return null;
    }

    public static TaskEntryTypeEnum getEnum(String name) {
        for (TaskEntryTypeEnum r : TaskEntryTypeEnum.values()) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
