package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class PencilSketchAdapter extends PageRecyclerView.PageAdapter<PencilSketchAdapter.ViewHolder> {
    private List<NoteModel> dataList;
    private List<Boolean> listCheck;
    private OnItemClickListener onItemClickListener;

    public void setDataList(List<NoteModel> dataList, List<Boolean> listCheck) {
        this.dataList.addAll(dataList);
        this.listCheck.addAll(listCheck);
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.pencil_sketch_tab_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.pencil_sketch_tab_column);
    }

    @Override
    public int getDataCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_pencil_sketch, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        NoteModel bean = dataList.get(position);
        holder.time.setText(TimeUtils.getStringByDate(bean.getUpdatedAt()));
        holder.title.setText(bean.getTitle());
        holder.checkBox.setChecked(listCheck.get(position));
        holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (onItemClickListener != null) {
                    onItemClickListener.setOnItemCheckedChanged(position, b);
                }
            }
        });
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    if (holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                        onItemClickListener.setOnItemClick(position, false);
                    } else {
                        holder.checkBox.setChecked(true);
                        onItemClickListener.setOnItemClick(position, true);
                    }
                }
            }
        });
    }

    public interface OnItemClickListener {
        void setOnItemClick(int position, boolean isCheck);
        void setOnItemCheckedChanged(int position, boolean isCheck);
    }

    public void setOnItemListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_sketch_title_checkbox)
        CheckBox checkBox;
        @Bind(R.id.item_sketch_picture)
        ImageView picture;
        @Bind(R.id.item_sketch_time)
        TextView time;
        @Bind(R.id.item_sketch_title)
        TextView title;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
