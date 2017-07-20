package com.onyx.android.dr.common;

import android.content.Context;
import android.content.Intent;

import com.onyx.android.dr.activity.DictQueryActivity;
import com.onyx.android.dr.activity.DictResultShowActivity;
import com.onyx.android.dr.activity.GoodSentenceNotebookActivity;
import com.onyx.android.dr.activity.GoodSentenceTypeActivity;
import com.onyx.android.dr.activity.LoginActivity;
import com.onyx.android.dr.activity.MyNotesActivity;
import com.onyx.android.dr.activity.NewWordNotebookActivity;
import com.onyx.android.dr.activity.NewWordQueryActivity;
import com.onyx.android.dr.activity.NewWordTypeActivity;
import com.onyx.android.dr.activity.QueryRecordActivity;
import com.onyx.android.dr.activity.ApplicationsActivity;
import com.onyx.android.dr.reader.data.OpenBookParam;
import com.onyx.android.dr.reader.utils.ReaderUtil;
import com.onyx.android.sdk.data.model.Metadata;

/**
 * Created by hehai on 17-6-29.
 */

public class ActivityManager {
    public static void startLoginActivity(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startDictQueryActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, DictQueryActivity.class);
        context.startActivity(intent);
    }
    public static void startDictResultShowActivity(Context context, String editQuery) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.EDITQUERY, editQuery);
        intent.setClass(context, DictResultShowActivity.class);
        context.startActivity(intent);
    }

    public static void startMyNotesActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, MyNotesActivity.class);
        context.startActivity(intent);
    }

    public static void startGoodSentenceTypeActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, GoodSentenceTypeActivity.class);
        context.startActivity(intent);
    }

    public static void startGoodSentenceNotebookActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, GoodSentenceNotebookActivity.class);
        context.startActivity(intent);
    }

    public static void startQueryRecordActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, QueryRecordActivity.class);
        context.startActivity(intent);
    }

    public static void startNewWordTypeActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, NewWordTypeActivity.class);
        context.startActivity(intent);
    }

    public static void startNewWordNotebookActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, NewWordNotebookActivity.class);
        context.startActivity(intent);
    }

    public static void startNewWordQueryActivity(Context context, String editQuery) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.EDITQUERY, editQuery);
        intent.setClass(context, NewWordQueryActivity.class);
        context.startActivity(intent);
    }

    public static void openBook(Context context, Metadata metadata,String localPath){
        OpenBookParam openBookParam = new OpenBookParam();
        openBookParam.setBookName(metadata.getName());
        openBookParam.setLocalPath(localPath);
        ReaderUtil.openBook(context, openBookParam);
    }

    public static void startApplicationsActivity(Context context) {
        Intent intent = new Intent(context, ApplicationsActivity.class);
        context.startActivity(intent);
    }
}
