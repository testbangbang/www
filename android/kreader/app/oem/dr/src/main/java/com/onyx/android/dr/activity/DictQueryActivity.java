package com.onyx.android.dr.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.DictTypeAdapter;
import com.onyx.android.dr.adapter.TabMenuAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.DictFunctionData;
import com.onyx.android.dr.data.DictTypeData;
import com.onyx.android.dr.event.ChineseQueryEvent;
import com.onyx.android.dr.event.EnglishQueryEvent;
import com.onyx.android.dr.event.FrenchQueryEvent;
import com.onyx.android.dr.event.JapaneseQueryEvent;
import com.onyx.android.dr.interfaces.DictResultShowView;
import com.onyx.android.dr.presenter.DictFunctionPresenter;
import com.onyx.android.dr.presenter.MainPresenter;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-6-26.
 */

public class DictQueryActivity extends BaseActivity implements DictResultShowView, View.OnClickListener{
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
    @Bind(R.id.activity_word_query_search)
    Button wordQuerySearch;
    @Bind(R.id.activity_fuzzy_query_search)
    Button fuzzyQuerySearch;
    @Bind(R.id.activity_phrase_query_search)
    Button phraseQuerySearch;
    @Bind(R.id.activity_example_query_search)
    Button exampleQuerySearch;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    private MainPresenter mainPresenter;
    private List<Library> libraryList;
    private TabMenuAdapter tabMenuAdapter;
    private DividerItemDecoration dividerItemDecoration;
    private DictTypeAdapter dictTypeAdapter;
    private DictFunctionPresenter dictPresenter;

    public static void startDictQueryActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, DictQueryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_dict_query;
    }

    @Override
    protected void initConfig() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ButterKnife.bind(this);
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
        dictTypeAdapter = new DictTypeAdapter();
        tabMenu.setAdapter(dictTypeAdapter);
    }

    @Override
    protected void initData() {
        dictPresenter = new DictFunctionPresenter(this);
        dictPresenter.loadData(this);
        dictPresenter.loadDictType(Constants.ACCOUNT_TYPE_DICT_LANGUAGE);
        initEvent();
    }

    @Override
    public void setDictResultData(List<DictFunctionData> functionData) {
    }

    @Override
    public void setDictTypeData(List<DictTypeData> dictData) {
        dictTypeAdapter.setMenuDatas(dictData);
    }

    public void initEvent() {
        startSoftKeyboardSearch(wordQuery);
        startSoftKeyboardSearch(fuzzyQuery);
        startSoftKeyboardSearch(phraseQuery);
        startSoftKeyboardSearch(exampleQuery);
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
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChineseQueryEvent(ChineseQueryEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJapaneseQueryEvent(JapaneseQueryEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFrenchQueryEvent(FrenchQueryEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @OnClick({R.id.activity_word_query_search,
            R.id.activity_fuzzy_query_search,
            R.id.activity_phrase_query_search,
            R.id.image_view_back,
            R.id.activity_example_query_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
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
        }
    }

    public void startDictResultShowActivity(EditText editText) {
        String editQueryString = editText.getText().toString();
        if (!StringUtils.isNullOrEmpty(editQueryString)) {
            Intent intent = new Intent(this, DictResultShowActivity.class);
            intent.putExtra("editQuery", editQueryString);
            startActivity(intent);
        } else {
            CommonNotices.showMessage(this, getString(R.string.illegalInput));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
