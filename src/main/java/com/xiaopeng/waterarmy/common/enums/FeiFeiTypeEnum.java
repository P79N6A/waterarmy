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
public enum FeiFeiTypeEnum {

    /**
     * 10100	1位纯数字	低至 3.6
     * 10200	2位纯数字	低至 3.6
     * 10300	3位纯数字	低至 3.6
     * 10400	4位纯数字	低至 3.6
     * 10500	5位纯数字	低至 4.3
     * 10600	6位纯数字	低至 5.4
     * 10700	7位纯数字	低至 6.3
     * 10800	8位纯数字	低至 7.2
     * 10900	9位纯数字	低至 7.9
     * 类型	描述
     * 10400	4位纯数字
     * 20400	4位纯英文
     * 30400	4位数字英文
     * 40400	4位汉字
     * 50200	复杂计算题
     */

    /**
     * 四位数字
     */
    FOUR_NUMBERS ("10400", "四位数字"),
    FOUR_WORDS ("20400", "四位纯英文"),
    FOUR_WORDS_NUMBERS ("30400", "四位数字英文");



    private String name;
    private String desc;

    private FeiFeiTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static List<Map<String, Object>> parseMap() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (FeiFeiTypeEnum taskTypeEnum : FeiFeiTypeEnum.values()) {
            Map<String, Object> taskType = new HashMap<>();
            taskType.put("name", taskTypeEnum.getName());
            taskType.put("desc", taskTypeEnum.getDesc());
            lists.add(taskType);
        }
        return lists;
    }

    public static String getDesc(String name) {
        for (FeiFeiTypeEnum r : FeiFeiTypeEnum.values()) {
            if (r.getName().equals(name)) {
                return r.desc;
            }
        }
        return null;
    }

    public static FeiFeiTypeEnum getEnum(String name) {
        for (FeiFeiTypeEnum r : FeiFeiTypeEnum.values()) {
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
