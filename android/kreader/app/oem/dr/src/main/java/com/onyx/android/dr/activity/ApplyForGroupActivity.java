package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.ManageGroupAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.event.ExportHtmlSuccessEvent;
import com.onyx.android.dr.interfaces.ManageGroupView;
import com.onyx.android.dr.presenter.ManageGroupPresenter;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.data.model.DeleteGroupMemberBean;
import com.onyx.android.sdk.data.model.v2.AllGroupBean;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/10/9.
 */
public class ApplyForGroupActivity extends BaseActivity implements ManageGroupView {
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.group_manager_activity_recycler_view)
    PageRecyclerView recyclerView;
    private ManageGroupPresenter presenter;
    private ManageGroupAdapter manageGroupAdapter;
    private List<AllGroupBean> groupList;

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
        manageGroupAdapter = new ManageGroupAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
    }

    @Override
    protected void initData() {
        presenter = new ManageGroupPresenter(this);
        presenter.getAllGroup();
        groupList = new ArrayList<>();
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.ic_group);
        title.setText(getString(R.string.group_manager_activity_manage_group));
    }

    @Override
    public void setGroupMemberResult(List<AllGroupBean> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        groupList = list;
        manageGroupAdapter.setDataList(this, groupList, presenter);
        recyclerView.setAdapter(manageGroupAdapter);
    }

    @Override
    public void setExitGroupResult(DeleteGroupMemberBean result) {
        if (result != null) {
            CommonNotices.showMessage(this, getString(R.string.exit_success));
            String position = DRPreferenceManager.getExitGroupPosition(this, "");
            Integer tag = Integer.valueOf(position);
            groupList.remove(tag);
            manageGroupAdapter.notifyItemRemoved(tag);
        } else {
            CommonNotices.showMessage(this, getString(R.string.exit_failed));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlSuccessEvent(ExportHtmlSuccessEvent event) {
        CommonNotices.showMessage(this, getString(R.string.export_success));
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
