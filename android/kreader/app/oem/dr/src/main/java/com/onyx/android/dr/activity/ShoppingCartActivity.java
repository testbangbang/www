package com.onyx.android.dr.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.ShoppingCartAdapter;
import com.onyx.android.dr.bean.ProductBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.interfaces.ShoppingCartView;
import com.onyx.android.dr.presenter.ShoppingCartPresenter;
import com.onyx.android.dr.reader.view.CustomDialog;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-9-12.
 */

public class ShoppingCartActivity extends BaseActivity implements ShoppingCartView {

    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.title_bar_right_icon_one)
    ImageView delete;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.shopping_cart_recycler)
    SinglePageRecyclerView shoppingCartRecycler;
    @Bind(R.id.shopping_cart_select_all)
    TextView shoppingCartSelectAll;
    @Bind(R.id.shopping_cart_total_price)
    TextView shoppingCartTotalPrice;
    @Bind(R.id.shopping_cart_buy)
    TextView shoppingCartBuy;
    private ShoppingCartPresenter shoppingCartPresenter;
    private ShoppingCartAdapter shoppingCartAdapter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_shopping_cart;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        shoppingCartRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        shoppingCartAdapter = new ShoppingCartAdapter();
        shoppingCartRecycler.addItemDecoration(dividerItemDecoration);
        shoppingCartRecycler.setAdapter(shoppingCartAdapter);
        shoppingCartAdapter.setOnCheckedChangeListener(new ShoppingCartAdapter.OnCheckedChangeListener() {

            @Override
            public void onCheckedChangeListener(int count, float price) {
                shoppingCartTotalPrice.setText(String.format(getString(R.string.shopping_total_price_format), count, price));
            }
        });
        titleBarTitle.setText(getString(R.string.shopping_cart));
        image.setImageResource(R.drawable.ic_reader_cart);
        shoppingCartTotalPrice.setText(String.format(getString(R.string.shopping_total_price_format), 0, 0f));
        delete.setImageResource(R.drawable.ic_reader_choose_delet);
        delete.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        if (shoppingCartPresenter == null) {
            shoppingCartPresenter = new ShoppingCartPresenter(this);
        }
        shoppingCartPresenter.getProducts();
    }

    @Override
    public void setProducts(List<ProductBean> products) {
        DRApplication.getInstance().setCartCount(products.size());
        shoppingCartAdapter.setList(products);
    }

    @Override
    public void setOrderId(String id) {
        ActivityManager.startPayActivity(this, id);
    }

    @OnClick({R.id.menu_back, R.id.shopping_cart_select_all, R.id.shopping_cart_buy, R.id.title_bar_right_icon_one})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.shopping_cart_select_all:
                shoppingCartAdapter.selectAll();
                shoppingCartSelectAll.setText(shoppingCartAdapter.isSelectAll() ? getString(R.string.cancel) : getString(R.string.select_all));
                break;
            case R.id.shopping_cart_buy:
                if (!CollectionUtils.isNullOrEmpty(shoppingCartAdapter.getSelectList())) {
                    shoppingCartPresenter.buy(shoppingCartAdapter.getSelectList());
                }
                break;
            case R.id.title_bar_right_icon_one:
                delete();
                break;
        }
    }

    private void showDeleteDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getString(R.string.prompt))
                .setMessage(getString(R.string.remove_from_cart_message))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shoppingCartPresenter.removeProduct(shoppingCartAdapter.getSelectList());
                        dialog.dismiss();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void delete() {
        if (!CollectionUtils.isNullOrEmpty(shoppingCartAdapter.getSelectList())) {
            showDeleteDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        shoppingCartPresenter.getProducts();
    }
}
