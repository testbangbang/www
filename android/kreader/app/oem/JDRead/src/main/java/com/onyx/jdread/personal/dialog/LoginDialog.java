package com.onyx.jdread.personal.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.util.Utils;

/**
 * Created by li on 2018/2/3.
 */

public class LoginDialog extends Dialog {
    public LoginDialog(@NonNull Context context) {
        this(context, 0);
    }

    public LoginDialog(@NonNull Context context, int themeResId) {
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
        WindowManager.LayoutParams attributes = window.getAttributes();
        int screenWidth = Utils.getScreenWidth(JDReadApplication.getInstance());
        int screenHeight = Utils.getScreenHeight(JDReadApplication.getInstance());
        attributes.width = (int) (screenWidth * Utils.getValuesFloat(R.integer.login_dialog_width_rate));
        attributes.height = (int) (screenHeight * Utils.getValuesFloat(R.integer.login_dialog_height_rate));
        window.setAttributes(attributes);
        super.show();
    }
}
