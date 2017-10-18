package com.onyx.android.sun.common;

import android.content.Context;
import android.content.Intent;

import com.onyx.android.sun.scribble.ScribbleActivity;

/**
 * Created by hehai on 17-10-13.
 */

public class ManagerActivityUtils {
    public static void startScribbleActivity(Context context, String questionID, String questionTitle, String question) {
        Intent intent = new Intent(context, ScribbleActivity.class);
        intent.putExtra(Constants.QUESTION_ID, questionID);
        intent.putExtra(Constants.QUESTION_TITLE, questionTitle);
        intent.putExtra(Constants.QUESTION_TAG, question);
        context.startActivity(intent);
    }
}
