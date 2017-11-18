package com.onyx.android.dr.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.onyx.android.dr.dialog.SelectAlertDialog;
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
    private String offset = "0";
    private String limit = "200";
    private String sortBy = "createdAt";
    private String order = "-1";
    private ArrayList<Boolean> listCheck;
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
        recyclerView.setAdapter(groupMemberAdapter);
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
        groupList.clear();
        listCheck.clear();
        if (bean.list == null || bean.list.size() <= 0) {
            allNumber.setText(getString(R.string.member_number) + getString(R.string.zero_people));
            return;
        }
        groupList = bean.list;
        allNumber.setText(getString(R.string.member_number) + groupList.size() + getString(R.string.people_unit));
        listCheck = groupMemberManagePresenter.getListCheck(groupList);
        groupMemberAdapter.isShow = false;
        groupMemberAdapter.setMenuDataList(groupList, listCheck);
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
            case R.id.group_member_manage_activity_delete:
                manageMember();
                break;
            case R.id.group_member_activity_search:
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
            loadDialog();
        }
    }

    private void loadDialog() {
        LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.dialog_delete_group_member, null);
        final SelectAlertDialog selectTimeDialog = new SelectAlertDialog(this);
        // find id
        TextView title = (TextView) view.findViewById(R.id.dialog_title);
        Button confirm = (Button) view.findViewById(R.id.dialog_button_confirm);
        Button cancel = (Button) view.findViewById(R.id.dialog_button_cancel);
        //set data
        title.setText(DRApplication.getInstance().getString(R.string.delete_group_member_hint));
        WindowManager.LayoutParams attributes = selectTimeDialog.getWindow().getAttributes();
        Float heightProportion = Float.valueOf(getString(R.string.dialog_delete_group_member_height));
        Float widthProportion = Float.valueOf(getString(R.string.application_note_dialog_width));
        attributes.height = (int) (Utils.getScreenHeight(DRApplication.getInstance()) * heightProportion);
        attributes.width = (int) (Utils.getScreenWidth(DRApplication.getInstance()) * widthProportion);
        selectTimeDialog.getWindow().setAttributes(attributes);
        selectTimeDialog.setView(view);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupMemberManagePresenter.remoteAdapterData(id, listCheck, groupMemberAdapter, groupList);
                List<ListBean> data = groupMemberManagePresenter.getData(listCheck, groupList);
                allNumber.setText(getString(R.string.member_number) + (groupList.size() - data.size())+ getString(R.string.people_unit));
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectTimeDialog.isShowing()) {
                    selectTimeDialog.dismiss();
                }
                groupMemberAdapter.isShow = false;
                groupMemberAdapter.notifyDataSetChanged();
                deleteMember.setText(getString(R.string.delete));
            }
        });
        selectTimeDialog.show();
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
