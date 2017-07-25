package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.NewWordAdapter;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.interfaces.NewWordView;
import com.onyx.android.dr.presenter.NewWordPresenter;
import com.onyx.android.dr.util.ExportToHtmlUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
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
    private ArrayList<Boolean> listCheck;

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
        listCheck = new ArrayList<>();
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
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        newWordList = dataList;
        for (int i = 0; i < newWordList.size(); i++) {
            listCheck.add(false);
        }
        newWordAdapter.setDataList(newWordList, listCheck);
        goodSentenceRecyclerView.setAdapter(newWordAdapter);
    }

    public void initEvent() {
        newWordAdapter.setOnItemListener(new NewWordAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClick(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }

            @Override
            public void setOnItemCheckedChanged(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }
        });
    }

    @OnClick({R.id.new_word_activity_delete,
            R.id.image_view_back,
            R.id.new_word_activity_export})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.new_word_activity_delete:
                remoteAdapterDatas();
                break;
            case R.id.new_word_activity_export:
                exportData();
                break;
        }
    }

    public void remoteAdapterDatas() {
        int length = listCheck.size();
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                //delete basedata data
                NewWordNoteBookEntity newWordNoteBookEntity = newWordList.get(i);
                newWordPresenter.deleteNewWord(newWordNoteBookEntity.currentTime);
                newWordList.remove(i);
                listCheck.remove(i);
                newWordAdapter.notifyItemRemoved(i);
                newWordAdapter.notifyItemRangeChanged(0, newWordList.size());
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
    }
}
