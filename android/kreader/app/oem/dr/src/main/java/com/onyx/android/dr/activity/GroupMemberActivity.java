package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.GroupMemberAdapter;
import com.onyx.android.dr.bean.MemberParameterBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.dialog.AlertInfoDialog;
import com.onyx.android.dr.event.ManageEvent;
import com.onyx.android.dr.interfaces.GroupMemberView;
import com.onyx.android.dr.presenter.GroupMemberPresenter;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.sdk.data.model.DeleteGroupMemberBean;
import com.onyx.android.sdk.data.model.v2.GroupMemberBean;
import com.onyx.android.sdk.data.model.v2.ListBean;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/8/28.
 */
public class GroupMemberActivity extends BaseActivity implements GroupMemberView {
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.group_member_manage_activity_delete)
    TextView deleteMember;
    @Bind(R.id.group_member_activity_search_container)
    RelativeLayout searchContainer;
    @Bind(R.id.group_member_activity_all_number)
    TextView allNumber;
    @Bind(R.id.group_member_activity_member_name)
    EditText memberName;
    @Bind(R.id.group_member_activity_search)
    ImageView search;
    @Bind(R.id.group_member_manage_activity_recycler_view)
    PageRecyclerView recyclerView;
    private GroupMemberPresenter groupMemberManagePresenter;
    private GroupMemberAdapter groupMemberAdapter;
    private List<ListBean> groupList;
    private String offset = "1";
    private String limit = "200";
    private String sortBy = "createdAt";
    private String order = "-1";
    private ArrayList<Boolean> listCheck;
    private AlertInfoDialog alertDialog;
    private String id;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_group_member;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        groupMemberAdapter = new GroupMemberAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
    }

    @Override
    protected void initData() {
        groupList = new ArrayList<>();
        listCheck = new ArrayList<>();
        getIntentData();
        initTitleData();
        initEvent();
    }

    private void getIntentData() {
        id = getIntent().getStringExtra(Constants.GROUP_ID);
        groupMemberManagePresenter = new GroupMemberPresenter(this);
        MemberParameterBean bean = new MemberParameterBean(offset, limit, sortBy, order);
        String json = JSON.toJSON(bean).toString();
        groupMemberManagePresenter.getGroupMember(id, json);
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.ic_group);
        title.setText(getString(R.string.group_home_page_activity_manage));
    }

    @Override
    public void setGroupMemberResult(GroupMemberBean bean) {
        if (bean.list == null || bean.list.size() <= 0) {
            return;
        }
        groupList.clear();
        listCheck.clear();
        groupList = bean.list;
        allNumber.setText(getString(R.string.member_number) + bean.list.size() + getString(R.string.people_unit));
        listCheck = groupMemberManagePresenter.getListCheck(groupList);
        groupMemberAdapter.isShow = false;
        groupMemberAdapter.setMenuDataList(groupList, listCheck);
        recyclerView.setAdapter(groupMemberAdapter);
    }

    public void initEvent() {
        groupMemberAdapter.setOnItemListener(new GroupMemberAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClick(int position, boolean isCheck) {
                if (deleteMember.getText().equals(getString(R.string.delete))) {
                    listCheck.set(position, isCheck);
                }
            }

            @Override
            public void setOnItemCheckedChanged(int position, boolean isCheck) {
                if (deleteMember.getText().equals(getString(R.string.delete))) {
                    listCheck.set(position, isCheck);
                }
            }
        });
    }

    @OnClick({R.id.menu_back,
            R.id.group_member_activity_search,
            R.id.group_member_manage_activity_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case com.onyx.android.dr.R.id.group_member_manage_activity_delete:
                manageMember();
                break;
            case com.onyx.android.dr.R.id.group_member_activity_search:
                searchMember();
                break;
        }
    }

    private void searchMember() {
        String keyWord = memberName.getText().toString();
        groupMemberManagePresenter.searchGroupMember(id, keyWord);
    }

    private void manageMember() {
        String text = deleteMember.getText().toString();
        if (text.equals(getString(R.string.manage))) {
            EventBus.getDefault().post(new ManageEvent());
        } else if (text.equals(getString(R.string.delete))) {
            showDialog();
        }
    }

    private void showDialog() {
        alertDialog = new AlertInfoDialog(this, getString(R.string.delete_group_member_hint), true,
                getResources().getString(R.string.dialog_button_confirm), getResources().getString(R.string.dialog_button_cancel));
        Utils.setDialogAttributes(alertDialog);
        alertDialog.setOKOnClickListener(new AlertInfoDialog.OnOKClickListener() {
            @Override
            public void onOKClick(int value) {
                groupMemberManagePresenter.remoteAdapterData(id, listCheck, groupMemberAdapter, groupList);
                List<ListBean> data = groupMemberManagePresenter.getData(listCheck, groupList);
                allNumber.setText(getString(R.string.member_number) + (groupList.size() - data.size())+ getString(R.string.people_unit));
            }
        });
        alertDialog.setCancelOnClickListener(new AlertInfoDialog.OnCancelClickListener() {
            @Override
            public void onCancelClick() {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void setDeleteGroupMemberResult(DeleteGroupMemberBean bean) {
        if (bean != null) {
            CommonNotices.showMessage(this, getString(R.string.delete_group_member_success));
        }else{
            CommonNotices.showMessage(this, getString(R.string.delete_group_member_failed));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onManageEvent(ManageEvent event) {
        deleteMember.setText(getString(R.string.delete));
        searchContainer.setVisibility(View.GONE);
        groupMemberAdapter.isShow = true;
        groupMemberAdapter.notifyDataSetChanged();
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
