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
import com.onyx.android.dr.adapter.AnnotationListAdapter;
import com.onyx.android.dr.bean.AnnotationStatisticsBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.event.ExportHtmlSuccessEvent;
import com.onyx.android.dr.interfaces.AnnotationView;
import com.onyx.android.dr.presenter.AnnotationListPresenter;
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
public class AnnotationListActivity extends BaseActivity implements AnnotationView {
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
    @Bind(R.id.annotation_list_all_select)
    CheckBox annotationListAllSelect;
    @Bind(R.id.annotation_list_all_recycler)
    PageRecyclerView annotationListRecycler;
    @Bind(R.id.page_indicator_layout)
    RelativeLayout pageIndicatorLayout;
    private AnnotationListAdapter listAdapter;
    private AnnotationListPresenter presenter;
    private PageIndicator pageIndicator;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_annotation_list;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        annotationListRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        annotationListRecycler.addItemDecoration(dividerItemDecoration);
        listAdapter = new AnnotationListAdapter();
        annotationListRecycler.setAdapter(listAdapter);

        titleBarTitle.setText(getString(R.string.postil));
        image.setImageResource(R.drawable.ic_reader_note_remark);
        titleBarRightIconOne.setVisibility(View.VISIBLE);
        titleBarRightIconTwo.setVisibility(View.VISIBLE);
        titleBarRightIconOne.setImageResource(R.drawable.ic_reader_note_export);
        titleBarRightIconTwo.setImageResource(R.drawable.ic_reader_note_delet);
        initPageIndicator(pageIndicatorLayout);
        annotationListRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
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

        annotationListAllSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listAdapter.selectAll(isChecked);
            }
        });
    }

    @Override
    protected void initData() {
        presenter = new AnnotationListPresenter(this);
        presenter.getAnnotationList();
    }

    @Override
    public void setAnnotationList(List<AnnotationStatisticsBean> AnnotationList) {
        listAdapter.setReadAnnotationList(AnnotationList);
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
        List<AnnotationStatisticsBean> selectedList = listAdapter.getSelectedList();
        if (CollectionUtils.isNullOrEmpty(selectedList)) {
            return;
        }
        presenter.removeAnnotation(selectedList);
    }

    private void exportData() {
        List<AnnotationStatisticsBean> selectedList = listAdapter.getSelectedList();
        if (selectedList.size() > 0) {
            ArrayList<String> htmlTitleData = presenter.getHtmlTitleData();
            presenter.exportDataToHtml(htmlTitleData, selectedList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
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
                annotationListRecycler.prevPage();
            }

            @Override
            public void next() {
                annotationListRecycler.nextPage();
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
        annotationListRecycler.setCurrentPage(0);
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
