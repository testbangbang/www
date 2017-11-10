package com.onyx.android.dr.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.GoodSentenceAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.dialog.ExportSuccessHintDialog;
import com.onyx.android.dr.dialog.TimePickerDialog;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.event.ExportHtmlSuccessEvent;
import com.onyx.android.dr.interfaces.GoodSentenceView;
import com.onyx.android.dr.presenter.GoodSentencePresenter;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageIndicator;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.good_sentence_activity_radio_group)
    RadioGroup radioGroup;
    @Bind(R.id.good_sentence_activity_english)
    RadioButton englishRadioButton;
    @Bind(R.id.good_sentence_activity_chinese)
    RadioButton chineseRadioButton;
    @Bind(R.id.good_sentence_activity_minority_language)
    RadioButton minorityLanguageRadioButton;
    @Bind(R.id.title_bar_right_select_time)
    TextView selectTime;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.title_bar_right_icon_three)
    ImageView iconThree;
    @Bind(R.id.good_sentence_activity_all_check)
    CheckBox allCheck;
    @Bind(R.id.page_indicator_layout)
    RelativeLayout pageIndicatorLayout;
    private DividerItemDecoration dividerItemDecoration;
    private GoodSentenceAdapter goodSentenceAdapter;
    private GoodSentencePresenter goodSentencePresenter;
    private int dictType = Constants.ENGLISH_TYPE;
    private List<GoodSentenceNoteEntity> goodSentenceList;
    private ArrayList<Boolean> listCheck;
    private TimePickerDialog timePickerDialog;
    private PageIndicator pageIndicator;
    private ExportSuccessHintDialog hintDialog;

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
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        goodSentenceAdapter = new GoodSentenceAdapter();
        goodSentenceRecyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        goodSentenceRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        goodSentenceList = new ArrayList<>();
        listCheck = new ArrayList<>();
        timePickerDialog = new TimePickerDialog(this);
        hintDialog = new ExportSuccessHintDialog(this);
        initPageIndicator(pageIndicatorLayout);
        loadData();
        initEvent();
    }

    private void loadData() {
        goodSentencePresenter = new GoodSentencePresenter(getApplicationContext(), this);
        radioGroup.setOnCheckedChangeListener(new TabCheckedListener());
        radioGroup.check(R.id.good_sentence_activity_english);
        goodSentencePresenter.getGoodSentenceByType(dictType);
        initTitleData();
    }

    private void initTitleData() {
        selectTime.setVisibility(View.VISIBLE);
        iconFour.setVisibility(View.VISIBLE);
        iconThree.setVisibility(View.VISIBLE);
        image.setImageResource(R.drawable.good_sentence_notebook);
        iconFour.setImageResource(R.drawable.ic_reader_note_delet);
        iconThree.setImageResource(R.drawable.ic_reader_note_export);
        title.setText(getString(R.string.good_sentence_notebook));
        selectTime.setText(getString(R.string.select_time));
    }

    @Override
    public void setGoodSentenceData(List<GoodSentenceNoteEntity> dataList, ArrayList<Boolean> checkList) {
        goodSentenceList.clear();
        if (dataList != null || dataList.size() > 0) {
            goodSentenceList = dataList;
            listCheck = checkList;
        }
        if (dictType == Constants.ENGLISH_TYPE) {
            englishRadioButton.setText(getString(R.string.english) + "(" + goodSentenceList.size() + getString(R.string.new_word_unit) + ")");
        } else if (dictType == Constants.CHINESE_TYPE) {
            chineseRadioButton.setText(getString(R.string.chinese) + "(" + goodSentenceList.size() + getString(R.string.new_word_unit) + ")");
        } else if (dictType == Constants.OTHER_TYPE) {
            minorityLanguageRadioButton.setText(getString(R.string.small_language) + "(" + goodSentenceList.size() + getString(R.string.new_word_unit) + ")");
        }
        setRecyclerViewData();
    }

    private void setRecyclerViewData() {
        goodSentenceAdapter.setDataList(goodSentenceList, listCheck);
        goodSentenceRecyclerView.setAdapter(goodSentenceAdapter);
        goodSentenceAdapter.notifyDataSetChanged();
        updatePageIndicator();
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
        allCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    for (int i = 0, j = goodSentenceList.size(); i < j; i++) {
                        listCheck.set(i, true);
                    }
                } else {
                    for (int i = 0, j = goodSentenceList.size(); i < j; i++) {
                        listCheck.set(i, false);
                    }
                }
                goodSentenceAdapter.notifyDataSetChanged();
            }
        });
        goodSentenceRecyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPrevPage(int prevPosition, int itemCount, int pageSize) {
                getPagination().prevPage();
                updatePageIndicator();
            }

            @Override
            public void onNextPage(int nextPosition, int itemCount, int pageSize) {
                getPagination().nextPage();
                updatePageIndicator();
            }
        });
    }

    private class TabCheckedListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.good_sentence_activity_english:
                    loadEnglishData();
                    break;
                case R.id.good_sentence_activity_chinese:
                    loadChineseData();
                    break;
                case R.id.good_sentence_activity_minority_language:
                    loadMinorityData();
                    break;
            }
        }
    }

    private void loadMinorityData() {
        allCheck.setChecked(false);
        dictType = Constants.OTHER_TYPE;
        goodSentencePresenter.getGoodSentenceByType(dictType);
    }

    private void loadChineseData() {
        allCheck.setChecked(false);
        dictType = Constants.CHINESE_TYPE;
        goodSentencePresenter.getGoodSentenceByType(dictType);
    }

    private void loadEnglishData() {
        allCheck.setChecked(false);
        dictType = Constants.ENGLISH_TYPE;
        goodSentencePresenter.getGoodSentenceByType(dictType);
    }

    @OnClick({R.id.title_bar_right_icon_three,
            R.id.menu_back,
            R.id.title_bar_right_select_time,
            R.id.title_bar_right_icon_four})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.title_bar_right_select_time:
                timePickerDialog.showDatePickerDialog();
                break;
            case R.id.title_bar_right_icon_four:
                deleteCheckedData();
                break;
            case R.id.title_bar_right_icon_three:
                exportData();
                break;
        }
    }

    private void exportData() {
        if (goodSentenceList.size() > 0) {
            ArrayList<String> htmlTitleData = goodSentencePresenter.getHtmlTitle();
            goodSentencePresenter.exportDataToHtml(this, listCheck, htmlTitleData, goodSentenceList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    private void deleteCheckedData() {
        if (goodSentenceList.size() > 0) {
            goodSentencePresenter.remoteAdapterData(listCheck, goodSentenceAdapter, goodSentenceList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    @Override
    public void positiveListener() {
        long startDateMillisecond = timePickerDialog.getStartDateMillisecond();
        long endDateMillisecond = timePickerDialog.getEndDateMillisecond();
        goodSentencePresenter.getGoodSentenceByTime(dictType, startDateMillisecond, endDateMillisecond);
    }

    @Override
    public void setGoodSentenceByTime(List<GoodSentenceNoteEntity> dataList, ArrayList<Boolean> checkList) {
        if (dataList != null && dataList.size() > 0) {
            goodSentenceList = dataList;
            listCheck = checkList;
            setRecyclerViewData();
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
            goodSentenceList.clear();
            goodSentenceAdapter.notifyDataSetChanged();
        }
    }

    private void initPageIndicator(ViewGroup parentView) {
        if (parentView == null) {
            return;
        }
        initPagination();
        pageIndicator = new PageIndicator(parentView.findViewById(R.id.page_indicator_layout), goodSentenceRecyclerView.getPaginator());
        pageIndicator.showRefresh(false);
        pageIndicator.setTotalFormat(getString(R.string.total_format));
        pageIndicator.setPageChangedListener(new PageIndicator.PageChangedListener() {
            @Override
            public void prev() {
                goodSentenceRecyclerView.prevPage();
            }

            @Override
            public void next() {
                goodSentenceRecyclerView.nextPage();
            }

            @Override
            public void gotoPage(int page) {
            }
        });
        pageIndicator.setDataRefreshListener(new PageIndicator.DataRefreshListener() {
            @Override
            public void onRefresh() {
            }
        });
    }

    private void initPagination() {
        QueryPagination pagination = getPagination();
        pagination.resize(goodSentenceAdapter.getRowCount(), goodSentenceAdapter.getColumnCount(), 0);
        pagination.setCurrentPage(0);
        goodSentenceRecyclerView.setCurrentPage(0);
    }

    private QueryPagination getPagination() {
        return DRApplication.getLibraryDataHolder().getCloudViewInfo().getQueryPagination();
    }

    private void updatePageIndicator() {
        int totalCount = goodSentenceAdapter.getDataCount();
        getPagination().resize(goodSentenceAdapter.getRowCount(), goodSentenceAdapter.getColumnCount(), totalCount);
        pageIndicator.resetGPaginator(getPagination());
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlSuccessEvent(ExportHtmlSuccessEvent event) {
        hintDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlFailedEvent(ExportHtmlFailedEvent event) {
        CommonNotices.showMessage(this, getString(R.string.export_failed));
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
