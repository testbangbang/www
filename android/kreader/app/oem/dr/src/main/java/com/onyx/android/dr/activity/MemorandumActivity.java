package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.MemorandumAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.interfaces.MemorandumView;
import com.onyx.android.dr.presenter.MemorandumPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class MemorandumActivity extends BaseActivity implements MemorandumView {
    @Bind(R.id.memorandum_activity_recyclerview)
    PageRecyclerView recyclerView;
    @Bind(R.id.memorandum_activity_delete)
    TextView deleteInfromalEssay;
    @Bind(R.id.memorandum_activity_new)
    TextView newInfromalEssay;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    private DividerItemDecoration dividerItemDecoration;
    private MemorandumAdapter memorandumAdapter;
    private MemorandumPresenter memorandumPresenter;
    private List<MemorandumEntity> memorandumList;
    private ArrayList<Boolean> listCheck;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_memorandum;
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
        memorandumAdapter = new MemorandumAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        memorandumList = new ArrayList<MemorandumEntity>();
        listCheck = new ArrayList<>();
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.memorandum);
        title.setText(getString(R.string.memorandum));
    }

    @Override
    protected void onResume() {
        super.onResume();
        memorandumPresenter = new MemorandumPresenter(getApplicationContext(), this);
        memorandumPresenter.getAllMemorandumData();
    }

    @Override
    public void setMemorandumData(List<MemorandumEntity> dataList, ArrayList<Boolean> checkList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        memorandumList = dataList;
        listCheck = checkList;
        memorandumAdapter.setDataList(memorandumList, listCheck);
        recyclerView.setAdapter(memorandumAdapter);
    }

    public void initEvent() {
        memorandumAdapter.setOnItemListener(new MemorandumAdapter.OnItemClickListener() {
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

    @OnClick({R.id.image_view_back,
            R.id.memorandum_activity_delete,
            R.id.memorandum_activity_new})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.memorandum_activity_delete:
                if (memorandumList.size() > 0) {
                    memorandumPresenter.remoteAdapterDatas(listCheck, memorandumAdapter);
                } else {
                    CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
                }
                break;
            case R.id.memorandum_activity_new:
                ActivityManager.startAddMemorandumActivity(this);
                break;
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
