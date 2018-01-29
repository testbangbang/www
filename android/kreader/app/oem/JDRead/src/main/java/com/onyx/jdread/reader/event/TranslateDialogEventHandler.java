package com.onyx.jdread.reader.event;

import com.onyx.jdread.reader.model.TranslateViewModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2018/1/26.
 */

public class TranslateDialogEventHandler {
    private TranslateViewModel translateViewModel;

    public TranslateDialogEventHandler(TranslateViewModel translateViewModel) {
        this.translateViewModel = translateViewModel;
    }

    public void registerListener() {
        if (!translateViewModel.getEventBus().isRegistered(this)) {
            translateViewModel.getEventBus().register(this);
        }
    }

    public void unregisterListener() {
        if (translateViewModel.getEventBus().isRegistered(this)) {
            translateViewModel.getEventBus().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTextTranslateResultEvent(TextTranslateResultEvent event) {
        translateViewModel.updateTranslateResult(event.getTranslateResult());
    }
}
