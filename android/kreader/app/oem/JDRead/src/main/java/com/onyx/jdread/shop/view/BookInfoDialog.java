package com.onyx.jdread.shop.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.util.Utils;

/**
 * Created by jackdeng on 2018/2/5.
 */

public class BookInfoDialog extends Dialog {
    public BookInfoDialog(@NonNull Context context) {
        this(context, 0);
    }

    public BookInfoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
    }

    public void setView(View view) {
        this.setContentView(view);
    }

    @Override
    public void show() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL);
        WindowManager.LayoutParams attributes = window.getAttributes();
        int screenWidth = Utils.getScreenWidth(JDReadApplication.getInstance());
        attributes.width = (int) (screenWidth * Utils.getValuesFloat(R.integer.login_dialog_width_rate));
        attributes.y = ResManager.getInteger(R.integer.associated_email_dialog_offset_y);
        attributes.x = ResManager.getInteger(R.integer.associated_email_dialog_offset_x);
        window.setAttributes(attributes);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        super.show();
    }
}
