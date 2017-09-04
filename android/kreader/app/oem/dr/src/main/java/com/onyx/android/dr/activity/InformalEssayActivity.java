package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.InformalEssayAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.dialog.TimePickerDialog;
import com.onyx.android.dr.interfaces.InformalEssayView;
import com.onyx.android.dr.presenter.InformalEssayPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class InformalEssayActivity extends BaseActivity implements InformalEssayView, TimePickerDialog.TimePickerDialogInterface {
    @Bind(R.id.infromal_essay_activity_recyclerview)
    PageRecyclerView recyclerView;
    @Bind(R.id.infromal_essay_activity_share)
    TextView shareInformalEssay;
    @Bind(R.id.infromal_essay_activity_export)
    TextView exportInformalEssay;
    @Bind(R.id.infromal_essay_activity_delete)
    TextView deleteInformalEssay;
    @Bind(R.id.infromal_essay_activity_new)
    TextView newInformalEssay;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.informal_essay_activity_bottom)
    RelativeLayout bottomContainer;
    private DividerItemDecoration dividerItemDecoration;
    private InformalEssayAdapter informalEssayAdapter;
    private InformalEssayPresenter informalEssayPresenter;
    private List<InformalEssayEntity> informalEssayList;
    private ArrayList<Boolean> listCheck;
    private TimePickerDialog timePickerDialog;
    private int jumpSource = 0;
    private String informalEssayContent = "";

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_infromal_essay;
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
        informalEssayAdapter = new InformalEssayAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        informalEssayList = new ArrayList<InformalEssayEntity>();
        listCheck = new ArrayList<>();
        timePickerDialog = new TimePickerDialog(this);
        getIntentData();
        initEvent();
    }

    private void getIntentData() {
        jumpSource = getIntent().getIntExtra(Constants.JUMP_SOURCE, -1);
        if (jumpSource == Constants.MY_NOTE_TO_INFORMAL_ESSAY) {
            bottomContainer.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.informal_essay);
            title.setText(getString(R.string.informal_essay));
        } else if (jumpSource == Constants.RECORD_TIME_SETTING_TO_INFORMAL_ESSAY) {
            bottomContainer.setVisibility(View.GONE);
            image.setImageResource(R.drawable.speech_recording);
            title.setText(getString(R.string.speech_recording));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        informalEssayPresenter = new InformalEssayPresenter(getApplicationContext(), this);
        informalEssayPresenter.getAllInformalEssayData();
    }

    @Override
    public void setInformalEssayData(List<InformalEssayEntity> dataList, ArrayList<Boolean> checkList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        informalEssayList = dataList;
        listCheck = checkList;
        informalEssayAdapter.setDataList(informalEssayList, listCheck);
        recyclerView.setAdapter(informalEssayAdapter);
    }

    public void initEvent() {
        informalEssayAdapter.setOnItemListener(new InformalEssayAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClick(int position, boolean isCheck) {
                if (jumpSource == Constants.MY_NOTE_TO_INFORMAL_ESSAY) {
                    listCheck.set(position, isCheck);
                } else if (jumpSource == Constants.RECORD_TIME_SETTING_TO_INFORMAL_ESSAY) {
                    informalEssayContent = informalEssayList.get(position).content;
                    ActivityManager.startSpeechRecordingActivity(InformalEssayActivity.this, informalEssayContent);
                }
            }

            @Override
            public void setOnItemCheckedChanged(int position, boolean isCheck) {
                if (jumpSource == Constants.MY_NOTE_TO_INFORMAL_ESSAY) {
                    listCheck.set(position, isCheck);
                }
            }
        });
    }

    @OnClick({R.id.infromal_essay_activity_share,
            R.id.image_view_back,
            R.id.infromal_essay_activity_delete,
            R.id.infromal_essay_activity_new,
            R.id.infromal_essay_activity_export})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.infromal_essay_activity_delete:
                removeData();
                break;
            case R.id.infromal_essay_activity_export:
                timePickerDialog.showDatePickerDialog();
                break;
            case R.id.infromal_essay_activity_new:
                ActivityManager.startAddInformalEssayActivity(this);
                break;
        }
    }

    private void removeData() {
        if (informalEssayList.size() > 0) {
            informalEssayPresenter.remoteAdapterDatas(listCheck, informalEssayAdapter);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    @Override
    public void positiveListener() {
        long startDateMillisecond = timePickerDialog.getStartDateMillisecond();
        long endDateMillisecond = timePickerDialog.getEndDateMillisecond();
        informalEssayPresenter.getInformalEssayByTime(startDateMillisecond, endDateMillisecond);
    }

    @Override
    public void setInformalEssayByTime(List<InformalEssayEntity> dataList) {
        if (dataList != null && dataList.size() > 0) {
            ArrayList<String> htmlTitleData = informalEssayPresenter.getHtmlTitleData();
            informalEssayPresenter.exportDataToHtml(this, htmlTitleData, dataList);
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
