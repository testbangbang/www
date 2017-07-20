/**
 *
 */
package com.onyx.android.dr.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onyx.android.dr.R;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.DimenUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author Simon
 */
public class HTMLReaderWebView extends WebView {

    private static final Class TAG = HTMLReaderWebView.class;

    private int mCurrentPage;
    private int mTotalPage;

    public static final int PAGE_TURN_TYPE_VERTICAL = 1;
    public static final int PAGE_TURN_TYPE_HORIZOTAL = 2;
    private int pageTurnType = PAGE_TURN_TYPE_VERTICAL;
    private float lastX, lastY;
    private boolean loadCssStyle = true;

    private int heightForSaveView = 50;
    private int pageTurnThreshold = 300;
    private int marginTop = 10;

    public void setPageTurnType(int pageTurnType) {
        this.pageTurnType = pageTurnType;
    }

    public void setHeightForSaveView(int heightForSaveView) {
        this.heightForSaveView = heightForSaveView;
    }

    public void setPageTurnThreshold(int pageTurnThreshold) {
        this.pageTurnThreshold = pageTurnThreshold;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(mInternalScrollX, mInternalScrollY);
    }

    @Override
    public void draw(Canvas canvas) {
        scrollTo(mInternalScrollX, mInternalScrollY);
        super.draw(canvas);
    }

    @Override
    public void computeScroll() {
        scrollTo(mInternalScrollX, mInternalScrollY);
    }

    private int mInternalScrollX = 0;
    private int mInternalScrollY = 0;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        scrollTo(mInternalScrollX, mInternalScrollY);
        super.onScrollChanged(mInternalScrollX, mInternalScrollY, oldl, oldt);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    public void setLoadCssStyle(boolean loadCssStyle) {
        this.loadCssStyle = loadCssStyle;
    }

    public void setScroll(int l, int t) {
        mInternalScrollX = l;
        mInternalScrollY = t;
    }

    /**
     * @param context
     */
    public HTMLReaderWebView(Context context) {
        super(context);
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public HTMLReaderWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public HTMLReaderWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });

        setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                setScroll(0, 0);
                mCurrentPage = 0;
                super.onPageFinished(view, url);

                final HTMLReaderWebView myWebView = (HTMLReaderWebView) view;

                applyCSS(myWebView);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });

        EpdController.disableA2ForSpecificView(this);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setBuiltInZoomControls(false);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setDefaultTextEncodingName("UTF-8");
        getSettings().setBlockNetworkImage(false);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        setLongClickable(false);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getX();
                        lastY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        return true;
                    case MotionEvent.ACTION_UP:
                        int direction = detectDirection(event);
                        if (direction == TurningDirection.NEXT) {
                            nextPage();
                            return true;
                        } else if (direction == TurningDirection.PREV) {
                            prevPage();
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void applyCSS(WebView webView) {
        if (!loadCssStyle) {
            return;
        }
        String varMySheet = "var mySheet = document.styleSheets[0];";

        String addCSSRule = "function addCSSRule(selector, newRule) {"
                + "ruleIndex = mySheet.cssRules.length;"
                + "mySheet.insertRule(selector + '{' + newRule + ';}', ruleIndex);"

                + "}";

        int width = webView.getMeasuredWidth();
        float fontSize = DimenUtils.getCssPx(getContext(), getResources().getDimension(R.dimen.control_panel_floating_tittle_text_size));
        String insertRule1 = "addCSSRule('html', '"
                + " -webkit-column-gap: 0px; -webkit-column-width: "
                + width + "px; margin-top:" + marginTop + "px;"
                + " line-height:130%; letter-spacing:2px; text-align:justify; font-size: " + fontSize + "px')";


        String css = varMySheet + addCSSRule + insertRule1;

        webView.loadUrl("javascript:" + css);
    }

    private int detectDirection(MotionEvent currentEvent) {
        return TurningDetector.detectBothAxisTuring(getContext(), (int) (currentEvent.getX() - lastX), (int) (currentEvent.getY() - lastY));
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }


    public interface OnPageChangedListener {
        public void onPageChanged(int totalPage, int curPage);
    }

    private OnPageChangedListener mOnPageChangedListener;

    public void registerOnOnPageChangedListener(OnPageChangedListener l) {
        mOnPageChangedListener = l;
    }

    public void unRegisterOnOnPageChangedListener() {
        mOnPageChangedListener = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (refreshWebViewSize() || !loadCssStyle) {
            Debug.d(TAG, "onDraw: ");
            super.onDraw(canvas);
        }

    }

    private boolean refreshWebViewSize() {
        int width = getWidth();
        int scrollWidth = computeHorizontalScrollRange();
        if (width == 0) {
            return true;
        }

        mTotalPage = (scrollWidth + width - 1) / width;
        if (mCurrentPage > mTotalPage) {
            mCurrentPage = mTotalPage;
        }

        if (mCurrentPage <= 0) {
            mCurrentPage = 1;
        }

        if (mOnPageChangedListener != null) {
            mOnPageChangedListener.onPageChanged(mTotalPage, mCurrentPage);
        }

        Debug.e(TAG, "page: " + mCurrentPage + " / " + mTotalPage + " "
                + getContentHeight() + " " + scrollWidth + " view: " + getHeight());

        return mTotalPage >= 1 && getContentHeight() <= getHeight();
    }

    private int getScrollWidth() {
        return getMeasuredWidth();
    }

    public void nextPage() {
        if (mCurrentPage < mTotalPage) {
            // EpdController.invalidate(webView, UpdateMode.GC);
            mCurrentPage++;
            setScroll(getScrollX() + getScrollWidth(), 0);
            scrollBy(getScrollWidth(), 0);
            if (mOnPageChangedListener != null)
                mOnPageChangedListener.onPageChanged(mTotalPage, mCurrentPage);
        }
    }

    public void prevPage() {
        if (mCurrentPage > 1) {
            // EpdController.invalidate(webView, UpdateMode.GC);
            mCurrentPage--;
            setScroll(getScrollX() - getScrollWidth(), 0);
            scrollBy(getScrollWidth(), 0);
            if (mOnPageChangedListener != null)
                mOnPageChangedListener.onPageChanged(mTotalPage, mCurrentPage);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            nextPage();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            prevPage();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static String getHtmlCacheDir(Context context) {
        return "/data/data/" + context.getPackageName() + "/html";
    }

    public String saveWebContentToFile(Context context, String expString) {

        String saveTempFile = getHtmlCacheDir(context) + "/result.html";

        OutputStreamWriter outputStreamWriter = null;
        File dirFile = new File(getHtmlCacheDir(context));
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File saveTemp = new File(saveTempFile);
        if (saveTemp.exists()) {
            saveTemp.delete();
        }
        try {
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(saveTemp));
            if (expString != null) {
                saveTemp.createNewFile();
                outputStreamWriter.write(expString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return saveTempFile;
    }

}
