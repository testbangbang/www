package com.onyx.android.dr.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.InformalEssayAdapter;
import com.onyx.android.dr.bean.MemberParameterBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.event.ExportHtmlSuccessEvent;
import com.onyx.android.dr.interfaces.InformalEssayView;
import com.onyx.android.dr.presenter.InformalEssayPresenter;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageIndicator;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class InformalEssayActivity extends BaseActivity implements InformalEssayView {
    @Bind(R.id.infromal_essay_activity_recyclerview)
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
    @Bind(R.id.title_bar_right_icon_two)
    ImageView iconTwo;
    @Bind(R.id.title_bar_right_icon_one)
    ImageView iconOne;
    @Bind(R.id.good_sentence_activity_all_check)
    CheckBox allCheck;
    @Bind(R.id.page_indicator_layout)
    RelativeLayout pageIndicatorLayout;
    private DividerItemDecoration dividerItemDecoration;
    private InformalEssayAdapter informalEssayAdapter;
    private InformalEssayPresenter informalEssayPresenter;
    private List<CreateInformalEssayBean> informalEssayList;
    private ArrayList<Boolean> listCheck;
    private PageIndicator pageIndicator;
    private String offset = "0";
    private String limit = "200";
    private String sortBy = "createdAt";
    private String order = "-1";

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_infromal_essay;
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
        informalEssayAdapter = new InformalEssayAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        informalEssayList = new ArrayList<>();
        listCheck = new ArrayList<>();
        initPageIndicator(pageIndicatorLayout);
        getIntentData();
        initEvent();
    }

    private void getIntentData() {
        image.setImageResource(R.drawable.informal_essay);
        title.setText(getString(R.string.informal_essay));
        iconFour.setVisibility(View.VISIBLE);
        iconThree.setVisibility(View.VISIBLE);
        iconTwo.setVisibility(View.VISIBLE);
        iconOne.setVisibility(View.VISIBLE);
        iconFour.setImageResource(R.drawable.ic_reader_note_delet);
        iconThree.setImageResource(R.drawable.ic_reader_share);
        iconTwo.setImageResource(R.drawable.ic_reader_note_export);
        iconOne.setImageResource(R.drawable.ic_reader_note_diary_set);
    }

    @Override
    protected void onResume() {
        super.onResume();
        informalEssayPresenter = new InformalEssayPresenter(getApplicationContext(), this);
        MemberParameterBean bean = new MemberParameterBean(offset, limit, sortBy, order);
        String json = JSON.toJSON(bean).toString();
        informalEssayPresenter.getInformalEssay(json);
    }

    @Override
    public void setInformalEssayData(List<CreateInformalEssayBean> dataList, ArrayList<Boolean> checkList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        informalEssayList = dataList;
        listCheck = checkList;
        informalEssayAdapter.setDataList(informalEssayList, listCheck);
        recyclerView.setAdapter(informalEssayAdapter);
        updatePageIndicator();
    }

    public void initEvent() {
        informalEssayAdapter.setOnItemListener(new InformalEssayAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClick(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }

            @Override
            public void setOnItemCheckedChanged(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }
        });
        allCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    for (int i = 0, j = informalEssayList.size(); i < j; i++) {
                        listCheck.set(i, true);
                    }
                } else {
                    for (int i = 0, j = informalEssayList.size(); i < j; i++) {
                        listCheck.set(i, false);
                    }
                }
                informalEssayAdapter.notifyDataSetChanged();
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
            R.id.title_bar_right_icon_three,
            R.id.title_bar_right_icon_one,
            R.id.title_bar_right_icon_two})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.title_bar_right_icon_four:
                deleteCheckedData();
                break;
            case R.id.title_bar_right_icon_three:
                informalEssayPresenter.shareInformalEssay(listCheck, informalEssayList);
                break;
            case R.id.title_bar_right_icon_two:
                exportData();
                break;
            case R.id.title_bar_right_icon_one:
                ActivityManager.startAddInformalEssayActivity(this, "", "");
                break;
        }
    }

    private void exportData() {
        if (informalEssayList.size() > 0) {
            ArrayList<String> htmlTitleData = informalEssayPresenter.getHtmlTitleData();
            informalEssayPresenter.exportDataToHtml(this, listCheck, htmlTitleData, informalEssayList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    private void deleteCheckedData() {
        if (informalEssayList.size() > 0) {
            informalEssayPresenter.remoteAdapterData(listCheck, informalEssayAdapter, informalEssayList);
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
        pagination.resize(informalEssayAdapter.getRowCount(), informalEssayAdapter.getColumnCount(), 0);
        pagination.setCurrentPage(0);
        recyclerView.setCurrentPage(0);
    }

    private QueryPagination getPagination() {
        return DRApplication.getLibraryDataHolder().getCloudViewInfo().getQueryPagination();
    }

    private void updatePageIndicator() {
        int totalCount = informalEssayAdapter.getDataCount();
        getPagination().resize(informalEssayAdapter.getRowCount(), informalEssayAdapter.getColumnCount(), totalCount);
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
    public void setInformalEssayByTime(List<InformalEssayEntity> dataList) {
    }

    @Override
    public void setInformalEssayByTitle(List<InformalEssayEntity> dataList) {
    }

    @Override
    public void createInformalEssay(boolean tag) {
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
