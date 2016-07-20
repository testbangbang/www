package com.onyx.android.note.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.onyx.android.note.R;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by solskjaer49 on 15/12/2 17:33.
 */
public class DialogRenameNote extends OnyxAlertDialog {
    static final public String ARGS_CURRENT_NOTE_NAME = "args_current_note_name";
    private OnRenameCallback callback;
    EditText mInputEditText = null;

    public interface OnRenameCallback {
        void onRename(String newName);
    }

    public void setOnRenameListener(OnRenameCallback cb) {
        this.callback = cb;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final String fileName = getArguments().getString(ARGS_CURRENT_NOTE_NAME);
        setParams(new OnyxAlertDialog.Params().setTittleString(getString(R.string.rename))
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_rename_note)
                .setCustomViewAction(new OnyxAlertDialog.CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        mInputEditText = (EditText) customView.findViewById(R.id.editText_rename);
                        TextView currentName = (TextView) customView.findViewById(R.id.textView_current_name);
                        currentName.setText(fileName);
                    }
                })
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String newFileName = mInputEditText.getText().toString().trim();
                        if (StringUtils.isNullOrEmpty(newFileName)) {
                            dismiss();
                            return;
                        }
                        callback.onRename(newFileName);
                        dismiss();
                    }
                }));
        super.onCreate(savedInstanceState);
    }

    public void show(FragmentManager fm) {
        super.show(fm, DialogRenameNote.class.getSimpleName());
    }
}
