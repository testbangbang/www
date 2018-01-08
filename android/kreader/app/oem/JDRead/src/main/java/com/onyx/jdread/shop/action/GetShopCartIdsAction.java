package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ShoppingCartBookIdsBean;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestGetServiceCartIds;

/**
 * Created by li on 2018/1/6.
 */

public class GetShopCartIdsAction extends BaseAction {
    private String[] bookIds;

    public String[] getBookIds() {
        return bookIds;
    }

    @Override
    public void execute(ShopDataBundle dataBundle, final RxCallback rxCallback) {
        BaseRequestBean requestBean = new BaseRequestBean();
        requestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        String body = getShoppingCartBody("", Constants.CART_TYPE_GET);
        requestBean.setBody(body);
        final RxRequestGetServiceCartIds rq = new RxRequestGetServiceCartIds();
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ShoppingCartBookIdsBean resultBean = rq.getResultBean();
                if (resultBean != null) {
                    ShoppingCartBookIdsBean.ResultBean result = resultBean.getResult();
                    String bookList = result.getBookList();
                    if (StringUtils.isNotBlank(bookList)) {
                        bookIds = CommonUtils.string2Arr(bookList);
                        if (rxCallback != null) {
                            rxCallback.onNext(GetShopCartIdsAction.class);
                        }
                    }
                }
            }
        });
    }
}
