package con.onyx.android.libsetting.view;

import android.databinding.BaseObservable;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by solskjaer49 on 2016/12/5 19:54.
 */

public abstract class SettingPageAdapter<VH extends RecyclerView.ViewHolder, T extends BaseObservable>
        extends PageRecyclerView.PageAdapter<VH> {
    public void setDataList(List<T> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
    }

    public List<T> getDataList() {
        return dataList;
    }

    @Override
    public int getDataCount() {
        return dataList.size();
    }

    //Always should override this method,here just to setup some on click /on long click event,not actually bind data to view.
    @Override
    @CallSuper
    public void onPageBindViewHolder(final VH holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClick(getDataList().get(holder.getAdapterPosition()));
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (itemLongClickListener != null) {
                    itemLongClickListener.itemLongClick(getDataList().get(holder.getAdapterPosition()));
                    return true;
                }
                return false;
            }
        });
    }

    private List<T> dataList = new LinkedList<>();

    public void setItemClickListener(PageRecyclerViewItemClickListener<T> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(PageRecyclerViewItemLongClickListener<T> itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    private PageRecyclerViewItemClickListener<T> itemClickListener;
    private PageRecyclerViewItemLongClickListener<T> itemLongClickListener;

}
