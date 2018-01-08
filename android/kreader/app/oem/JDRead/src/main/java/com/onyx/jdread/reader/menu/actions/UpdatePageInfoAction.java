package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.jdread.databinding.ReaderSettingMenuBinding;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.common.ReaderPageInfoFormat;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2018/1/5.
 */

public class UpdatePageInfoAction extends BaseReaderAction {
    private ReaderSettingMenuBinding binding;

    public UpdatePageInfoAction(ReaderSettingMenuBinding binding) {
        this.binding = binding;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        String bookName = ReaderPageInfoFormat.getChapterName(readerDataHolder);
        binding.readerSettingPageInfoBar.getReaderPageInfoModel().setBookName(bookName);

        float progress = ReaderPageInfoFormat.getReadProgress(readerDataHolder);
        binding.readerSettingPageInfoBar.getReaderPageInfoModel().setReadProgress(progress + "%");

        int currentPage = PagePositionUtils.getPageNumber(readerDataHolder.getReader().getReaderViewHelper().getReaderViewInfo().getFirstVisiblePage().getName());
        int total = readerDataHolder.getReader().getReaderHelper().getNavigator().getTotalPage();

        binding.readerSettingPageInfoBar.getReaderPageInfoModel().setPageTotal(total);
        binding.readerSettingPageInfoBar.getReaderPageInfoModel().setCurrentPage(currentPage);
    }
}
