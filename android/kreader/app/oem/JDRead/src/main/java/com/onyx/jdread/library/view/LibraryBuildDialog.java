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
import com.onyx.jdread.databinding.DialogLibraryBuildLayoutBinding;

import java.util.Observable;


/**
 * Created by 12 on 2016/12/14.
 */

public class LibraryBuildDialog extends Dialog {
    private LibraryBuildDialog(Context context) {
        super(context);
    }

    private LibraryBuildDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private DialogModel model;

        public Builder(Context context, DialogModel model) {
            this.context = context;
            this.model = model;
        }

        public LibraryBuildDialog create() {
            final LibraryBuildDialog dialog = new LibraryBuildDialog(context, R.style.CustomDialogStyle);
            DialogLibraryBuildLayoutBinding bind = DataBindingUtil.bind(View.inflate(context, R.layout.dialog_library_build_layout, null));
            bind.setDialogModel(model);
            dialog.setContentView(bind.getRoot());
            return dialog;
        }
    }

    public static class DialogModel extends Observable {
        public final ObservableField<String> title = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.build_library));
        public final ObservableField<String> libraryName = new ObservableField<>();
        public final ObservableField<String> positiveText = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.ok));
        public final ObservableField<String> negativeText = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.cancel));

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
        attributes.height = getContext().getResources().getInteger(R.integer.library_build_dialog_height);
        window.setAttributes(attributes);
        super.show();
    }
}
