package com.onyx.kreader.ui.view;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.kreader.R;

/**
 * Created by ming on 16/9/23.
 */
public class PreviewViewHolder extends RecyclerView.ViewHolder {
    private int page;
    private ImageView imageView;
    private TextView pageTextView;
    private TextView pageText;
    private RelativeLayout container;

    public PreviewViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
        pageTextView = (TextView) itemView.findViewById(R.id.text_view_page);
        pageText = (TextView) itemView.findViewById(R.id.btn_page);
        container = (RelativeLayout) itemView.findViewById(R.id.item_container);
    }

    public void bindPreview(Bitmap bitmap, int page) {
        this.page = page;
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
        }
        pageTextView.setVisibility(View.GONE);
        pageText.setVisibility(View.VISIBLE);
        pageText.setText(String.valueOf(page + 1));
    }

    public RelativeLayout getContainer() {
        return container;
    }

    public int getPage() {
        return page;
    }
}