package com.onyx.jdread.reader.catalog.event;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.databinding.ReaderBookInfoBinding;
import com.onyx.jdread.reader.actions.GotoPositionAction;
import com.onyx.jdread.reader.catalog.dialog.ReaderBookInfoViewBack;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.PopupNoteClickEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2018/1/9.
 */

public class ReaderBookInfoDialogHandler {
    private ReaderBookInfoViewBack readerBookInfoViewBack;
    private ReaderDataHolder readerDataHolder;
    private ReaderBookInfoBinding binding;

    public ReaderBookInfoDialogHandler(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    public ReaderDataHolder getReaderDataHolder() {
        return readerDataHolder;
    }

    public void setReaderBookInfoViewBack(ReaderBookInfoViewBack readerBookInfoViewBack) {
        this.readerBookInfoViewBack = readerBookInfoViewBack;
    }

    public ReaderBookInfoBinding getBinding() {
        return binding;
    }

    public void setBinding(ReaderBookInfoBinding binding) {
        this.binding = binding;
    }

    public void registerListener() {
        if (!readerDataHolder.getEventBus().isRegistered(this)) {
            readerDataHolder.getEventBus().register(this);
        }
    }

    public void unregisterListener() {
        if (readerDataHolder.getEventBus().isRegistered(this)) {
            readerDataHolder.getEventBus().unregister(this);
        }
    }

    @Subscribe
    public void onTabCatalogClickEvent(TabCatalogClickEvent event){

    }

    @Subscribe
    public void onTabBookmarkClickEvent(TabBookmarkClickEvent event){

    }

    @Subscribe
    public void onTabNoteClickEvent(TabNoteClickEvent event){

    }

    @Subscribe
    public void onReaderBookInfoTitleBackEvent(ReaderBookInfoTitleBackEvent event){
        readerBookInfoViewBack.getContent().dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetDocumentInfoResultEvent(GetDocumentInfoResultEvent event){
        readerBookInfoViewBack.updateView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookmarkItemClickEvent(BookmarkItemClickEvent event){
        new GotoPositionAction(event.getPosition()).execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerBookInfoViewBack.getContent().dismiss();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnnotationItemClickEvent(AnnotationItemClickEvent event){

    }
}
