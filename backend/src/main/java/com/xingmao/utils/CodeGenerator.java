package com.xingmao.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CodeGenerator {

    /**
     * 生成订单号：XM + 时间戳 + 4位随机数
     */
    public static String generateOrderNo() {
        String timeStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int random = new Random().nextInt(10000);
        return "XM" + timeStr + String.format("%04d", random);
    }

    /**
     * 生成配送单号：PS + 时间戳 + 4位随机数
     */
    public static String generateDeliveryNo() {
        String timeStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int random = new Random().nextInt(10000);
        return "PS" + timeStr + String.format("%04d", random);
    }
}