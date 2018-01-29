package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.shop.cloud.entity.jdbean.SearchHotWord;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestSearchHotWord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 18-1-18.
 */

public class SearchHotWordAction extends BaseAction<ShopDataBundle> {
    private List<String> hotWords = new ArrayList<>();

    @Override
    public void execute(ShopDataBundle dataBundle, final RxCallback rxCallback) {
        RxRequestSearchHotWord requestSearchHotWord = new RxRequestSearchHotWord();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        appBaseInfo.setSign(appBaseInfo.getSignValue(CloudApiContext.BookShopURI.HOT_SEARCH_URI));
        requestSearchHotWord.setRequestBean(appBaseInfo);
        requestSearchHotWord.execute(new RxCallback<RxRequestSearchHotWord>() {
            @Override
            public void onNext(RxRequestSearchHotWord request) {
                hotWords.clear();
                SearchHotWord searchHotWord = request.getSearchHotWord();
                if (searchHotWord != null && !CollectionUtils.isNullOrEmpty(searchHotWord.data)) {
                    hotWords.addAll(searchHotWord.data);
                }
                rxCallback.onNext(request);
            }
        });
    }

    public List<String> getHotWords() {
        return hotWords;
    }
}
