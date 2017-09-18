package com.onyx.android.dr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.BookReportListAdapter;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by li on 2017/9/15.
 */

public class BookReportListActivity extends BaseActivity {
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
    @Bind(R.id.book_report_list_recycle)
    PageRecyclerView bookReportListRecycle;
    @Bind(R.id.book_report_list_total_size)
    TextView bookReportListTotalSize;
    @Bind(R.id.book_report_list_page)
    TextView bookReportListPage;
    private BookReportListAdapter bookReportListAdapter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.book_report_list_layout;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        titleBarTitle.setText(getResources().getString(R.string.reader_response));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        bookReportListRecycle.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        bookReportListRecycle.addItemDecoration(dividerItemDecoration);
        bookReportListAdapter = new BookReportListAdapter();
        bookReportListRecycle.setAdapter(bookReportListAdapter);
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.image_view_back, R.id.title_bar_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                break;
            case R.id.title_bar_title:
                break;
        }
    }
}
