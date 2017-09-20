package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.DictTypeAdapter;
import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.dr.bean.GoodSentenceBean;
import com.onyx.android.dr.bean.NewWordBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.QueryRecordEntity;
import com.onyx.android.dr.event.DictFunctionVisibleEvent;
import com.onyx.android.dr.event.GoodSentenceNotebookEvent;
import com.onyx.android.dr.event.NewWordQueryEvent;
import com.onyx.android.dr.event.PlaySoundEvent;
import com.onyx.android.dr.event.RefreshWebviewEvent;
import com.onyx.android.dr.event.ReloadDictImageEvent;
import com.onyx.android.dr.event.UpdateSoundIconEvent;
import com.onyx.android.dr.event.WebViewLoadOverEvent;
import com.onyx.android.dr.event.WebviewPageChangedEvent;
import com.onyx.android.dr.interfaces.ActionSelectListener;
import com.onyx.android.dr.interfaces.DictResultShowView;
import com.onyx.android.dr.manager.OperatingDataManager;
import com.onyx.android.dr.presenter.DictFunctionPresenter;
import com.onyx.android.dr.util.DictPreference;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.dr.view.AutoPagedWebView;
import com.onyx.android.sdk.dict.conf.AppConfig;
import com.onyx.android.sdk.dict.data.DictionaryManager;
import com.onyx.android.sdk.dict.data.DictionaryQueryResult;
import com.onyx.android.sdk.dict.data.bean.DictionaryInfo;
import com.onyx.android.sdk.dict.request.QueryWordRequest;
import com.onyx.android.sdk.dict.request.common.DictBaseCallback;
import com.onyx.android.sdk.dict.request.common.DictBaseRequest;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-6-26.
 */
