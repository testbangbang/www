package com.onyx.jdread.library.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.model.SearchHistory;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.SearchHistoryItemBinding;
import com.onyx.jdread.library.event.SearchBookKeyEvent;
import com.onyx.jdread.library.model.LibraryDataBundle;

import java.util.List;

/**
 * Created by hehai on 18-1-18.
 */

public class SearchHistoryAdapter extends PageRecyclerView.PageAdapter<SearchHistoryAdapter.ViewHolder> {
    private List<SearchHistory> searchHistories;

    public void setSearchHistories(List<SearchHistory> searchHistories) {
        this.searchHistories = searchHistories;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.search_history_recycler_row);
    }

    @Override
    public int getColumnCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.search_history_recycler_col);
    }

    @Override
    public int getDataCount() {
        return searchHistories == null ? 0 : searchHistories.size();
    }

    @Override
    public SearchHistoryAdapter.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_item, null));
    }

    @Override
    public void onPageBindViewHolder(SearchHistoryAdapter.ViewHolder holder, int position) {
        final SearchHistory searchHistory = searchHistories.get(position);
        holder.bind.setHistory(searchHistory.getContent());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LibraryDataBundle.getInstance().getEventBus().post(new SearchBookKeyEvent(searchHistory.getContent()));
            }
        });
    }

    static class ViewHolder extends PageRecyclerView.ViewHolder {

        private final SearchHistoryItemBinding bind;
        private View rootView;

        public ViewHolder(View view) {
            super(view);
            rootView = view;
            bind = DataBindingUtil.bind(view);
        }

        public SearchHistoryItemBinding getBind() {
            return bind;
        }

        public void bindTo(String history) {
            bind.setHistory(history);
            bind.executePendingBindings();
        }
    }
}
