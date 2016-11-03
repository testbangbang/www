/**
 *
 */

package com.onyx.kreader.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.RectF;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.TextSize;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.actions.DictionaryQueryAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.highlight.HighlightCursor;
import com.onyx.kreader.ui.view.HTMLReaderWebView;

import static com.onyx.kreader.ui.data.SingletonSharedPreference.AnnotationHighlightStyle.Highlight;

public class PopupSelectionMenu extends LinearLayout {
    private static final String TAG = PopupSelectionMenu.class.getSimpleName();

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
    private TextView mDictTitle;
    private HTMLReaderWebView mWebView;
    private TextView mPageIndicator;
    private MenuCallback mMenuCallback;
    private View mDictNextPage;
    private View mDictPrevPage;
    private View webViewDividerLine;
    private ImageView highlightView;
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

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(this, p);

        highlightView = (ImageView) findViewById(R.id.imageview_highlight);
        mDictTitle = (TextView) findViewById(R.id.dict_title);
        webViewDividerLine = findViewById(R.id.webView_divider_line);
        mDictNextPage = findViewById(R.id.dict_next_page);
        mDictNextPage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mWebView != null) {
                    mWebView.nextPage();
                }
            }
        });

        highlightView.setImageResource(SingletonSharedPreference.getAnnotationHighlightStyle(mActivity).equals(Highlight) ?
                R.drawable.ic_dialog_reader_choose_highlight : R.drawable.ic_dialog_reader_choose_underline);

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

    public void show(final ReaderDataHolder readerDataHolder, boolean isWord) {
        if (readerDataHolder.getReaderUserDataInfo().getHighlightResult() == null) {
            return;
        }

        if (isWord) {
            updateTranslation(readerDataHolder, mMenuCallback.getSelectionText());
            showTranslation();
        } else {
            hideTranslation();
        }

        requestLayoutView(readerDataHolder);
    }

    public void hide() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                setVisibility(View.GONE);
            }
        });
    }

    private void requestLayoutView(ReaderDataHolder readerDataHolder){
        setVisibility(VISIBLE);

        HighlightCursor beginHighlightCursor = readerDataHolder.getSelectionManager().getHighlightCursor(HighlightCursor.BEGIN_CURSOR_INDEX);
        HighlightCursor endHighlightCursor = readerDataHolder.getSelectionManager().getHighlightCursor(HighlightCursor.END_CURSOR_INDEX);
        if (beginHighlightCursor == null || endHighlightCursor == null){
            return;
        }
        RectF beginCursorRectF = beginHighlightCursor.getDisplayRect();
        RectF endCursorRectF = endHighlightCursor.getDisplayRect();

        float dividerHeight = readerDataHolder.getContext().getResources().getDimension(R.dimen.popup_selection_menu_divider_height);

        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        this.measure(w, h);
        int measuredHeight = this.getMeasuredHeight();

        RectF start = beginCursorRectF;
        RectF end = endCursorRectF;
        final float screenHeight = ((View) this.getParent()).getHeight();
        final float diffTop = start.top;
        final float diffBottom = screenHeight - end.bottom;

        if (diffTop > diffBottom){
            float y = start.top - dividerHeight - measuredHeight;
            y = isShowTranslation() ? dividerHeight : Math.max(y,0);
            setY(y);
        }else {
            float y = end.bottom + dividerHeight;
            y = isShowTranslation() ? screenHeight - measuredHeight - dividerHeight : Math.min(y,screenHeight - measuredHeight);
            setY(y);
        }
    }

    public boolean isShowTranslation(){
        return this.webViewDividerLine.getVisibility() == VISIBLE;
    }

    public boolean isShow() {
        return (getVisibility() == View.VISIBLE);
    }

    public void hideTranslation() {
        this.webViewDividerLine.setVisibility(GONE);
        this.findViewById(R.id.layout_dict).setVisibility(View.GONE);
    }

    public void showTranslation() {
        this.webViewDividerLine.setVisibility(VISIBLE);
        this.findViewById(R.id.layout_dict).setVisibility(View.VISIBLE);
    }

    private void updateTranslation(final ReaderDataHolder readerDataHolder, String token) {
        mDictTitle.setText(token);
        final DictionaryQueryAction dictionaryQueryAction = new DictionaryQueryAction(token, "content://com.onyx.android.dict.OnyxDictProvider");
        dictionaryQueryAction.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mWebView.loadDataWithBaseURL(null, dictionaryQueryAction.getExpString(), "text/html", "utf-8", null);
            }
        });
    }
}
