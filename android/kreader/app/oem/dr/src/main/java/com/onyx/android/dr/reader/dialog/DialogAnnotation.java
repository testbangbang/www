package com.onyx.android.dr.reader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.sdk.utils.StringUtils;

public class DialogAnnotation extends Dialog {

    public enum AnnotationAction {add, update, onlyUpdate}

    public static abstract class Callback {
        public abstract void onAddAnnotation(String annotation);

        public abstract void onUpdateAnnotation(String annotation);

        public abstract void onRemoveAnnotation();
    }

    private EditText mEditTextAnnotation = null;
    private String note = null;
    private Callback callback;
    private TextView selectContent;
    private String userSelectContent;

    public DialogAnnotation(Context context, AnnotationAction action, String userSelectContent,Callback callback) {
        super(context, R.style.dialog_no_title);

        setCanceledOnTouchOutside(true);
        this.userSelectContent = userSelectContent;
        this.callback = callback;
        initView(action);
    }

    public DialogAnnotation(Context context, AnnotationAction action, String userSelectContent, String note, Callback callback) {
        super(context);

        this.note = note;
        this.userSelectContent = userSelectContent;
        this.callback = callback;
        initView(action);
    }

    private void initView(AnnotationAction action) {
        setContentView(R.layout.dialog_annotation);
        selectContent = (TextView) findViewById(R.id.select_content);
        if(StringUtils.isNotBlank(userSelectContent)){
            selectContent.setText(userSelectContent);
        }

        mEditTextAnnotation = (EditText) findViewById(R.id.edittext_annotation);
        if (StringUtils.isNotBlank(note)) {
            mEditTextAnnotation.setText(note);
        }

        Button buttonSave = (Button) findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onAddAnnotation(mEditTextAnnotation.getText().toString());
                }
                DialogAnnotation.this.dismiss();
            }
        });

        Button buttonUpdate = (Button) findViewById(R.id.button_update);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onUpdateAnnotation(mEditTextAnnotation.getText().toString());
                }
                DialogAnnotation.this.dismiss();
            }
        });

        Button buttonRemove = (Button) findViewById(R.id.button_remove);
        buttonRemove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onRemoveAnnotation();
                }
                DialogAnnotation.this.dismiss();
            }
        });

        Button buttonCancel = (Button) findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogAnnotation.this.dismiss();
            }
        });


        if (action == AnnotationAction.add) {
            buttonSave.setVisibility(View.VISIBLE);
            buttonCancel.setVisibility(View.VISIBLE);
            buttonRemove.setVisibility(View.GONE);
            buttonUpdate.setVisibility(View.GONE);
        } else if (action == AnnotationAction.update) {
            buttonSave.setVisibility(View.GONE);
            buttonCancel.setVisibility(View.GONE);
            buttonRemove.setVisibility(View.VISIBLE);
            buttonUpdate.setVisibility(View.VISIBLE);
        } else {
            buttonSave.setVisibility(View.GONE);
            buttonCancel.setVisibility(View.GONE);
            buttonRemove.setVisibility(View.GONE);
            buttonUpdate.setVisibility(View.VISIBLE);
        }

        LayoutParams params = getWindow().getAttributes();
        params.y = -getWindow().getWindowManager().getDefaultDisplay().getHeight();
        getWindow().setAttributes(params);

        InputMethodManager imm = (InputMethodManager) mEditTextAnnotation.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditTextAnnotation, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void dismiss() {
        InputMethodManager imm = (InputMethodManager) mEditTextAnnotation.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditTextAnnotation.getWindowToken(), 0);

        super.dismiss();
    }

}
