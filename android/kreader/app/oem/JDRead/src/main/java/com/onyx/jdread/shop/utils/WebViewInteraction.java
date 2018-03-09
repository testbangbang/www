package com.onyx.jdread.shop.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.onyx.jdread.shop.event.BuyBookSuccessEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2018/1/6.
 */

public class WebViewInteraction {
    private static final String TAG = WebViewInteraction.class.getSimpleName();
    public static final String INTERACTION_NAME = "bridge";

    @JavascriptInterface
    public void goToBookcaseCloud() {
        Log.i(TAG,"goToBookcaseCloud()");
    }

    @JavascriptInterface
    public void goToBookStore() {
        Log.i(TAG,"goToBookStore()");
    }

    @JavascriptInterface
    public void goToLogin(String params) {
        Log.i(TAG,"goToLogin()");
    }

    @JavascriptInterface
    public void goToAppMarket() {
        Log.i(TAG,"goToAppMarket()");
    }

    @JavascriptInterface
    public void goToAppMarket(String packagename) {
        Log.i(TAG, "goToAppMarket()");
    }

    @JavascriptInterface
    public void clearShoppingCart(String buiedBookIds) {
        Log.i(TAG,"clearShoppingCart()");
        EventBus.getDefault().post(new BuyBookSuccessEvent(buiedBookIds,false));
    }

    @JavascriptInterface
    public void goToShopping() {
        Log.i(TAG,"goToShopping()");
    }

    @JavascriptInterface
    public void goToMyJD() {
        Log.i(TAG,"goToMyJD()");
    }

    @JavascriptInterface
    public void goToBookShelf() {
        Log.i(TAG,"goToBookShelf()");
    }

    @JavascriptInterface
    public void quickDownload(String jsonString) {
        Log.i(TAG,"quickDownload()");
    }

    @JavascriptInterface
    private void download(final String key) {
        Log.i(TAG,"download()");
    }

    @JavascriptInterface
    public void goToOrder(String bookid) {
        Log.i(TAG,"goToOrder()");
    }

    @JavascriptInterface
    public void goToIngegrationIndex() {
        Log.i(TAG,"goToIngegrationIndex()");
    }

    @JavascriptInterface
    public void goToBookDetail(String bookid) {
        Log.i(TAG,"goToBookDetail()");
    }

    @JavascriptInterface
    public void weiboShare(String title, String imageUrl, String linkUrl) {
        Log.i(TAG,"weiboShare()");
    }

    @JavascriptInterface
    public void weixinShare(String title, String imageUrl, String linkUrl, int type) {
        Log.i(TAG,"weixinShare()");
    }

    private void shareToGetScore() {
        Log.i(TAG,"shareToGetScore()");
    }

    private void addBook2ShelfWithoutDownload(Bundle bundle) {
        Log.i(TAG,"addBook2ShelfWithoutDownload()");
    }

    @JavascriptInterface
    public void sendFinishBroadCast(Context context) {
        Log.i(TAG,"sendFinishBroadCast()");
    }

    @JavascriptInterface
    public void openBrowser(String url) {
        Log.i(TAG,"openBrowser()");
    }

    @JavascriptInterface
    public void socialShare(String title, String imageUrl, String linkUrl, int shareAccess,String shareType,final String remark) {
        Log.i(TAG,"socialShare()");
    }

    private void rollBackSendBookStatus(String remark){
        Log.i(TAG,"rollBackSendBookStatus()");
    }

    @JavascriptInterface
    public void gotoMyOrderList() {
        Log.i(TAG,"gotoMyOrderList()");
    }
}
