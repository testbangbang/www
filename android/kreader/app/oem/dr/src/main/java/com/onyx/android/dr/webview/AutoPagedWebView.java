package com.onyx.android.dr.webview;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.onyx.android.dr.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 11/4/15.
 */
public class AutoPagedWebView extends WebView {
    //public static final String DICT_WEBSIT = "file://" + Utils.WEBSIT_DIR + Utils.HTML_FILE;
    public static final String ONYX_DICT = "OnyxDict";
    //js function
    private static final String JS_SETKEYWORDEXPLAIN = "jsSetKeywordExplain";
    private static final String JS_RELOAD_DICT_IMAGE = "jsReloadDictImage";
    private static final String TAG = AutoPagedWebView.class.getSimpleName();
    public MediaPlayer mediaPlayer = null;
    public Map<String, String> explainList = new HashMap<String, String>();
    private String currentSoundPath = null;
    private List<String> resourceList = new ArrayList<String>();
    private int currentDictionary = -1;
    private WebChromeClient webChromeClient;
    private WebViewClient webViewClient;
    private OnTouchListener touchListener;
    private int currentPage;
    private int totalPage;
    private int lastScrollRange = 0;
    private PageChangedListener pageChangedListener;
    private UpdateDictionaryListCallback updateDictionaryListCallback;
    private int measuredHeight = 0;
    private int measuredWidth = 0;

    @Override
    public void destroy() {
        super.destroy();
    }

    private String headwordSoundPath = null;
    private Context mContext = null;

    /**
     * @param context
     */
    public AutoPagedWebView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public AutoPagedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AutoPagedWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public static String getHtmlCacheDir(Context context) {
        return context.getDir("html", Context.MODE_PRIVATE).getPath();
    }

    private void updatePageNumber() {
        Log.i(TAG, "applyCSS updatePageNumber");
        scrollTo(0, 0);
        applyCSS(AutoPagedWebView.this);
        reset();
        triggerCalculation();
    }

    public int getCurrentDictionary() {
        return currentDictionary;
    }

    public void setCurrentDictionary(int currentDictionary) {
        this.currentDictionary = currentDictionary;
    }

    public int getPageCount() {
        return totalPage;
    }

    private void reset() {
        currentPage = 1;
        totalPage = 1;
    }

    private WebChromeClient getWebChromeClient() {
        if (webChromeClient == null) {
            webChromeClient = new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress >= 100) {
                        setScroll(0, 0);
                        triggerCalculation();
                    }
                }

                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    String message = consoleMessage.message();
                    int lineNumber = consoleMessage.lineNumber();
                    String sourceID = consoleMessage.sourceId();
                    String messageLevel = consoleMessage.message();

