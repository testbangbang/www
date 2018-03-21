package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.utils.ChapterInfo;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.reader.utils.TocUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.reader.actions.GotoPageAction;
import com.onyx.jdread.reader.actions.GotoPositionAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.actions.GetTableOfContentAction;
import com.onyx.jdread.reader.menu.event.GotoPageEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemNextChapterEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemPreviousChapterEvent;
import com.onyx.jdread.reader.menu.event.UpdateProgressEvent;
import com.onyx.jdread.reader.menu.request.GetTableOfContentRequest;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderPageInfoModel {
    private ObservableField<String> bookName = new ObservableField<>();
    private ObservableField<String> readProgress = new ObservableField<>();
    private ObservableInt pageTotal = new ObservableInt(0);
    private ObservableInt currentPage = new ObservableInt(0);
    private ObservableBoolean isShow = new ObservableBoolean(true);
    private ReaderDataHolder readerDataHolder;
    private List<ChapterInfo> tocChapterNodeList;
    private static boolean hasChapterInfo = true;

    public ReaderPageInfoModel(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
        resetReaderMenu();
    }

    public void resetReaderMenu() {
        if (tocChapterNodeList != null) {
            tocChapterNodeList.clear();
            tocChapterNodeList = null;
        }
    }

    public static void setHasChapterInfo(boolean hasChapterInfo) {
        ReaderPageInfoModel.hasChapterInfo = hasChapterInfo;
    }

    public EventBus getEventBus() {
        return readerDataHolder.getEventBus();
    }

    public ObservableField<String> getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName.set(bookName.trim());
    }

    public ObservableField<String> getReadProgress() {
        return readProgress;
    }

    public void setReadProgress(String readProgress) {
        this.readProgress.set(readProgress);
    }

    public ObservableInt getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal.set(pageTotal);
    }

    public ObservableInt getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage.set(currentPage);
    }

    public void nextChapter() {
        if(!checkLoadingState()){
            return;
        }
        if (hasChapterInfo) {
            prepareGotoChapter(readerDataHolder, false);
        } else {
            getEventBus().post(new ReaderSettingMenuItemNextChapterEvent());
        }
    }

    private boolean checkLoadingState(){
        if(!readerDataHolder.getReaderViewInfo().isLoadComplete()){
            ToastUtil.showToast(ResManager.getString(R.string.reader_loading));
            return false;
        }
        return true;
    }

    public void previousChapter() {
        if(!checkLoadingState()){
            return;
        }
        if (hasChapterInfo) {
            prepareGotoChapter(readerDataHolder, true);
        } else {
            getEventBus().post(new ReaderSettingMenuItemPreviousChapterEvent());
        }
    }

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow.set(isShow);
    }

    public List<ChapterInfo> getTocChapterNodeList() {
        return tocChapterNodeList;
    }

    public void setTocChapterNodeList(List<ChapterInfo> tocChapterNodeList) {
        this.tocChapterNodeList = tocChapterNodeList;
    }

    private void prepareGotoChapter(final ReaderDataHolder readerDataHolder, final boolean back) {
        List<ChapterInfo> tocChapterNodeList = getTocChapterNodeList();
        if (tocChapterNodeList == null || tocChapterNodeList.size() <= 0) {
            new GetTableOfContentAction().execute(readerDataHolder, new RxCallback() {
                @Override
                public void onNext(Object request) {
                    GetTableOfContentRequest readerRequest = (GetTableOfContentRequest) request;
                    boolean hasToc = readerRequest.isHasToc();
                    if (!hasToc) {
                        if (back) {
                            getEventBus().post(new ReaderSettingMenuItemPreviousChapterEvent());
                        } else {
                            getEventBus().post(new ReaderSettingMenuItemNextChapterEvent());
                        }
                        hasChapterInfo = false;
                        return;
                    }
                    List<ChapterInfo> readTocChapterNodeList = readerRequest.getReadTocChapterNodeList();
                    setTocChapterNodeList(readTocChapterNodeList);
                    gotoChapter(readerDataHolder, back, readTocChapterNodeList);
                }
            });
        } else {
            gotoChapter(readerDataHolder, back, tocChapterNodeList);
        }
    }

    private void gotoChapter(final ReaderDataHolder readerDataHolder, final boolean back, final List<ChapterInfo> tocChapterNodeList) {
        if (tocChapterNodeList.size() <= 0) {
            return;
        }
        int currentPagePosition = PagePositionUtils.getPosition(readerDataHolder.getCurrentPagePosition());
        ChapterInfo chapterInfo = tocChapterNodeList.get(0);
        if (back && currentPagePosition <= chapterInfo.getPosition()) {
            ToastUtil.showToast(readerDataHolder.getAppContext(), R.string.first_chapter);
            return;
        }

        chapterInfo = tocChapterNodeList.get(tocChapterNodeList.size() - 1);
        if (!back && currentPagePosition >= chapterInfo.getPosition()) {
            ToastUtil.showToast(readerDataHolder.getAppContext(), R.string.last_chapter);
            return;
        }

        int chapterPosition;
        if (back) {
            chapterPosition = getChapterPositionByPage(currentPagePosition, back, tocChapterNodeList);
        } else {
            chapterPosition = getChapterPositionByPage(currentPagePosition, back, tocChapterNodeList);
        }
        gotoPosition(readerDataHolder, chapterPosition, true);
    }

    public static int getChapterPositionByPage(int pagePosition, boolean back, List<ChapterInfo> tocChapterNodeList) {
        if (tocChapterNodeList.size() <= 0) {
            return pagePosition;
        }

        int i;
        int size = tocChapterNodeList.size();

        for (i = 0; i < size; i++) {
            ChapterInfo nextChapter = tocChapterNodeList.get(i);
            if (pagePosition < nextChapter.getPosition()) {
                if (!back) {
                    return nextChapter.getPosition();
                }

                int index = i - 2; // goto previous chapter
                if (index <= 0) {
                    return 0;
                }
                return tocChapterNodeList.get(index).getPosition();
            }
        }

        return pagePosition;
    }

    private void gotoPosition(final ReaderDataHolder readerDataHolder, Object object, final boolean abortPendingTasks) {
        if (object == null) {
            return;
        }
        int page = (int) object;
        new GotoPositionAction(page).execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerDataHolder.getEventBus().post(new UpdateProgressEvent());
            }
        });
    }
}
