package com.onyx.android.dr.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 17-7-11.
 */

public class BookshelfItemAdapter extends PageRecyclerView.PageAdapter<BookshelfItemAdapter.ViewHolder> implements View.OnClickListener {
    private List<Metadata> metadatas;
    private Context context;

    public BookshelfItemAdapter(List<Metadata> metadatas) {
        this.metadatas = metadatas;
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.bookshelf_item_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.bookshelf_item_col);
    }

    @Override
    public int getDataCount() {
        return metadatas == null ? 0 : metadatas.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = View.inflate(parent.getContext(), R.layout.item_book_cover, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        Metadata metadata = metadatas.get(position);
        Glide.with(context).load(metadata.getCoverUrl()).into(holder.bookCover);
        holder.bookCover.setOnClickListener(this);
        holder.bookCover.setTag(position);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        Toast.makeText(context, metadatas.get(position).getName(), Toast.LENGTH_SHORT).show();
    }

    static class ViewHolder extends PageRecyclerView.ViewHolder {
        @Bind(R.id.book_cover)
        ImageView bookCover;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
