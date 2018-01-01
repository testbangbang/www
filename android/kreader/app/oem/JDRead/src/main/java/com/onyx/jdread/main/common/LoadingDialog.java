package com.onyx.jdread.main.common;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.LoadingDialogLayoutBinding;

import java.util.Observable;


/**
 * Created by 12 on 2016/12/14.
 */

public class LoadingDialog extends Dialog {
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
            final LoadingDialog dialog = new LoadingDialog(context, R.style.CustomDialogStyle);
            LoadingDialogLayoutBinding bind = DataBindingUtil.bind(View.inflate(context, R.layout.loading_dialog_layout, null));
            bind.setLoadingModel(model);
            dialog.setContentView(bind.getRoot());
            return dialog;
        }
    }

    public static class DialogModel extends Observable {
        private final ObservableField<String> loadingText = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.loading));

        public String getLoadingText() {
            return loadingText.get();
        }

        public void setLoadingText(String s) {
            loadingText.set(s);
        }
    }

    @Override
    public void show() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = getContext().getResources().getInteger(R.integer.loading_dialog_width);
        attributes.height = getContext().getResources().getInteger(R.integer.loading_dialog_height);
        window.setAttributes(attributes);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        super.show();
    }
}
