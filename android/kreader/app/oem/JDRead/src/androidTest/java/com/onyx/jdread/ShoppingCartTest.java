package com.onyx.jdread;

import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.action.AddOrDeleteCartAction;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.ShopDataBundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ShoppingCartTest extends ApplicationTestCase<JDReadApplication> {
    private static final String TAG = ShoppingCartTest.class.getSimpleName();

    public ShoppingCartTest() {
        super(JDReadApplication.class);
    }

    public void testGetBookDetail() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        AddOrDeleteCartAction addOrDeleteCartAction = new AddOrDeleteCartAction(new String[]{"30310588"}, Constants.CART_TYPE_ADD);
        addOrDeleteCartAction.execute(ShopDataBundle.getInstance(),new RxCallback<AddOrDeleteCartAction>() {
            @Override
            public void onNext(AddOrDeleteCartAction addOrDeleteCartAction) {
                assertNotNull(addOrDeleteCartAction.getData());
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                assertNull(throwable);
                countDownLatch.countDown();

            }
        });
        countDownLatch.await();
    }

    private String getBookCartJsonBody(List<Map<String, String>> list) {
        String result = JSON.toJSONString(list);
        return new String("{\"" + CloudApiContext.GotoOrder.THESKUS + "\":" + result + "}");
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


}
