package com.onyx.android.edu.utils;

import android.util.Log;

import com.onyx.android.edu.BuildConfig;


/**
 * log类
 * Created by ming on 15/12/15.
 */
public class LogUtils {

    /**
     * 任何消息都会输出
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg){
        if (BuildConfig.DEBUG){
            Log.v(tag, msg);
        }
    }

    /**
     * 提示性的消息information
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg){
        if (BuildConfig.DEBUG){
            Log.i(tag, msg);
        }
    }

    /**
     * debug调试的意思
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg){
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    /**
     * warning警告
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg){
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    /**
     * error错误
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg){
        if (BuildConfig.DEBUG){
            Log.e(tag, msg);
        }
    }


}
