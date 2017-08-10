package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.DictTypeAdapter;
import com.onyx.android.dr.adapter.LanguageQueryTypeAdapter;
import com.onyx.android.dr.bean.DictFunctionBean;
import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.ChineseQueryEvent;
import com.onyx.android.dr.event.EnglishQueryEvent;
import com.onyx.android.dr.event.JapaneseQueryEvent;
import com.onyx.android.dr.interfaces.DictResultShowView;
import com.onyx.android.dr.presenter.DictFunctionPresenter;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-6-26.
 */

public class DictQueryActivity extends BaseActivity implements DictResultShowView, View.OnClickListener {
    @Bind(R.id.tab_menu)
    PageRecyclerView tabMenu;
    @Bind(R.id.activity_query_word)
    EditText wordQuery;
    @Bind(R.id.activity_query_fuzzy)
    EditText fuzzyQuery;
    @Bind(R.id.activity_query_phrase)
    EditText phraseQuery;
    @Bind(R.id.activity_query_example)
    EditText exampleQuery;
    @Bind(R.id.activity_query_spell_query)
    EditText spellQuery;
    @Bind(R.id.activity_query_japanese_query)
    EditText japaneseQuery;
    @Bind(R.id.activity_query_radical_strokes)
    EditText radicalStrokesQuery;
    @Bind(R.id.activity_query_total_strokes)
    EditText totalStrokesQuery;
    @Bind(R.id.activity_word_query_search)
    Button wordQuerySearch;
    @Bind(R.id.activity_fuzzy_query_search)
    Button fuzzyQuerySearch;
    @Bind(R.id.activity_phrase_query_search)
    Button phraseQuerySearch;
    @Bind(R.id.activity_example_query_search)
    Button exampleQuerySearch;
    @Bind(R.id.activity_strokes_search)
    Button strokeSearch;
    @Bind(R.id.activity_word_spell_search)
    Button spellSearch;
    @Bind(R.id.activity_word_japanese_search)
    Button japaneseSearch;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.activity_query_chinese_linearlayout)
    LinearLayout chineseLinearLayout;
    @Bind(R.id.activity_query_english_linearlayout)
    LinearLayout englishLinearLayout;
    @Bind(R.id.activity_query_japanese_container)
    LinearLayout japaneseLinearLayout;
    @Bind(R.id.activity_query_dict_view)
    PageRecyclerView dictTypeRecyclerView;
    private DividerItemDecoration dividerItemDecoration;
    private LanguageQueryTypeAdapter languageQueryTypeAdapter;
    private DictFunctionPresenter dictPresenter;
    private DictTypeAdapter dictTypeAdapter;
    private List<DictTypeBean> englishDictName;
    private List<DictTypeBean> chineseDictName;
    private List<DictTypeBean> japaneseDictName;
    private int dictType = Constants.ENGLISH_TYPE;

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
        dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        tabMenu.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        tabMenu.addItemDecoration(dividerItemDecoration);
        languageQueryTypeAdapter = new LanguageQueryTypeAdapter();
        tabMenu.setAdapter(languageQueryTypeAdapter);
        dictTypeRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
    }

    @Override
    protected void initData() {
        dictPresenter = new DictFunctionPresenter(this);
        dictPresenter.loadData(this);
        dictPresenter.loadDictType(Constants.ACCOUNT_TYPE_DICT_LANGUAGE);
        loadDictData();
        initEvent();
    }

    private void loadDictData() {
        dictTypeRecyclerView.addItemDecoration(dividerItemDecoration);
        dictTypeAdapter = new DictTypeAdapter();
        englishDictName = Utils.getDictName(Constants.ENGLISH_DICTIONARY);
        chineseDictName = Utils.getDictName(Constants.CHINESE_DICTIONARY);
        japaneseDictName = Utils.getDictName(Constants.OTHER_DICTIONARY);
        dictTypeAdapter.setMenuDatas(englishDictName);
        dictTypeRecyclerView.setAdapter(dictTypeAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void setDictResultData(List<DictFunctionBean> functionData) {
    }

    @Override
    public void setDictTypeData(List<DictTypeBean> dictData) {
        languageQueryTypeAdapter.setMenuDatas(dictData);
    }

    public void initEvent() {
        startSoftKeyboardSearch(wordQuery);
        startSoftKeyboardSearch(fuzzyQuery);
        startSoftKeyboardSearch(phraseQuery);
        startSoftKeyboardSearch(exampleQuery);
        startSoftKeyboardSearch(spellQuery);
        startSoftKeyboardSearch(japaneseQuery);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEnglishQueryEvent(EnglishQueryEvent event) {
        englishLinearLayout.setVisibility(View.VISIBLE);
        chineseLinearLayout.setVisibility(View.GONE);
        japaneseLinearLayout.setVisibility(View.GONE);
        dictTypeAdapter.setMenuDatas(englishDictName);
        dictTypeAdapter.notifyDataSetChanged();
        dictType = Constants.ENGLISH_TYPE;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChineseQueryEvent(ChineseQueryEvent event) {
        chineseLinearLayout.setVisibility(View.VISIBLE);
        englishLinearLayout.setVisibility(View.GONE);
        japaneseLinearLayout.setVisibility(View.GONE);
        dictTypeAdapter.setMenuDatas(chineseDictName);
        dictTypeAdapter.notifyDataSetChanged();
        dictType = Constants.CHINESE_TYPE;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJapaneseQueryEvent(JapaneseQueryEvent event) {
        japaneseLinearLayout.setVisibility(View.VISIBLE);
        englishLinearLayout.setVisibility(View.GONE);
        chineseLinearLayout.setVisibility(View.GONE);
        dictTypeAdapter.setMenuDatas(japaneseDictName);
        dictTypeAdapter.notifyDataSetChanged();
        dictType = Constants.OTHER_TYPE;
    }

    @OnClick({R.id.activity_word_query_search,
            R.id.activity_fuzzy_query_search,
            R.id.activity_phrase_query_search,
            R.id.image_view_back,
            R.id.activity_strokes_search,
            R.id.activity_word_spell_search,
            R.id.activity_example_query_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.activity_word_query_search:
                startDictResultShowActivity(wordQuery);
                break;
            case R.id.activity_fuzzy_query_search:
                startDictResultShowActivity(fuzzyQuery);
                break;
            case R.id.activity_phrase_query_search:
                startDictResultShowActivity(phraseQuery);
                break;
            case R.id.activity_example_query_search:
                startDictResultShowActivity(exampleQuery);
                break;
            case R.id.activity_strokes_search:
                break;
            case R.id.activity_word_spell_search:
                startDictResultShowActivity(spellQuery);
                break;
        }
    }

    public void startDictResultShowActivity(EditText editText) {
        String editQueryString = editText.getText().toString();
        editQueryString = Utils.trim(editQueryString);
        if (!StringUtils.isNullOrEmpty(editQueryString)) {
            ActivityManager.startDictResultShowActivity(this, editQueryString, dictType);
        } else {
            CommonNotices.showMessage(this, getString(R.string.illegalInput));
        }
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
