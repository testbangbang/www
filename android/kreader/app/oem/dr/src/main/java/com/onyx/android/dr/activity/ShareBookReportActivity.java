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
import com.onyx.android.dr.interfaces.ShareBookReportView;
import com.onyx.android.dr.presenter.ShareBookReportPresenter;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.model.v2.GroupBean;

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
    private ShareBookReportAdapter adapter;
    private ShareBookReportPresenter shareBookReportPresenter;

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
        titleBarRightSelectTime.setVisibility(View.VISIBLE);
        titleBarRightSelectTime.setText(getResources().getString(R.string.infromal_essay_activity_share));
        shareBookReportRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        shareBookReportRecycler.addItemDecoration(dividerItemDecoration);
        adapter = new ShareBookReportAdapter();
        shareBookReportRecycler.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        shareBookReportPresenter = new ShareBookReportPresenter(this);
        shareBookReportPresenter.getAllGroup();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.image_view_back)
    public void onViewClicked() {
    }

    @Override
    public void setGroupData(List<GroupBean> groups) {
        adapter.setData(groups);
    }
}
