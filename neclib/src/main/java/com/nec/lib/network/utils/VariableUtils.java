package com.nec.lib.network.utils;

public class VariableUtils {

    /**
     * 收到Token失效的时间记录值
     */
    public static volatile long temp_system_time = 0;

    /**
     * 接收到Token失效的次数
     */
    public static volatile int receive_token_count = 0;
}
