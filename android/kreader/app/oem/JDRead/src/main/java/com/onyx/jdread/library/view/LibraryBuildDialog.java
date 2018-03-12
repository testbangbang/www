package com.onyx.jdread.library.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogLibraryBuildLayoutBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.util.InputUtils;

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
            final DialogLibraryBuildLayoutBinding bind = DataBindingUtil.bind(View.inflate(context, R.layout.dialog_library_build_layout, null));
            bind.setDialogModel(model);
            bind.editName.addTextChangedListener(new TextWatcher() {
                private CharSequence temp;

                @Override
                public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                              int arg3) {
                    temp = s;
                }

                @Override
                public void onTextChanged(CharSequence s, int arg1, int arg2,
                                          int arg3) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (InputUtils.getByteCount(temp.toString()) > ResManager.getInteger(R.integer.group_name_max_length)) {
                        ToastUtil.showOffsetToast(ResManager.getString(R.string.the_input_has_exceeded_the_upper_limit), ResManager.getInteger(R.integer.toast_offset_y));
                        bind.editName.setText(InputUtils.getEffectiveString(temp.toString(), ResManager.getInteger(R.integer.group_name_max_length)));
                        bind.editName.setSelection(bind.editName.length());
                    }
                }

            });
            bind.editName.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
            bind.positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtils.isNotBlank(model.libraryName.get())) {
                        if (InputUtils.haveSpecialCharacters(model.libraryName.get())) {
                            ToastUtil.showOffsetToast(ResManager.getString(R.string.group_names_do_not_support_special_characters), ResManager.getInteger(R.integer.toast_offset_y));
                            return;
                        }
                        model.onPositiveClick();
                    } else {
                        ToastUtil.showOffsetToast(ResManager.getString(R.string.please_enter_group_name), ResManager.getInteger(R.integer.toast_offset_y));
                    }
                }
            });
            bind.negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setContentView(bind.getRoot());
            return dialog;
        }
    }

    public static class DialogModel extends Observable {
        public final ObservableField<String> title = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.build_library));
        public final ObservableField<String> libraryName = new ObservableField<>();
        public final ObservableField<String> positiveText = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.confirm));
        public final ObservableField<String> negativeText = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.cancel));

        public interface OnClickListener {
            void onClicked();
        }

        private OnClickListener positiveClickLister;

        public void setPositiveClickLister(OnClickListener positiveClickLister) {
            this.positiveClickLister = positiveClickLister;
        }

        public void onPositiveClick() {
            positiveClickLister.onClicked();
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
