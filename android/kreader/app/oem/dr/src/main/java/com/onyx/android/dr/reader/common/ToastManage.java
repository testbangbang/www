package com.onyx.android.dr.reader.common;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by huxiaomao on 17/5/12.
 */

public class ToastManage {
    public static void showMessage(Context context,String text){
        Toast.makeText(context,text,Toast.LENGTH_LONG).show();
    }

    public static void showMessage(Context context,int resourceID){
        String text = context.getString(resourceID);
        Toast.makeText(context,text,Toast.LENGTH_LONG).show();
    }
}
