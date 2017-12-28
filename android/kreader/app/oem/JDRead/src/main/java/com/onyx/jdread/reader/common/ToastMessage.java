package com.onyx.jdread.reader.common;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by huxiaomao on 17/11/13.
 */

public class ToastMessage {
    public static void showMessage(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
