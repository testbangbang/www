package com.onyx.android.dr.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.ReadingRateAdapter;
import com.onyx.android.dr.bean.MemberParameterBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.ReadingRateEntity;
import com.onyx.android.dr.dialog.ReadingRateDialog;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.event.ExportHtmlSuccessEvent;
import com.onyx.android.dr.interfaces.ReadingRateView;
import com.onyx.android.dr.presenter.ReadingRatePresenter;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageIndicator;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.model.CreateReadingRateBean;
import com.onyx.android.sdk.data.model.ReadingRateBean;
import com.onyx.android.sdk.data.model.v2.ShareBookReportRequestBean;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.onyx.android.dr.common.Constants.READING_RATE_DIALOG_EXPORT;
import static com.onyx.android.dr.common.Constants.READING_RATE_DIALOG_SHARE;

/**
 * Created by zhouzhiming on 2017/9/27.
 */
public class ReadingRateActivity extends BaseActivity implements ReadingRateView, ReadingRateDialog.ReadingRateDialogInterface {
    @Bind(R.id.reading_rate_activity_recycler_view)
    PageRecyclerView recyclerView;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.title_bar_right_icon_three)
    ImageView iconThree;
    @Bind(R.id.page_indicator_layout)
    RelativeLayout pageIndicatorLayout;
    private DividerItemDecoration dividerItemDecoration;
    private ReadingRateAdapter readingRateAdapter;
    private ReadingRatePresenter presenter;
    private List<CreateReadingRateBean> readingRateList;
    private ArrayList<Boolean> listCheck;
    private PageIndicator pageIndicator;
    private ReadingRateDialog timePickerDialog;
    private int type;
    private String offset = "1";
    private String limit = "200";
    private String sortBy = "createdAt";
    private String order = "-1";

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_reading_rate;
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
                new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        recyclerView.addItemDecoration(dividerItemDecoration);
        readingRateAdapter = new ReadingRateAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
    }

    @Override
    protected void initData() {
        readingRateList = new ArrayList<>();
        listCheck = new ArrayList<>();
        timePickerDialog = new ReadingRateDialog(this);
        initPageIndicator(pageIndicatorLayout);
        presenter = new ReadingRatePresenter(getApplicationContext(), this);
        MemberParameterBean bean = new MemberParameterBean(offset, limit, sortBy, order);
        String json = JSON.toJSON(bean).toString();
        presenter.getReadingRate(json);
        getIntentData();
        initEvent();
    }

    private void getIntentData() {
        image.setImageResource(R.drawable.reading_rate);
        title.setText(getString(R.string.reading_rate));
        iconFour.setImageResource(R.drawable.ic_reader_share);
        iconThree.setImageResource(R.drawable.ic_reader_note_export);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setReadingRateData(List<ReadingRateBean> dataList) {
    }

    @Override
    public void getReadingRateData(List<CreateReadingRateBean> dataList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        iconFour.setVisibility(View.VISIBLE);
        iconThree.setVisibility(View.VISIBLE);
        readingRateList = dataList;
        readingRateAdapter.setDataList(readingRateList);
        recyclerView.setAdapter(readingRateAdapter);
        updatePageIndicator();
    }

    public void initEvent() {
        readingRateAdapter.setOnItemListener(new ReadingRateAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClick(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }

            @Override
            public void setOnItemCheckedChanged(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }
        });
        recyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
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
    }

    @OnClick({R.id.title_bar_right_icon_four,
            R.id.menu_back,
            R.id.title_bar_right_icon_three})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.title_bar_right_icon_four:
                timePickerDialog.showDatePickerDialog(READING_RATE_DIALOG_SHARE);
                break;
            case R.id.title_bar_right_icon_three:
                timePickerDialog.showDatePickerDialog(READING_RATE_DIALOG_EXPORT);
                break;
        }
    }

    @Override
    public void positiveListener(int type) {
        this.type = type;
        long startDateMillisecond = timePickerDialog.getStartDateMillisecond();
        long endDateMillisecond = timePickerDialog.getEndDateMillisecond();
        String language = timePickerDialog.getLanguage();
        presenter.getDataByTimeAndType(language, startDateMillisecond, endDateMillisecond);
    }

    @Override
    public void setDataByTimeAndType(List<ReadingRateEntity> dataList, ArrayList<Boolean> listCheck) {
        if (dataList.size() > 0) {
            if (type == Constants.READING_RATE_DIALOG_EXPORT) {
                ArrayList<String> htmlTitleData = presenter.getHtmlTitleData();
                presenter.exportDataToHtml(this, htmlTitleData, dataList);
            } else if (type == Constants.READING_RATE_DIALOG_SHARE) {
                int length = dataList.size();
                ShareBookReportRequestBean shareBookReportRequestBean = new ShareBookReportRequestBean();
                String[] array = new String[]{};
                for (int i = length - 1; i >= 0; i--) {
                    ReadingRateEntity bean = dataList.get(i);
                    array = Arrays.copyOf(array, array.length + 1);
                    array[array.length - 1] = bean.cloudId;
                }
                shareBookReportRequestBean.setChildren(array);
                DRPreferenceManager.saveShareType(DRApplication.getInstance(), Constants.READING_RATE);
                ActivityManager.startShareBookReportActivity(this, "", array);
            }
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    private void initPageIndicator(ViewGroup parentView) {
        if (parentView == null) {
            return;
        }
        initPagination();
        pageIndicator = new PageIndicator(parentView.findViewById(R.id.page_indicator_layout), recyclerView.getPaginator());
        pageIndicator.showRefresh(false);
        pageIndicator.setTotalFormat(getString(R.string.total_format));
        pageIndicator.setPageChangedListener(new PageIndicator.PageChangedListener() {
            @Override
            public void prev() {
                recyclerView.prevPage();
            }

            @Override
            public void next() {
                recyclerView.nextPage();
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
        pagination.resize(readingRateAdapter.getRowCount(), readingRateAdapter.getColumnCount(), 0);
        pagination.setCurrentPage(0);
        recyclerView.setCurrentPage(0);
    }

    private QueryPagination getPagination() {
        return DRApplication.getLibraryDataHolder().getCloudViewInfo().getQueryPagination();
    }

    private void updatePageIndicator() {
        int totalCount = readingRateAdapter.getDataCount();
        getPagination().resize(readingRateAdapter.getRowCount(), readingRateAdapter.getColumnCount(), totalCount);
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
