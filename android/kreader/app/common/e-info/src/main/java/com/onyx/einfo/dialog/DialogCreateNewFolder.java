package com.onyx.einfo.dialog;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.onyx.android.sdk.data.RefValue;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.einfo.R;

import java.io.File;

/**
 * Created by solskjaer49 on 15/12/2 18:19.
 */
public class DialogCreateNewFolder extends OnyxAlertDialog {
    static final public String ARGS_PARENT_DIR = "parent_dir";

    public interface OnCreateListener {
        void onCreated(File file);
    }

    public void setOnCreatedListener(OnCreateListener onCreatedListener) {
        this.createdListener = onCreatedListener;
    }

    private OnCreateListener createdListener;
    private EditText inputEditText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final String parentPath = getArguments().getString(ARGS_PARENT_DIR);
        setParams(new OnyxAlertDialog.Params().setTittleString(getString(R.string.new_folder))
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_input)
                .setCustomViewAction(new OnyxAlertDialog.CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        inputEditText = (EditText) customView.findViewById(R.id.editText_Input);
                    }
                }).setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fileName = inputEditText.getText().toString().trim();
                        if (!checkInputNameValid(fileName)) {
                            return;
                        }
                        File file = new File(parentPath, fileName);
                        RefValue<String> errMsg = new RefValue<>();
                        if (!createFolder(v.getContext(), file, errMsg)) {
                            ToastUtils.showToast(v.getContext(), errMsg.getValue());
                        } else {
                            onFolderCreated(file);
                            ToastUtils.showToast(v.getContext(), R.string.create_folder_success);
                        }
                        dismiss();
                    }
                }));
        super.onCreate(savedInstanceState);
    }

    private void onFolderCreated(File file) {
        if (createdListener != null) {
            createdListener.onCreated(file);
        }
    }

    private boolean checkInputNameValid(String fileName) {
        if (StringUtils.isNullOrEmpty(fileName)) {
            ToastUtils.showToast(getActivity().getApplicationContext(), R.string.name_can_not_empty);
            return false;
        }
        return true;
    }

    private boolean createFolder(Context context, File file, RefValue<String> errMsg) {
        if (file.exists()) {
            errMsg.setValue(context.getString(R.string.folder_already_exists) + file.getAbsolutePath());
            return false;
        }
        if (file.mkdir()) {
            return true;
        } else {
            errMsg.setValue(context.getString(R.string.create_folder_failed));
            return false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        super.onActivityCreated(savedInstanceState);
    }

    public void show(FragmentManager fm) {
        super.show(fm, DialogCreateNewFolder.class.getSimpleName());
    }
}
