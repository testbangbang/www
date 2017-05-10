package com.onyx.kreader.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.onyx.kreader.R;

public class DialogAnnotation extends DialogBase {

    public enum AnnotationAction { add, update, onlyUpdate }

    public static abstract class Callback {
        public abstract void onAddAnnotation(String annotation);
        public abstract void onUpdateAnnotation(String annotation);
        public abstract void onRemoveAnnotation();
    }

    private EditText mEditTextAnnotation = null;
    private String note = null;
    private Callback callback;

    public DialogAnnotation(Context context, AnnotationAction action, Callback callback) {
        super(context);

        this.callback = callback;
        init(action);
    }

    public DialogAnnotation(Context context, AnnotationAction action, String note, Callback callback) {
        super(context);

        this.note = note;
        this.callback = callback;
        init(action);
    }

    private void init(AnnotationAction action) {
        setContentView(R.layout.dialog_annotation);

        mEditTextAnnotation = (EditText) findViewById(R.id.edittext_annotation);
        if (note != null) {
            mEditTextAnnotation.setText(note);
        }

        Button buttonAdd = (Button) findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {

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
            buttonAdd.setVisibility(View.VISIBLE);
            buttonCancel.setVisibility(View.VISIBLE);
            buttonRemove.setVisibility(View.GONE);
            buttonUpdate.setVisibility(View.GONE);
        } else if (action == AnnotationAction.update) {
            buttonAdd.setVisibility(View.GONE);
            buttonCancel.setVisibility(View.GONE);
            buttonRemove.setVisibility(View.VISIBLE);
            buttonUpdate.setVisibility(View.VISIBLE);
        } else {
            buttonAdd.setVisibility(View.GONE);
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
