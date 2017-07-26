package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.InformalEssayAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.data.database.InformalEssayEntity;
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
public class InformalEssayActivity extends BaseActivity implements InformalEssayView {
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
    private InformalEssayAdapter infromalEssayAdapter;
    private InformalEssayPresenter infromalEssayPresenter;
    private List<InformalEssayEntity> infromalEssayList;
    private ArrayList<Boolean> listCheck;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_infromal_essay;
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
        infromalEssayAdapter = new InformalEssayAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        infromalEssayList = new ArrayList<InformalEssayEntity>();
        listCheck = new ArrayList<>();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        infromalEssayPresenter = new InformalEssayPresenter(getApplicationContext(), this);
        infromalEssayPresenter.getAllInformalEssayData();
    }

    @Override
    public void setInformalEssayData(List<InformalEssayEntity> dataList, ArrayList<Boolean> checkList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        infromalEssayList = dataList;
        listCheck = checkList;
        infromalEssayAdapter.setDataList(infromalEssayList, listCheck);
        recyclerView.setAdapter(infromalEssayAdapter);
    }

    public void initEvent() {
        infromalEssayAdapter.setOnItemListener(new InformalEssayAdapter.OnItemClickListener() {
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
                infromalEssayPresenter.remoteAdapterDatas(listCheck, infromalEssayAdapter);
                break;
            case R.id.infromal_essay_activity_export:
                infromalEssayPresenter.getHtmlTitle();
                break;
            case R.id.infromal_essay_activity_new:
                ActivityManager.startAddInfromalEssayActivity(this);
                break;
        }
    }

    @Override
    public void setHtmlTitleData(ArrayList<String> dataList) {
        infromalEssayPresenter.exportDataToHtml(this, dataList, infromalEssayList);
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
