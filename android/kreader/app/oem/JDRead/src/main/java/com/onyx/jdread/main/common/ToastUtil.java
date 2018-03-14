package com.onyx.jdread.main.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;

/**
 * Created by jackdeng on 2017/12/7.
 */

public class ToastUtil {

    private static String oldMsg;
    private static Toast toast = null;
    private static int left = JDReadApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.toast_view_padding_left_and_right);
    private static int top = JDReadApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.toast_view_padding_top_and_bottom);
    private static int right = JDReadApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.toast_view_padding_left_and_right);
    private static int bottom = JDReadApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.toast_view_padding_top_and_bottom);
    private static float radius = JDReadApplication.getInstance().getResources().getInteger(R.integer.toast_view_shadow_radius);
    private static float dx = JDReadApplication.getInstance().getResources().getInteger(R.integer.toast_view_shadow_dx);
    private static float dy = JDReadApplication.getInstance().getResources().getInteger(R.integer.toast_view_shadow_dy);
    private static float textSize = JDReadApplication.getInstance().getResources().getDimension(R.dimen.level_three_heading_font);

    public static void showOffsetToast(String message, int offsetY) {
        showToast(JDReadApplication.getInstance(), message, offsetY);
    }

    public static void showToast(Context appContext, String message) {
        showToast(appContext, message, 0);
    }

    public static void showToast(Context appContext, String message, int offset) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(appContext.getApplicationContext(), message, Toast.LENGTH_SHORT);
            View view = toast.getView();
            setBackground(view, getDrawable(appContext, R.drawable.rectangle_stroke));
            TextView textView = (TextView) view.findViewById(android.R.id.message);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setMaxLines(20);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(left, top, right, bottom);
            textView.setShadowLayer(radius, dx, dy, Color.TRANSPARENT);
            setGravity(offset);
            toast.show();
        } else {
            if (message.equals(oldMsg)) {
                if (!toast.getView().isShown()) {
                    setGravity(offset);
                    toast.show();
                }
            } else {
                oldMsg = message;
                toast.setText(message);
                setGravity(offset);
                toast.show();
            }
        }
    }

    private static void setGravity(int offset) {
        if (offset != 0) {
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, offset);
        } else {
            toast.setGravity(ResManager.getInteger(R.integer.default_toast_gravity),
                    0, ResManager.getDimens(R.dimen.default_toast_y_offset));
        }
    }

    public static void showToast(int resId, Object... formatArgs) {
        showToast(ResManager.getString(resId), formatArgs);
    }

    public static void showToast(String text, Object... formatArgs) {
        showToast(String.format(text, formatArgs));
    }

    public static void showToast(Context appContext, int resId) {
        showToast(appContext, appContext.getString(resId));
    }

    public static void showToast(String message) {
        showToast(JDReadApplication.getInstance(), message);
    }

    public static void showToast(int resId) {
        showToast(JDReadApplication.getInstance(), JDReadApplication.getInstance().getString(resId));
    }

    public static void showToastErrorMsgForDownBook(String errorCode) {
        showToast(JDReadApplication.getInstance(), getErrorMsgForDownBook(errorCode));
    }

    public static String getErrorMsgByCode(String errorCode) {
        String errorMsg = ResManager.getString(R.string.login_resutl_unknown_error);
        if (Constants.RESULT_CODE_UNKNOWN_ERROR.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.login_resutl_unknown_error);
        } else if (Constants.RESULT_CODE_NO_FUNCTION.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.login_resutl_no_function);
        } else if (Constants.RESULT_CODE_NOT_LOGIN.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.login_resutl_not_login);
        } else if (Constants.RESULT_CODE_PARAMS_ERROR.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.login_resutl_params_error);
        } else if (Constants.RESULT_CODE_PARAMS_LENGTH_ERROR.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.login_resutl_params_error);
        } else if (Constants.RESULT_CODE_PARAMS_LACK.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.login_resutl_params_error);
        } else if (Constants.RESULT_CODE_PARAMS_FORMAT_ERRO.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.login_resutl_params_error);
        }
        return errorMsg;
    }

    public static String getErrorMsgForDownBook(String errorCode) {
        String errorMsg = getErrorMsgByCode(errorCode);
        if (Constants.RESULT_CODE_BOOK_NO_READ_VIP.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.down_book_resutl_no_read_vip);
        } else if (Constants.RESULT_CODE_BOOK_CAN_NOT_READ.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.down_book_can_not_read);
        } else if (Constants.RESULT_CODE_BOOK_NOT_FOUND.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.down_book_book_not_found);
        } else if (Constants.RESULT_CODE_BOOK_ILLEGAL_ORDER.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.down_book_illegal_order);
        } else if (Constants.RESULT_CODE_BOOK_ILLEGAL_DEVICE.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.down_book_illegal_device);
        } else if (Constants.RESULT_CODE_BOOK_GENERATE_CERT_ERROR.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.down_book_server_error);
        } else if (Constants.RESULT_CODE_BOOK_GET_CONTENT_ERROR.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.down_book_server_error);
        } else if (Constants.RESULT_CODE_BOOK_CERIFY_ORDER_ERROR.equals(errorCode)) {
            errorMsg = ResManager.getString(R.string.down_book_server_error);
        }
        return errorMsg;
    }

    private static void setBackground(@NonNull View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    private static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}
