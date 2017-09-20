package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.ManageGroupAdapter;
import com.onyx.android.dr.bean.GroupMemberBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.interfaces.ManageGroupView;
import com.onyx.android.dr.presenter.ManageGroupPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/9/19.
 */
public class ManageGroupActivity extends BaseActivity implements ManageGroupView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.group_manager_activity_recycler_view)
    PageRecyclerView recyclerView;
    private ManageGroupPresenter presenter;
    private ManageGroupAdapter manageGroupAdapter;
    private List<GroupMemberBean> groupList;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_group_manage;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        manageGroupAdapter = new ManageGroupAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
    }

    @Override
    protected void initData() {
        presenter = new ManageGroupPresenter(this);
        presenter.getGroupMember();
        groupList = new ArrayList<>();
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.ic_group);
        title.setText(getString(R.string.group_manager_activity_manage_group));
    }

    @Override
    public void setGroupMemberResult(List<GroupMemberBean> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        groupList = list;
        manageGroupAdapter.setDataList(groupList);
        recyclerView.setAdapter(manageGroupAdapter);
    }

    public void initEvent() {
    }

    @OnClick({R.id.image_view_back,
            R.id.group_member_manage_activity_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.group_member_manage_activity_delete:
                presenter.deleteGroupMember();
                break;
        }
    }

    @Override
    public void setDeleteGroupMemberResult(boolean result) {
        if (result) {
            ActivityManager.startGroupHomePageActivity(this);
            CommonNotices.showMessage(this, getString(R.string.delete_group_member_success));
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
