package com.onyx.jdread.library.view;

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
import com.onyx.jdread.databinding.DialogCustomLayoutBinding;

import java.util.Observable;


/**
 * Created by 12 on 2016/12/14.
 */

public class LibraryDeleteDialog extends Dialog {
    private LibraryDeleteDialog(Context context) {
        super(context);
    }

    private LibraryDeleteDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private DialogModel model;

        public Builder(Context context, DialogModel model) {
            this.context = context;
            this.model = model;
        }

        public LibraryDeleteDialog create() {
            final LibraryDeleteDialog dialog = new LibraryDeleteDialog(context, R.style.CustomDialogStyle);
            DialogCustomLayoutBinding bind = DataBindingUtil.bind(View.inflate(context, R.layout.dialog_custom_layout, null));
            bind.setDialogModel(model);
            dialog.setContentView(bind.getRoot());
            return dialog;
        }
    }

    public static class DialogModel extends Observable {
        public final ObservableField<String> title = new ObservableField<>();
        public final ObservableField<String> message = new ObservableField<>();
        public final ObservableField<String> positiveText = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.yes));
        public final ObservableField<String> negativeText = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.no));

        public interface OnClickListener {
            void onClicked();
        }

        private OnClickListener positiveClickLister;
        private OnClickListener negativeClickLister;

        public void setPositiveClickLister(OnClickListener positiveClickLister) {
            this.positiveClickLister = positiveClickLister;
        }

        public void setNegativeClickLister(OnClickListener negativeClickLister) {
            this.negativeClickLister = negativeClickLister;
        }

        public void onPositiveClick() {
            positiveClickLister.onClicked();
        }

        public void onNegativeClick() {
            negativeClickLister.onClicked();
        }
    }

    @Override
    public void show() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = getContext().getResources().getInteger(R.integer.library_delete_dialog_width);
        attributes.height = getContext().getResources().getInteger(R.integer.library_delete_dialog_height);
        window.setAttributes(attributes);
        super.show();
    }
}
