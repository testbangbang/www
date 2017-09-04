package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.GroupAdapter;
import com.onyx.android.dr.bean.GroupInfoBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.interfaces.JoinGroupView;
import com.onyx.android.dr.presenter.JoinGroupPresenter;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;

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
    @Bind(R.id.join_group_activity_group_owner)
    EditText groupOwnerName;
    @Bind(R.id.join_group_activity_search_group)
    TextView searchGroup;
    @Bind(R.id.join_group_activity_group_name_check)
    CheckBox groupNameCheck;
    @Bind(R.id.join_group_activity_group_owner_check)
    CheckBox groupOwnerCheck;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.join_group_activity_rollback)
    TextView rollback;
    @Bind(R.id.join_group_activity_apply_join)
    TextView applyJoin;
    @Bind(R.id.join_group_activity_recycler_view)
    PageRecyclerView recyclerView;
    @Bind(R.id.join_group_activity_real_name)
    EditText realName;
    @Bind(R.id.join_group_activity_confirm)
    TextView confirm;
    @Bind(R.id.search_group_layout)
    LinearLayout searchGroupLayout;
    @Bind(R.id.group_result_layout)
    RelativeLayout groupResultLayout;
    @Bind(R.id.confirm_real_name_layout)
    LinearLayout confirmRealNameLayout;
    private JoinGroupPresenter joinGroupPresenter;
    private String content = "";
    private DividerItemDecoration dividerItemDecoration;
    private GroupAdapter groupAdapter;
    private List<GroupInfoBean> groupList;
    private ArrayList<Boolean> listCheck;
    private static final int STEP_SECOND = 2;
    private static final int STEP_FIRST = 1;
    private static final int STEP_THIRD = 3;
    private int step = STEP_FIRST;

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
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        groupAdapter = new GroupAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        joinGroupPresenter = new JoinGroupPresenter(this);
        groupList = new ArrayList<GroupInfoBean>();
        listCheck = new ArrayList<>();
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.ic_group);
        title.setText(getString(R.string.group_home_page_activity_join));
    }

    public void initEvent() {
        groupNameCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    groupOwnerCheck.setChecked(false);
                }
            }
        });
        groupOwnerCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    groupNameCheck.setChecked(false);
                }
            }
        });
        groupAdapter.setOnItemListener(new GroupAdapter.OnItemClickListener() {
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
            R.id.join_group_activity_rollback,
            R.id.join_group_activity_apply_join,
            R.id.join_group_activity_confirm,
            R.id.join_group_activity_search_group})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                joinGroupBack();
                break;
            case R.id.join_group_activity_rollback:
                showSearchGroupLayout();
                break;
            case R.id.join_group_activity_search_group:
                searchGroupRequest();
                break;
            case R.id.join_group_activity_apply_join:
                searchGroupLayout.setVisibility(View.GONE);
                groupResultLayout.setVisibility(View.GONE);
                confirmRealNameLayout.setVisibility(View.VISIBLE);
                step = STEP_THIRD;
                break;
            case R.id.join_group_activity_confirm:
                joinGroupConfirm();
                break;
        }
    }

    private void joinGroupBack() {
        if (step == STEP_THIRD) {
            showGroupResultLayout();
        } else if (step == STEP_SECOND) {
            showSearchGroupLayout();
        } else if (step == STEP_FIRST) {
            finish();
        }
    }

    private void searchGroupRequest() {
        if (groupNameCheck.isChecked()) {
            String recommendGroupName = groupName.getText().toString();
            if (StringUtils.isNullOrEmpty(recommendGroupName)) {
                CommonNotices.showMessage(this, getString(R.string.input_recommend_group_name));
                return;
            }
            content = recommendGroupName;
        } else if (groupOwnerCheck.isChecked()) {
            String customGroupName = groupOwnerName.getText().toString();
            if (StringUtils.isNullOrEmpty(customGroupName)) {
                CommonNotices.showMessage(this, getString(R.string.input_custom_group_name));
                return;
            }
            content = customGroupName;
        } else {
            CommonNotices.showMessage(this, getString(R.string.select_search_group_name_type));
            return;
        }
        if (!NetworkUtil.isWiFiConnected(this)) {
            connectNetwork();
            return;
        }
        joinGroupPresenter.searchGroup(content);
    }

    private void joinGroupConfirm() {
        String name = realName.getText().toString();
        if (StringUtils.isNullOrEmpty(name)) {
            CommonNotices.showMessage(this, getString(R.string.input_real_name));
            return;
        }
        if (!NetworkUtil.isWiFiConnected(this)) {
            connectNetwork();
            return;
        }
        joinGroupPresenter.joinGroup();
    }

    @Override
    public void setSearchGroupResult(List<GroupInfoBean> list, ArrayList<Boolean> checkList) {
        if (list == null || list.size() <= 0) {
            return;
        }
        searchGroupLayout.setVisibility(View.GONE);
        groupResultLayout.setVisibility(View.VISIBLE);
        confirmRealNameLayout.setVisibility(View.GONE);
        step = STEP_SECOND;
        groupList = list;
        listCheck = checkList;
        groupAdapter.setDataList(groupList, listCheck);
        recyclerView.setAdapter(groupAdapter);
    }

    @Override
    public void setJoinGroupResult(boolean result) {
        if (result) {
            ActivityManager.startGroupHomePageActivity(this);
            CommonNotices.showMessage(this, getString(R.string.join_group_success));
        }
    }

    private void showGroupResultLayout() {
        step = STEP_SECOND;
        searchGroupLayout.setVisibility(View.GONE);
        groupResultLayout.setVisibility(View.VISIBLE);
        confirmRealNameLayout.setVisibility(View.GONE);
    }

    private void showSearchGroupLayout() {
        step = STEP_FIRST;
        searchGroupLayout.setVisibility(View.VISIBLE);
        groupResultLayout.setVisibility(View.GONE);
        confirmRealNameLayout.setVisibility(View.GONE);
    }

    private void connectNetwork() {
        Device.currentDevice().enableWifiDetect(this);
        NetworkUtil.enableWiFi(this, true);
        CommonNotices.showMessage(this, getString(R.string.network_not_connected));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (step == STEP_THIRD) {
                showGroupResultLayout();
                return true;
            }
            if (step == STEP_SECOND) {
                showSearchGroupLayout();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
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
