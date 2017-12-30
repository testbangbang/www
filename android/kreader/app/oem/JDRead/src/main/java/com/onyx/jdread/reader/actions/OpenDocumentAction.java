package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.OpenDocumentFailResultEvent;
import com.onyx.jdread.reader.request.OpenDocumentRequest;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 17/11/13.
 */

public class OpenDocumentAction extends BaseReaderAction {
    private ReaderDataHolder readerDataHolder;

    public OpenDocumentAction(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        OpenDocumentRequest openDocumentRequest = new OpenDocumentRequest(readerDataHolder);
        OpenDocumentRequest.setAppContext(JDReadApplication.getInstance());
        openDocumentRequest.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                analysisOpenDocumentSuccessResult();
            }

            @Override
            public void onError(Throwable throwable) {
                analysisOpenDocumentErrorResult(throwable);
            }
        });
    }

    private void analysisOpenDocumentSuccessResult(){
        InitPageViewAction createPageViewAction = new InitPageViewAction();
        createPageViewAction.execute(readerDataHolder);
    }

    private void analysisOpenDocumentErrorResult(Throwable throwable) {
        OpenDocumentFailResultEvent event = new OpenDocumentFailResultEvent();
        String message = throwable.getMessage();
        if(StringUtils.isNullOrEmpty(message)){
            message = JDReadApplication.getInstance().getString(R.string.open_book_fail);
        }
        event.setMessage(message);
        EventBus.getDefault().post(event);
    }
}
