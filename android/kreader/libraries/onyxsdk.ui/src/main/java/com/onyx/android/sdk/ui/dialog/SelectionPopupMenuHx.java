/**
 *
 */

package com.onyx.android.sdk.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;


public class SelectionPopupMenuHx extends LinearLayout {

    public enum SelectionType {
        SingleWordType,
        MultiWordsType
    }

    private enum Tab {
        Copy, Highlight, Annotation
    }

    public static abstract class MenuCallback {
        public abstract void resetSelection();

        public abstract String getSelectionText();

        public abstract void copy();

        public abstract void highLight();

        public abstract void addAnnotation(String comment);
    }

    private boolean mIsShow = false;
    private Tab mCurrentChoice = Tab.Copy;
    private final Activity mActivity;
    private MenuCallback mMenuCallback;
    private EditText mEditText;
    private Button mOkButton, mCancelButton;
    private TextView imageButtonCopy, imageButtonHighlight, imageButtonAnnotation;
    InputMethodManager imm;

    /**
     * eliminate compiler warning
     *
     * @param context
     */
    private SelectionPopupMenuHx(Context context) {
        super(context);
        throw new IllegalAccessError();
    }

    public SelectionPopupMenuHx(Activity activity, RelativeLayout layout, MenuCallback menuCallback) {
        super(activity);
        mActivity = activity;
        setFocusable(false);
        final LayoutInflater inflater = (LayoutInflater)
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_selection_popup_menu_hx, this, true);
        mMenuCallback = menuCallback;
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(this, p);
        mEditText = (EditText) findViewById(R.id.content_text);
        mCancelButton = (Button) findViewById(R.id.button_cancel);
        mOkButton = (Button) findViewById(R.id.button_ok);
        imageButtonCopy = (TextView) findViewById(R.id.imagebutton_copy);
        imageButtonHighlight = (TextView) findViewById(R.id.imagebutton_highlight);
        imageButtonAnnotation = (TextView) findViewById(R.id.imagebutton_annotaion);
        InitFunction();
        setVisibility(View.GONE);
        mCurrentChoice = Tab.Highlight;
        setCurrentTabIndicator();
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    Activity getActivity() {
        return mActivity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    private void InitFunction() {
        imageButtonCopy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentChoice = Tab.Copy;
                mEditText.setFocusable(false);
                mEditText.setText(mMenuCallback.getSelectionText());
                setCurrentTabIndicator();
            }
        });
        imageButtonHighlight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentChoice = Tab.Highlight;
                mEditText.setFocusable(false);
                mEditText.setText(mMenuCallback.getSelectionText());
                setCurrentTabIndicator();
            }
        });
        imageButtonAnnotation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentChoice = Tab.Annotation;
                mEditText.setFocusable(true);
                mEditText.setFocusableInTouchMode(true);
                mEditText.requestFocus();
                mEditText.setText(null);
                mEditText.setHint(R.string.tabwidget_annotation);
                move(2000);
                imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
                setCurrentTabIndicator();
            }
        });
        mCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                }
                SelectionPopupMenuHx.this.hide();
            }
        });
        mOkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                }
                switch (mCurrentChoice) {
                    case Copy:
                        mMenuCallback.copy();
                        mMenuCallback.resetSelection();
                        SelectionPopupMenuHx.this.hide();
                        break;
                    case Highlight:
                        mMenuCallback.highLight();
                        SelectionPopupMenuHx.this.hide();
                        break;
                    case Annotation:
                        mMenuCallback.addAnnotation(mEditText.getText().toString());
                        SelectionPopupMenuHx.this.hide();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setCurrentTabIndicator() {
        switch (mCurrentChoice) {
            case Copy:
                imageButtonCopy.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                imageButtonCopy.setTextColor(getResources().getColor(android.R.color.black));
                imageButtonAnnotation.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                imageButtonAnnotation.setTextColor(getResources().getColor(android.R.color.darker_gray));
                imageButtonHighlight.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                imageButtonHighlight.setTextColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case Highlight:
                imageButtonHighlight.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                imageButtonHighlight.setTextColor(getResources().getColor(android.R.color.black));
                imageButtonAnnotation.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                imageButtonAnnotation.setTextColor(getResources().getColor(android.R.color.darker_gray));
                imageButtonCopy.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                imageButtonCopy.setTextColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case Annotation:
                imageButtonAnnotation.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                imageButtonAnnotation.setTextColor(getResources().getColor(android.R.color.black));
                imageButtonHighlight.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                imageButtonHighlight.setTextColor(getResources().getColor(android.R.color.darker_gray));
                imageButtonCopy.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                imageButtonCopy.setTextColor(getResources().getColor(android.R.color.darker_gray));
                break;
            default:
                break;
        }
    }

    public void show() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (mCurrentChoice != Tab.Annotation) {
                    mEditText.setText(mMenuCallback.getSelectionText());
                }
                setVisibility(View.VISIBLE);
                mIsShow = true;
            }
        });
    }

    public void hide() {
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                setVisibility(View.GONE);
                mIsShow = false;
            }
        });
    }

    public void move(int selectionEndY) {
        if (this == null) {
            return;
        }
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        final int verticalPosition;
        final int screenHeight = ((View) this.getParent()).getHeight();
        final int diffTop = screenHeight - selectionEndY;
        if (diffTop > selectionEndY) {
            verticalPosition = diffTop > this.getHeight() + 20
                    ? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.CENTER_VERTICAL;
        } else {
            verticalPosition = selectionEndY > this.getHeight() + 20
                    ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.CENTER_VERTICAL;
        }

        layoutParams.addRule(verticalPosition);
        setLayoutParams(layoutParams);
    }

    public boolean isShow() {
        return (getVisibility() == View.VISIBLE);
    }

}
