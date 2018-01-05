package com.onyx.jdread.shop.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ShopCartBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.personal.adapter.ShopCartAdapter;
import com.onyx.jdread.shop.action.GetShopCartItemsAction;
import com.onyx.jdread.shop.model.ShopCartItemData;
import com.onyx.jdread.shop.model.ShopCartModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.util.Utils;

import java.util.List;

/**
 * Created by li on 2018/1/4.
 */

public class ShopCartFragment extends BaseFragment {
    private ShopCartBinding binding;
    private ShopCartAdapter shopCartAdapter;
    private ShopCartModel shopCartModel;
    private GPaginator paginator;
    private int defaultCurrent = 1;
    private int pages;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (ShopCartBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_shop_cart, container, false);
        initView();
        initData();
        initListener();
        return binding.getRoot();
    }

    private void initView() {
        binding.shopCartRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration decoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.shopCartRecycler.addItemDecoration(decoration);
        shopCartAdapter = new ShopCartAdapter();
        binding.shopCartRecycler.setAdapter(shopCartAdapter);
        paginator = binding.shopCartRecycler.getPaginator();
    }

    private void initData() {
        shopCartModel = ShopDataBundle.getInstance().getShopCartModel();
        final GetShopCartItemsAction action = new GetShopCartItemsAction();
        action.execute(ShopDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<ShopCartItemData> datas = shopCartModel.getDatas();
                if (datas != null && datas.size() > 0) {
                    shopCartModel.setSize(datas.size() + "");
                    shopCartModel.setSelectedAll(true);
                    binding.setModel(shopCartModel);
                    shopCartAdapter.setData(datas);
                    binding.shopCartRecycler.resize(shopCartAdapter.getRowCount(), shopCartAdapter.getColumnCount(), datas.size());
                    pages = paginator.pages();
                    shopCartModel.setPageSize(paginator.getProgressText());
                }
            }
        });
    }

    private void initListener() {
        if (shopCartAdapter != null) {
            shopCartAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    setAmount();
                }
            });
        }

        binding.shopCartCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedAll(binding.shopCartCheck.isChecked());
            }
        });

        binding.shopCartRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                shopCartModel.setPageSize((position / pageSize + 1) + "/" + pages);
            }
        });
    }

    private void setAmount() {
        int selected = 0;
        double cashBack = 0;
        double original = 0;
        List<ShopCartItemData> data = shopCartAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            ShopCartItemData itemData = data.get(i);
            if (itemData.isChecked()) {
                selected++;
                original = original + itemData.detail.jdPrice;
                cashBack = cashBack + itemData.reAmount;
            }
        }
        shopCartModel.setSize(selected + "");
        shopCartModel.setTotalAmount(Utils.keepPoints(original - cashBack));
        shopCartModel.setOriginalPrice(Utils.keepPoints(original));
        shopCartModel.setCashBack(Utils.keepPoints(cashBack));
        shopCartModel.setSelectedAll(selected == data.size());
    }

    private void selectedAll(boolean checked) {
        if (shopCartAdapter != null) {
            List<ShopCartItemData> data = shopCartAdapter.getData();
            if (data != null && data.size() > 0) {
                for (ShopCartItemData item : data) {
                    item.setChecked(checked);
                }
                setAmount();
            }
        }
    }
}
