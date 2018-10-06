package com.xiaopeng.waterarmy.common.util;

import java.util.Random;

/**
 * Created by iason on 2018/10/6.
 */
public class NumUtil {

    /**
     *
     * 获取随机数
     *
     * @param size
     * @return
     */
    public static Integer getRandomNum(Integer size) {
        int min = 0;
        int max = size;
        Random random = new Random();
        int num = random.nextInt(max) % (max - min + 1) + min;
        return num;
    }

}
