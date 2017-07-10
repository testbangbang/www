package com.onyx.android.dr.view;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.ExplanationInfo;
import com.onyx.android.dr.bean.WordExplanation;
import com.onyx.android.dr.event.PlaySoundEvent;
import com.onyx.android.dr.event.RefreshWebviewEvent;
import com.onyx.android.dr.event.ReloadDictImageEvent;
import com.onyx.android.dr.event.UpdateVoiceStatusEvent;
import com.onyx.android.dr.event.WebViewLoadOverEvent;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.sdk.dict.data.DictionaryManager;
import com.onyx.android.sdk.dict.data.DictionaryProviderBase;
import com.onyx.android.sdk.dict.data.DictionaryQueryResult;
import com.onyx.android.sdk.dict.data.speex.SpeexConversionRequest;
import com.onyx.android.sdk.dict.request.common.DictBaseCallback;
import com.onyx.android.sdk.dict.request.common.DictBaseRequest;
import com.onyx.android.sdk.dict.utils.PatternUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhuzeng on 11/4/15.
 */
public class AutoPagedWebView extends WebView {
    public static final String WEBSIT_DIR = "/mnt/sdcard/dicts/.onyxdict/";
    public static final String HTML_FILE = "onyxdict.html";
    public static final String DICT_WEBSIT = "file://" + WEBSIT_DIR + HTML_FILE;
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
    public void enableA2ForSpecificView(View view){};
    public void disableA2ForSpecificView(View view){};
    private String headwordSoundPath = null;
    private List<Set<DictionaryQueryResult>> resultList = new ArrayList<Set<DictionaryQueryResult>>();
    private Context mContext = null;

    public void playSound(PlaySoundEvent event) {
        String soundPath = checkSoundFile((String) event.obj);
        if (soundPath != null) {
            playSound(soundPath);
        }
    }

    public void reloadDictImage(ReloadDictImageEvent event) {
        String dictPath = (String) event.obj;
        loadUrl("javascript:" + JS_RELOAD_DICT_IMAGE + "(\"" + dictPath + "\")");
        updatePageNumber();
    }

    public void refreshWebview(RefreshWebviewEvent event) {
        updatePageNumber();
    }

