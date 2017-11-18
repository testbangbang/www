package com.onyx.android.dr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.ShareToMemberAdapter;
import com.onyx.android.dr.bean.MemberParameterBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.ShareFailedEvent;
import com.onyx.android.dr.event.ShareSuccessEvent;
import com.onyx.android.dr.interfaces.ShareBookReportView;
import com.onyx.android.dr.presenter.ShareBookReportPresenter;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.model.v2.GroupBean;
import com.onyx.android.sdk.data.model.v2.GroupMemberBean;
import com.onyx.android.sdk.data.model.v2.ListBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by li on 2017/9/28.
 */

public class ShareToMemberActivity extends BaseActivity implements ShareBookReportView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_right_select_time)
    TextView titleBarRightSelectTime;
    @Bind(R.id.title_bar_right_icon_one)
    ImageView titleBarRightIconOne;
    @Bind(R.id.title_bar_right_icon_two)
    ImageView titleBarRightIconTwo;
    @Bind(R.id.title_bar_right_icon_three)
    ImageView titleBarRightIconThree;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView titleBarRightIconFour;
    @Bind(R.id.title_bar_right_shopping_cart)
    TextView titleBarRightShoppingCart;
    @Bind(R.id.title_bar_right_container)
    LinearLayout titleBarRightContainer;
    @Bind(R.id.title_bar_right_image)
    ImageView titleBarRightImage;
    @Bind(R.id.title_bar_right_edit_text)
    EditText titleBarRightEditText;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;
    @Bind(R.id.share_to_member_count)
    TextView shareToMemberCount;
    @Bind(R.id.share_to_member_recycler)
    PageRecyclerView shareToMemberRecycler;
    private String offset = "0";
    private String limit = "200";
    private String sortBy = "createdAt";
    private String order = "-1";
    private ShareToMemberAdapter adapter;
    private ShareBookReportPresenter presenter;
    private String impressionId;
    private String[] childrenId;
    private int shareType;
    private int number = 0;

    @Override
    protected Integer getLayoutId() {
        return R.layout.share_to_member_layout;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        titleBarRightIconOne.setVisibility(View.VISIBLE);
        image.setVisibility(View.GONE);
        titleBarRightIconOne.setImageResource(R.drawable.ic_reader_share);
        shareToMemberRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        shareToMemberRecycler.addItemDecoration(dividerItemDecoration);
        adapter = new ShareToMemberAdapter();
        shareToMemberRecycler.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        presenter = new ShareBookReportPresenter(this);
        String id = getIntent().getStringExtra(Constants.GROUP_ID);
        String groupName = getIntent().getStringExtra(Constants.GROUP_NAME);
        impressionId = getIntent().getStringExtra(Constants.IMPRESSION_ID);
        childrenId = getIntent().getStringArrayExtra(Constants.CHILDREN_ID);
        shareType = DRPreferenceManager.getShareType(this, -1);
        titleBarTitle.setText(groupName);
        MemberParameterBean bean = new MemberParameterBean(offset, limit, sortBy, order);
        String json = JSON.toJSON(bean).toString();
        presenter.getGroupMember(id, json);
    }

    @Override
    public void setGroupData(List<GroupBean> groups) {
    }

    @Override
    public void setGroupMemberResult(GroupMemberBean groupMembers) {
        List<ListBean> list = groupMembers.list;
        if (list != null && list.size() > 0) {
            shareToMemberCount.setText(String.format(getResources().getString(R.string.share_to_member_count), list.size()));
            adapter.setData(list);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.image_view_back, R.id.title_bar_title, R.id.title_bar_right_icon_one})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.title_bar_title:
                finish();
                break;
            case R.id.title_bar_right_icon_one:
                shareToMember();
                break;
        }
    }

    private void shareToMember() {
        if (adapter == null) {
            return;
        }

        List<ListBean> selectedData = adapter.getSelectedData();
        if (selectedData == null || selectedData.size() == 0) {
            CommonNotices.showMessage(DRApplication.getInstance(), getResources().getString(R.string.share_to_member_no_share));
            return;
        }
        for (ListBean bean : selectedData) {
            if (shareType == Constants.INFORMAL_ESSAY) {
                presenter.shareInformalEssay(bean.child.library, childrenId);
            } else if (shareType == Constants.READING_RATE) {
                presenter.shareReadingRate(bean.child.library, childrenId);
            } else if (shareType == Constants.READER_RESPONSE) {
                presenter.shareImpression(bean.child.library, childrenId);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShareSuccessEvent(ShareSuccessEvent event) {
        CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance()
                .getResources().getString(R.string.share_book_impression_success));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShareFailedEvent(ShareFailedEvent event) {
        if (number < Constants.REQUEST_NUMBER) {
            number++;
            shareToMember();
        } else {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance()
                    .getResources().getString(R.string.share_book_impression_fail));
        }
    }
}
