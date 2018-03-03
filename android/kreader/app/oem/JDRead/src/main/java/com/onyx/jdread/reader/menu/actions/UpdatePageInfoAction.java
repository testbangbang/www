package com.onyx.jdread.reader.menu.actions;

import android.util.Log;

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

        float progress = ReaderPageInfoFormat.getReadProgress(readerDataHolder,readerViewInfo);

        int currentPage = readerDataHolder.getCurrentPage();
        int total = readerDataHolder.getReader().getReaderHelper().getNavigator().getTotalPage() - 1;

        if(isInit) {
            if(!readerViewInfo.canNextScreen){
                progress = 100;
                currentPage = total;
            }
            binding.readerSettingPageInfoBar.getReaderPageInfoModel().setReadProgress(progress + "%");
            binding.readerSettingPageInfoBar.getReaderPageInfoModel().setPageTotal(total);
            binding.readerSettingPageInfoBar.getReaderPageInfoModel().setCurrentPage(currentPage);
        }
    }
}
