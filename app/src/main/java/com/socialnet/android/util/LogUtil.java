package com.socialnet.android.util;

import android.util.Log;

public class LogUtil {
    private static boolean shouldLogInfo = true;
    private static boolean shouldLogWarn = true;
    private static boolean shouldLogError = true;
    private static String TAG = "LogUtil";

    public static void logi(String tag, String s) {
        if (shouldLogInfo) {
            Log.i(tag, "logi: " + s);
        }
    }

    public static void logi(String s) {
        if (shouldLogInfo) {
            Log.i(TAG, "logi: " + s);
        }
    }

    public static void logw(String tag, String s) {
        if (shouldLogWarn) {
            Log.i(tag, "logw: " + s);
        }
    }

    public static void loge(String tag, String s) {
        if (shouldLogError) {
            Log.i(tag, "loge: " + s);
        }
    }

}
