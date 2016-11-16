package com.onyx.android.note.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.note.R;
import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;

/**
 * Created by solskjaer49 on 16/06/28 14:07.
 */
public class DialogCreateNewFolder extends OnyxAlertDialog {

    public interface OnCreateListener {
        boolean onCreated(String title);
    }

    public void setOnCreatedListener(OnCreateListener onCreatedListener) {
        this.mOnCreatedListener = onCreatedListener;
    }

    private OnCreateListener mOnCreatedListener;
    EditText mInputEditText = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (NoteAppConfig.sharedInstance(getActivity()).showInputMethodInstantlyAfterOpenDialog()) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Params params = new OnyxAlertDialog.Params().setTittleString(getString(R.string.new_folder))
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_input)
                .setCustomViewAction(new OnyxAlertDialog.CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        mInputEditText = (EditText) customView.findViewById(R.id.editText_Input);
                    }
                }).setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String folder_name = mInputEditText.getText().toString().trim();
                        if (folder_name.length() <= 0) {
                            Toast.makeText(getActivity(),
                                    R.string.name_can_not_empty, Toast.LENGTH_SHORT).show();
                        } else {
                            if (mOnCreatedListener.onCreated(mInputEditText.getText().toString().trim())) {
                                dismiss();
                            }
                        }
                    }
                });
        if (NoteAppConfig.sharedInstance(getActivity()).useMXUIStyle()) {
            params.setCustomLayoutResID(R.layout.mx_custom_alert_dialog);
        }
        setParams(params);
        super.onCreate(savedInstanceState);
    }

    public void show(FragmentManager fm) {
        super.show(fm, DialogCreateNewFolder.class.getSimpleName());
    }
}