    @Override
    public void destroy() {
        enableA2ForSpecificView(this);
        super.destroy();
    }

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
        }
    }

    private void applyCSS(final WebView webView) {

        if (measuredHeight <= 0 || measuredWidth <= 0) {
            return;
        }
        String insertRule1 = "addCSSRule('html', 'padding: 0px; height: "
                + (measuredHeight / getContext().getResources().getDisplayMetrics().density)
                + "px; -webkit-column-gap: 0px; -webkit-column-width: "
                + measuredWidth + "px;  text-align:justify; ')";
        webView.loadUrl("javascript:" + insertRule1);
    }

    private WebViewClient getWebViewClient() {
        if (webViewClient == null) {
            webViewClient = new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
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
        if (!isInEditMode()) {
            if (Utils.localDictWebsiteFile(DRApplication.getInstance().getApplicationContext())) {
                DictionaryManager manager = new DictionaryManager();
                manager.loadLemmagenData();
            }
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
            loadUrl(DICT_WEBSIT);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentSoundPath = null;
                }
            });
        }
        disableA2ForSpecificView(this);
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

        totalPage = (scrollWidth + width - 1) / width;
        float marginLeft = getResources().getDimension(R.dimen.webview_margin_left);
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
            pageChangedListener.onPageChanged(totalPage, currentPage);
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

    // return false if no clear action.
    public boolean clearPreviousResult() {
        currentDictionary = -1;
        explainList.clear();
        resourceList.clear();
        if (resultList != null) {
            if (resultList.size() > 0) {
                resultList.clear();
                return true;
            }
        }
        headwordSoundPath = null;
        stopLoading();
        //clearHistory();
        loadUrl("javascript:jsCleanDict()");
        return false;
    }

    //TODO:need to change for much better visual effect,only test code now.
    public void loadResultAsHtml(final Map<String, DictionaryQueryResult> result) {
        if (result.size() > 0) {
            String resultString = "";
            boolean isShow = false;
            for (Map.Entry<String, DictionaryQueryResult> entry : result.entrySet()) {
                if(entry.getValue().dictionary.dictVoiceInfo != null){
                    continue;
                }
                if (resourceList.indexOf(entry.getKey()) >= 0) {
                    continue;
                }

                resultString = buildDetailedExplanation(entry.getValue().explanation);
                if (resultString == null || resultString.length() <= 0) {
                    continue;
                }
                resultString = resultString.replaceAll("<BR><BR>", "<br>");
                resultString = resultString.replaceAll("<br><br>", "<br>");
                resultString = resultString.replace("\n", "<br>");
                StringBuffer sbString = new StringBuffer(resultString);

                int firstBr = sbString.indexOf("<br>");
                if (firstBr < 0) {
                    firstBr = sbString.indexOf("<BR>");
                }
                if (firstBr >= 0) {
                    resultString = sbString.replace(firstBr, firstBr + 4, "<Br>").toString();
                }
                resultString = resultString.replaceAll("<br>", "</p><p>");
                resultString = resultString.replaceAll("<BR>", "</p><p>");

                if (explainList.get(entry.getValue().dictionary.name) != null) {
                    continue;
                }
                if (resourceList.size() <= 0) {
                    isShow = true;
                } else {
                    isShow = false;
                }
                resourceList.add(entry.getKey());
                explainList.put(entry.getValue().dictionary.name, resultString);
                loadUrl("javascript:" + JS_SETKEYWORDEXPLAIN + "(\"" + entry.getValue().dictionary.name + "\",\"" + entry.getValue().dictionary.dictPath + "\"," + isShow + ")");
                if (updateDictionaryListCallback != null) {
                    updateDictionaryListCallback.update(entry.getValue().dictionary.name);
                }
            }
        }
        triggerCalculation();
    }

    private String buildResultPage() {
        String resultString = "";
        boolean isSetKeyword = false;
        String keyword = "";
        String dictName = "";
        String soundmark = "";

        int count = 0;
        for (Set<DictionaryQueryResult> resultSet : resultList) {
            for (DictionaryQueryResult perResult : resultSet) {
                if (explainList.get(perResult.dictionary.name) != null) {
                    continue;
                }
                //resultString = resultString + perResult.dictionary.name + "<br>" + buildDetailedExplanation(perResult.explanation) + "<br>";
                resultString = buildDetailedExplanation(perResult.explanation);
                if (!isSetKeyword) {
                    keyword = perResult.originWord;
                    int index = 0;
                    //这里主要是想取音标
                    if (Utils.isChinese(keyword)) {
                        index = resultString.indexOf("\n");
                    } else {
                        index = resultString.indexOf("<br>");
                    }
                    if (index >= 0 && index < resultString.length()) {
                        soundmark = resultString.substring(0, index);
                    }
                    isSetKeyword = true;
                }
                if (resultString == null || resultString.length() <= 0) {
                    continue;
                }
                resultString = resultString.replace("\n", "<br>");
                //相同的词典不能添加多本
                if (explainList.get(perResult.dictionary.name) != null) {
                    continue;
                }
                explainList.put(perResult.dictionary.name, resultString);
                loadUrl("javascript:" + JS_SETKEYWORDEXPLAIN + "(\"" + perResult.dictionary.name + "\",\"" + perResult.dictionary.dictPath + "\")");
                count++;
                if (updateDictionaryListCallback != null) {
                    updateDictionaryListCallback.update(perResult.dictionary.name);
                }
            }
        }
        return resultString;
    }

    private String buildDetailedExplanation(String rawString) {
        WordExplanation explanation = new WordExplanation();
        String detailedExplanation = "";
        String phoneticSymbolString = "";
        String explanationString = "";
        Matcher m = PatternUtils.getPhoneticSymbolPattern().matcher(rawString);
        List<Integer> tempList = new ArrayList<Integer>();
        List<String> tempExplanationList = new ArrayList<String>();
        if (m.find()) {
            explanation.setPhoneticSymbol(m.group(1));
            explanationString = rawString.replace(m.group(1), "");
        } else {
            return rawString;
        }
        Matcher m0 = Pattern.compile("\\n").matcher(explanationString);
        while (m0.find()) {
            tempList.add(m0.start());
            tempList.add(m0.end());
        }
        if (tempList.size() > 0) {
            tempList.add(0, 0);
            tempList.add(explanationString.length() - 1);
            for (int i = 0; i < tempList.size(); i += 2) {
                String targetString = explanationString.substring(tempList.get(i), tempList.get(i + 1)).trim();
                if (targetString.length() > 1) {
                    tempExplanationList.add(targetString);
                }
            }
        }

        for (String s : tempExplanationList) {
            ExplanationInfo info = new ExplanationInfo();
            Matcher m1 = PatternUtils.getPartOfSpeechPattern().matcher(explanationString);
            if (m1.find()) {
                Log.e(TAG, "m1.group(0) " + m1.group(0));
                info.setPartOfSpeechPattern(m1.group(0));
                info.setExplanation(s.replace(m1.group(0), ""));
            } else {
                info.setExplanation(s);
            }
            explanation.getExplanationList().add(info);
        }

        //替换0x2e0x20
        explanationString = explanationString.replace(". ", ". <br><br>");
        explanationString = explanationString.replace("\n", "<br>");
        detailedExplanation = explanation.getPhoneticSymbol() + "<br>" + explanationString;
        return detailedExplanation;
    }


    public void setTextZoom(int textZoom) {
        getSettings().setTextZoom(textZoom);
        triggerCalculation();
        notifyPageChanged();
    }

    @JavascriptInterface
    public void loadOver(){
        EventBus.getDefault().post(new WebViewLoadOverEvent());
    }

    @JavascriptInterface
    public void refreshWebview() {
        EventBus.getDefault().post(new RefreshWebviewEvent());
    }

    @JavascriptInterface
    public String javaGetKeyWordExplain(String dictName) {
        if (dictName != null && dictName.length() > 0) {
            return explainList.get(dictName);
        }
        return "";
    }

    public String getHeadwordSoundPath() {
        return headwordSoundPath;
    }

    @JavascriptInterface
    public void jsSetHeadwordSoundPath(String headwordSoundPath) {
        if (this.headwordSoundPath == null) {
            //save first sound
            this.headwordSoundPath = headwordSoundPath;
            UpdateVoiceStatusEvent updateVoiceStatusEvent = new UpdateVoiceStatusEvent();
            EventBus.getDefault().post(updateVoiceStatusEvent);
        }
    }

    @JavascriptInterface
    public void jsUpdateHeadwordSoundPath(String headwordSoundPath) {
        //save first sound
        this.headwordSoundPath = headwordSoundPath;
        UpdateVoiceStatusEvent updateVoiceStatusEvent = new UpdateVoiceStatusEvent();
        EventBus.getDefault().post(updateVoiceStatusEvent);
    }

    @JavascriptInterface
    public void jsPlaySound(String soundPath) {
        PlaySoundEvent playSoundEvent = new PlaySoundEvent();
        playSoundEvent.setObj(soundPath);
        EventBus.getDefault().post(playSoundEvent);
    }

    @JavascriptInterface
    public void jsLoadResourceList(String dictPath, String resourcePath) {
        if (dictPath == null || dictPath.length() <= 0 || resourcePath == null || resourcePath.length() <= 0) {
            return;
        }
        resourcePath = resourcePath.replaceAll("\u001f", "");
        resourcePath = resourcePath.replaceAll("\u001e", "");
        String[] paths = resourcePath.split("`");
        List<String> mdictResourcelist = new ArrayList<String>();
        String resourceName = null;
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                continue;
            }
            resourceName = path.substring(path.lastIndexOf("/") + 1, path.length());
            resourceName = "\\" + resourceName;
            if (mdictResourcelist.indexOf(resourceName) >= 0) {
                continue;
            }
            mdictResourcelist.add(resourceName);
        }
        if (mdictResourcelist.size() > 0) {
            String dictDir = dictPath.substring(0, dictPath.lastIndexOf("/"));
            DictionaryManager manager = new DictionaryManager();
            DictionaryProviderBase dictionaryProviderBase = manager.getDictionaryProviderBase(dictDir);
            if (dictionaryProviderBase != null) {
                dictionaryProviderBase.loadDictResource(mdictResourcelist);
            }
        }
        ReloadDictImageEvent reloadDictImageEvent = new ReloadDictImageEvent();
        reloadDictImageEvent.setObj(dictPath);
        EventBus.getDefault().post(reloadDictImageEvent);
    }

    /**
     * 判断是否是spx文件如果是就转换成wav
     *
     * @param soundPath
     * @return
     */
    public String checkSoundFile(String soundPath) {
        if (soundPath == null || soundPath.length() <= 0) {
            return null;
        }
        int i = soundPath.indexOf(SpeexConversionRequest.SPEEX_SUFFIX);
        if (i < 0) {
            //".mp3" ".wav" ...
            return soundPath;
        }
        File file = new File(soundPath);
        if (file.exists()) {
            String wavSoundPath = soundPath.replace(SpeexConversionRequest.SPEEX_SUFFIX, SpeexConversionRequest.WAV_SUFFIX);
            file = new File(wavSoundPath);
            if (file.exists()) {
                return wavSoundPath;
            }
        } else {
            return null;
        }
        speexConversionSound(soundPath);
        return null;
    }

    private void speexConversionSound(String soundPath) {
        final SpeexConversionRequest speexConversionRequest = new SpeexConversionRequest(soundPath);
        DictionaryManager manager = new DictionaryManager();
        manager.sendRequest(getContext(), speexConversionRequest, new DictBaseCallback() {
            @Override
            public void done(DictBaseRequest request, Exception e) {
                playSound(speexConversionRequest.getDestPath());
            }
        });
    }

    public void stopPlayer(int type) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void playSound(String soundPath) {
        if (soundPath == null || soundPath.length() <= 0) {
            return;
        }
        try {
            if (currentSoundPath != null && soundPath.equals(currentSoundPath)) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    currentSoundPath = null;
                    return;
                }
            } else {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                currentSoundPath = soundPath;
            }

            mediaPlayer.reset();
            mediaPlayer.setDataSource(soundPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }

    public void setWebviewDefaultFontSize(int density) {
        float defaultFontSize = Float.parseFloat(getResources().getString(R.string.webview_default_font_size));
        int textZoom = getSettings().getTextZoom();
        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                textZoom *= defaultFontSize;
                break;
            case DisplayMetrics.DENSITY_MEDIUM://160
                textZoom *= defaultFontSize;
                break;
            case DisplayMetrics.DENSITY_HIGH://280
                textZoom *= defaultFontSize;
                break;
            case DisplayMetrics.DENSITY_XHIGH://320
                textZoom *= defaultFontSize;
                break;
            case DisplayMetrics.DENSITY_TV:
                textZoom *= defaultFontSize;
                break;
            default:
                textZoom *= defaultFontSize;
                break;
        }
        getSettings().setTextZoom(textZoom);
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
