package com.onyx.kcb.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kcb.R;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by solskjaer49 on 15/12/2 17:33.
 */
public class DialogRenameFile extends OnyxAlertDialog {
    static final public String ARGS_FILE_PATH = "args_file_path";

    private OnRenameFinishedListener listener;
    private EditText inputEditText = null;

    public interface OnRenameFinishedListener {
        void onRenameFinish(String newFullPath, String newName);
    }

    public void setOnRenameListener(OnRenameFinishedListener l) {
        this.listener = l;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final String filePath = getArguments().getString(ARGS_FILE_PATH);
        setParams(new Params().setTittleString(getString(R.string.menu_file_rename))
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_rename_file)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        initView(customView, filePath);
                    }
                })
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String newFileName = getInputContent();
                        if (!checkInputNameValid(newFileName)) {
                            return;
                        }
                        String targetPath = buildRenameFile(new File(filePath), newFileName);
                        boolean result = processFileRename(filePath, targetPath);
                        if (!result) {
                            ToastUtils.showToast(getActivity().getApplicationContext(), R.string.rename_failed);
                            return;
                        }
                        ToastUtils.showToast(getActivity().getApplicationContext(), R.string.rename_succeed);
                        onFileRename(targetPath, newFileName);
                        dismiss();
                    }
                }));
        super.onCreate(savedInstanceState);
    }

    private void initView(View customView, String filePath) {
        TextView currentName = (TextView) customView.findViewById(R.id.textView_current_name);
        currentName.setText(new File(filePath).getName());
        String baseName = FilenameUtils.getBaseName(currentName.getText().toString());
        inputEditText = (EditText) customView.findViewById(R.id.editText_rename);
        inputEditText.setText(baseName);
        inputEditText.setSelection(baseName.length());
    }

    private String getInputContent() {
        return inputEditText.getText().toString().trim();
    }

    private boolean checkInputNameValid(String newName) {
        if (StringUtils.isNullOrEmpty(newName)) {
            dismiss();
            return false;
        }
        return true;
    }

    private void onFileRename(String targetPath, String newName) {
        if (listener != null) {
            listener.onRenameFinish(targetPath, newName);
        }
    }

    private boolean processFileRename(final String sourceFilePath, final String targetFilePath) {
        File sourceFile = new File(sourceFilePath);
        File targetFile = new File(targetFilePath);
        try {
            return sourceFile.renameTo(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String buildRenameFile(File originFile, String targetName) {
        String targetString = originFile.getParent() + File.separator + targetName;
        if (originFile.isFile()) {
            targetString = targetString + "." + FilenameUtils.getExtension(originFile.getName());
        }
        return targetString;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        super.onActivityCreated(savedInstanceState);
    }

    public void show(FragmentManager fm) {
        super.show(fm, DialogRenameFile.class.getSimpleName());
    }
}
