package com.onyx.android.dr.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.LanguageQueryTypeAdapter;
import com.onyx.android.dr.adapter.QueryDictTypeAdapter;
import com.onyx.android.dr.adapter.SearchWordAutoCompleteAdapter;
import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.DictSettingEntity;
import com.onyx.android.dr.dialog.AlertInfoDialog;
import com.onyx.android.dr.event.ChineseQueryEvent;
import com.onyx.android.dr.event.ClearHistoryEvent;
import com.onyx.android.dr.event.DictSettingSuccessEvent;
import com.onyx.android.dr.event.EnglishQueryEvent;
import com.onyx.android.dr.event.FrenchQueryEvent;
import com.onyx.android.dr.event.JapaneseQueryEvent;
import com.onyx.android.dr.event.SelectHeadwordEvent;
import com.onyx.android.dr.event.ShowQueryListResultEvent;
import com.onyx.android.dr.interfaces.DictResultShowView;
import com.onyx.android.dr.presenter.DictFunctionPresenter;
import com.onyx.android.dr.reader.view.CustomDialog;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.dr.view.SearchDropDownListView;
import com.onyx.android.sdk.dict.data.DictionaryManager;
import com.onyx.android.sdk.dict.data.DictionaryQueryResult;
import com.onyx.android.sdk.dict.request.QueryWordListRequest;
import com.onyx.android.sdk.dict.request.common.DictBaseCallback;
import com.onyx.android.sdk.dict.request.common.DictBaseRequest;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by zhouzhiming on 17-6-26.
 */
