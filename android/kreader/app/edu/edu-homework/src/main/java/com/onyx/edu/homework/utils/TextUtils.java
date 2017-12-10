package com.onyx.edu.homework.utils;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by lxm on 2017/11/18.
 */

public class TextUtils {

    public static Spanned fromHtml(String source) {
        if (StringUtils.isNullOrEmpty(source)) {
            return new SpannableString("");
        }
        return Html.fromHtml(source);
    }

    public static Spanned fromHtml(String source, Html.ImageGetter imageGetter, Html.TagHandler tagHandler) {
        if (StringUtils.isNullOrEmpty(source)) {
            return new SpannableString("");
        }
        return Html.fromHtml(source, imageGetter, tagHandler);
    }

    public static TextView setLinkMovementMethod(TextView view) {
        view.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

}
