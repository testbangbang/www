package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ChangeLayoutParameter;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.ChangeLayoutRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ImageReflowAction extends BaseReaderAction {
    private ImageReflowSettings settings;

    public ImageReflowAction(ImageReflowSettings settings) {
        this.settings = settings;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        ChangeLayoutParameter parameter = new ChangeLayoutParameter(PageConstants.IMAGE_REFLOW_PAGE, new NavigationArgs());
        new ChangeLayoutRequest(readerDataHolder.getReader(),parameter,settings).execute(new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }
}
