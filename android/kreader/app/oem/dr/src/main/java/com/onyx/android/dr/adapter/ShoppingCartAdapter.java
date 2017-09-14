package com.onyx.android.dr.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.ProductBean;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.request.cloud.v2.CloudThumbnailLoadRequest;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 17-9-12.
 */

public class ShoppingCartAdapter extends PageRecyclerView.PageAdapter<ShoppingCartAdapter.ViewHolder> {
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.shopping_cart_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.shopping_cart_col);
    private List<ProductBean> list;
    private List<ProductBean> selectList = new ArrayList<>();
    private float totalPrice = 0;
    private OnCheckedChangeListener onCheckedChangeListener;
    private boolean selectAll;

    public void setList(List<ProductBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return col;
    }

    @Override
    public int getDataCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_shopping_cart, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, int position) {
        final ProductBean productBean = list.get(position);
        holder.itemShoppingCartTitle.setText(StringUtils.isNullOrEmpty(productBean.getProductCart().product.getLanguage()) ? Constants.EMPTY_STRING : productBean.getProductCart().product.getLanguage());
        holder.itemShoppingCartTitle.setVisibility(productBean.isFirst() ? View.VISIBLE : View.GONE);
        loadThumbnailRequest(holder.itemShoppingCartBookCover, productBean);
        holder.itemShoppingCartBookName.setText(String.format(DRApplication.getInstance().getResources().getString(R.string.book_detail_book_name), productBean.getProductCart().product.getName()));
        holder.itemShoppingCartBookPublisher.setText(String.format(DRApplication.getInstance().getResources().getString(R.string.book_detail_book_publisher), productBean.getProductCart().product.getPublisher()));
        holder.itemShoppingCartBookPrice.setText(String.format(DRApplication.getInstance().getResources().getString(R.string.book_detail_book_price), productBean.getProductCart().product.getPrice()));
        holder.itemShoppingCartCheckbox.setChecked(productBean.isChecked());
        holder.itemShoppingCartCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                productBean.setChecked(isChecked);
                updateTotalPrice();
            }
        });
    }

    private void updateTotalPrice() {
        selectList.clear();
        totalPrice = 0;
        for (ProductBean productBean : list) {
            if (productBean.isChecked()) {
                selectList.add(productBean);
                totalPrice += productBean.getProductCart().product.getPrice();
            }
        }
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChangeListener(selectList.size(), totalPrice);
        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChangeListener(int count, float price);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_shopping_cart_title)
        TextView itemShoppingCartTitle;
        @Bind(R.id.item_shopping_cart_checkbox)
        CheckBox itemShoppingCartCheckbox;
        @Bind(R.id.item_shopping_cart_book_cover)
        ImageView itemShoppingCartBookCover;
        @Bind(R.id.item_shopping_cart_book_name)
        TextView itemShoppingCartBookName;
        @Bind(R.id.item_shopping_cart_book_publisher)
        TextView itemShoppingCartBookPublisher;
        @Bind(R.id.item_shopping_cart_book_crowd)
        TextView itemShoppingCartBookCrowd;
        @Bind(R.id.item_shopping_cart_book_price)
        TextView itemShoppingCartBookPrice;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public List<ProductBean> getSelectList() {
        return selectList;
    }

    public void selectAll() {
        for (ProductBean productBean : list) {
            productBean.setChecked(!selectAll);
        }
        selectAll = !selectAll;
        notifyDataSetChanged();
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    private void loadThumbnailRequest(final ImageView imageView, final ProductBean productBean) {
        final CloudThumbnailLoadRequest loadRequest = new CloudThumbnailLoadRequest(
                productBean.getProductCart().product.getCoverUrl(),
                productBean.getProductCart().product.getAssociationId(), OnyxThumbnail.ThumbnailKind.Original);
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (!isContentValid(request, e)) {
                    return;
                }
                CloseableReference<Bitmap> closeableRef = loadRequest.getRefBitmap();
                if (closeableRef != null && closeableRef.isValid()) {
                    imageView.setImageBitmap(closeableRef.get());
                }
            }
        });
    }

    private boolean isContentValid(BaseRequest request, Throwable e) {
        if (e != null || request.isAbort()) {
            return false;
        }
        return true;
    }
}
