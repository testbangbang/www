package com.onyx.kreader.ui.handler;

import android.view.KeyEvent;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.utils.PagePositionUtils;

/**
 * Created by joy on 7/29/16.
 */
public class TtsHandler extends BaseHandler {

    public TtsHandler(HandlerManager parent) {
        super(parent);
    }

    @Override
    public boolean onKeyDown(ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        final int page = readerDataHolder.getCurrentPage();
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (page > 0) {
                    getParent().getReaderDataHolder().getTtsManager().stop();
                    gotoPage(readerDataHolder, page -1);
                }
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (page < readerDataHolder.getPageCount() - 1) {
                    getParent().getReaderDataHolder().getTtsManager().stop();
                    gotoPage(readerDataHolder, page + 1);
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                getParent().getReaderDataHolder().getTtsManager().stop();
                readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.BASE_PROVIDER);
                return true;
        }
        return super.onKeyDown(readerDataHolder, keyCode, event);
    }

    private void gotoPage(final ReaderDataHolder readerDataHolder, final int page) {
        new GotoPageAction(PagePositionUtils.fromPageNumber(page)).execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getParent().getReaderDataHolder().getTtsManager().play();
            }
        });
    }
}
