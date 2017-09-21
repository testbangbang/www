package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.SummaryListAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.interfaces.SummaryView;
import com.onyx.android.dr.presenter.SummaryListPresenter;
import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.dr.reader.event.ReadingSummaryMenuEvent;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by zhouzhiming on 17-7-11.
 */
public class SummaryListActivity extends BaseActivity implements SummaryView {

    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_right_icon_one)
    ImageView titleBarRightIconOne;
    @Bind(R.id.title_bar_right_icon_two)
    ImageView titleBarRightIconTwo;
    @Bind(R.id.summary_list_all_select)
    CheckBox summaryListAllSelect;
    @Bind(R.id.summary_list_all_recycler)
    PageRecyclerView summaryListRecycler;
    private SummaryListAdapter listAdapter;
    private SummaryListPresenter presenter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_summary_list;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        summaryListRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        summaryListRecycler.addItemDecoration(dividerItemDecoration);
        listAdapter = new SummaryListAdapter();
        summaryListRecycler.setAdapter(listAdapter);

        titleBarTitle.setText(getString(R.string.read_summary));
        image.setImageResource(R.drawable.ic_read_summary);
        titleBarRightIconOne.setVisibility(View.VISIBLE);
        titleBarRightIconTwo.setVisibility(View.VISIBLE);
        titleBarRightIconOne.setImageResource(R.drawable.ic_reader_share);
        titleBarRightIconTwo.setImageResource(R.drawable.ic_reader_choose_delet);
    }

    @Override
    protected void initData() {
        presenter = new SummaryListPresenter(this);
        presenter.getSummaryList();
    }

    @Override
    public void setSummaryList(List<ReadSummaryEntity> summaryList) {
        listAdapter.setReadSummaryList(summaryList);
    }

    @OnClick({R.id.menu_back, R.id.title_bar_right_icon_one, R.id.title_bar_right_icon_two})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.title_bar_right_icon_one:
                share();
                break;
            case R.id.title_bar_right_icon_two:
                delete();
                break;
        }
    }

    private void delete() {
        List<ReadSummaryEntity> selectedList = listAdapter.getSelectedList();
        if (CollectionUtils.isNullOrEmpty(selectedList)) {
            return;
        }
        presenter.removeSummary(selectedList);
    }

    private void share() {
        // TODO: 17-9-20 share
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadingSummaryMenuEvent(ReadingSummaryMenuEvent event) {
        String[] strings = new String[2];
        strings[0] = event.getBookName();
        strings[1] = event.getPageNumber();
        ActivityManager.startReadSummaryActivity(this, strings);
    }
}
