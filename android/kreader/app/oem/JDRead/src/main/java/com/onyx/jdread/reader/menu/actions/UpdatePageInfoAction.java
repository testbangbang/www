package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.reader.common.ReaderViewInfo;
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
    private boolean isInit = false;

    public UpdatePageInfoAction(ReaderSettingMenuBinding binding,ReaderViewInfo readerViewInfo,boolean isInit) {
        this.binding = binding;
        this.readerViewInfo = readerViewInfo;
        this.isInit = isInit;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        String bookName = ReaderPageInfoFormat.getChapterName(readerDataHolder);
        binding.readerSettingPageInfoBar.getReaderPageInfoModel().setBookName(bookName);

        int currentPage = readerDataHolder.getCurrentPage() + 1;
        int total = readerViewInfo.getTotalPage();
        String readProgress = ReaderPageInfoFormat.getReadProgress(readerViewInfo);
        binding.readerSettingPageInfoBar.getReaderPageInfoModel().setReadProgress(readProgress);
        if(isInit) {
            binding.readerSettingPageInfoBar.getReaderPageInfoModel().setPageTotal(total);
            binding.readerSettingPageInfoBar.getReaderPageInfoModel().setCurrentPage(currentPage);
        }
    }
}
