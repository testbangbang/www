package com.onyx.android.dr.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.LanguageQueryTypeAdapter;
import com.onyx.android.dr.adapter.QueryDictTypeAdapter;
import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.DictTypeConfig;
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
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
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
    @Bind(R.id.activity_query_dict_view)
    PageRecyclerView dictTypeRecyclerView;
    private LanguageQueryTypeAdapter languageQueryTypeAdapter;
    private DictFunctionPresenter dictPresenter;
    private QueryDictTypeAdapter queryDictTypeAdapter;
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
        tabMenu.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        languageQueryTypeAdapter = new LanguageQueryTypeAdapter();
        tabMenu.setAdapter(languageQueryTypeAdapter);
        dictTypeRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
    }

    @Override
    protected void initData() {
        queryDictTypeAdapter = new QueryDictTypeAdapter();
        dictPresenter = new DictFunctionPresenter(this);
        dictTypeRecyclerView.setAdapter(queryDictTypeAdapter);
        dictPresenter.getDictMapData();
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
        List<DictTypeBean> dictLanguageData = DictTypeConfig.dictLanguageData;
        if (dictLanguageData == null || dictLanguageData.size() <= 0) {
            dictPresenter.loadData(this);
            dictPresenter.loadDictType(Constants.ACCOUNT_TYPE_DICT_LANGUAGE);
        } else {
            intContainerVisible(dictLanguageData);
            languageQueryTypeAdapter.setMenuDatas(dictLanguageData);
            languageQueryTypeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setDictTypeData(List<DictTypeBean> dictData) {
        if (dictData == null || dictData.size() <= 0) {
            return;
        }
        intContainerVisible(dictData);
        languageQueryTypeAdapter.setMenuDatas(dictData);
        languageQueryTypeAdapter.notifyDataSetChanged();
    }

    public void intContainerVisible(List<DictTypeBean> dictLanguageData) {
        int type = dictLanguageData.get(0).getType();
        englishDictName = Utils.getEnglishDictData();
        chineseDictName = Utils.getChineseDictData();
        japaneseDictName = Utils.getMinorityDictData();
        if (type == Constants.ENGLISH_TYPE) {
            englishLinearLayout.setVisibility(View.VISIBLE);
            chineseLinearLayout.setVisibility(View.GONE);
            japaneseLinearLayout.setVisibility(View.GONE);
            queryDictTypeAdapter.setMenuDatas(englishDictName);
            queryDictTypeAdapter.notifyDataSetChanged();
        } else if (type == Constants.CHINESE_TYPE) {
            chineseLinearLayout.setVisibility(View.VISIBLE);
            englishLinearLayout.setVisibility(View.GONE);
            japaneseLinearLayout.setVisibility(View.GONE);
            queryDictTypeAdapter.setMenuDatas(chineseDictName);
            queryDictTypeAdapter.notifyDataSetChanged();
        } else if (type == Constants.OTHER_TYPE) {
            japaneseLinearLayout.setVisibility(View.VISIBLE);
            englishLinearLayout.setVisibility(View.GONE);
            chineseLinearLayout.setVisibility(View.GONE);
            queryDictTypeAdapter.setMenuDatas(japaneseDictName);
            queryDictTypeAdapter.notifyDataSetChanged();
        }
    }

    public void initEvent() {
        startSoftKeyboardSearch(wordQuery);
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
        queryDictTypeAdapter.setMenuDatas(englishDictName);
        queryDictTypeAdapter.notifyDataSetChanged();
        dictType = Constants.ENGLISH_TYPE;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChineseQueryEvent(ChineseQueryEvent event) {
        chineseLinearLayout.setVisibility(View.VISIBLE);
        englishLinearLayout.setVisibility(View.GONE);
        japaneseLinearLayout.setVisibility(View.GONE);
        queryDictTypeAdapter.setMenuDatas(chineseDictName);
        queryDictTypeAdapter.notifyDataSetChanged();
        dictType = Constants.CHINESE_TYPE;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJapaneseQueryEvent(JapaneseQueryEvent event) {
        japaneseLinearLayout.setVisibility(View.VISIBLE);
        englishLinearLayout.setVisibility(View.GONE);
        chineseLinearLayout.setVisibility(View.GONE);
        queryDictTypeAdapter.setMenuDatas(japaneseDictName);
        queryDictTypeAdapter.notifyDataSetChanged();
        dictType = Constants.OTHER_TYPE;
    }

    @OnClick({R.id.activity_word_query_search,
            R.id.activity_phrase_query_search,
            R.id.image_view_back,
            R.id.activity_word_spell_search,
            R.id.activity_word_japanese_search,
            R.id.title_bar_right_icon_four,
            R.id.activity_example_query_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
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
            case R.id.activity_word_spell_search:
                startDictResultShowActivity(spellQuery);
                break;
            case R.id.title_bar_right_icon_four:
                ActivityManager.startDictSettingActivity(this, Constants.DICT_QUERY);
                break;
        }
    }

    public void startDictResultShowActivity(EditText editText) {
        String editQueryString = editText.getText().toString();
        editQueryString = Utils.trim(editQueryString);
        if (!StringUtils.isNullOrEmpty(editQueryString)) {
            finish();
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
