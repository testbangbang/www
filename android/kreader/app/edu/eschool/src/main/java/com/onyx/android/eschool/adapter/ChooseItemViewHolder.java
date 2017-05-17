package com.onyx.android.eschool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.eschool.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2016/11/26.
 */

public class ChooseItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.title_text)
    public TextView titleTextView;
    @Bind(R.id.choose_image)
    public ImageView chooseImageView;

    public static ChooseItemViewHolder create(Context context, int layoutResId, ViewGroup parent) {
        return new ChooseItemViewHolder(LayoutInflater.from(context).inflate(layoutResId, parent, false));
    }

    public static ChooseItemViewHolder create(Context context, ViewGroup parent) {
        return create(context, R.layout.teaching_category_item, parent);
    }

    protected ChooseItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
