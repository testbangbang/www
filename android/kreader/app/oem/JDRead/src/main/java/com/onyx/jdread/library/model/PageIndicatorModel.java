package com.onyx.jdread.library.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2017/4/10.
 */

public class PageIndicatorModel extends BaseObservable {

    public final ObservableField<String> total = new ObservableField<>();
    public final ObservableInt currentPage = new ObservableInt();
    public final ObservableInt totalPage = new ObservableInt();
    private PageChangedListener listener;

    private GPaginator gPaginator;
    private String totalFormat;

    public interface PageChangedListener {
        void prev();

        void next();

        void gotoPage(int currentPage);

        void onRefresh();
    }

    public PageIndicatorModel(GPaginator gPaginator, PageChangedListener pageChangedListener) {
        this.gPaginator = gPaginator;
        updateCurrentPage();
        this.listener = pageChangedListener;
    }

    public void updateCurrentPage() {
        updateCurrentPage(gPaginator.getSize());
    }

    public void updateCurrentPage(int total) {
        if (gPaginator.pages() > 0 && gPaginator.getCurrentPage() > gPaginator.pages() - 1) {
            gPaginator.setCurrentPage(gPaginator.pages() - 1);
        } else if (gPaginator.getCurrentPage() < 0) {
            gPaginator.setCurrentPage(0);
        }
        int currentPage = gPaginator.getCurrentPage() + 1;
        int itemsPerPage = gPaginator.itemsPerPage();
        int totalPage = (int) Math.ceil(total * 1.0f / itemsPerPage);
        this.currentPage.set(totalPage == 0 ? 0 : currentPage);
        this.totalPage.set(totalPage);
        updateTotal(total);
    }

    public void updateTotal(int totalCount) {
        String total;
        if (StringUtils.isNotBlank(totalFormat)) {
            total = totalFormat + totalCount;
        } else {
            total = String.valueOf(gPaginator.getSize());
        }
        this.total.set(total);
    }

    public void resetGPaginator(GPaginator gPaginator) {
        this.gPaginator = gPaginator;
    }

    public GPaginator getPaginator() {
        return this.gPaginator;
    }

    public void setTotalFormat(String totalFormat) {
        this.totalFormat = totalFormat;
    }

    public void prev() {
        listener.prev();
    }

    public void next() {
        listener.next();
    }

    public void onRefresh() {
        listener.onRefresh();
    }

    public void onPrev(int currentPage) {
        listener.gotoPage(currentPage);
    }
}
