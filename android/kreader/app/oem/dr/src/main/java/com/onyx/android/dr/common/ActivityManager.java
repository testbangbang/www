package com.onyx.android.dr.common;

import android.content.Context;
import android.content.Intent;

import com.onyx.android.dr.activity.DictQueryActivity;
import com.onyx.android.dr.activity.DictResultShowActivity;
import com.onyx.android.dr.activity.GoodSentenceNotebookActivity;
import com.onyx.android.dr.activity.GoodSentenceTypeActivity;
import com.onyx.android.dr.activity.LoginActivity;
import com.onyx.android.dr.activity.MyNotesActivity;

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
        intent.putExtra("editQuery", editQuery);
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
}
