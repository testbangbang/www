package com.onyx.jdread.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;

/**
 * Created by huxiaomao on 2016/11/30.
 */

public class CommonUtils {

    private static final String JDBOOK_ROOT = "/JDBooks";
    private static final String WEB_CACHE = "/webcache";
    private static final String LOCAL_JDBOOKS_PATH = Environment.getExternalStorageDirectory().getPath() + JDBOOK_ROOT;
    private static final String LOCAL_WEB_CACHE_PATH = Environment.getExternalStorageDirectory().getPath() + WEB_CACHE;

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static float calculateDiscount(float oldPrice, float newPrice) {
        float i = newPrice / oldPrice;
        return i * 10;
    }

    public static String formatWordCount(int wordCount) {
        if (wordCount > 10000) {
            return wordCount / 10000 + JDReadApplication.getInstance().getString(R.string.word_count_million_words);
        } else {
            return wordCount + JDReadApplication.getInstance().getString(R.string.word_count_words);
        }
    }

    public static String getJDBooksPath() {
        return LOCAL_JDBOOKS_PATH;
    }
}