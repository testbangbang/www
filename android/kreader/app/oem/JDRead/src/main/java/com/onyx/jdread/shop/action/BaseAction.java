package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.CloudApiContext;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.model.ShopDataBundle;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseAction<T extends ShopDataBundle> {

    public abstract void execute(T dataBundle, RxCallback rxCallback);

    public void showLoadingDialog(T dataBundle, int messageResId) {
        dataBundle.getEventBus().post(new LoadingDialogEvent(messageResId));
    }

    public void hideLoadingDialog(T dataBundle) {
        dataBundle.getEventBus().post(new HideAllDialogEvent());
    }

    public String getShoppingCartBody(String bookList, String type) {
        JSONObject json = new JSONObject();
        try {
            json.put(CloudApiContext.NewBookDetail.BOOK_LIST, bookList);
            json.put(CloudApiContext.NewBookDetail.TYPE, type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
