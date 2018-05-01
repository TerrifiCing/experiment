package com.hust.experiment.util;

public class RedisKeyUtil {
    public static String subKey = "SUBSCRIBE_OF_COURSE";
    public static String attendKey = "ATTENDANCEKEY_OF_COURSE";

    public static String getSubscribeKey(int id){
        return subKey + String.valueOf(id);
    }

    public static String getAttendanceKey(int id){
        return attendKey + id;
    }
}
