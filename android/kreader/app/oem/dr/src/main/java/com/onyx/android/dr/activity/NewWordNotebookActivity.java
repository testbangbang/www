package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.NewWordAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.dialog.TimePickerDialog;
import com.onyx.android.dr.interfaces.NewWordView;
import com.onyx.android.dr.presenter.NewWordPresenter;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

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
    @Bind(R.id.new_word_activity_delete)
    TextView delete;
    @Bind(R.id.new_word_activity_export)
    TextView export;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    private DividerItemDecoration dividerItemDecoration;
    private NewWordAdapter newWordAdapter;
    private NewWordPresenter newWordPresenter;
    private List<NewWordNoteBookEntity> newWordList;
    private ArrayList<Boolean> listCheck;
    private int dictType;
    private TimePickerDialog timePickerDialog;

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
        newWordList = new ArrayList<NewWordNoteBookEntity>();
        listCheck = new ArrayList<>();
        timePickerDialog = new TimePickerDialog(this);
        loadNewWordData();
        initEvent();
    }

    private void loadNewWordData() {
        dictType = getIntent().getIntExtra(Constants.DICTTYPE, -1);
        newWordPresenter = new NewWordPresenter(getApplicationContext(), this);
        newWordPresenter.getAllNewWordByType(dictType);
        initTitleData();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.new_word_notebook);
        if (dictType == Constants.ENGLISH_TYPE) {
            title.setText(getString(R.string.english_new_word_notebook));
        } else if (dictType == Constants.CHINESE_TYPE) {
            title.setText(getString(R.string.chinese_new_word_notebook));
        } else if (dictType == Constants.OTHER_TYPE) {
            title.setText(getString(R.string.dict_query_japanese_language));
        }
    }

    @Override
    public void setNewWordData(List<NewWordNoteBookEntity> dataList, ArrayList<Boolean> checkList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        newWordList = dataList;
        listCheck = checkList;
        newWordAdapter.setDataList(newWordList, listCheck);
        newWordRecyclerView.setAdapter(newWordAdapter);

        for (int i = 0; i < newWordList.size(); i++) {
            NewWordNoteBookEntity temp = newWordList.get(i);
            System.out.println("@@@时间:" + TimeUtils.getTime(temp.currentTime));
        }
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
                newWordPresenter.remoteAdapterDatas(listCheck, newWordAdapter);
                break;
            case R.id.new_word_activity_export:
                timePickerDialog.showDatePickerDialog();
                break;
        }
    }

    @Override
    public void positiveListener() {
        long startDateMillisecond = timePickerDialog.getStartDateMillisecond();
        long endDateMillisecond = timePickerDialog.getEndDateMillisecond();
        newWordPresenter.getNewWordByTime(startDateMillisecond, endDateMillisecond);
    }

    @Override
    public void setNewWordByTime(List<NewWordNoteBookEntity> dataList) {
        if (dataList != null && dataList.size() > 0) {
            ArrayList<String> htmlTitleData = newWordPresenter.getHtmlTitle();
            newWordPresenter.exportDataToHtml(this, htmlTitleData, dataList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
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
