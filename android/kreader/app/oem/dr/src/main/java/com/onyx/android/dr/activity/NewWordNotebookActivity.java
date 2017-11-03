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
import com.onyx.android.dr.adapter.NewWordAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.dialog.TimePickerDialog;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.event.ExportHtmlSuccessEvent;
import com.onyx.android.dr.interfaces.NewWordView;
import com.onyx.android.dr.presenter.NewWordPresenter;
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
public class NewWordNotebookActivity extends BaseActivity implements NewWordView, TimePickerDialog.TimePickerDialogInterface {
    @Bind(R.id.new_word_activity_recyclerview)
    PageRecyclerView newWordRecyclerView;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.new_word_activity_radio_group)
    RadioGroup radioGroup;
    @Bind(R.id.new_word_activity_english)
    RadioButton englishRadioButton;
    @Bind(R.id.new_word_activity_chinese)
    RadioButton chineseRadioButton;
    @Bind(R.id.new_word_activity_minority_language)
    RadioButton minorityLanguageRadioButton;
    @Bind(R.id.title_bar_right_select_time)
    TextView selectTime;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.title_bar_right_icon_three)
    ImageView iconThree;
    @Bind(R.id.new_word_activity_all_check)
    CheckBox allCheck;
    @Bind(R.id.page_indicator_layout)
    RelativeLayout pageIndicatorLayout;
    private DividerItemDecoration dividerItemDecoration;
    private NewWordAdapter newWordAdapter;
    private NewWordPresenter newWordPresenter;
    private List<NewWordNoteBookEntity> newWordList;
    private ArrayList<Boolean> listCheck;
    private TimePickerDialog timePickerDialog;
    private int dictType = Constants.ENGLISH_TYPE;
    private PageIndicator pageIndicator;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_new_word_notebook;
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
        newWordRecyclerView.addItemDecoration(dividerItemDecoration);
        newWordAdapter = new NewWordAdapter();
        DisableScrollGridManager disableScrollGridManager = new DisableScrollGridManager(DRApplication.getInstance());
        disableScrollGridManager.setScrollEnable(true);
        newWordRecyclerView.setLayoutManager(disableScrollGridManager);
        newWordRecyclerView.setAdapter(newWordAdapter);
    }

    @Override
    protected void initData() {
        newWordList = new ArrayList<>();
        listCheck = new ArrayList<>();
        timePickerDialog = new TimePickerDialog(this);
        initPageIndicator(pageIndicatorLayout);
        initTitleData();
        loadNewWordData();
        initEvent();
    }

    private void loadNewWordData() {
        newWordPresenter = new NewWordPresenter(getApplicationContext(), this);
        radioGroup.setOnCheckedChangeListener(new TabCheckedListener());
        radioGroup.check(R.id.new_word_activity_english);
        newWordPresenter.getAllNewWordByType(dictType);
    }

    private void initTitleData() {
        selectTime.setVisibility(View.VISIBLE);
        iconFour.setVisibility(View.VISIBLE);
        iconThree.setVisibility(View.VISIBLE);
        image.setImageResource(R.drawable.new_word_notebook);
        iconFour.setImageResource(R.drawable.ic_reader_note_delet);
        iconThree.setImageResource(R.drawable.ic_reader_note_export);
        title.setText(getString(R.string.new_word_notebook));
        selectTime.setText(getString(R.string.select_time));
    }

    @Override
    public void setNewWordData(List<NewWordNoteBookEntity> dataList, ArrayList<Boolean> checkList) {
        newWordList.clear();
        if (dataList != null || dataList.size() > 0) {
            newWordList = dataList;
            listCheck = checkList;
        }
        if (dictType == Constants.ENGLISH_TYPE) {
            englishRadioButton.setText(getString(R.string.english) + "(" + newWordList.size() + getString(R.string.new_word_unit) + ")");
        } else if (dictType == Constants.CHINESE_TYPE) {
            chineseRadioButton.setText(getString(R.string.chinese) + "(" + newWordList.size() + getString(R.string.new_word_unit) + ")");
        } else if (dictType == Constants.OTHER_TYPE) {
            minorityLanguageRadioButton.setText(getString(R.string.small_language) + "(" + newWordList.size() + getString(R.string.new_word_unit) + ")");
        }
        setRecyclerViewData();
    }

    private void setRecyclerViewData() {
        newWordAdapter.setDataList(newWordList, listCheck);
        newWordAdapter.notifyDataSetChanged();
        updatePageIndicator();
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
        allCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    for (int i = 0, j = newWordList.size(); i < j; i++) {
                        listCheck.set(i, true);
                    }
                } else {
                    for (int i = 0, j = newWordList.size(); i < j; i++) {
                        listCheck.set(i, false);
                    }
                }
                newWordAdapter.notifyDataSetChanged();
            }
        });
        newWordRecyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
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
                case R.id.new_word_activity_english:
                    loadEnglishData();
                    break;
                case R.id.new_word_activity_chinese:
                    loadChineseData();
                    break;
                case R.id.new_word_activity_minority_language:
                    loadMinorityData();
                    break;
            }
        }
    }

    private void loadMinorityData() {
        allCheck.setChecked(false);
        dictType = Constants.OTHER_TYPE;
        newWordPresenter.getAllNewWordByType(dictType);
    }

    private void loadChineseData() {
        allCheck.setChecked(false);
        dictType = Constants.CHINESE_TYPE;
        newWordPresenter.getAllNewWordByType(dictType);
    }

    private void loadEnglishData() {
        allCheck.setChecked(false);
        dictType = Constants.ENGLISH_TYPE;
        newWordPresenter.getAllNewWordByType(dictType);
    }

    @OnClick({R.id.menu_back,
            R.id.title_bar_right_select_time,
            R.id.title_bar_right_icon_three,
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
                deleteCheckedNewWord();
                break;
            case R.id.title_bar_right_icon_three:
                exportData();
                break;
        }
    }

    private void exportData() {
        if (newWordList.size() > 0) {
            ArrayList<String> htmlTitleData = newWordPresenter.getHtmlTitle();
            newWordPresenter.exportDataToHtml(this, listCheck, htmlTitleData, newWordList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    private void deleteCheckedNewWord() {
        if (newWordList.size() > 0) {
            newWordPresenter.remoteAdapterData(listCheck, newWordAdapter, newWordList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    @Override
    public void positiveListener() {
        long startDateMillisecond = timePickerDialog.getStartDateMillisecond();
        long endDateMillisecond = timePickerDialog.getEndDateMillisecond();
        newWordPresenter.getNewWordByTime(dictType, startDateMillisecond, endDateMillisecond);
    }

    @Override
    public void setNewWordByTime(List<NewWordNoteBookEntity> dataList, ArrayList<Boolean> checkList) {
        if (dataList != null && dataList.size() > 0) {
            newWordList = dataList;
            listCheck = checkList;
            setRecyclerViewData();
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
            newWordList.clear();
            newWordAdapter.notifyDataSetChanged();
        }
    }

    private void initPageIndicator(ViewGroup parentView) {
        if (parentView == null) {
            return;
        }
        initPagination();
        pageIndicator = new PageIndicator(parentView.findViewById(R.id.page_indicator_layout), newWordRecyclerView.getPaginator());
        pageIndicator.showRefresh(false);
        pageIndicator.setTotalFormat(getString(R.string.total_format));
        pageIndicator.setPageChangedListener(new PageIndicator.PageChangedListener() {
            @Override
            public void prev() {
                newWordRecyclerView.prevPage();
            }

            @Override
            public void next() {
                newWordRecyclerView.nextPage();
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
        pagination.resize(newWordAdapter.getRowCount(), newWordAdapter.getColumnCount(), 0);
        pagination.setCurrentPage(0);
        newWordRecyclerView.setCurrentPage(0);
    }

    private QueryPagination getPagination() {
        return DRApplication.getLibraryDataHolder().getCloudViewInfo().getQueryPagination();
    }

    private void updatePageIndicator() {
        int totalCount = newWordAdapter.getDataCount();
        getPagination().resize(newWordAdapter.getRowCount(), newWordAdapter.getColumnCount(), totalCount);
        pageIndicator.resetGPaginator(getPagination());
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlSuccessEvent(ExportHtmlSuccessEvent event) {
        CommonNotices.showMessage(this, getString(R.string.has_exported_to) + event.getFilePath());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlFailedEvent(ExportHtmlFailedEvent event) {
        CommonNotices.showMessage(this, getString(R.string.export_failed));
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
