package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.databinding.ReaderSettingMenuBinding;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.common.ReaderPageInfoFormat;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2018/1/5.
 */

public class UpdatePageInfoAction extends BaseReaderAction {
    private ReaderSettingMenuBinding binding;
    private ReaderViewInfo readerViewInfo;

    public UpdatePageInfoAction(ReaderSettingMenuBinding binding,ReaderViewInfo readerViewInfo) {
        this.binding = binding;
        this.readerViewInfo = readerViewInfo;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        String bookName = ReaderPageInfoFormat.getChapterName(readerDataHolder);
        binding.readerSettingPageInfoBar.getReaderPageInfoModel().setBookName(bookName);

        float progress = ReaderPageInfoFormat.getReadProgress(readerDataHolder,readerViewInfo);
        binding.readerSettingPageInfoBar.getReaderPageInfoModel().setReadProgress(progress + "%");

        int currentPage = PagePositionUtils.getPageNumber(readerViewInfo.getFirstVisiblePage().getName());
        int total = readerDataHolder.getReader().getReaderHelper().getNavigator().getTotalPage();

        binding.readerSettingPageInfoBar.getReaderPageInfoModel().setPageTotal(total);
        binding.readerSettingPageInfoBar.getReaderPageInfoModel().setCurrentPage(currentPage);
    }
}
