package com.onyx.jdread.main.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;

import java.text.DecimalFormat;

/**
 * Created by huxiaomao on 2016/11/30.
 */

public class CommonUtils {

    private static final String JDBOOK_ROOT = "/Books";
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

    public static String getYueDouPrice(float price) {
        return String.valueOf(new DecimalFormat("0").format(price * 100));
    }

    public static String array2String(String[] bookIds) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bookIds != null && bookIds.length > 0) {
            for (String bookId : bookIds) {
                stringBuilder.append(bookId);
                stringBuilder.append(",");
            }
            return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
        } else {
            return stringBuilder.toString();
        }
    }

    public static String[] string2Arr(String bookList) {
        String[] bookArr = new String[]{};
        if (bookList != null) {
            if (bookList.contains(",")) {
                bookArr = bookList.split(",");
            } else {
                bookArr = new String[]{bookList};
            }
        }
        return bookArr;
    }

    public static boolean isCanNowRead(BookDetailResultBean.Detail detailBean){
        boolean canNowRead = false;
        if (detailBean != null) {
            canNowRead = detailBean.isTryRead() || detailBean.isFree();
        }
        return canNowRead;
    }

    public static boolean showBuyBookButton(BookDetailResultBean.Detail detailBean){
        boolean showBuyBookButton = false;
        if (detailBean != null) {
            showBuyBookButton = !detailBean.isFree();
        }
        return showBuyBookButton;
    }
}