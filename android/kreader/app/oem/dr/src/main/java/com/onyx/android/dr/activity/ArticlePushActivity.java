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
import com.onyx.android.dr.adapter.ArticlePushAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.dialog.ExportSuccessHintDialog;
import com.onyx.android.dr.event.ArticlePushEnterEBookEvent;
import com.onyx.android.dr.interfaces.ArticlePushView;
import com.onyx.android.dr.presenter.ArticlePushPresenter;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageIndicator;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.model.ArticleInfoBean;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.utils.CollectionUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by zhouzhiming on 17-7-11.
 */
public class ArticlePushActivity extends BaseActivity implements ArticlePushView {
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.title_bar_right_checkbox_container)
    LinearLayout checkBoxContainer;
    @Bind(R.id.title_bar_right_checkbox)
    CheckBox checkBox;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_right_icon_one)
    ImageView titleBarRightIconOne;
    @Bind(R.id.activity_article_push_recycler)
    PageRecyclerView recyclerView;
    @Bind(R.id.page_indicator_layout)
    RelativeLayout pageIndicatorLayout;
    private ArticlePushAdapter listAdapter;
    private ArticlePushPresenter presenter;
    private PageIndicator pageIndicator;
    private ExportSuccessHintDialog hintDialog;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_article_push;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        recyclerView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        recyclerView.addItemDecoration(dividerItemDecoration);
        listAdapter = new ArticlePushAdapter();
        recyclerView.setAdapter(listAdapter);
        setData();
        initEvent();
    }

    private void setData() {
        titleBarTitle.setText(getString(R.string.menu_article_push));
        image.setImageResource(R.drawable.ic_push);
        titleBarRightIconOne.setVisibility(View.VISIBLE);
        checkBoxContainer.setVisibility(View.VISIBLE);
        titleBarRightIconOne.setImageResource(R.drawable.ic_reader_note_delet);
        initPageIndicator(pageIndicatorLayout);
        presenter = new ArticlePushPresenter(this);
        presenter.getArticleList();
    }

    private void initEvent() {
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

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listAdapter.selectAll(isChecked);
            }
        });
    }

    @Override
    protected void initData() {
        hintDialog = new ExportSuccessHintDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setDataList(List<ArticleInfoBean> summaryList) {
        listAdapter.setReadSummaryList(summaryList);
        updatePageIndicator();
    }

    @OnClick({R.id.menu_back,
            R.id.title_bar_right_icon_one})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                ActivityManager.startMainActivity(this);
                break;
            case R.id.title_bar_right_icon_one:
                delete();
                break;
        }
    }

    private void delete() {
        List<ArticleInfoBean> selectedList = listAdapter.getSelectedList();
        if (CollectionUtils.isNullOrEmpty(selectedList)) {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
            return;
        }
        presenter.removeArticle(selectedList);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArticlePushEnterEBookEvent(ArticlePushEnterEBookEvent event) {
        ActivityManager.startEBookStoreActivity(this);
    }

    private void initPagination() {
        QueryPagination pagination = getPagination();
        pagination.resize(listAdapter.getRowCount(), listAdapter.getColumnCount(), 0);
        pagination.setCurrentPage(0);
        recyclerView.setCurrentPage(0);
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
}
