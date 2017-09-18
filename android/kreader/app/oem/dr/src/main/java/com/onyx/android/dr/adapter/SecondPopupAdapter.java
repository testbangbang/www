package com.onyx.android.dr.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 2017/9/15.
 */
public class SecondPopupAdapter extends PageRecyclerView.PageAdapter<SecondPopupAdapter.ViewHolder> {
    private List<String> list;
    private OnItemClickListener listener;

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.second_popup_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.address_popup_list_col);
    }

    @Override
    public int getDataCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_create_group_spinner, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, final int position) {
        holder.spinnerText.setText(list.get(position));
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position, list.get(position));
                }
            }
        });
    }

    static class ViewHolder extends PageRecyclerView.ViewHolder {
        @Bind(R.id.spinner_text)
        TextView spinnerText;
        View rootView;

        ViewHolder(View view) {
            super(view);
            rootView = view;
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position, String string);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
