package com.onyx.android.dr.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.SpeechRecordingAdapter;
import com.onyx.android.dr.adapter.SpeechRecordingKeywordAdapter;
import com.onyx.android.dr.bean.MemberParameterBean;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.event.SearchKeywordEvent;
import com.onyx.android.dr.interfaces.SpeechRecordingView;
import com.onyx.android.dr.presenter.SpeechRecordingPresenter;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageIndicator;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by zhouzhiming on 2017/9/6.
 */
public class SpeechRecordingFragment extends BaseFragment implements SpeechRecordingView {
    @Bind(R.id.fragment_speech_recording_recycler_view)
    PageRecyclerView recyclerView;
    @Bind(R.id.page_indicator_layout)
    RelativeLayout pageIndicatorLayout;
    private SpeechRecordingAdapter speechRecordingAdapter;
    private SpeechRecordingKeywordAdapter speechRecordingKeywordAdapter;
    private SpeechRecordingPresenter presenter;
    private List<CreateInformalEssayBean> informalEssayList;
    private List<InformalEssayEntity> informalEssayKeywordList;
    private int jumpSource = 0;
    private String offset = "0";
    private String limit = "200";
    private String sortBy = "createdAt";
    private String order = "-1";
    private PageIndicator pageIndicator;

    @Override
    protected void initListener() {
    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_speech_recording;
    }

    @Override
    protected void initView(View rootView) {
        initRecyclerView();
    }

    private void initRecyclerView() {
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        speechRecordingAdapter = new SpeechRecordingAdapter();
        speechRecordingKeywordAdapter = new SpeechRecordingKeywordAdapter();
    }

    @Override
    protected void loadData() {
        EventBus.getDefault().register(this);
        initPageIndicator(pageIndicatorLayout);
        loadInformalEssay();
        initEvent();
    }

    private void loadInformalEssay() {
        presenter = new SpeechRecordingPresenter(getActivity(), this);
        MemberParameterBean bean = new MemberParameterBean(offset, limit, sortBy, order);
        String json = JSON.toJSON(bean).toString();
        presenter.getInformalEssay(json);
        informalEssayList = new ArrayList<>();
        informalEssayKeywordList = new ArrayList<>();
    }

    @Override
    public void setInformalEssayData(List<CreateInformalEssayBean> dataList, ArrayList<Boolean> checkList) {
        showData(dataList);
    }

    @Override
    public void setInformalEssayByTitle(List<InformalEssayEntity> dataList) {
        showKeywordData(dataList);
    }

    private void showData(List<CreateInformalEssayBean> dataList) {
        if (dataList != null && !dataList.isEmpty()) {
            informalEssayList.clear();
            informalEssayList = dataList;
        }
        speechRecordingAdapter.setDataList(informalEssayList);
        recyclerView.setAdapter(speechRecordingAdapter);
        speechRecordingAdapter.notifyDataSetChanged();
        updatePageIndicator();
    }

    private void showKeywordData(List<InformalEssayEntity> dataList) {
        if (dataList != null && !dataList.isEmpty()) {
            informalEssayList.clear();
            informalEssayKeywordList.clear();
            informalEssayKeywordList = dataList;
        }
        speechRecordingKeywordAdapter.setDataList(informalEssayKeywordList);
        recyclerView.setAdapter(speechRecordingKeywordAdapter);
        speechRecordingKeywordAdapter.notifyDataSetChanged();
        updatePageIndicator();
    }

    public void initEvent() {
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
        pagination.resize(speechRecordingAdapter.getRowCount(), speechRecordingAdapter.getColumnCount(), 0);
        pagination.setCurrentPage(0);
        recyclerView.setCurrentPage(0);
    }

    private QueryPagination getPagination() {
        return DRApplication.getLibraryDataHolder().getCloudViewInfo().getQueryPagination();
    }

    private void updatePageIndicator() {
        int totalCount = speechRecordingAdapter.getDataCount();
        getPagination().resize(speechRecordingAdapter.getRowCount(), speechRecordingAdapter.getColumnCount(), totalCount);
        pageIndicator.resetGPaginator(getPagination());
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchKeywordEvent(SearchKeywordEvent event) {
        String searchKeyword = DRPreferenceManager.getSearchKeyword(getActivity(), "");
        if (!StringUtils.isNullOrEmpty(searchKeyword)) {
            presenter.getInformalEssayQueryByTitle(searchKeyword);
        }
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
