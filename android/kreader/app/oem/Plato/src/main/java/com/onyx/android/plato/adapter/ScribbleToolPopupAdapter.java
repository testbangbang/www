package com.onyx.android.plato.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.bean.ScribbleToolBean;
import com.onyx.android.plato.databinding.ItemScribbleToolBinding;
import com.onyx.android.plato.scribble.SubMenuClickEvent;
import com.onyx.android.plato.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by hehai on 17-10-16.
 */

public class ScribbleToolPopupAdapter extends PageRecyclerView.PageAdapter<ScribbleToolPopupAdapter.ViewHolder> {
    private List<ScribbleToolBean> tools;

    public void setTools(List<ScribbleToolBean> tools) {
        this.tools = tools;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.scribble_tool_menu_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.scribble_tool_menu_col);
    }

    @Override
    public int getDataCount() {
        return tools == null ? 0 : tools.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.scribble_popup_window_layout_item, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        holder.getBind().itemScribbleTool.setImageResource(tools.get(position).getImageResource());
        holder.getBind().itemScribbleTool.setTag(position);
        holder.getBind().itemScribbleTool.setOnClickListener(this);
        holder.getBind().executePendingBindings();
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        int position = (int) v.getTag();
        EventBus.getDefault().post(new SubMenuClickEvent(tools.get(position).getScribbleSubMenuID()));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemScribbleToolBinding bind;

        public ViewHolder(View itemView) {
            super(itemView);
            bind = DataBindingUtil.bind(itemView);
        }

        public ItemScribbleToolBinding getBind() {
            return bind;
        }
    }
}
