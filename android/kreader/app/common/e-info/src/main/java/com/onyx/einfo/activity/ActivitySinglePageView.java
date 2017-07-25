package com.onyx.einfo.activity;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.einfo.R;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/5/20.
 */

public class ActivitySinglePageView extends BaseActivity {

    @Bind(R.id.content_page_view)
    SinglePageRecyclerView contentPageView;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_single_page_view;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        contentPageView.setLayoutManager(new DisableScrollGridManager(this));
        contentPageView.setOnChangePageListener(new SinglePageRecyclerView.OnChangePageListener() {
            @Override
            public void prev() {
                Log.e("##single", "prev");
            }

            @Override
            public void next() {
                Log.e("##single", "next");
            }
        });
        contentPageView.setAdapter(new PageRecyclerView.PageAdapter<BookItemHolder>() {
            @Override
            public int getRowCount() {
                return 2;
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public int getDataCount() {
                return 6;
            }

            @Override
            public BookItemHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new BookItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.book_reading_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(BookItemHolder holder, int position) {
                holder.titleView.setVisibility(View.VISIBLE);
                holder.titleView.setText(String.valueOf(position));
            }
        });
    }

    @Override
    protected void initData() {

    }

    class BookItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_cover)
        ImageView coverImage;
        @Bind(R.id.image_get_widget)
        ImageView getWidgetImage;
        @Bind(R.id.textView_title)
        TextView titleView;

        public BookItemHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            ButterKnife.bind(this, itemView);
        }
    }
}
