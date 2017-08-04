package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.InformalEssayAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
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
    private DividerItemDecoration dividerItemDecoration;
    private InformalEssayAdapter informalEssayAdapter;
    private InformalEssayPresenter informalEssayPresenter;
    private List<InformalEssayEntity> informalEssayList;
    private ArrayList<Boolean> listCheck;
    private TimePickerDialog timePickerDialog;

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
        initEvent();
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
                listCheck.set(position, isCheck);
            }

            @Override
            public void setOnItemCheckedChanged(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
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
                informalEssayPresenter.remoteAdapterDatas(listCheck, informalEssayAdapter);
                break;
            case R.id.infromal_essay_activity_export:
                timePickerDialog.showDatePickerDialog();
                break;
            case R.id.infromal_essay_activity_new:
                ActivityManager.startAddInfromalEssayActivity(this);
                break;
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
