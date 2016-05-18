package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.onyx.android.sdk.ui.R;

public class DialogAnnotation extends DialogBaseOnyx
{
    public enum AnnotationAction{add, update, onlyUpdate}

    public interface onAddAnnotationListener
    {
        public void addAnnotation(String annotation);
    }
    private onAddAnnotationListener mOnAddAnnotationListener = new onAddAnnotationListener()
    {

        @Override
        public void addAnnotation(String annotation)
        {
            //do nothing
        }
    };
    public void setOnAddAnnotationListener(onAddAnnotationListener l)
    {
        mOnAddAnnotationListener = l;
    }

    public interface onUpdateAnnotationListener
    {
        public void updateAnnotation(String note);
    }
    private onUpdateAnnotationListener mOnUpdateAnnotationListener = new onUpdateAnnotationListener()
    {

        @Override
        public void updateAnnotation(String note)
        {
            //do nothing
        }
    };
    public void setOnUpdateAnnotationListener(onUpdateAnnotationListener l)
    {
        mOnUpdateAnnotationListener = l;
    }

    public interface onRemoveAnnotationListener
    {
        public void removeAnnotation();
    }
    private onRemoveAnnotationListener mOnRemoveAnnotationListener = new onRemoveAnnotationListener()
    {

        @Override
        public void removeAnnotation()
        {
            //do nothing
        }
    };
    public void setOnRemoveAnnotationListener(onRemoveAnnotationListener l)
    {
        mOnRemoveAnnotationListener = l;
    }

    private EditText mEditTextAnnotation = null;
    private String mNote = null;

    public DialogAnnotation(Context context, AnnotationAction a)
    {
        super(context);

        init(a);
    }

    public DialogAnnotation(Context context, AnnotationAction a, String note)
    {
        super(context);

        mNote = note;
        init(a);
    }

    private void init(AnnotationAction a)
    {
        setContentView(R.layout.dialog_annotation);

        mEditTextAnnotation = (EditText) findViewById(R.id.edittext_annotation);
        if (mNote != null) {
            mEditTextAnnotation.setText(mNote);
        }

        Button buttonAdd = (Button) findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mOnAddAnnotationListener.addAnnotation(mEditTextAnnotation.getText().toString());
                DialogAnnotation.this.dismiss();
            }
        });

        Button buttonUpdate = (Button) findViewById(R.id.button_update);
        buttonUpdate.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mOnUpdateAnnotationListener.updateAnnotation(mEditTextAnnotation.getText().toString());
                DialogAnnotation.this.dismiss();
            }
        });

        Button buttonRemove = (Button) findViewById(R.id.button_remove);
        buttonRemove.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mOnRemoveAnnotationListener.removeAnnotation();
                DialogAnnotation.this.dismiss();
            }
        });

        Button buttonCancel = (Button) findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogAnnotation.this.dismiss();
            }
        });


        if (a == AnnotationAction.add) {
            buttonAdd.setVisibility(View.VISIBLE);
            buttonCancel.setVisibility(View.VISIBLE);
            buttonRemove.setVisibility(View.GONE);
            buttonUpdate.setVisibility(View.GONE);
        }
        else if (a == AnnotationAction.update) {
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

        InputMethodManager imm = (InputMethodManager)mEditTextAnnotation.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditTextAnnotation, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void dismiss()
    {
        InputMethodManager imm = (InputMethodManager)mEditTextAnnotation.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditTextAnnotation.getWindowToken(), 0);

        super.dismiss();
    }

}
