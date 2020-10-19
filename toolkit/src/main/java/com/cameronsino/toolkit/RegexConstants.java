package com.cameronsino.toolkit;

public class RegexConstants {
    /**
     * 电子邮箱正则表达式
     */
    public static final String EMAIL = "^[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?$";

    /**
     * 不能包含特殊字符
     */
    public static final String SPECIAL_CHARACTERS = "([^`~!@#$%^&*()+=|{}':;,.<>/?！￥…（）—【】‘；：”“’。，、？])*";
}
