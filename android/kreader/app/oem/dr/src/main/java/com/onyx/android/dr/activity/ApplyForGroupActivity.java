package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.ApplyForGroupAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.interfaces.ApplyForGroupView;
import com.onyx.android.dr.presenter.ApplyForGroupPresenter;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.sdk.data.model.v2.ChangePendingGroupBean;
import com.onyx.android.sdk.data.model.v2.PendingGroupBean;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/10/9.
 */
public class ApplyForGroupActivity extends BaseActivity implements ApplyForGroupView {
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.apply_for_group_activity_recycler_view)
    PageRecyclerView recyclerView;
    @Bind(R.id.apply_for_group_activity_all_number)
    TextView allNumber;
    private ApplyForGroupPresenter presenter;
    private ApplyForGroupAdapter applyForGroupAdapter;
    private List<PendingGroupBean> groupList;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_apply_for_group;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        recyclerView.addItemDecoration(dividerItemDecoration);
        applyForGroupAdapter = new ApplyForGroupAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
    }

    @Override
    protected void initData() {
        presenter = new ApplyForGroupPresenter(this);
        presenter.getPendingGroups();
        groupList = new ArrayList<>();
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.ic_group);
        title.setText(getString(R.string.group_manager_activity_manage_group));
    }

    @Override
    public void setGetPendingGroupResult(List<PendingGroupBean> list) {
        if (list == null || list.size() <= 0) {
            allNumber.setText(getString(R.string.apply_for_group_activity_application) + getString(R.string.zero_data_unit));
            return;
        }
        groupList = list;
        allNumber.setText(getString(R.string.apply_for_group_activity_application) + groupList.size() + getString(R.string.data_unit));
        applyForGroupAdapter.setDataList(this, groupList, presenter);
        recyclerView.setAdapter(applyForGroupAdapter);
    }

    @Override
    public void setDisposePendingGroupResult(ChangePendingGroupBean result) {
        if (result != null) {
            if (result.status == Constants.PASS_TAG) {
                CommonNotices.showMessage(this, getString(R.string.pass_success));
            }else if(result.status == Constants.REFUSE_TAG) {
                CommonNotices.showMessage(this, getString(R.string.refuse_success));
            }
        } else {
            CommonNotices.showMessage(this, getString(R.string.pass_failed));
        }
    }

    public void initEvent() {
    }

    @OnClick({R.id.menu_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
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
