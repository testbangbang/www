package com.onyx.android.sdk.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.data.ScribbleFactory;

/**
 * Created by Joy on 2014/3/25.
 */
public final class ScribblePopupMenu extends LinearLayout {

    public static abstract class MenuCallback {
        public abstract void dismiss();
        public abstract void setScribbleMode();
        public abstract void setNormalMode();
    }

    private static enum Mode { Normal, Scribble }

    private final MenuCallback mMenuCallback;

    private boolean mIsShow = false;
    private Mode mMode = Mode.Scribble;

    private LinearLayout mNormalPenModeButton = null;
    private LinearLayout mBoldPenModeButton = null;
    private LinearLayout mUltraBoldPenModeButton =null;
    private LinearLayout mWhitePenModeButton = null;
    private LinearLayout mBlackPenModeButton = null;
    private LinearLayout mNormalModeButton = null;
    private LinearLayout mScribbleModeButton = null;
    private LinearLayout mDismissButton = null;

    public ScribblePopupMenu(Context context, RelativeLayout parentLayout, MenuCallback callback) {
        super(context);

        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.popup_window_scribble, this, true);

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        parentLayout.addView(this, p);

        setFocusable(false);

        this.setVisibility(View.GONE);

        mMenuCallback = callback;

        mNormalPenModeButton =(LinearLayout)findViewById(R.id.layout_pen_mode_normal);
        mBoldPenModeButton =(LinearLayout)findViewById(R.id.layout_pen_mode_bold);
        mUltraBoldPenModeButton =(LinearLayout)findViewById(R.id.layout_pen_mode_ultra_bold);
        mWhitePenModeButton=(LinearLayout)findViewById(R.id.layout_pen_mode_white);
        mBlackPenModeButton=(LinearLayout)findViewById(R.id.layout_pen_mode_black);
        mNormalModeButton = (LinearLayout)findViewById(R.id.layout_normal_mode);
        mScribbleModeButton = (LinearLayout)findViewById(R.id.layout_scribble_mode);
        mDismissButton = (LinearLayout)findViewById(R.id.layout_dismiss);

        mBlackPenModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScribbleFactory.singleton().setColor(Color.BLACK);
                updateButtonFocus();
            }
        });

        mWhitePenModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScribbleFactory.singleton().setColor(Color.WHITE);
                updateButtonFocus();
            }
        });

        mNormalPenModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScribbleFactory.singleton().setThickness(getContext(), 3);
                updateButtonFocus();
            }
        });

        mBoldPenModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScribbleFactory.singleton().setThickness(getContext(), 5);
                updateButtonFocus();
            }
        });

        mUltraBoldPenModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScribbleFactory.singleton().setThickness(getContext(), 7);
                updateButtonFocus();
            }
        });

        mNormalModeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuCallback.setNormalMode();
                mMode = Mode.Normal;
                updateButtonFocus();
            }
        });

        mScribbleModeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuCallback.setScribbleMode();
                mMode = Mode.Scribble;
                updateButtonFocus();
            }
        });

        mDismissButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuCallback.dismiss();
            }
        });
    }

    public void show()
    {
        mMode = Mode.Scribble;
        this.updateButtonFocus();

        setVisibility(View.VISIBLE);
        mIsShow = true;
    }

    public void hide()
    {
        setVisibility(View.GONE);
        mIsShow = false;
    }

    public void move(int selectionStartY, int selectionEndY) {
        if (this == null) {
            return;
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        final int verticalPosition;
        final int screenHeight = ((View)this.getParent()).getHeight();
        final int diffTop = screenHeight - selectionEndY;
        final int diffBottom = selectionEndY;
        if (diffTop > diffBottom) {
            verticalPosition = diffTop > this.getHeight() + 20
                    ? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.CENTER_VERTICAL;
        } else {
            verticalPosition = diffBottom > this.getHeight() + 20
                    ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.CENTER_VERTICAL;
        }

        layoutParams.addRule(verticalPosition);
        setLayoutParams(layoutParams);
    }

    public boolean isShow()
    {
        return mIsShow;
    }

    private void updateButtonFocus() {
        mBlackPenModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
        mWhitePenModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
        mNormalPenModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
        mBoldPenModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
        mUltraBoldPenModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
        mNormalModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
        mScribbleModeButton.setBackgroundResource(R.drawable.imagebtn_bg);

        switch (ScribbleFactory.singleton().getColor()) {
            case Color.BLACK:
                mBlackPenModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
                break;
            case Color.WHITE:
                mWhitePenModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
                break;
            default:
                break;
        }
        switch (ScribbleFactory.singleton().getThickness(getContext())) {
            case 3:
                mNormalPenModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
                break;
            case 5:
                mBoldPenModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
                break;
            case 7:
                mUltraBoldPenModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
                break;
            default:
                break;
        }
        switch (mMode) {
            case Normal:
                mNormalModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
                break;
            case Scribble:
                mScribbleModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
                break;
            default:
                break;
        }
    }
}
