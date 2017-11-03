package com.onyx.android.dr.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.SummaryListAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.event.ExportHtmlSuccessEvent;
import com.onyx.android.dr.interfaces.SummaryView;
import com.onyx.android.dr.presenter.SummaryListPresenter;
import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.dr.reader.event.ReadingSummaryMenuEvent;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageIndicator;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.utils.CollectionUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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
    @Bind(R.id.page_indicator_layout)
    RelativeLayout pageIndicatorLayout;
    private SummaryListAdapter listAdapter;
    private SummaryListPresenter presenter;
    private PageIndicator pageIndicator;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_summary_list;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        summaryListRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        summaryListRecycler.addItemDecoration(dividerItemDecoration);
        listAdapter = new SummaryListAdapter();
        summaryListRecycler.setAdapter(listAdapter);

        titleBarTitle.setText(getString(R.string.read_summary));
        image.setImageResource(R.drawable.ic_read_summary);
        titleBarRightIconOne.setVisibility(View.VISIBLE);
        titleBarRightIconTwo.setVisibility(View.VISIBLE);
        titleBarRightIconOne.setImageResource(R.drawable.ic_reader_note_export);
        titleBarRightIconTwo.setImageResource(R.drawable.ic_reader_note_delet);
        initPageIndicator(pageIndicatorLayout);
        summaryListRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPrevPage(int prevPosition, int itemCount, int pageSize) {
                getPagination().prevPage();
                updatePageIndicator();
            }

            @Override
            public void onNextPage(int nextPosition, int itemCount, int pageSize) {
                getPagination().nextPage();
                updatePageIndicator();
            }
        });

        summaryListAllSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listAdapter.selectAll(isChecked);
            }
        });
    }

    @Override
    protected void initData() {
        presenter = new SummaryListPresenter(this);
        presenter.getSummaryList();
    }

    @Override
    public void setSummaryList(List<ReadSummaryEntity> summaryList) {
        listAdapter.setReadSummaryList(summaryList);
        updatePageIndicator();
    }

    @OnClick({R.id.menu_back, R.id.title_bar_right_icon_one, R.id.title_bar_right_icon_two})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.title_bar_right_icon_one:
                exportData();
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

    private void exportData() {
        List<ReadSummaryEntity> selectedList = listAdapter.getSelectedList();
        if (selectedList.size() > 0) {
            ArrayList<String> htmlTitleData = presenter.getHtmlTitleData();
            presenter.exportDataToHtml(htmlTitleData, selectedList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadingSummaryMenuEvent(ReadingSummaryMenuEvent event) {
        String[] strings = new String[2];
        strings[0] = event.getBookName();
        strings[1] = event.getPageNumber();
        ActivityManager.startReadSummaryActivity(this, strings);
    }

    private void initPageIndicator(ViewGroup parentView) {
        if (parentView == null) {
            return;
        }
        initPagination();
        pageIndicator = new PageIndicator(parentView.findViewById(R.id.page_indicator_layout), getPagination());
        pageIndicator.showRefresh(false);
        pageIndicator.setTotalFormat(getString(R.string.total_format));
        pageIndicator.setPageChangedListener(new PageIndicator.PageChangedListener() {
            @Override
            public void prev() {
                summaryListRecycler.prevPage();
            }

            @Override
            public void next() {
                summaryListRecycler.nextPage();
            }

            @Override
            public void gotoPage(int page) {
            }
        });
        pageIndicator.setDataRefreshListener(new PageIndicator.DataRefreshListener() {
            @Override
            public void onRefresh() {
            }
        });
    }

    private void initPagination() {
        QueryPagination pagination = getPagination();
        pagination.resize(listAdapter.getRowCount(), listAdapter.getColumnCount(), 0);
        pagination.setCurrentPage(0);
        summaryListRecycler.setCurrentPage(0);
    }

    private QueryPagination getPagination() {
        return DRApplication.getLibraryDataHolder().getCloudViewInfo().getQueryPagination();
    }

    private void updatePageIndicator() {
        int totalCount = listAdapter.getDataCount();
        getPagination().resize(listAdapter.getRowCount(), listAdapter.getColumnCount(), totalCount);
        pageIndicator.resetGPaginator(getPagination());
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlSuccessEvent(ExportHtmlSuccessEvent event) {
        CommonNotices.showMessage(this, getString(R.string.has_exported_to) + event.getFilePath());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlFailedEvent(ExportHtmlFailedEvent event) {
        CommonNotices.showMessage(this, getString(R.string.export_failed));
    }
}
