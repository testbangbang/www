package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.NewWordAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.event.EnglishGoodSentenceEvent;
import com.onyx.android.dr.interfaces.NewWordView;
import com.onyx.android.dr.presenter.NewWordPresenter;
import com.onyx.android.dr.util.ExportToHtmlUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class NewWordNotebookActivity extends BaseActivity implements NewWordView {
    @Bind(R.id.new_word_activity_recyclerview)
    PageRecyclerView goodSentenceRecyclerView;
    @Bind(R.id.new_word_activity_month_spinner)
    Spinner monthSpinner;
    @Bind(R.id.new_word_activity_week_spinner)
    Spinner weekSpinner;
    @Bind(R.id.new_word_activity_day_spinner)
    Spinner daySpinner;
    @Bind(R.id.new_word_activity_delete)
    TextView delete;
    @Bind(R.id.new_word_activity_export)
    TextView export;
    private DividerItemDecoration dividerItemDecoration;
    private NewWordAdapter newWordAdapter;
    private NewWordPresenter newWordPresenter;
    private List<NewWordNoteBookEntity> newWordList;
    private HashMap<Integer, Boolean> isSelectedMap;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_new_word_notebook;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecylcerView();
    }

    private void initRecylcerView() {
        dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        newWordAdapter = new NewWordAdapter();
        goodSentenceRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        goodSentenceRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        newWordPresenter = new NewWordPresenter(getApplicationContext(), this);
        newWordPresenter.getAllNewWordData();
        newWordList = new ArrayList<NewWordNoteBookEntity>();
        isSelectedMap = new HashMap<Integer, Boolean>();

        initSpinnerDatas();
        initEvent();
    }

    private void initSpinnerDatas() {
        String[] monthDatas = getResources().getStringArray(R.array.month);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_unexpanded_pattern, monthDatas);
        monthAdapter.setDropDownViewResource(R.layout.item_spinner_expanded_pattern);
        monthSpinner.setAdapter(monthAdapter);

        String[] weekDatas = getResources().getStringArray(R.array.week);
        ArrayAdapter<String> weekAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_unexpanded_pattern, weekDatas);
        weekAdapter.setDropDownViewResource(R.layout.item_spinner_expanded_pattern);
        weekSpinner.setAdapter(weekAdapter);

        String[] dayDatas = getResources().getStringArray(R.array.day);
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_unexpanded_pattern, dayDatas);
        dayAdapter.setDropDownViewResource(R.layout.item_spinner_expanded_pattern);
        daySpinner.setAdapter(dayAdapter);
    }

    @Override
    public void setNewWordData(List<NewWordNoteBookEntity> dataList) {
        newWordList = dataList;
        newWordAdapter.setDataList(newWordList, isSelectedMap);
        goodSentenceRecyclerView.setAdapter(newWordAdapter);
    }

    public void initEvent() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEnglishGoodSentenceEvent(EnglishGoodSentenceEvent event) {
        CommonNotices.showMessage(this, getString(R.string.menu_graded_books));
    }

    @OnClick({R.id.new_word_activity_delete,
            R.id.image_view_back,
            R.id.new_word_activity_export})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
            case R.id.new_word_activity_delete:
                remoteAdapterDatas();
                break;
            case R.id.new_word_activity_export:
                exportData();
                break;
        }
    }

    public void remoteAdapterDatas() {
        isSelectedMap = newWordAdapter.getIsSelected();
        for (int i = 0; i < isSelectedMap.size(); i++) {
            if (isSelectedMap.get(i).equals(true)) {
                newWordList.remove(i);
                isSelectedMap.put(i, false);
                isSelectedMap.remove(isSelectedMap.size() - 1);
                newWordAdapter.notifyItemRemoved(i);
            }
        }
    }

    public void exportData() {
        ArrayList<String> newWordTitle = new ArrayList<String>();
        newWordTitle.add(getString(R.string.good_sentence_activity_month));
        newWordTitle.add(getString(R.string.good_sentence_activity_week));
        newWordTitle.add(getString(R.string.good_sentence_activity_day));
        newWordTitle.add(getString(R.string.new_word_activity_new_word));
        newWordTitle.add(getString(R.string.new_word_activity_dictionaryLookup));
        newWordTitle.add(getString(R.string.good_sentence_activity_involved_reading_matter));
        ExportToHtmlUtils.exportNewWordToHtml(newWordTitle, getString(R.string.new_word_notebook_html), newWordList);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (NewWordAdapter.isSelected != null) {
            NewWordAdapter.isSelected.clear();
        }
    }
}
