package com.onyx.edu.reader.ui.data;

import android.content.Context;

import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.edu.reader.R;

/**
 * Created by ming on 2017/6/16.
 */

public class ExceptionMessage {

    public static String getString(Context context, int code) {
        switch (code) {
            case ReaderException.NO_REVIEW_DATA:
                return context.getString(R.string.no_review_data);
            case ReaderException.JSON_EXCEPTION:
                return context.getString(R.string.json_exception);
        }
        return null;
    }

}
