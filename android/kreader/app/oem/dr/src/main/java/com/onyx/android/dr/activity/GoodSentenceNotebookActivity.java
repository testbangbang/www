package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.GoodSentenceAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.dialog.TimePickerDialog;
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
public class GoodSentenceNotebookActivity extends BaseActivity implements GoodSentenceView, TimePickerDialog.TimePickerDialogInterface {
    @Bind(R.id.good_sentence_activity_recyclerview)
    PageRecyclerView goodSentenceRecyclerView;
    @Bind(R.id.good_sentence_activity_export)
    TextView delete;
    @Bind(R.id.good_sentence_activity_delete)
    TextView export;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    private DividerItemDecoration dividerItemDecoration;
    private GoodSentenceAdapter goodSentenceAdapter;
    private GoodSentencePresenter goodSentencePresenter;
    private int dictType;
    private List<GoodSentenceNoteEntity> goodSentenceList;
    private ArrayList<Boolean> listCheck;
    private TimePickerDialog timePickerDialog;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_good_sentence_notebook;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        goodSentenceAdapter = new GoodSentenceAdapter();
        goodSentenceRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        goodSentenceRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        goodSentenceList = new ArrayList<GoodSentenceNoteEntity>();
        listCheck = new ArrayList<>();
        timePickerDialog = new TimePickerDialog(this);
        loadData();
        initEvent();
    }

    private void loadData() {
        dictType = getIntent().getIntExtra(Constants.DICTTYPE, -1);
        goodSentencePresenter = new GoodSentencePresenter(getApplicationContext(), this);
        goodSentencePresenter.getGoodSentenceByType(dictType);
        initTitleData();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.good_sentence_notebook);
        if (dictType == Constants.ENGLISH_TYPE) {
            title.setText(getString(R.string.english_good_sentence_notebook));
        } else if (dictType == Constants.CHINESE_TYPE) {
            title.setText(getString(R.string.chinese_good_sentence_notebook));
        }else if (dictType == Constants.OTHER_TYPE) {
            title.setText(getString(R.string.minority_language_good_sentence_notebook));
        }
    }

    @Override
    public void setGoodSentenceData(List<GoodSentenceNoteEntity> dataList, ArrayList<Boolean> checkList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        goodSentenceList = dataList;
        listCheck = checkList;
        goodSentenceAdapter.setDataList(goodSentenceList, listCheck);
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
                if (goodSentenceList.size() > 0) {
                    goodSentencePresenter.remoteAdapterDatas(listCheck, goodSentenceAdapter);
                } else {
                    CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
                }
                break;
            case R.id.good_sentence_activity_export:
                timePickerDialog.showDatePickerDialog();
                break;
        }
    }

    @Override
    public void positiveListener() {
        long startDateMillisecond = timePickerDialog.getStartDateMillisecond();
        long endDateMillisecond = timePickerDialog.getEndDateMillisecond();
        goodSentencePresenter.getGoodSentenceByTime(startDateMillisecond, endDateMillisecond);
    }

    @Override
    public void setGoodSentenceByTime(List<GoodSentenceNoteEntity> dataList) {
        if (dataList != null && dataList.size() > 0) {
            ArrayList<String> htmlTitleData = goodSentencePresenter.getHtmlTitle();
            goodSentencePresenter.exportDataToHtml(this, htmlTitleData, dataList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
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
