package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.GroupAdapter;
import com.onyx.android.dr.bean.GroupInfoBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.interfaces.ExitGroupView;
import com.onyx.android.dr.presenter.ExitGroupPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/8/28.
 */
public class ExitGroupActivity extends BaseActivity implements ExitGroupView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.exit_group_activity_rollback)
    TextView rollback;
    @Bind(R.id.exit_group_activity_exit)
    TextView exitGroup;
    @Bind(R.id.exit_group_activity_recycler_view)
    PageRecyclerView recyclerView;
    private ExitGroupPresenter exitGroupPresenter;
    private DividerItemDecoration dividerItemDecoration;
    private GroupAdapter groupAdapter;
    private List<GroupInfoBean> groupList;
    private ArrayList<Boolean> listCheck;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_exit_group;
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
        exitGroupPresenter = new ExitGroupPresenter(this);
        exitGroupPresenter.getAllGroup();
        groupList = new ArrayList<GroupInfoBean>();
        listCheck = new ArrayList<>();
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.ic_group);
        title.setText(getString(R.string.group_home_page_activity_exit));
    }

    @Override
    public void setAllGroupResult(List<GroupInfoBean> list, ArrayList<Boolean> checkList) {
        if (list == null || list.size() <= 0) {
            return;
        }
        groupList = list;
        listCheck = checkList;
        groupAdapter.setDataList(groupList, listCheck);
        recyclerView.setAdapter(groupAdapter);
    }

    public void initEvent() {
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
            R.id.exit_group_activity_rollback,
            R.id.exit_group_activity_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.exit_group_activity_exit:
                exitGroupPresenter.exitGroup();
                break;
        }
    }

    @Override
    public void setExitGroupResult(boolean result) {
        if (result) {
            ActivityManager.startGroupHomePageActivity(this);
            CommonNotices.showMessage(this, getString(R.string.exit_group_success));
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
