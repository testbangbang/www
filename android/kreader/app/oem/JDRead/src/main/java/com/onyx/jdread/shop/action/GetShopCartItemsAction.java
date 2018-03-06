package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.cloud.entity.BookCartDetailBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCartBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CartDetailResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopCartItemData;
import com.onyx.jdread.shop.model.ShopCartModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestCartDetail;
import com.onyx.jdread.util.Utils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by li on 2018/1/5.
 */

public class GetShopCartItemsAction extends BaseAction {
    private List<BookCartBean> bookCartBeanList;
    private String[] bookIds;
    private static final String ZERO = "0";

    public GetShopCartItemsAction(String[] bookIds) {
        this.bookIds = bookIds;
    }

    public GetShopCartItemsAction(List<BookCartBean> bookCartBeanList) {
        this.bookCartBeanList = bookCartBeanList;
    }

    @Override
    public void execute(final ShopDataBundle dataBundle, final RxCallback rxCallback) {
        BaseShopRequestBean bean = new BaseShopRequestBean();
        List<BookCartDetailBean> list = new ArrayList<>();
        for (int i = 0; i < bookCartBeanList.size(); i++) {
            BookCartBean bookCartBean = bookCartBeanList.get(i);
            BookCartDetailBean detailBean = new BookCartDetailBean();
            detailBean.ebook_id = String.valueOf(bookCartBean.id);
            list.add(detailBean);
        }
        String s = JSON.toJSONString(list);
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constants.PARSE_JSON_TYPE), s);

        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.GotoOrder.CART_DETAIL);
        baseInfo.setSign(signValue);

        bean.setBaseInfo(baseInfo);
        bean.setBody(requestBody);
        final RxRequestCartDetail rq = new RxRequestCartDetail();
        rq.setRequestBean(bean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ShopCartModel shopCartModel = dataBundle.getShopCartModel();
                CartDetailResultBean.DataBean resultBean = rq.getResultBean();
                if (resultBean != null) {
                    String originalPrice = Utils.keepPoints(resultBean.origin_price);
                    String cashBack = Utils.keepPoints(resultBean.re_price);
                    String totalAmount = resultBean.total_price;
                    shopCartModel.setOriginalPrice(originalPrice);
                    shopCartModel.setCashBack(cashBack);
                    shopCartModel.setTotalAmount(totalAmount);

                    List<ShopCartItemData> shopCartItems = rq.getShopCartItems();
                    if (shopCartItems != null && shopCartItems.size() > 0) {
                        shopCartModel.setDatas(shopCartItems);
                    }
                } else {
                    shopCartModel.setOriginalPrice(ZERO);
                    shopCartModel.setCashBack(ZERO);
                    shopCartModel.setTotalAmount(ZERO);
                    List<ShopCartItemData> datas = shopCartModel.getDatas();
                    if (datas != null && datas.size() > 0) {
                        datas.clear();
                    }
                }

                if (rxCallback != null) {
                    rxCallback.onNext(GetShopCartItemsAction.class);
                }
            }
        });
    }
}
