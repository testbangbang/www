package com.onyx.jdread.personal.cloud.entity.jdbean;

/**
 * Created by li on 2017/12/30.
 */

public class TopUpValueBean {
    private String value;
    private String valueGift;
    private Object event;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueGift() {
        return valueGift;
    }

    public void setValueGift(String valueGift) {
        this.valueGift = valueGift;
    }

    public Object getEvent() {
        return event;
    }

    public void setEvent(Object event) {
        this.event = event;
    }
}
