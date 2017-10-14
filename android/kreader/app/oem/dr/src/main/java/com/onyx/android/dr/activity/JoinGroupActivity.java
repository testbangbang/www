package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.GroupAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.interfaces.JoinGroupView;
import com.onyx.android.dr.presenter.JoinGroupPresenter;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.sdk.data.model.v2.JoinGroupBean;
import com.onyx.android.sdk.data.model.v2.SearchGroupBean;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/8/28.
 */
public class JoinGroupActivity extends BaseActivity implements JoinGroupView {
    @Bind(R.id.join_group_activity_group_name)
    EditText groupName;
    @Bind(R.id.join_group_activity_search_group)
    ImageView searchGroup;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.join_group_activity_recycler_view)
    PageRecyclerView recyclerView;
    @Bind(R.id.join_group_activity_all_number)
    TextView allNumber;
    private JoinGroupPresenter joinGroupPresenter;
    private DividerItemDecoration dividerItemDecoration;
    private GroupAdapter groupAdapter;
    private List<SearchGroupBean> groupList;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_join_group;
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
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        groupAdapter = new GroupAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        joinGroupPresenter = new JoinGroupPresenter(this);
        groupList = new ArrayList<>();
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.ic_group);
        title.setText(getString(R.string.group_home_page_activity_join));
    }

    public void initEvent() {
    }

    @OnClick({R.id.menu_back,
            R.id.join_group_activity_search_group})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.join_group_activity_search_group:
                searchGroupRequest();
                break;
        }
    }

    private void searchGroupRequest() {
        String recommendGroupName = groupName.getText().toString();
        if (StringUtils.isNullOrEmpty(recommendGroupName)) {
            CommonNotices.showMessage(this, getString(R.string.input_group_name_hint));
            return;
        }
        if (!NetworkUtil.isWiFiConnected(this)){
            connectNetwork();
            return;
        }
        joinGroupPresenter.searchGroup(recommendGroupName);
    }

    @Override
    public void setSearchGroupResult(List<SearchGroupBean> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        groupList = list;
        allNumber.setText(getString(R.string.join_group_activity_all_number) + groupList.size() + getString(R.string.data_unit));
        groupAdapter.setDataList(groupList, joinGroupPresenter);
        recyclerView.setAdapter(groupAdapter);
    }

    @Override
    public void setJoinGroupResult(List<JoinGroupBean> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        CommonNotices.showMessage(this, getString(R.string.join_group_success));
    }

    private void connectNetwork() {
        Device.currentDevice().enableWifiDetect(this);
        NetworkUtil.enableWiFi(this, true);
        CommonNotices.showMessage(this, getString(R.string.network_not_connected));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlFailedEvent(ExportHtmlFailedEvent event) {
        CommonNotices.showMessage(this, getString(R.string.apply_join_failed));
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
