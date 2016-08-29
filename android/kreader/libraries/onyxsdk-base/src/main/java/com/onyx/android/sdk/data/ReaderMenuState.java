package com.onyx.android.sdk.data;

/**
 * Created by joy on 6/28/16.
 */
public class ReaderMenuState {
    private static final String TITLE_TAG = "title";
    private static final String PAGE_INDEX_TAG = "page_index";
    private static final String PAGE_COUNT_TAG = "page_count";
    private static final String CAN_GO_BACK = "can_go_back";
    private static final String CAN_GO_FORWARD = "can_go_forward";

    GObject data = new GObject();

    public String getTitle() {
        return data.getString(TITLE_TAG);
    }

    public void setTitle(String title) {
        data.putString(TITLE_TAG, title);
    }

    public int getPageIndex() {
        return data.getInt(PAGE_INDEX_TAG);
    }

    public void setPageIndex(int page) {
        data.putInt(PAGE_INDEX_TAG, page);
    }

    public int getPageCount() {
        return data.getInt(PAGE_COUNT_TAG);
    }

    public void setPageCount(int count) {
        data.putInt(PAGE_COUNT_TAG, count);
    }

    public boolean canGoBack(){
        return data.getBoolean(CAN_GO_BACK,false);
    }

    public void setCanGoBack(boolean canGoBack){
        data.putBoolean(CAN_GO_BACK, canGoBack);
    }

    public boolean canGoForward(){
        return data.getBoolean(CAN_GO_FORWARD,false);
    }

    public void setCanGoForward(boolean canGoForward){
        data.putBoolean(CAN_GO_FORWARD, canGoForward);
    }

    public int getValue(String key, int defaultValue) {
        return data.getInt(key, defaultValue);
    }

    public void setValue(String key, int value) {
        data.putInt(key, value);
    }

    public Object getValue(String key) {
        return data.getObject(key);
    }

    public void setValue(String key, Object value) {
        data.putObject(key, value);
    }
}
