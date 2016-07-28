/**
 *
 */

package com.onyx.kreader.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.TextSize;
import android.widget.*;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.view.HTMLReaderWebView;

public class PopupSelectionMenu extends LinearLayout {

    public enum SelectionType {
        SingleWordType,
        MultiWordsType
    }

    public static abstract class MenuCallback {
        public abstract void resetSelection();
        public abstract String getSelectionText();
        public abstract void copy();
        public abstract void highLight();
        public abstract void addAnnotation();
        public abstract void showDictionary();
        public abstract boolean supportSelectionMode();
        public abstract void closeMenu();
    }

    private final Activity mActivity;
    private ReaderDataHolder readerDataHolder;
    private TextView mDictTitle;
    private HTMLReaderWebView mWebView;
    private TextView mPageIndicator;
    private MenuCallback mMenuCallback;
    private View mDictNextPage;
    private View mDictPrevPage;

    /**
     * eliminate compiler warning
     *
     * @param context
     */
    private PopupSelectionMenu(Context context) {
        super(context);
        throw new IllegalAccessError();
    }

    public PopupSelectionMenu(ReaderDataHolder readerDataHolder, RelativeLayout layout, MenuCallback menuCallback) {
        super(readerDataHolder.getContext());
        mActivity = (Activity)readerDataHolder.getContext();

        setFocusable(false);

        final LayoutInflater inflater = (LayoutInflater)
                readerDataHolder.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.popup_selection_menu, this, true);
        mMenuCallback = menuCallback;
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(this, p);

        mDictTitle = (TextView) findViewById(R.id.dict_title);

        mDictNextPage = findViewById(R.id.dict_next_page);
        mDictNextPage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mWebView != null) {
                    mWebView.nextPage();
                }
            }
        });

        mDictPrevPage = findViewById(R.id.dict_prev_page);
        mDictPrevPage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mWebView != null) {
                    mWebView.prevPage();
                }
            }
        });

        mPageIndicator = (TextView) findViewById(R.id.page_indicator);

        mWebView = (HTMLReaderWebView) findViewById(R.id.explain);
        mWebView.getSettings().setTextSize(TextSize.LARGER);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setPageTurnType(HTMLReaderWebView.PAGE_TURN_TYPE_VERTICAL);
        mWebView.setPageTurnThreshold(15);
        mWebView.registerOnOnPageChangedListener(new HTMLReaderWebView.OnPageChangedListener() {

            @Override
            public void onPageChanged(int totalPage, int curPage) {
                if (totalPage > 1) {
                    mPageIndicator.setVisibility(View.VISIBLE);
                    mDictNextPage.setVisibility(View.VISIBLE);
                    mDictPrevPage.setVisibility(View.VISIBLE);
                }
                mPageIndicator.setText(curPage + "/" + totalPage);
            }
        });

        LinearLayout imagebuttonCopy = (LinearLayout) findViewById(R.id.imagebutton_copy);
        imagebuttonCopy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mMenuCallback.copy();
                mMenuCallback.resetSelection();
                PopupSelectionMenu.this.hide();
            }
        });

        LinearLayout imagebuttonHighlight = (LinearLayout) findViewById(R.id.imagebutton_highlight);
        imagebuttonHighlight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mMenuCallback.highLight();
                PopupSelectionMenu.this.hide();
            }
        });

        LinearLayout imagebuttonAnnotation = (LinearLayout) findViewById(R.id.imagebutton_annotaion);
        imagebuttonAnnotation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mMenuCallback.addAnnotation();
                PopupSelectionMenu.this.hide();
            }
        });

        LinearLayout button_dict = (LinearLayout) findViewById(R.id.imagebutton_dict);
        button_dict.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mMenuCallback.showDictionary();
                mMenuCallback.resetSelection();
                PopupSelectionMenu.this.hide();
            }
        });

        ImageView buttonCloseMenu = (ImageView) findViewById(R.id.button_close);
        buttonCloseMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuCallback.closeMenu();
            }
        });
        setVisibility(View.GONE);
    }

    Activity getActivity() {
        return mActivity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void show() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                PopupSelectionMenu.this.updateTranslation(mMenuCallback.getSelectionText());
                setVisibility(View.VISIBLE);
            }
        });
    }

    public void hide() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                setVisibility(View.GONE);
            }
        });
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
        final int screenHeight = ((View) this.getParent()).getHeight();
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

    public boolean isShow() {
        return (getVisibility() == View.VISIBLE);
    }

    public void hideTranslation() {
        this.findViewById(R.id.layout_dict).setVisibility(View.GONE);
    }

    public void showTranslation() {
        this.findViewById(R.id.layout_dict).setVisibility(View.VISIBLE);
    }

    private String getExplanation(String token) {
        Cursor cursor = mActivity.getContentResolver().query(
                Uri.parse("content://com.onyx.android.dict.OnyxDictProvider"), null, "token=\'" + token + "\'", null,
                null);
        String expString = "";
        try {
            if (cursor == null || cursor.getCount() == 0)
                return null;
            int count = cursor.getCount();
            int index = 0;
            while (cursor.moveToNext()) {
                expString += cursor.getString(3);
                if (index >= 0 && index < count - 1)
                    expString += "<br><br><br><br>";
                index++;
            }
            return expString;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void updateTranslation(String token) {
        mDictTitle.setText(token);
        mWebView.loadDataWithBaseURL(null, getExplanation(token), "text/html", "utf-8", null);
    }
}
