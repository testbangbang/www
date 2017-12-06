package com.onyx.edu.student.adapter;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;

import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by solskjaer49 on 2016/12/5 19:54.
 */

public abstract class PageAdapter<VH extends RecyclerView.ViewHolder, T extends Object, VM extends BaseObservable>
        extends PageRecyclerView.PageAdapter<VH> {

    public List<VM> getItemVMList() {
        return itemVMList;
    }

    @Override
    public int getDataCount() {
        return itemVMList.size();
    }

    private List<VM> itemVMList = new LinkedList<>();
    private List<T> rawData = new ArrayList<>();

    public List<T> getRawData() {
        return rawData;
    }

    public void setItemVMList(List<VM> itemVMList) {
        this.itemVMList.clear();
        this.itemVMList.addAll(itemVMList);
    }

    //TODO:how about partial update?e.g. notifyItemChanged(index)?
    @CallSuper
    public void setRawData(List<T> rawData, Context context) {
        this.rawData = rawData;
        this.itemVMList.clear();
    }
}
