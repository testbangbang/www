package com.onyx.jdread.main.common;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.LoadingDialogLayoutBinding;


/**
 * Created by 12 on 2016/12/14.
 */

public class LoadingDialog extends AlertDialog {

    private static AnimationDrawable animation;

    private LoadingDialog(Context context) {
        super(context);
    }

    private LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private DialogModel model;

        public Builder(Context context, DialogModel model) {
            this.context = context;
            this.model = model;
        }

        public LoadingDialog create() {
            return new LoadingDialog(context, R.style.CustomDialogStyle) {
                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    LoadingDialogLayoutBinding bind = DataBindingUtil.bind(View.inflate(context, R.layout.loading_dialog_layout, null));
                    bind.setLoadingModel(model);
                    animation = (AnimationDrawable) context.getResources().getDrawable(R.drawable.loading_animation);
                    bind.imageLoading.setBackgroundDrawable(animation);
                    setContentView(bind.getRoot());
                }
            };
        }
    }

    public static class DialogModel extends BaseObservable {
        private final ObservableField<String> loadingText = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.loading));

        public String getLoadingText() {
            return loadingText.get();
        }

        public void setLoadingText(String s) {
            loadingText.set(s);
            notifyChange();
        }
    }

    @Override
    public void show() {
        if (animation != null && !animation.isRunning()) {
            animation.start();
        }
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        super.show();
    }

    @Override
    public void dismiss() {
        if (animation != null && animation.isRunning()) {
            animation.stop();
        }
        super.dismiss();
    }
}
