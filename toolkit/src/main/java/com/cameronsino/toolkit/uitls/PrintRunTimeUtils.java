package com.cameronsino.toolkit.uitls;


public class PrintRunTimeUtils {

    private static long startTime;

    public static void start() {
        startTime = System.currentTimeMillis();
    }

    public static void end() {
        end( "");
    }

    public static void end(String msg) {
        // 背景颜色代号(41-46)
        // 前景色代号(31-36)
        // 前景色代号和背景色代号可选，就是或可以写，也可以不写 \33[%d;%d;1m] 文本内容 %n \33[0m
        // 数字+m：1加粗；3斜体；4下划线
        // 格式：System.out.println("\33[前景色代号;背景色代号;数字m");
        long endTime = System.currentTimeMillis();
        System.out.format("\33[%d;1m[%s]>>>>>>>>>>>>>>>>>>>>>>>>>>执行了:%sms%n\33[0m", 31, msg, (endTime - startTime));
    }

}