public class DictQueryActivity extends BaseActivity implements DictResultShowView, View.OnClickListener {
    @Bind(R.id.dict_query_activity_view)
    PageRecyclerView tabMenu;
    @Bind(R.id.activity_query_word)
    EditText wordQuery;
    @Bind(R.id.activity_query_phrase)
    EditText phraseQuery;
    @Bind(R.id.activity_query_example)
    EditText exampleQuery;
    @Bind(R.id.activity_query_spell_query)
    EditText spellQuery;
    @Bind(R.id.activity_query_japanese_query)
    EditText japaneseQuery;
    @Bind(R.id.activity_query_french_query)
    EditText frenchQuery;
    @Bind(R.id.activity_word_query_search)
    ImageView wordQuerySearch;
    @Bind(R.id.activity_phrase_query_search)
    ImageView phraseQuerySearch;
    @Bind(R.id.activity_example_query_search)
    ImageView exampleQuerySearch;
    @Bind(R.id.activity_word_spell_search)
    ImageView spellSearch;
    @Bind(R.id.activity_word_japanese_search)
    ImageView japaneseSearch;
    @Bind(R.id.activity_word_french_search)
    ImageView frenchSearch;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.activity_query_chinese_linearlayout)
    LinearLayout chineseLinearLayout;
    @Bind(R.id.activity_query_english_linearlayout)
    LinearLayout englishLinearLayout;
    @Bind(R.id.activity_query_japanese_container)
    LinearLayout japaneseLinearLayout;
    @Bind(R.id.activity_query_french_container)
    LinearLayout frenchLinearLayout;
    @Bind(R.id.activity_query_dict_view)
    PageRecyclerView dictTypeRecyclerView;
    private LanguageQueryTypeAdapter languageQueryTypeAdapter;
    private DictFunctionPresenter dictPresenter;
    private QueryDictTypeAdapter queryDictTypeAdapter;
    private List<DictTypeBean> englishDictName;
    private List<DictTypeBean> chineseDictName;
    private List<DictTypeBean> japaneseDictName;
    private List<DictTypeBean> frenchDictName;
    private int dictType;
    private AlertInfoDialog alertDialog;
    private static CustomDialog dialog;
    private boolean isActivityShow = false;
    private boolean tag = false;
    private int limit;
    private int number = 0;
    private SearchDropDownListView searchDropDownListView;
    private List<String> headwordHistory = null;
    private EditText exchangeView = wordQuery;
    private List<DictTypeBean> dictLanguageData;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_dict_query;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initDictTypeView();
    }

    private void initDictTypeView() {
        tabMenu.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        languageQueryTypeAdapter = new LanguageQueryTypeAdapter();
        tabMenu.setAdapter(languageQueryTypeAdapter);
        dictTypeRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
    }

    @Override
    protected void initData() {
        queryDictTypeAdapter = new QueryDictTypeAdapter();
        dictLanguageData = new ArrayList<>();
        dictPresenter = new DictFunctionPresenter(this);
        dictTypeRecyclerView.setAdapter(queryDictTypeAdapter);
        limit = getResources().getInteger(R.integer.query_word_list_limit);
        searchDropDownListView = new SearchDropDownListView(getBaseContext());
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.dictionary);
        title.setText(getString(R.string.dictionary));
        iconFour.setVisibility(View.VISIBLE);
        iconFour.setImageResource(R.drawable.ic_reader_dic_setting);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DRApplication.getInstance().initDictDatas();
        dictPresenter.getDictSettingData();
    }

    @Override
    public void setDictTypeData(List<DictTypeBean> dictData) {
        if (dictData == null || dictData.size() <= 0) {
            return;
        }
        intContainerVisible(tag);
        languageQueryTypeAdapter.setMenuDatas(dictData);
        languageQueryTypeAdapter.notifyDataSetChanged();
    }

    @Override
    public void setDictSettingData(List<DictSettingEntity> dictData) {
        dictLanguageData.clear();
        if (dictData == null || dictData.size() <= 0) {
            dictType = Constants.ENGLISH_TYPE;
        } else {
            dictLanguageData = dictPresenter.getSaveDictSettingData(dictData);
            if (number == 0) {
                dictType = dictLanguageData.get(0).getType();
            }
        }
        if (dictLanguageData == null || dictLanguageData.size() <= 0) {
            dictPresenter.loadData(this);
            dictPresenter.loadDictType(Constants.ACCOUNT_TYPE_DICT_LANGUAGE);
        } else {
            intContainerVisible(tag);
            languageQueryTypeAdapter.setMenuDatas(dictLanguageData);
            languageQueryTypeAdapter.notifyDataSetChanged();
        }
    }

    public void intContainerVisible(boolean tag) {
        int type;
        if(tag){
            type = dictLanguageData.get(0).getType();
        }else {
            type = dictType;
        }
        getDictData(type);
        languageQueryTypeAdapter.setSelectedPosition(type);
        englishDictName = Utils.getEnglishDictData();
        chineseDictName = Utils.getChineseDictData();
        japaneseDictName = Utils.getJapaneseDictData();
        frenchDictName = Utils.getFrenchDictData();
        if (type == Constants.ENGLISH_TAG) {
            englishLinearLayout.setVisibility(View.VISIBLE);
            chineseLinearLayout.setVisibility(View.GONE);
            japaneseLinearLayout.setVisibility(View.GONE);
            frenchLinearLayout.setVisibility(View.GONE);
            queryDictTypeAdapter.setMenuDatas(englishDictName);
            queryDictTypeAdapter.notifyDataSetChanged();
            wordQuery.requestFocus();
        } else if (type == Constants.CHINESE_TAG) {
            chineseLinearLayout.setVisibility(View.VISIBLE);
            englishLinearLayout.setVisibility(View.GONE);
            japaneseLinearLayout.setVisibility(View.GONE);
            frenchLinearLayout.setVisibility(View.GONE);
            queryDictTypeAdapter.setMenuDatas(chineseDictName);
            queryDictTypeAdapter.notifyDataSetChanged();
            spellQuery.requestFocus();
        } else if (type == Constants.JAPANESE_TAG) {
            japaneseLinearLayout.setVisibility(View.VISIBLE);
            englishLinearLayout.setVisibility(View.GONE);
            chineseLinearLayout.setVisibility(View.GONE);
            frenchLinearLayout.setVisibility(View.GONE);
            queryDictTypeAdapter.setMenuDatas(japaneseDictName);
            queryDictTypeAdapter.notifyDataSetChanged();
            japaneseQuery.requestFocus();
        } else if (type == Constants.FRENCH_TAG) {
            japaneseLinearLayout.setVisibility(View.GONE);
            englishLinearLayout.setVisibility(View.GONE);
            chineseLinearLayout.setVisibility(View.GONE);
            frenchLinearLayout.setVisibility(View.VISIBLE);
            queryDictTypeAdapter.setMenuDatas(frenchDictName);
            queryDictTypeAdapter.notifyDataSetChanged();
            frenchQuery.requestFocus();
        }
    }

    private void getDictData(int type) {
        List<String> pathList = Utils.getPathList(type);
        dictPresenter.getDictMapData();
        if (pathList == null || pathList.size() <= 0) {
            showDialog(this, getString(R.string.no_dict_data_hint));
        }
    }

    public static void showDialog(final Context context, String content) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(context);
        View inflate = View.inflate(context, R.layout.dialog_dict_download_hint, null);
        TextView text = (TextView) inflate.findViewById(R.id.dict_download_dialog_content);
        dialog = builder.setContentView(inflate).setTitle(R.string.dict_download_hint)
                .setPositiveButton(context.getString(R.string.download), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityManager.startEBookStoreActivity(context);
                    }
                }).setNegativeButton(context.getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        text.setText(content);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        Utils.setDictDialogAttributes(dialog);
        dialog.show();
    }

    public void initEvent() {
        startSoftKeyboardSearch(wordQuery);
        startSoftKeyboardSearch(phraseQuery);
        startSoftKeyboardSearch(exampleQuery);
        startSoftKeyboardSearch(spellQuery);
        startSoftKeyboardSearch(japaneseQuery);
        wordQuery.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                phraseQuerySearch.setVisibility(View.GONE);
                exampleQuerySearch.setVisibility(View.GONE);
                wordQuerySearch.setVisibility(View.VISIBLE);
                phraseQuery.setBackgroundColor(getResources().getColor(R.color.light_gray));
                exampleQuery.setBackgroundColor(getResources().getColor(R.color.light_gray));
                wordQuery.setBackgroundResource(R.drawable.rectangle_stroke);
            }
        });
        phraseQuery.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                wordQuerySearch.setVisibility(View.GONE);
                exampleQuerySearch.setVisibility(View.GONE);
                phraseQuerySearch.setVisibility(View.VISIBLE);
                wordQuery.setBackgroundColor(getResources().getColor(R.color.light_gray));
                exampleQuery.setBackgroundColor(getResources().getColor(R.color.light_gray));
                phraseQuery.setBackgroundResource(R.drawable.rectangle_stroke);
            }
        });
        exampleQuery.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                wordQuerySearch.setVisibility(View.GONE);
                phraseQuerySearch.setVisibility(View.GONE);
                exampleQuerySearch.setVisibility(View.VISIBLE);
                wordQuery.setBackgroundColor(getResources().getColor(R.color.light_gray));
                exampleQuery.setBackgroundResource(R.drawable.rectangle_stroke);
                phraseQuery.setBackgroundColor(getResources().getColor(R.color.light_gray));
            }
        });
        textChangedListener(wordQuery);
        textChangedListener(phraseQuery);
        textChangedListener(exampleQuery);
        textChangedListener(spellQuery);
        textChangedListener(japaneseQuery);
    }

    private void textChangedListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            //input 0 ,show history list
            @Override
            public void afterTextChanged(Editable s) {
                if (!isActivityShow) {
                    return;
                }
                exchangeView = editText;
                getHeadwordList(s.toString());
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            isActivityShow = true;
        }
    }

    private void getHeadwordList(String headword) {
        List<String> pathList = Utils.getDictPathListByMap(dictType);
        QueryWordListRequest queryWordListRequest = new QueryWordListRequest(headword, limit, false);
        DictionaryManager dictionaryManager = DRApplication.getInstance().getDictionaryManager();
        if (dictionaryManager == null) {
            return;
        }
        dictionaryManager.newProviderMap.clear();
        try {
            dictionaryManager.sendRequest(DictQueryActivity.this, queryWordListRequest, pathList, new DictBaseCallback() {
                @Override
                public void done(DictBaseRequest request, Exception e) {
                    QueryWordListRequest wordListRequest = (QueryWordListRequest) request;
                    if (wordListRequest.queryResult.size() > 0) {
                        ArrayList<String> resultList = new ArrayList<>();
                        List<DictionaryQueryResult> list = new ArrayList<>(wordListRequest.queryResult.values());
                        Comparator comparator = new SearchWordAutoCompleteAdapter.ComparatorListSort();
                        Collections.sort(list, comparator);
                        for (DictionaryQueryResult result : list) {
                            resultList.add(result.candidate);
                        }
                        EventBus.getDefault().post(new ShowQueryListResultEvent(resultList));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSoftKeyboardSearch(final EditText editText) {
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    Utils.hideSoftWindow(DictQueryActivity.this);
                    startDictResultShowActivity(editText);
                }
                return false;
            }
        });
    }

    @OnClick({R.id.activity_word_query_search,
            R.id.activity_phrase_query_search,
            R.id.menu_back,
            R.id.activity_word_spell_search,
            R.id.activity_word_japanese_search,
            R.id.activity_word_french_search,
            R.id.title_bar_right_icon_four,
            R.id.activity_example_query_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.activity_word_query_search:
                startDictResultShowActivity(wordQuery);
                break;
            case R.id.activity_phrase_query_search:
                startDictResultShowActivity(phraseQuery);
                break;
            case R.id.activity_example_query_search:
                startDictResultShowActivity(exampleQuery);
                break;
            case R.id.activity_word_japanese_search:
                startDictResultShowActivity(japaneseQuery);
                break;
            case R.id.activity_word_french_search:
                startDictResultShowActivity(frenchQuery);
                break;
            case R.id.activity_word_spell_search:
                startDictResultShowActivity(spellQuery);
                break;
            case R.id.title_bar_right_icon_four:
                ActivityManager.startDictSettingActivity(this, Constants.DICT_QUERY);
                break;
        }
    }

    public void startDictResultShowActivity(EditText editText) {
        tag = false;
        number++;
        String editQueryString = editText.getText().toString();
        editQueryString = Utils.trim(editQueryString);
        if (!StringUtils.isNullOrEmpty(editQueryString)) {
            ActivityManager.startDictResultShowActivity(this, editQueryString, dictType);
        } else {
            CommonNotices.showMessage(this, getString(R.string.illegalInput));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (searchDropDownListView.isShowing()) {
                    searchDropDownListView.setFocusable();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                searchDropDownListView.hide();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void onClearHistory() {
        searchDropDownListView.hide();
        headwordHistory.clear();
    }

    public void selectHeadword(SelectHeadwordEvent event) {
        String headword = (String) event.obj;
        if (headword != null && headword.length() > 0) {
            exchangeView.setText(headword);
            Utils.movingCursor(exchangeView);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ShowQueryListResultEvent event) {
        searchDropDownListView.show(exchangeView, SearchDropDownListView.LIST_TYPE_SEARCH_RESULT, event.getResult());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ClearHistoryEvent event) {
        onClearHistory();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SelectHeadwordEvent event) {
        selectHeadword(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDictSettingSuccessEvent(DictSettingSuccessEvent event) {
        tag = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEnglishQueryEvent(EnglishQueryEvent event) {
        englishLinearLayout.setVisibility(View.VISIBLE);
        chineseLinearLayout.setVisibility(View.GONE);
        japaneseLinearLayout.setVisibility(View.GONE);
        frenchLinearLayout.setVisibility(View.GONE);
        queryDictTypeAdapter.setMenuDatas(englishDictName);
        queryDictTypeAdapter.notifyDataSetChanged();
        dictType = Constants.ENGLISH_TAG;
        wordQuery.requestFocus();
        getDictData(dictType);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChineseQueryEvent(ChineseQueryEvent event) {
        chineseLinearLayout.setVisibility(View.VISIBLE);
        englishLinearLayout.setVisibility(View.GONE);
        japaneseLinearLayout.setVisibility(View.GONE);
        frenchLinearLayout.setVisibility(View.GONE);
        queryDictTypeAdapter.setMenuDatas(chineseDictName);
        queryDictTypeAdapter.notifyDataSetChanged();
        dictType = Constants.CHINESE_TAG;
        spellQuery.requestFocus();
        getDictData(dictType);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJapaneseQueryEvent(JapaneseQueryEvent event) {
        japaneseLinearLayout.setVisibility(View.VISIBLE);
        englishLinearLayout.setVisibility(View.GONE);
        chineseLinearLayout.setVisibility(View.GONE);
        frenchLinearLayout.setVisibility(View.GONE);
        queryDictTypeAdapter.setMenuDatas(japaneseDictName);
        queryDictTypeAdapter.notifyDataSetChanged();
        dictType = Constants.JAPANESE_TAG;
        japaneseQuery.requestFocus();
        getDictData(dictType);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFrenchQueryEvent(FrenchQueryEvent event) {
        japaneseLinearLayout.setVisibility(View.GONE);
        englishLinearLayout.setVisibility(View.GONE);
        chineseLinearLayout.setVisibility(View.GONE);
        frenchLinearLayout.setVisibility(View.VISIBLE);
        queryDictTypeAdapter.setMenuDatas(frenchDictName);
        queryDictTypeAdapter.notifyDataSetChanged();
        dictType = Constants.FRENCH_TAG;
        frenchQuery.requestFocus();
        getDictData(dictType);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
