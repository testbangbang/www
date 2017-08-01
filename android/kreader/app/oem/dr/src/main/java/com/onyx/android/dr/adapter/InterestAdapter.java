package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.InterestBean;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 17-7-29.
 */

public class InterestAdapter extends PageRecyclerView.PageAdapter<InterestAdapter.ViewHolder> {
    private List<InterestBean> list;

    public void setList(List<InterestBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.interest_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.interest_col);
    }

    @Override
    public int getDataCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_interest, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        holder.interestCheckbox.setText(list.get(position).name);
        list.get(position).checked = holder.interestCheckbox.isChecked();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.interest_checkbox)
        CheckBox interestCheckbox;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public List<String> getSelectedInterest() {
        List<String> interests = new ArrayList<>();
        for (InterestBean bean : list) {
            if (bean.checked) {
                interests.add(bean.name);
            }
        }
        return interests;
    }
}
