package com.onyx.edu.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiang.android.lib.adapter.BaseAdapter;
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersAdapter;
import com.jiang.android.lib.widget.SwipeItemLayout;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.model.BaseEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/6/15.
 */
public abstract class ContactAdapter<T extends BaseEntity> extends BaseAdapter<T, ContactAdapter.ContactChildViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private Context context;
    private boolean swipeAble = true;
    private List<T> contactEntityList;

    private List<SwipeItemLayout> openedSwipeLayoutList = new ArrayList<>();
    private ItemClickListener itemClickListener;
    private ItemClickListener itemUnbundledClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemUnbundledClickListener(ItemClickListener listener) {
        this.itemUnbundledClickListener = listener;
    }

    public ContactAdapter(Context context, List<T> list) {
        this.context = context;
        this.contactEntityList = list;
    }

    public void setContactEntityList(List<T> list) {
        this.contactEntityList = list;
        notifyDataSetChanged();
    }

    public void setSwipeAble(boolean swipeAble) {
        this.swipeAble = swipeAble;
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.getSize(contactEntityList);
    }

    @Override
    public T getItem(int position) {
        return contactEntityList.get(position);
    }

    @Override
    public ContactChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ContactChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ContactChildViewHolder holder, int position) {
        holder.itemView.setTag(position);
        T entity = getItem(position);
        holder.usernameTv.setText(entity.username);
        String mac = getMacAddress(entity);
        holder.macAddressView.setText(mac);
    }

    public abstract String getMacAddress(T t);

    @Override
    public long getHeaderId(int position) {
        return getItem(position).sortLetter.charAt(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header, parent, false);
        return new ContactHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        String showValue = String.valueOf(getItem(position).sortLetter.charAt(0));
        textView.setText(showValue);
    }

    public int getPositionForSection(char section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = getItem(i).sortLetter;
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    public void closeOpenedSwipeLayout(SwipeItemLayout nowSwipeLayout) {
        for (SwipeItemLayout swipeItemLayout : openedSwipeLayoutList) {
            swipeItemLayout.closeWithAnim();
        }
        openedSwipeLayoutList.clear();
        if (nowSwipeLayout != null) {
            openedSwipeLayoutList.add(nowSwipeLayout);
        }
    }

    public void removeClosedSwipeLayout(SwipeItemLayout closedSwipeLayout) {
        openedSwipeLayoutList.remove(closedSwipeLayout);
    }

    private void processItemClick(View view) {
        if (itemClickListener != null) {
            int position = (Integer) view.getTag();
            itemClickListener.onClick(position, view);
        }
    }

    private void processItemUnbundledClick(View view) {
        if (itemUnbundledClickListener != null) {
            int position = (Integer) view.getTag();
            itemUnbundledClickListener.onClick(position, view);
        }
    }

    private void processItemLongClick(View view) {
        if (itemClickListener != null) {
            int position = (Integer) view.getTag();
            itemClickListener.onLongClick(position, view);
        }
    }

    public class ContactChildViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_swipe_root)
        SwipeItemLayout rootSwipeLayout;
        @Bind(R.id.tv_username)
        TextView usernameTv;
        @Bind(R.id.tv_mac_address)
        TextView macAddressView;
        @Bind(R.id.item_unbundled)
        TextView itemUnbundledTv;

        public ContactChildViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemClick(v);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    processItemLongClick(v);
                    return true;
                }
            });
            itemUnbundledTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemUnbundledClick(itemView);
                }
            });
            rootSwipeLayout.setSwipeAble(swipeAble);
            rootSwipeLayout.setDelegate(new SwipeItemLayout.SwipeItemLayoutDelegate() {
                @Override
                public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout) {
                    closeOpenedSwipeLayout(swipeItemLayout);
                }

                @Override
                public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout) {
                    removeClosedSwipeLayout(swipeItemLayout);
                }

                @Override
                public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout) {
                    closeOpenedSwipeLayout(null);
                }
            });
        }
    }

    public class ContactHeaderViewHolder extends RecyclerView.ViewHolder {
        public ContactHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