                    Log.i(TAG, "lineNumber:" + lineNumber + ",message:" + message);
                    return true;
                }
            };
        }
        return webChromeClient;
    }

    /**
     * continuous calculating, until it's finished.
     */
    private void triggerCalculation() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!calculate()) {
                    triggerCalculation();
                } else {
                    notifyPageChanged();
                }
            }
        }, 500);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (measuredHeight == 0 || measuredWidth == 0) {
            measuredHeight = getMeasuredHeight();
            measuredWidth = getMeasuredWidth();
            applyCSS(this);
            Log.d("applyCSS", "onWindowFocusChanged: " + getMeasuredHeight() + "uuu" + getMeasuredWidth() + ".." + measuredHeight + "qq" + measuredWidth);
        }
    }

    private void applyCSS(final WebView webView) {
        String varMySheet = "var mySheet = document.styleSheets[0];";

        String addCSSRule = "function addCSSRule(selector, newRule) {"
                + "ruleIndex = mySheet.cssRules.length;"
                + "mySheet.insertRule(selector + '{' + newRule + ';}', ruleIndex);"
                + "}";

        String insertRule1 = "addCSSRule('html', 'padding: 0px; height: "
                + (measuredHeight / getContext().getResources().getDisplayMetrics().density)
                + "px; -webkit-column-gap: 0px; -webkit-column-width: "
                + measuredWidth + "px;  text-align:justify; ')";
        Log.d("applyCSS", "applyCSS: " + getMeasuredHeight() + "uuu" + getMeasuredWidth() + ".." + measuredHeight + "qq" + measuredWidth);
        webView.loadUrl("javascript:" + varMySheet);
        webView.loadUrl("javascript:" + addCSSRule);
        webView.loadUrl("javascript:" + insertRule1);
    }

    private WebViewClient getWebViewClient() {
        if (webViewClient == null) {
            webViewClient = new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.i(TAG, "applyCSS pageFinished");
                    setScroll(0, 0);
                    applyCSS(view);
                    reset();
                }

                @Override
                public void onScaleChanged(WebView view, float oldScale, float newScale) {
                    super.onScaleChanged(view, oldScale, newScale);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return true;
                }
            };
        }
        return webViewClient;
    }

    private boolean processAction(final float downX, final float downY, final float upX, final float upY) {
        float xPivot = upX - downX;
        float yPivot = upY - downY;

        int h = getHorizontalThreshold();
        int v = getVerticalThreshold();

        Log.i(TAG, "upX:" + upX + "upY:" + upY);
        Log.i(TAG, "downX:" + downX + "downY:" + downY);

        Log.i(TAG, "Horizontal:" + h);
        Log.i(TAG, "Vertical:" + v);
        Log.i(TAG, "xPivot:" + xPivot);
        Log.i(TAG, "yPivot:" + yPivot);
        float distX = Math.abs(xPivot) - h;
        float distY = Math.abs(yPivot) - v;

        if (Math.abs(xPivot) > getHorizontalThreshold()) {
            if (!(Math.abs(yPivot) > getVerticalThreshold() && distY > distX)) {
                if (xPivot > 0) {
                    prevPage();
                } else {
                    nextPage();
                }
                return true;
            }
        }

        if (Math.abs(yPivot) > getVerticalThreshold()) {
            if (yPivot > 0) {
                prevPage();
            } else {
                nextPage();
            }
            return true;
        }
        return false;
    }

    private OnTouchListener getTouchListener() {
        if (touchListener == null) {
            touchListener = new OnTouchListener() {
                float downX = 0;
                float downY = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = event.getX();
                            downY = event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            float x = event.getX();
                            float y = event.getY();
                            if (processAction(downX, downY, x, y)) {
                                return true;
                            }
                            break;
                    }

                    return false;
                }
            };
        }
        return touchListener;
    }

    //TODO:these 2 thresholds should use device depend px size to improve touch performance.Avoid hard code px.
    private int getVerticalThreshold() {
        return (int) Math.ceil(getResources().getDisplayMetrics().heightPixels / 15);
    }

    private int getHorizontalThreshold() {
        return (int) Math.ceil(getResources().getDisplayMetrics().widthPixels / 15);
    }

    private void init() {
        Log.i(TAG, "init");
        if (!isInEditMode()) {
            setWebChromeClient(getWebChromeClient());
            setWebViewClient(getWebViewClient());
            getSettings().setJavaScriptEnabled(true);
            addJavascriptInterface(this, ONYX_DICT);
            setVerticalScrollBarEnabled(false);
            setHorizontalScrollBarEnabled(false);
            getSettings().setBuiltInZoomControls(false);
            setOverScrollMode(OVER_SCROLL_NEVER);

            setOnTouchListener(getTouchListener());
            setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            setLongClickable(false);
            clearHistory();
        }
    }

    public void setPageChangedListener(final PageChangedListener l) {
        pageChangedListener = l;
    }

    /**
     * @return dirty or not
     */
    private boolean calculate() {
        int width = measuredWidth;
        int scrollWidth = computeHorizontalScrollRange();
        int currentOffset = computeHorizontalScrollOffset();

        if (width <= 0 || scrollWidth <= 0) {
            reset();
            return false;
        }

        boolean changed = false;
        if (lastScrollRange != scrollWidth) {
            changed = true;
            lastScrollRange = scrollWidth;
        }
        Log.i(TAG, "scrollWidth:" + scrollWidth + ",width:" + width);
        totalPage = (scrollWidth + width - 1) / width;
        float marginLeft = getResources().getDimension(R.dimen.auto_page_web_view_margin_left);
        scrollWidth -= totalPage * marginLeft;
        totalPage = (scrollWidth + width - 1) / width;
        if (totalPage <= 0) {
            totalPage = 1;
        }

        currentPage = currentOffset / width + 1;
        if (currentPage > totalPage) {
            currentPage = totalPage;
        }
        if (currentPage <= 0) {
            currentPage = 1;
        }
        return !changed;
    }

    public void nextPage() {
        calculate();
        if (currentPage < totalPage) {
            currentPage++;
            setScroll(getScrollX() + getWidth(), 0);
            scrollBy(getWidth(), 0);
            notifyPageChanged();
        }
    }

    public void prevPage() {
        calculate();
        if (currentPage > 1) {
            currentPage--;
            setScroll(getScrollX() - getWidth(), 0);
            scrollBy(-getWidth(), 0);
            notifyPageChanged();
        }
    }

    public void setScroll(int l, int t) {
        mInternalScrollX = l;
        mInternalScrollY = t;
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
    protected void onDraw(Canvas canvas) {
        scrollTo(mInternalScrollX, mInternalScrollY);
        super.onDraw(canvas);
        calculate();
    }

    private void notifyPageChanged() {
        if (pageChangedListener != null) {
            pageChangedListener.onPageChanged(currentPage, totalPage);
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

    public void setTextZoom(int textZoom) {
        getSettings().setTextZoom(textZoom);
        triggerCalculation();
        notifyPageChanged();
    }

    @JavascriptInterface
    public String javaGetKeyWordExplain(String dictName) {
        if (dictName != null && dictName.length() > 0) {
            return explainList.get(dictName);
        }
        return "";
    }

    public void refresh() {
        if (measuredHeight == 0 || measuredWidth == 0) {
            measuredHeight = getMeasuredHeight();
            measuredWidth = getMeasuredWidth();
            applyCSS(this);
        }
        calculate();
        updatePageNumber();
    }

    public interface PageChangedListener {
        void onPageChanged(int currentPage, int totalPage);
    }

    public void setUpdateDictionaryListCallback(UpdateDictionaryListCallback updateDictionaryListCallback) {
        this.updateDictionaryListCallback = updateDictionaryListCallback;
    }

    public interface UpdateDictionaryListCallback {
        void update(String dictName);
    }
}
