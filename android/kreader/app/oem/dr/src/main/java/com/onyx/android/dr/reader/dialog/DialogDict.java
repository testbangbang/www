package com.onyx.android.dr.reader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.action.DictionaryQueryAction;
import com.onyx.android.dr.reader.action.ShowTextSelectionMenuAction;
import com.onyx.android.dr.reader.data.DictionaryQuery;
import com.onyx.android.dr.reader.event.DialogDictBaikeClickEvent;
import com.onyx.android.dr.reader.event.DialogDictDictClickEvent;
import com.onyx.android.dr.reader.event.DialogDictDictListClickEvent;
import com.onyx.android.dr.reader.event.DialogDictDismissClickEvent;
import com.onyx.android.dr.reader.event.DropDownListViewDismissEvent;
import com.onyx.android.dr.reader.event.DropDownListViewItemClickEvent;
import com.onyx.android.dr.reader.event.LoadDictEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.dr.reader.utils.ReaderUtil;
import com.onyx.android.dr.reader.view.DropDownListView;
import com.onyx.android.dr.reader.view.HTMLReaderWebView;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogDict extends Dialog implements View.OnClickListener {
    private static final String TAG = DialogDict.class.getSimpleName();
    public static final String BAIDU_BAIKE = "https://wapbaike.baidu.com/item/";
    public static final String WIKTIONARY_URL = "https://en.wiktionary.org/wiki/";
    private static final int MAX_DICTIONARY_LOAD_COUNT = 6;
    private static final int DELAY_DICTIONARY_LOAD_TIME = 2000;
    private static Map<Integer, Object> buttonEventList = new HashMap<>();
    private String headWord;
    private HTMLReaderWebView webView;
    private WebView dictBaikeContent;
    private TextView pageIndicator;
    private String uriString;
    private List<DictionaryQuery> textDictionaryQueries = new ArrayList<>();
    private List<DictionaryQuery> soundDictionaryQueries = new ArrayList<>();
    private ReaderPresenter readerPresenter;
    private int dictionaryLoadCount;
    private LinearLayout dictListLinearLayout;
    private TextView textViewDictName;
    private ImageView dictNameState;
    private DropDownListView dropDownListView;
    private TextView dict;
    private TextView baike;
    private View dismissView;
    private int currentPosition = 0;

    public DialogDict(Context context, ReaderPresenter readerPresenter) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        this.readerPresenter = readerPresenter;
        setCanceledOnTouchOutside(true);
        initView();
    }

    public DialogDict(Context context, ReaderPresenter readerPresenter, String headWord) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        this.readerPresenter = readerPresenter;
        this.headWord = headWord;
        initThirdLibrary();
        initData();
        initView();
    }

    public void initThirdLibrary() {
        EventBus.getDefault().register(this);
    }

    public void initData() {
        dictionaryQuery(headWord);
    }

    private void webSearchWord() {
        if (!ReaderUtil.requestWifi(readerPresenter.getReaderView().getViewContext())) {
            return;
        }

        uriString = ReaderUtil.isChinese(getContext()) ? BAIDU_BAIKE : WIKTIONARY_URL;
        uriString += headWord;
        dictBaikeContent.loadUrl(uriString);
    }


    private void initView() {
        setContentView(R.layout.dialog_dict);
        settingWebView();

        dict = (TextView) findViewById(R.id.dialog_dict_dict);
        dict.setOnClickListener(this);
        baike = (TextView) findViewById(R.id.dialog_dict_baike);
        baike.setOnClickListener(this);
        pageIndicator = (TextView) findViewById(R.id.dialog_dict_page_indicator);
        dictListLinearLayout = (LinearLayout) findViewById(R.id.dialog_dict_dict_list);
        dictListLinearLayout.setOnClickListener(this);
        textViewDictName = (TextView) findViewById(R.id.dialog_dict_dict_name);
        dictNameState = (ImageView) findViewById(R.id.dialog_dict_name_state);

        dismissView = findViewById(R.id.dialog_dict_dismiss);
        dismissView.setOnClickListener(this);

        dropDownListView = new DropDownListView(readerPresenter.getReaderView().getViewContext());
        bindButtonEvent();
        updateDictAndBaikeButtonState(R.id.dialog_dict_dict);
    }

    private void settingWebView() {
        webView = (HTMLReaderWebView) findViewById(R.id.dict_content);
        webView.getSettings().setTextSize(WebSettings.TextSize.LARGER);
        webView.setPageTurnType(HTMLReaderWebView.PAGE_TURN_TYPE_VERTICAL);
        webView.setPageTurnThreshold(15);
        webView.registerOnOnPageChangedListener(new HTMLReaderWebView.OnPageChangedListener() {

            @Override
            public void onPageChanged(int totalPage, int curPage) {
                if (totalPage > 1) {
                    pageIndicator.setVisibility(View.VISIBLE);
                }
                pageIndicator.setText(curPage + "/" + totalPage);
            }
        });

        dictBaikeContent = (WebView)findViewById(R.id.dict_baike_content);
        dictBaikeContent.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void dictionaryQuery(final String token) {
        final DictionaryQueryAction dictionaryQueryAction = new DictionaryQueryAction(token);
        dictionaryQueryAction.execute(readerPresenter, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                textDictionaryQueries = dictionaryQueryAction.getTextDictionaryQueries();
                soundDictionaryQueries = dictionaryQueryAction.getSoundDictionaryQueries();
                handleDictionaryQuery(readerPresenter, textDictionaryQueries, soundDictionaryQueries, token, dictionaryQueryAction.getErrorInfo());
            }
        });
    }

    private void handleDictionaryQuery(final ReaderPresenter readerPresenter, List<DictionaryQuery> textDictionaryQueries, List<DictionaryQuery> soundDictionaryQueries, final String token, final String error) {
        if (textDictionaryQueries.size() > 0 || soundDictionaryQueries.size() > 0) {
            DictionaryQuery query = textDictionaryQueries.size() > 0 ? textDictionaryQueries.get(0) : soundDictionaryQueries.get(0);
            int state = query.getState();
            if (state == DictionaryQuery.DICT_STATE_LOADING) {
                if (dictionaryLoadCount < MAX_DICTIONARY_LOAD_COUNT) {
                    dictionaryLoadCount++;
                    new LoadDictEvent().sendMessageDelayed(DELAY_DICTIONARY_LOAD_TIME);
                } else {
                    String content = readerPresenter.getReaderView().getViewContext().getString(R.string.load_fail);
                    query.setExplanation(content);
                }
            }
            if (state == DictionaryQuery.DICT_STATE_QUERY_SUCCESSFUL && textDictionaryQueries.size() > 0) {
                query = textDictionaryQueries.get(currentPosition);
            }
            if (state == DictionaryQuery.DICT_STATE_QUERY_SUCCESSFUL && soundDictionaryQueries.size() > 0) {

            }
            showExplanation(query);
        } else {
            webView.setLoadCssStyle(false);
            webView.loadDataWithBaseURL(null, error, "text/html", "utf-8", "about:blank");
        }
    }

    private void showExplanation(DictionaryQuery dictionaryQuery) {
        if (dictionaryQuery == null) {
            return;
        }
        String dict = dictionaryQuery.getDictPath();
        String url = "file:///";
        if (!StringUtils.isNullOrEmpty(dict)) {
            url += dict.substring(0, dict.lastIndexOf("/"));
        }
        String name = dictionaryQuery.getDictName();
        if (!StringUtils.isNullOrEmpty(name)) {
            textViewDictName.setText(name);
        }

        webView.setLoadCssStyle(dictionaryQuery.getState() == DictionaryQuery.DICT_STATE_QUERY_SUCCESSFUL);
        webView.loadDataWithBaseURL(url, dictionaryQuery.getExplanation(), "text/html", "utf-8", "about:blank");
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        Object event = buttonEventList.get(viewID);
        if (event != null) {
            EventBus.getDefault().post(event);
        }
    }

    public void updateDictAndBaikeButtonState(int id) {
        dict.setSelected(id == R.id.dialog_dict_dict ? true : false);
        baike.setSelected(id == R.id.dialog_dict_baike ? true : false);

        dictNameState.setVisibility(id == R.id.dialog_dict_dict ? View.VISIBLE : View.GONE);
        textViewDictName.setVisibility(id == R.id.dialog_dict_dict ? View.VISIBLE : View.GONE);
        dictListLinearLayout.setVisibility(id == R.id.dialog_dict_dict ? View.VISIBLE : View.GONE);
        if (id == R.id.dialog_dict_baike) {
            webView.setVisibility(View.GONE);
            dictBaikeContent.setVisibility(View.VISIBLE);
            pageIndicator.setVisibility(View.GONE);
            webSearchWord();
        } else {
            webView.setVisibility(View.VISIBLE);
            dictBaikeContent.setVisibility(View.GONE);
            pageIndicator.setVisibility(View.VISIBLE);
            DictionaryQuery dictionaryQuery = getDictionaryQuery(currentPosition);
            showExplanation(dictionaryQuery);
        }
    }

    private DictionaryQuery getDictionaryQuery(int position) {
        if (position >= 0 && position < textDictionaryQueries.size()) {
            return textDictionaryQueries.get(position);
        }
        return null;
    }

    @Subscribe
    public void onDialogDictDictListClickEvent(DialogDictDictListClickEvent event) {
        if (dropDownListView.isShowing()) {
            return;
        }
        setDictListState(false);
        dropDownListView.show(dictListLinearLayout, textDictionaryQueries);
    }

    @Subscribe
    public void onDropDownListViewDismissEvent(DropDownListViewDismissEvent event) {
        setDictListState(true);
    }

    @Subscribe
    public void onDropDownListViewItemClickEvent(DropDownListViewItemClickEvent event) {
        int position = event.getPosition();
        if (position != currentPosition) {
            DictionaryQuery dictionaryQuery = getDictionaryQuery(currentPosition);
            showExplanation(dictionaryQuery);
        }
        dropDownListView.hide();
    }

    @Subscribe
    public void onLoadDictEvent(LoadDictEvent event) {
        dictionaryQuery(headWord);
    }

    @Subscribe
    public void onDialogDictDictClickEvent(DialogDictDictClickEvent event) {
        updateDictAndBaikeButtonState(R.id.dialog_dict_dict);
    }

    @Subscribe
    public void onDialogDictBaikeClickEvent(DialogDictBaikeClickEvent event) {
        updateDictAndBaikeButtonState(R.id.dialog_dict_baike);
    }

    @Subscribe
    public void onDialogDictDismissClickEvent(DialogDictDismissClickEvent event) {
        dismiss();
    }

    private void setDictListState(boolean flag) {
        dictNameState.setImageResource(flag ? R.drawable.ic_dict_pack_up : R.drawable.ic_dict_unfold);
    }

    public void bindButtonEvent() {
        buttonEventList.put(R.id.dialog_dict_dict_list, new DialogDictDictListClickEvent());
        buttonEventList.put(R.id.dialog_dict_dict, new DialogDictDictClickEvent());
        buttonEventList.put(R.id.dialog_dict_baike, new DialogDictBaikeClickEvent());
        buttonEventList.put(R.id.dialog_dict_dismiss, new DialogDictDismissClickEvent());
    }

    @Override
    public void dismiss() {
        EventBus.getDefault().unregister(this);
        ShowTextSelectionMenuAction.hideTextSelectionPopupMenu(readerPresenter);
        super.dismiss();
    }
}
