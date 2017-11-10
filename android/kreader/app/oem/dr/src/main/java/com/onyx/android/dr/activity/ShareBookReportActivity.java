package com.onyx.android.dr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.ShareBookReportAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.interfaces.ShareBookReportView;
import com.onyx.android.dr.presenter.ShareBookReportPresenter;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.model.v2.GroupBean;
import com.onyx.android.sdk.data.model.v2.GroupMemberBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by li on 2017/9/27.
 */

public class ShareBookReportActivity extends BaseActivity implements ShareBookReportView {
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
    @Bind(R.id.share_book_report_recycler)
    PageRecyclerView shareBookReportRecycler;
    @Bind(R.id.activity_dict_result_no_data_hint)
    TextView noDataHint;
    private ShareBookReportAdapter adapter;
    private ShareBookReportPresenter shareBookReportPresenter;
    private String impressionId;
    private String[] childrenId;
    private int shareType;

    @Override
    protected Integer getLayoutId() {
        return R.layout.share_book_report_layout;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        titleBarTitle.setText(getResources().getString(R.string.share_book_impression_to_group));
        image.setImageResource(R.drawable.ic_group);
        shareBookReportRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        shareBookReportRecycler.addItemDecoration(dividerItemDecoration);
        adapter = new ShareBookReportAdapter();
        shareBookReportRecycler.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        impressionId = getIntent().getStringExtra(Constants.IMPRESSION_ID);
        childrenId = getIntent().getStringArrayExtra(Constants.CHILDREN_ID);
        shareType = DRPreferenceManager.getShareType(this, -1);
        shareBookReportPresenter = new ShareBookReportPresenter(this);
        shareBookReportPresenter.getAllGroup();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    public void setGroupData(List<GroupBean> groups) {
        if (groups == null || groups.size() <= 0) {
            titleBarRightIconOne.setVisibility(View.GONE);
            shareBookReportRecycler.setVisibility(View.GONE);
            noDataHint.setVisibility(View.VISIBLE);
            noDataHint.setText(getString(R.string.no_groups_hint));
        } else {
            titleBarRightIconOne.setVisibility(View.VISIBLE);
            shareBookReportRecycler.setVisibility(View.VISIBLE);
            noDataHint.setVisibility(View.GONE);
            titleBarRightIconOne.setImageResource(R.drawable.ic_reader_share);
            adapter.setData(groups, impressionId, childrenId);
        }
    }

    @Override
    public void setGroupMemberResult(GroupMemberBean groupMembers) {

    }

    @OnClick({R.id.image_view_back, R.id.image, R.id.title_bar_title, R.id.title_bar_right_icon_one})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.image:
                finish();
                break;
            case R.id.title_bar_title:
                finish();
                break;
            case R.id.title_bar_right_icon_one:
                shareToGroup();
                break;
        }
    }

    private void shareToGroup() {
        if (adapter == null) {
            return;
        }
        List<GroupBean> selectData = adapter.getSelectData();
        if (selectData == null || selectData.size() == 0) {
            CommonNotices.showMessage(DRApplication.getInstance(), getResources().getString(R.string.share_to_member_no_share));
            return;
        }
        for (GroupBean bean : selectData) {
            if (shareType == Constants.INFORMAL_ESSAY) {
                shareBookReportPresenter.shareInformalEssay(bean.library, childrenId);
            } else if (shareType == Constants.READING_RATE) {
                shareBookReportPresenter.shareReadingRate(bean.library, childrenId);
            } else if (shareType == Constants.READER_RESPONSE) {
                shareBookReportPresenter.shareImpression(bean.library, childrenId);
            }
        }
    }
}
