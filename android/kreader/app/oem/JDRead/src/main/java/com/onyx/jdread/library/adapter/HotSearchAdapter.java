package com.onyx.jdread.library.adapter;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.HotSearchItemBinding;
import com.onyx.jdread.library.event.SearchBookKeyEvent;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.main.common.ResManager;

import java.util.List;

/**
 * Created by hehai on 18-1-18.
 */

public class HotSearchAdapter extends PageRecyclerView.PageAdapter<HotSearchAdapter.ViewHolder> {
    private List<String> searchHotWords;

    public void setSearchHotWords(List<String> searchHotWords) {
        this.searchHotWords = searchHotWords;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.hot_search_recycler_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.hot_search_recycler_col);
    }

    @Override
    public int getDataCount() {
        return searchHotWords == null ? 0 : searchHotWords.size();
    }

    @Override
    public HotSearchAdapter.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_search_item, null));
    }

    @Override
    public void onPageBindViewHolder(HotSearchAdapter.ViewHolder holder, int position) {
        final String string = searchHotWords.get(position);
        holder.bind.hotItemNum.setBackgroundResource(position < getColumnCount() ? R.drawable.search_hot_black_background : R.drawable.search_hot_gray_background);
        holder.bind.hotItemNum.setTextColor(position < getColumnCount() ? Color.WHITE : Color.BLACK);
        holder.bindTo(String.valueOf(position + 1), string);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LibraryDataBundle.getInstance().getEventBus().post(new SearchBookKeyEvent(string));
            }
        });
    }

    static class ViewHolder extends PageRecyclerView.ViewHolder {

        private final HotSearchItemBinding bind;
        private View rootView;

        public ViewHolder(View view) {
            super(view);
            rootView = view;
            bind = DataBindingUtil.bind(view);
        }

        public HotSearchItemBinding getBind() {
            return bind;
        }

        public void bindTo(String num, String string) {
            bind.setNum(num);
            bind.setHotWord(string);
            bind.executePendingBindings();
        }
    }
}
