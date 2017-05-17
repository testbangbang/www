package com.onyx.edu.reader.ui.view;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.edu.reader.R;

/**
 * Created by ming on 16/9/23.
 */
public class PreviewViewHolder extends RecyclerView.ViewHolder {
    private int page;
    private String pagePosition;
    private ImageView imageView;
    private TextView pageTextView;
    private TextView pageText;
    private RelativeLayout container;
    private Bitmap bitmap;
    private ImageView closeView;

    public PreviewViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
        closeView = (ImageView) itemView.findViewById(R.id.close_view);
        pageTextView = (TextView) itemView.findViewById(R.id.text_view_page);
        pageText = (TextView) itemView.findViewById(R.id.btn_page);
        container = (RelativeLayout) itemView.findViewById(R.id.item_container);
    }

    public void bindPreview(Bitmap bitmap, int page) {
        this.page = page;
        this.bitmap = bitmap;
        if (bitmap != null && !bitmap.isRecycled()) {
            imageView.setImageBitmap(bitmap);
        }
        pageTextView.setVisibility(View.GONE);
        pageText.setVisibility(View.VISIBLE);
        pageText.setText(String.valueOf(page + 1));
    }

    public void bindPreview(Bitmap bitmap, int page, String pagePosition) {
        bindPreview(bitmap, page);
        this.pagePosition = pagePosition;
    }

    public RelativeLayout getContainer() {
        return container;
    }

    public int getPage() {
        return page;
    }

    public String getPagePosition() {
        return pagePosition;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public ImageView getCloseView() {
        return closeView;
    }
}