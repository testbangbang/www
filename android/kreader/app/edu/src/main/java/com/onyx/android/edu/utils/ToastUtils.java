package com.onyx.android.edu.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ming on 16/6/28.
 */
public class ToastUtils {

    public static void showToast(Context context,String message){
        if (message!=null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
