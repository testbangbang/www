package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.GoodSentenceAdapter;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.interfaces.GoodSentenceView;
import com.onyx.android.dr.presenter.GoodSentencePresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class GoodSentenceNotebookActivity extends BaseActivity implements GoodSentenceView {
    @Bind(R.id.good_sentence_activity_recyclerview)
    PageRecyclerView goodSentenceRecyclerView;
    @Bind(R.id.good_sentence_activity_export)
    TextView delete;
    @Bind(R.id.good_sentence_activity_delete)
    TextView export;
    private DividerItemDecoration dividerItemDecoration;
    private GoodSentenceAdapter goodSentenceAdapter;
    private GoodSentencePresenter goodSentencePresenter;
    private int dictType;
    private List<GoodSentenceNoteEntity> newWordList;
    private ArrayList<Boolean> listCheck;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_good_sentence_notebook;
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
        goodSentenceAdapter = new GoodSentenceAdapter();
        goodSentenceRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        goodSentenceRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        newWordList = new ArrayList<GoodSentenceNoteEntity>();
        listCheck = new ArrayList<>();
        loadData();
        initEvent();
    }

    private void loadData() {
        dictType = getIntent().getIntExtra(Constants.DICTTYPE, -1);
        goodSentencePresenter = new GoodSentencePresenter(getApplicationContext(), this);
        goodSentencePresenter.getGoodSentenceByType(dictType);
    }

    @Override
    public void setGoodSentenceData(List<GoodSentenceNoteEntity> dataList, ArrayList<Boolean> checkList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        newWordList = dataList;
        listCheck = checkList;
        goodSentenceAdapter.setDataList(newWordList, listCheck);
        goodSentenceRecyclerView.setAdapter(goodSentenceAdapter);
    }

    public void initEvent() {
        goodSentenceAdapter.setOnItemListener(new GoodSentenceAdapter.OnItemClickListener() {
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

    @OnClick({R.id.good_sentence_activity_delete,
            R.id.image_view_back,
            R.id.good_sentence_activity_export})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.good_sentence_activity_delete:
                goodSentencePresenter.remoteAdapterDatas(listCheck, goodSentenceAdapter);
                break;
            case R.id.good_sentence_activity_export:
                goodSentencePresenter.getHtmlTitle();
                break;
        }
    }

    @Override
    public void setHtmlTitleData(ArrayList<String> dataList) {
        goodSentencePresenter.exportDataToHtml(this, dataList, newWordList);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
