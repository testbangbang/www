package com.onyx.jdread.reader.menu.actions;

import android.graphics.RectF;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ChangeLayoutParameter;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.ChangeLayoutRequest;

/**
 * Created by huxiaomao on 2018/1/1.
 */

public class SwitchNavigationToArticleAction extends BaseReaderAction {
    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        NavigationArgs args = new NavigationArgs();
        RectF limit = new RectF(0, 0, 0, 0);
        args.columnsLeftToRight(NavigationArgs.Type.ALL, 2, 2, limit);
        ChangeLayoutParameter parameter = new ChangeLayoutParameter(PageConstants.SINGLE_PAGE_NAVIGATION_LIST, args);
        new ChangeLayoutRequest(readerDataHolder.getReader(), parameter,null).execute(new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }
}
