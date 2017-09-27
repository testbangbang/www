package com.onyx.android.dr.event;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/9/26.
 */
public class ShowQueryListResultEvent {
    List<String> result;

    public ShowQueryListResultEvent(List<String> result) {
        this.result = result;
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }
}
