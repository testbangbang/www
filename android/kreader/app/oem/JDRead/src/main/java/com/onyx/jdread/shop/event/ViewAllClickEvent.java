package com.onyx.jdread.shop.event;

/**
 * Created by jackdeng on 2018/1/9.
 */

public class ViewAllClickEvent {
    public int modelId;
    public int modelType;
    public String subjectName;
    public ViewAllClickEvent(int modelId, int modelType, String subjectName) {
        this.modelId = modelId;
        this.modelType = modelType;
        this.subjectName = subjectName;
    }
}