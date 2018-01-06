package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCartItemBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ShoppingCartBookIdsBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.ShopCartItemData;
import com.onyx.jdread.shop.model.ShopCartModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestGetCartItems;
import com.onyx.jdread.shop.request.cloud.RxRequestGetServiceCartIds;
import com.onyx.jdread.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2018/1/5.
 */

public class GetShopCartItemsAction extends BaseAction {
    private ShopDataBundle bundle;
    private RxCallback rxCallBack;

    @Override
    public void execute(ShopDataBundle dataBundle, RxCallback rxCallback) {
        this.bundle = dataBundle;
        this.rxCallBack = rxCallback;
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
                        String[] bookIds = CommonUtils.string2Arr(bookList);
                        getBookItems(bookIds);
                    }
                }
            }
        });
    }

    private void getBookItems(String[] bookIds) {
        BaseRequestBean requestBean = new BaseRequestBean();
        requestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        List<Map<String, String>> list = getList(bookIds);
        String body = getBookCartJsonBody(list);
        requestBean.setBody(body);

        final RxRequestGetCartItems rq = new RxRequestGetCartItems();
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ShopCartModel shopCartModel = bundle.getShopCartModel();
                BookCartItemBean.CartResultBean cartResult = rq.getCartResult();
                if (cartResult != null) {
                    String originalPrice = Utils.keepPoints(cartResult.getOriginalPrice());
                    String cashBack = Utils.keepPoints(cartResult.getCashback());
                    String totalAmount = cartResult.getTotalCostcontent();
                    shopCartModel.setOriginalPrice(originalPrice);
                    shopCartModel.setCashBack(cashBack);
                    shopCartModel.setTotalAmount(totalAmount);

                    List<ShopCartItemData> shopCartItems = rq.getShopCartItems();
                    if (shopCartItems != null && shopCartItems.size() > 0) {
                        shopCartModel.setDatas(shopCartItems);
                        if (rxCallBack != null) {
                            rxCallBack.onNext(GetShopCartItemsAction.class);
                        }
                    }
                }
            }
        });
    }

    private String getShoppingCartBody(String bookList, String type) {
        JSONObject json = new JSONObject();
        try {
            json.put(CloudApiContext.NewBookDetail.BOOK_LIST, bookList);
            json.put(CloudApiContext.NewBookDetail.TYPE, type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private List<Map<String, String>> getList(String[] bookIds) {
        List<Map<String, String>> list = new ArrayList<>();
        if (bookIds != null && bookIds.length > 0) {
            for (String bookId : bookIds) {
                Map<String, String> map = new HashMap<>();
                map.put(CloudApiContext.GotoOrder.ID, bookId);
                map.put(CloudApiContext.GotoOrder.NUM, Constants.CART_TYPE_GET);
                list.add(map);
            }
        }
        return list;
    }

    private String getBookCartJsonBody(List<Map<String, String>> list) {
        String result = JSON.toJSONString(list);
        return new String("{\"" + CloudApiContext.GotoOrder.THESKUS + "\":" + result + "}");
    }
}
