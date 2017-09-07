package com.onyx.edu.note.ui;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lxm on 2017/9/1.
 */

public abstract class PageDataBindingAdapter<T,
        VH extends BindingViewHolder,
        VM extends BaseObservable>
        extends PageRecyclerView.PageAdapter<VH> {

    public List<VM> getItemVMList() {
        return itemVMList;
    }

    public VM getItemVM(int position) {
        return itemVMList.get(position);
    }

    @Override
    public int getDataCount() {
        return itemVMList.size();
    }

    @CallSuper
    @Override
    public void onPageBindViewHolder(VH holder, int position) {
        holder.bindTo(getItemVM(position));
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

    public void addItemVM(VM item) {
        this.itemVMList.add(item);
    }

    @CallSuper
    public void setRawData(PageRecyclerView parent, List<T> rawData) {
        this.rawData = rawData;
        this.itemVMList.clear();
    }
}
