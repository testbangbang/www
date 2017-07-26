/**
 *
 */
package com.onyx.android.dr.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.EditText;

import com.onyx.android.dr.R;
import com.onyx.android.dr.util.Utils;

public class DefaultEditText extends EditText {

    private Drawable delDrawable;
    private boolean isShow;
    private int padding;
    private boolean mIsShowClean = true;
    
    private onSelectionChangedListener mSelectionChangedListener;
    
    public interface onSelectionChangedListener{
    	public void onSelectionChanged(int selStart, int selEnd);
    }

    public DefaultEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        delDrawable = getResources().getDrawable(R.drawable.icon_delete);
        isShow = false;
        padding = Utils.dip2px(context, 10.0f);
    }

    public void setShowClean(boolean isShowClean){
    	mIsShowClean = isShowClean;
    	if(!mIsShowClean){
    		hide();
    	}
    }
    
    public void setSelectionChangedListener(onSelectionChangedListener selectionChangedListener){
    	mSelectionChangedListener = selectionChangedListener;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(!mIsShowClean){
        	return;
        }
        if (focused) {
            if (getText().toString().length() > 0) {
                show();
            }
        } else {
            hide();
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if(!mIsShowClean){
        	return;
        }
        if (text.length() > 0 && !isShow) {
            show();
        } else {
            if (text.length() == 0) {
                hide();
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mIsShowClean && (event.getX() > getWidth() - delDrawable.getIntrinsicWidth() - padding)) {
            setText("");
        }

        return super.dispatchTouchEvent(event);
    }
    
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
    	super.onSelectionChanged(selStart, selEnd);
    	
    	if(mSelectionChangedListener != null){
    		mSelectionChangedListener.onSelectionChanged(selStart, selEnd);
    	}
    }

    public void show() {
        setCompoundDrawablesWithIntrinsicBounds(null, null, delDrawable, null);
        isShow = true;
    }

    public void hide() {
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        isShow = false;
    }

    public void moveCursorToLast() {
        String str = getText().toString() + "";
        setSelection(str.length());
    }

    public void showKeyBoard(final Dialog dialog) {
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
