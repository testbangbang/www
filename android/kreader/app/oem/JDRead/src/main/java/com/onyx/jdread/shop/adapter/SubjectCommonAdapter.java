package com.onyx.jdread.shop.adapter;

import android.support.v7.widget.RecyclerView;

import com.onyx.jdread.shop.model.BaseSubjectViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2018/2/2.
 */

public abstract class SubjectCommonAdapter extends RecyclerView.Adapter {

    private List<BaseSubjectViewModel> datas = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getSubjectType();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setDatas(List<BaseSubjectViewModel> data) {
        datas.clear();
        if (data != null) {
            datas.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<BaseSubjectViewModel> getDatas() {
        return datas;
    }
}
