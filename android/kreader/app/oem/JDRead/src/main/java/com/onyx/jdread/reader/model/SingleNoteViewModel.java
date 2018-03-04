package com.onyx.jdread.reader.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.util.Log;

import com.onyx.jdread.databinding.ActivitySingleLineBinding;
import com.onyx.jdread.reader.dialog.SinglelineViewCallBack;
import com.onyx.jdread.reader.dialog.ViewCallBack;
import com.onyx.jdread.reader.ui.view.PageTextView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/22.
 */

public class SingleNoteViewModel {
    private ActivitySingleLineBinding binding;
    private ObservableField<String> page = new ObservableField<>();
    private ObservableBoolean isShowPage = new ObservableBoolean(false);
    private EventBus eventBus;
    private SinglelineViewCallBack singlelineViewCallBack;

    public SingleNoteViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ObservableField<String> getPage() {
        return page;
    }

    public ObservableBoolean getIsShowPage() {
        return isShowPage;
    }

    public void setIsShowPage(boolean isShowPage) {
        this.isShowPage.set(isShowPage);
    }

    public void setPage(String page) {
        this.page.set(page);
    }

    public void setText(String text) {
        binding.translateView.setText(text);
    }

    public void setBinding(ActivitySingleLineBinding binding) {
        this.binding = binding;

        binding.translateView.setOnPagingListener(new PageTextView.OnPagingListener() {
            @Override
            public void onPageChange(int currentPage, int totalPage) {
                updatePageNumber(totalPage, currentPage);
            }
        });
    }

    private void updatePageNumber(int totalPage, int curPage) {
        setPage(curPage + "/" + totalPage);
    }

    public void setSinglelineViewCallBack(SinglelineViewCallBack singlelineViewCallBack) {
        this.singlelineViewCallBack = singlelineViewCallBack;
    }
}
