package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

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
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
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
    @Bind(R.id.new_word_activity_all_number)
    TextView allNumber;
    @Bind(R.id.title_bar_right_select_time)
    TextView selectTime;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.title_bar_right_icon_three)
    ImageView iconThree;
    @Bind(R.id.new_word_activity_all_check)
    CheckBox allCheck;
    private DividerItemDecoration dividerItemDecoration;
    private NewWordAdapter newWordAdapter;
    private NewWordPresenter newWordPresenter;
    private List<NewWordNoteBookEntity> newWordList;
    private ArrayList<Boolean> listCheck;
    private TimePickerDialog timePickerDialog;
    private int dictType = Constants.ENGLISH_TYPE;

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
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        newWordAdapter = new NewWordAdapter();
        DisableScrollGridManager disableScrollGridManager = new DisableScrollGridManager(DRApplication.getInstance());
        disableScrollGridManager.setScrollEnable(true);
        newWordRecyclerView.setLayoutManager(disableScrollGridManager);
        newWordRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        newWordList = new ArrayList<>();
        listCheck = new ArrayList<>();
        timePickerDialog = new TimePickerDialog(this);
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
        allNumber.setText(getString(R.string.informal_essay_activity_all_number) + newWordList.size() + getString(R.string.data_unit));
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
        newWordRecyclerView.setAdapter(newWordAdapter);
        newWordAdapter.notifyDataSetChanged();
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

    @OnClick({R.id.image_view_back,
            R.id.title_bar_right_select_time,
            R.id.title_bar_right_icon_three,
            R.id.title_bar_right_icon_four})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlSuccessEvent(ExportHtmlSuccessEvent event) {
        CommonNotices.showMessage(this, getString(R.string.export_success));
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
