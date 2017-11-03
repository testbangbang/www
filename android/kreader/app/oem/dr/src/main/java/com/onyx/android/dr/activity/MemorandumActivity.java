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
import com.onyx.android.dr.adapter.MemorandumAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.event.ExportHtmlSuccessEvent;
import com.onyx.android.dr.interfaces.MemorandumView;
import com.onyx.android.dr.presenter.MemorandumPresenter;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageIndicator;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.QueryPagination;
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
public class MemorandumActivity extends BaseActivity implements MemorandumView {
    @Bind(R.id.memorandum_activity_recyclerview)
    PageRecyclerView recyclerView;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.title_bar_right_icon_three)
    ImageView iconThree;
    @Bind(R.id.title_bar_right_icon_two)
    ImageView iconTwo;
    @Bind(R.id.memorandum_activity_all_check)
    CheckBox allCheck;
    @Bind(R.id.page_indicator_layout)
    RelativeLayout pageIndicatorLayout;
    private DividerItemDecoration dividerItemDecoration;
    private MemorandumAdapter memorandumAdapter;
    private MemorandumPresenter memorandumPresenter;
    private ArrayList<Boolean> listCheck;
    private List<MemorandumEntity> memorandumList;
    private PageIndicator pageIndicator;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_memorandum;
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
        memorandumAdapter = new MemorandumAdapter();
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void initData() {
        memorandumList = new ArrayList<>();
        listCheck = new ArrayList<>();
        initPageIndicator(pageIndicatorLayout);
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.memorandum);
        title.setText(getString(R.string.memorandum));
        iconTwo.setVisibility(View.VISIBLE);
        iconFour.setVisibility(View.VISIBLE);
        iconThree.setVisibility(View.VISIBLE);
        iconFour.setImageResource(R.drawable.ic_reader_note_delet);
        iconThree.setImageResource(R.drawable.ic_reader_note_export);
        iconTwo.setImageResource(R.drawable.ic_reader_note_diary_set);
    }

    @Override
    protected void onResume() {
        super.onResume();
        memorandumPresenter = new MemorandumPresenter(getApplicationContext(), this);
        memorandumPresenter.getAllMemorandumData();
    }

    @Override
    public void setMemorandumData(List<MemorandumEntity> dataList, ArrayList<Boolean> checkList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        memorandumList = dataList;
        listCheck = checkList;
        memorandumAdapter.setDataList(memorandumList, listCheck);
        recyclerView.setAdapter(memorandumAdapter);
        updatePageIndicator();
    }

    public void initEvent() {
        memorandumAdapter.setOnItemListener(new MemorandumAdapter.OnItemClickListener() {
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
                    for (int i = 0, j = memorandumList.size(); i < j; i++) {
                        listCheck.set(i, true);
                    }
                } else {
                    for (int i = 0, j = memorandumList.size(); i < j; i++) {
                        listCheck.set(i, false);
                    }
                }
                memorandumAdapter.notifyDataSetChanged();
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

    @OnClick({R.id.menu_back,
            R.id.title_bar_right_icon_four,
            R.id.title_bar_right_icon_two,
            R.id.title_bar_right_icon_three})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.title_bar_right_icon_four:
                deleteCheckedData();
                break;
            case R.id.title_bar_right_icon_three:
                exportData();
                break;
            case R.id.title_bar_right_icon_two:
                ActivityManager.startAddMemorandumActivity(this);
                break;
        }
    }

    private void exportData() {
        if (memorandumList.size() > 0) {
            ArrayList<String> htmlTitleData = memorandumPresenter.getHtmlTitle();
            memorandumPresenter.exportDataToHtml(this, listCheck, htmlTitleData, memorandumList);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    private void deleteCheckedData() {
        if (memorandumList.size() > 0) {
            memorandumPresenter.remoteAdapterData(listCheck, memorandumAdapter, memorandumList);
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
        pagination.resize(memorandumAdapter.getRowCount(), memorandumAdapter.getColumnCount(), 0);
        pagination.setCurrentPage(0);
        recyclerView.setCurrentPage(0);
    }

    private QueryPagination getPagination() {
        return DRApplication.getLibraryDataHolder().getCloudViewInfo().getQueryPagination();
    }

    private void updatePageIndicator() {
        int totalCount = memorandumAdapter.getDataCount();
        getPagination().resize(memorandumAdapter.getRowCount(), memorandumAdapter.getColumnCount(), totalCount);
        pageIndicator.resetGPaginator(getPagination());
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
