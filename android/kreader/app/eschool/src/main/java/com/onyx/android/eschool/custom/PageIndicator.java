package com.onyx.android.eschool.custom;

import android.view.View;
import android.widget.TextView;

import com.onyx.android.eschool.R;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.utils.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/4/10.
 */

public class PageIndicator {

    @Bind(R.id.total_tv)
    TextView totalText;
    @Bind(R.id.page_indicator)
    TextView currentPageView;

    private GPaginator gPaginator;
    private PageChangedListener pageChangedListener;
    private String totalFormat;

    public interface PageChangedListener {
        void prev();

        void next();

        void gotoPage(int currentPage);
    }

    public PageIndicator(View indicatorView, GPaginator gPaginator) {
        this.gPaginator = gPaginator;
        ButterKnife.bind(this, indicatorView);
    }

    public void updateCurrentPage() {
        updateCurrentPage(gPaginator.getSize());
    }

    public void updateCurrentPage(int total) {
        int currentPage = gPaginator.getCurrentPage() + 1;
        int itemsPerPage = gPaginator.itemsPerPage();
        int totalPage = total / itemsPerPage;
        if (totalPage * itemsPerPage < total) {
            totalPage++;
        }
        if (totalPage == 0) {
            totalPage = 1;
        }
        if (currentPage > totalPage) {
            currentPage = 1;
            gPaginator.setCurrentPage(0);
        }
        currentPageView.setText(currentPage + "/" + totalPage);
    }

    public void updateTotal(int totalCount) {
        String total;
        if (StringUtils.isNotBlank(totalFormat)) {
            total = String.format(totalFormat, totalCount);
        } else {
            total = String.valueOf(gPaginator.getSize());
        }
        totalText.setText(total);
    }

    public void resetGPaginator(GPaginator gPaginator) {
        this.gPaginator = gPaginator;
    }

    public void setTotalFormat(String totalFormat) {
        this.totalFormat = totalFormat;
    }

    public void setPageChangedListener(PageChangedListener pageChangedListener) {
        this.pageChangedListener = pageChangedListener;
    }

    @OnClick(R.id.prev)
    void onPrevClick() {
        if (pageChangedListener != null) {
            pageChangedListener.prev();
        }
    }

    @OnClick(R.id.next)
    void onNextClick() {
        if (pageChangedListener != null) {
            pageChangedListener.next();
        }
    }

    @OnClick(R.id.page_indicator)
    void onGotoPageClick() {
        if (pageChangedListener != null) {
            pageChangedListener.gotoPage(gPaginator.getCurrentPage());
        }
    }
}