public class DictResultShowActivity extends BaseActivity implements DictResultShowView, View.OnClickListener {
    @Bind(R.id.activity_dict_result_type)
    PageRecyclerView dictTypeRecyclerView;
    @Bind(R.id.activity_dict_result_view)
    AutoPagedWebView resultView;
    @Bind(R.id.activity_dict_button_next)
    ImageView nextPageButton;
    @Bind(R.id.activity_dict_button_previous)
    ImageView prevPageButton;
    @Bind(R.id.activity_dict_page_size_indicator)
    TextView pageIndicator;
    @Bind(R.id.activity_dict_iv_voice_two)
    ImageView ivVoiceTwo;
    @Bind(R.id.activity_dict_iv_voice_one)
    ImageView ivVoiceOne;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.title_bar_right_icon_three)
    ImageView iconThree;
    @Bind(R.id.title_bar_right_icon_two)
    ImageView iconTwo;
    @Bind(R.id.title_bar_right_icon_one)
    ImageView iconOne;
    private DictFunctionPresenter dictPresenter;
    private DictionaryManager dictionaryManager;
    private QueryWordRequest queryWordRequest;
    private static final String TAG = DictResultShowActivity.class.getSimpleName();
    private DictTypeAdapter dictTypeAdapter;
    private DividerItemDecoration dividerItemDecoration;
    private int customFontSize = 10;
    private List<DictTypeBean> searchResultList = new ArrayList<DictTypeBean>();
    private List<String> wordSoundList = new ArrayList<>();
    private String editQuery = "";
    private static final int SOUND_ONE = 1;
    private static final int SOUND_TWO = 2;
    private static final int CARDINAL_NUMBER = 1000;
    private static final String LINE_FEED = "\n\nValue: ";
    public volatile Map<String, DictionaryQueryResult> queryResult;
    private List<String> itemList;
    private String copyText = "";
    private String dictionaryLookup = "";
    private int dictType;
    private List<String> pathList;
    private int number = 0;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_dict_result_show;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initDictTypeView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int fontSize = DRApplication.getInstance().getCustomFontSize();
        if (fontSize != customFontSize) {
            if (resultView != null) {
                customFontSize = fontSize;
                resultView.setTextZoom(fontSize);
            }
        }
    }

    private void initDictTypeView() {
        dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.HORIZONTAL);
        dictTypeRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        dictTypeAdapter = new DictTypeAdapter();
    }

    @Override
    protected void initData() {
        queryResult = new ConcurrentHashMap<>();
        customFontSize = DRApplication.getInstance().getCustomFontSize();
        dictPresenter = new DictFunctionPresenter(this);
        DictPreference.init(this);
        dictPresenter.loadData(this);
        getIntentData();
        initItemData();
        initSound();
        settingDictionaryFunction();
        initEvent();
    }

    private void initItemData() {
        itemList = Utils.loadItemData(this);
        resultView.setActionList(itemList);
    }

    @Override
    public void setDictTypeData(List<DictTypeBean> dictData) {
    }

    private void getIntentData() {
        editQuery = getIntent().getStringExtra(Constants.EDITQUERY);
        dictType = getIntent().getIntExtra(Constants.DICTTYPE, -1);
        initTitleData();
        loadDictionary();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.new_word_notebook);
        if (dictType == Constants.ENGLISH_TYPE) {
            title.setText(getString(R.string.dict_query_language));
        } else if (dictType == Constants.CHINESE_TYPE) {
            title.setText(getString(R.string.dict_query_chinese_language));
        } else if (dictType == Constants.OTHER_TYPE) {
            title.setText(getString(R.string.Japanese));
        }
        iconFour.setVisibility(View.VISIBLE);
        iconThree.setVisibility(View.VISIBLE);
        iconTwo.setVisibility(View.VISIBLE);
        iconOne.setVisibility(View.GONE);
        iconFour.setImageResource(R.drawable.ic_reader_dic_setting);
        iconThree.setImageResource(R.drawable.ic_reader_dic_record_85);
        iconTwo.setImageResource(R.drawable.ic_reader_dic_add_word);
    }

    private void loadDictionary() {
        pathList = new ArrayList<>();
        pathList = Utils.getDictPathListByMap(dictType);
        dictionaryManager = DRApplication.getInstance().getDictionaryManager();
        if (dictionaryManager != null) {
            dictionaryManager.newProviderMap.clear();
        }
        DictPreference.setIntValue(this, Constants.DICTTYPE, dictType);
    }

    private void insertQueryRecord(String dictionaryLookup, DictionaryQueryResult dictionaryQueryResult) {
        if (!StringUtils.isNullOrEmpty(editQuery)) {
            long timeMillis = System.currentTimeMillis();
            timeMillis = timeMillis / CARDINAL_NUMBER;
            QueryRecordEntity bean = new QueryRecordEntity();
            bean.time = timeMillis;
            bean.word = editQuery;
            bean.paraphrase = dictionaryQueryResult.explanation;
            bean.dictionaryLookup = dictionaryLookup;
            dictPresenter.insertQueryRecord(bean);
        }
    }

    private void initSound() {
        wordSoundList.clear();
        setIvVoiceOneStatus(false);
        setIvVoiceTwoStatus(false);
    }

    public void settingDictionaryFunction() {
        boolean value = AppConfig.sharedInstance(this).getDictionaryFunctionSettingValue(AppConfig.SOUND_ONE_KEY, true);
        ivVoiceOne.setVisibility(value ? View.VISIBLE : View.GONE);

        value = AppConfig.sharedInstance(this).getDictionaryFunctionSettingValue(AppConfig.SOUND_TWO_KEY, true);
        ivVoiceTwo.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    private void initEvent() {
        resultView.setPageChangedListener(new AutoPagedWebView.PageChangedListener() {
            @Override
            public void onPageChanged(int totalPage, int curPage) {
                DictResultShowActivity.this.onPageChanged(totalPage, curPage);
            }
        });
        resultView.setUpdateDictionaryListCallback(new AutoPagedWebView.UpdateDictionaryListCallback() {
            @Override
            public void update(String dictName) {
                saveDictionary(dictName);
            }
        });
        resultView.setTextZoom(customFontSize);
        setWebviewDefaultFontSize();

        prevPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultView.prevPage();
            }
        });
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultView.nextPage();
            }
        });

        dictTypeAdapter.setOnItemClick(new DictTypeAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dictionaryLookup = searchResultList.get(position).getTabName();
                showResultToWebview(position);
            }
        });

        resultView.setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String title, String selectText) {
                copyText = selectText;
                if (!StringUtils.isNullOrEmpty(copyText)) {
                    if (title.equals(getString(R.string.new_word_query))) {
                        EventBus.getDefault().post(new NewWordQueryEvent(copyText));
                    } else if (title.equals(getString(R.string.good_sentence_excerpt))) {
                        EventBus.getDefault().post(new GoodSentenceNotebookEvent(copyText));
                    }
                } else {
                    CommonNotices.showMessage(DictResultShowActivity.this, getString(R.string.webview_toast_copy_failed));
                }
            }
        });
    }

    public void testWordDictQuery() {
        if (!StringUtils.isNullOrEmpty(editQuery)) {
            queryWordRequest = new QueryWordRequest(editQuery);
            boolean bRet = dictionaryManager.sendRequest(DRApplication.getInstance(), queryWordRequest, pathList, new DictBaseCallback() {
                @Override
                public void done(DictBaseRequest request, Exception e) {
                    if (queryWordRequest.queryResult == null) {
                        return;
                    }
                    resultView.loadResultAsHtml(queryWordRequest.queryResult);
                    getSoundData(queryWordRequest.queryResult);
                    addSearchResult(queryWordRequest.queryResult);

                    dictTypeAdapter.setMenuDatas(searchResultList);
                    dictTypeAdapter.notifyDataSetChanged();
                    dictTypeRecyclerView.setAdapter(dictTypeAdapter);
                    if (searchResultList.size() > 0) {
                        dictionaryLookup = searchResultList.get(0).getTabName();
                        if (number == 0) {
                            DictionaryQueryResult dictionaryQueryResult = queryResult.get(dictionaryLookup);
                            insertQueryRecord(dictionaryLookup, dictionaryQueryResult);
                            number++;
                        }
                    }
                }
            });
            if (!bRet) {
                CommonNotices.showMessage(this, getString(R.string.headword_search_empty));
            }
        }
    }

    private void reset() {
        searchResultList.clear();
        queryResult.clear();
    }

    private void addSearchResult(Map<String, DictionaryQueryResult> result) {
        if (result.size() > 0) {
            for (Map.Entry<String, DictionaryQueryResult> entry : result.entrySet()) {
                if (entry.getValue().dictionary.dictVoiceInfo != null) {
                    continue;
                }
                if (queryResult.get(entry.getValue().dictionary.name) != null) {
                    continue;
                }
                queryResult.put(entry.getValue().dictionary.name, entry.getValue());
            }
        }
    }

    private void showResultToWebview(int position) {
        resultView.setScroll(0, 0);
        resultView.stopPlayer(0);
        //get dictionary result
        DictTypeBean dictTypeData = searchResultList.get(position);
        String dictName = dictTypeData.getTabName();
        DictionaryQueryResult dictionaryQueryResult = queryResult.get(dictName);
        if (dictionaryQueryResult == null) {
            return;
        }
        String dictPath = dictionaryQueryResult.dictionary.dictPath;
        resultView.loadUrl("javascript:" + "showDict(\"" + dictName + "\",\"" + dictPath + "\")");
    }

    @OnClick({R.id.activity_dict_iv_voice_one,
            R.id.image_view_back,
            R.id.title_bar_right_icon_four,
            R.id.title_bar_right_icon_three,
            R.id.title_bar_right_icon_two,
            R.id.activity_dict_iv_voice_two})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.activity_dict_iv_voice_one:
                onVoiceOneClick();
                break;
            case R.id.activity_dict_iv_voice_two:
                onVoiceTwoClick();
                break;
            case R.id.title_bar_right_icon_four:
                ActivityManager.startDictSettingActivity(this, Constants.DICT_OTHER);
                break;
            case R.id.title_bar_right_icon_three:
                ActivityManager.startQueryRecordActivity(this);
                break;
            case R.id.title_bar_right_icon_two:
                insertNewWord();
                break;
        }
    }

    private void saveDictionary(String dictName) {
        DictTypeBean dictTypeData = new DictTypeBean(dictName);
        if (!searchResultList.contains(dictTypeData)) {
            searchResultList.add(dictTypeData);
        }
    }

    public void setWebviewDefaultFontSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int density = metrics.densityDpi;
        resultView.setWebviewDefaultFontSize(density);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_DOWN:
                resultView.nextPage();
                break;
            case KeyEvent.KEYCODE_PAGE_UP:
                resultView.prevPage();
                break;
            case KeyEvent.KEYCODE_BACK:
                if (resultView != null) {
                    resultView.stopPlayer(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void onPageChanged(int totalPage, int curPage) {
        if (prevPageButton == null || nextPageButton == null || pageIndicator == null) {
            return;
        }
        if (totalPage > 1) {
            if (curPage > 1) {
                prevPageButton.setVisibility(View.VISIBLE);
            } else {
                prevPageButton.setVisibility(View.INVISIBLE);
            }

            if (curPage < totalPage) {
                nextPageButton.setVisibility(View.VISIBLE);
            } else {
                nextPageButton.setVisibility(View.INVISIBLE);
            }
        } else {
            prevPageButton.setVisibility(View.INVISIBLE);
            nextPageButton.setVisibility(View.INVISIBLE);
        }
        if (totalPage <= 0) {
            pageIndicator.setVisibility(View.GONE);
        } else {
            pageIndicator.setVisibility(View.VISIBLE);
        }
        pageIndicator.setText(curPage + "/" + totalPage);
    }

    private void setIvVoiceOneStatus(boolean enabled) {
        ivVoiceOne.setEnabled(enabled);
        ivVoiceOne.setImageResource(enabled ? R.drawable.dict_pronounce_one : R.drawable.dict_pronounce_one_gray);
    }

    private void setIvVoiceTwoStatus(boolean enabled) {
        ivVoiceTwo.setEnabled(enabled);
        ivVoiceTwo.setImageResource(enabled ? R.drawable.dict_pronounce_two : R.drawable.dict_pronounce_two_gray);
    }

    public int getSoundData(final Map<String, DictionaryQueryResult> result) {
        int count = 0;
        for (Map.Entry<String, DictionaryQueryResult> entry : result.entrySet()) {
            DictionaryInfo dictionaryInfo = entry.getValue().dictionary;
            if (dictionaryInfo.dictVoiceInfo != null) {
                addWordVoiceData(entry.getValue().soundPath);
                count++;
            }
        }
        return count;
    }

    private void addWordVoiceData(final String soundPath) {
        int index = wordSoundList.indexOf(soundPath);
        if (index < 0) {
            wordSoundList.add(soundPath);
            EventBus.getDefault().post(new UpdateSoundIconEvent());
        }
    }

    private void onVoiceOneClick() {
        if (resultView.mediaPlayer.isPlaying()) {
            return;
        }
        if (wordSoundList.size() >= SOUND_ONE) {
            resultView.jsPlaySound(wordSoundList.get(SOUND_ONE - 1));
        } else {
            resultView.jsPlaySound(resultView.getHeadwordSoundPath());
        }
    }

    private void onVoiceTwoClick() {
        if (resultView.mediaPlayer.isPlaying()) {
            return;
        }
        if (wordSoundList.size() >= SOUND_TWO) {
            resultView.jsPlaySound(wordSoundList.get(SOUND_ONE));
        }
    }

    public void insertNewWord() {
        DictionaryQueryResult dictionaryQueryResult = queryResult.get(dictionaryLookup);
        NewWordBean bean = new NewWordBean();
        bean.setNewWord(editQuery);
        bean.setDictionaryLookup(dictionaryLookup);
        bean.setReadingMatter("");
        bean.setNewWordType(dictType);
        bean.setParaphrase(dictionaryQueryResult.explanation);
        OperatingDataManager.getInstance().insertNewWord(bean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewWordQueryEvent(NewWordQueryEvent event) {
        ActivityManager.startNewWordQueryActivity(this, event.getContent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoodSentenceNotebookEvent(GoodSentenceNotebookEvent event) {
        GoodSentenceBean bean = new GoodSentenceBean();
        bean.setDetails(event.getContent());
        bean.setReadingMatter(dictionaryLookup);
        bean.setPageNumber("");
        bean.setGoodSentenceType(dictType);
        OperatingDataManager.getInstance().insertGoodSentence(bean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(WebviewPageChangedEvent event) {
        onPageChanged(event.arg1, event.arg2);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PlaySoundEvent event) {
        resultView.playSound(event, pathList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RefreshWebviewEvent event) {
        resultView.refreshWebview(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ReloadDictImageEvent event) {
        resultView.reloadDictImage(event);
    }

    @Subscribe
    public void onWebViewLoadOverEvent(WebViewLoadOverEvent event) {
        testWordDictQuery();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSoundIcon(UpdateSoundIconEvent event) {
        UpdateSoundIconState();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDictFunctionVisibleEvent(DictFunctionVisibleEvent event) {
        testWordDictQuery();
    }

    private void UpdateSoundIconState() {
        switch (wordSoundList.size()) {
            case SOUND_ONE:
                setIvVoiceOneStatus(true);
                break;
            case SOUND_TWO:
                setIvVoiceTwoStatus(true);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (resultView != null) {
            resultView.dismissAction();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
