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
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.adapter.ShopCartAdapter;
import com.onyx.jdread.personal.dialog.TopUpDialog;
import com.onyx.jdread.shop.action.AddOrDeleteCartAction;
import com.onyx.jdread.shop.action.GetOrderInfoAction;
import com.onyx.jdread.shop.action.GetShopCartItemsAction;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCartBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetOrderInfoResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.UpdateBean;
import com.onyx.jdread.shop.event.CartBookItemClickEvent;
import com.onyx.jdread.shop.model.ShopCartItemData;
import com.onyx.jdread.shop.model.ShopCartModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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
        binding.shopCartRecycler.setPageTurningCycled(true);
        OnyxPageDividerItemDecoration decoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.shopCartRecycler.addItemDecoration(decoration);
        shopCartAdapter = new ShopCartAdapter();
        binding.shopCartRecycler.setAdapter(shopCartAdapter);
        paginator = binding.shopCartRecycler.getPaginator();
        checkWifi(ResManager.getString(R.string.cart));
    }

    private void initData() {
        shopCartModel = ShopDataBundle.getInstance().getShopCartModel();
        shopCartModel.setSettlementEnable(false);
        shopCartModel.setCheckAllEnable(false);
        shopCartModel.setTotalAmount("0");
        shopCartModel.setOriginalPrice("0");
        shopCartModel.setCashBack("0");
        shopCartModel.setSize("0");
        binding.setModel(shopCartModel);
        final AddOrDeleteCartAction getShopCartIdsAction = new AddOrDeleteCartAction(null, Constants.CART_TYPE_GET);
        getShopCartIdsAction.execute(ShopDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                UpdateBean data = getShopCartIdsAction.getData();
                if (data != null) {
                    List<BookCartBean> ebooks = data.ebooks;
                    if (ebooks != null && ebooks.size() > 0) {
                        getCartItems(ebooks);
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.ensureRegister(EventBus.getDefault(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(EventBus.getDefault(), this);
    }

    private void getCartItems(List<BookCartBean> ebooks) {
        GetShopCartItemsAction action = new GetShopCartItemsAction(ebooks);
        action.execute(ShopDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<ShopCartItemData> datas = shopCartModel.getDatas();
                if (datas != null) {
                    shopCartModel.setSize(datas.size() + "");
                    shopCartModel.setSelectedAll(true);
                    shopCartAdapter.setData(datas);
                    binding.shopCartRecycler.notifyDataSetChanged();
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
                shopCartModel.setSelectedAll(!shopCartModel.isSelectedAll());
                selectedAll(shopCartModel.isSelectedAll());
            }
        });

        binding.shopCartRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                shopCartModel.setPageSize((position / pageSize + 1) + "/" + pages);
            }
        });

        binding.shopCartDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItems();
            }
        });

        binding.shopCartSettlement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settlement();
            }
        });

        binding.shopCartTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewEventCallBack.viewBack();
            }
        });
    }

    private void settlement() {
        if (shopCartAdapter == null) {
            return;
        }
        List<ShopCartItemData> data = shopCartAdapter.getData();
        if (data != null && data.size() > 0) {
            List<String> ids = new ArrayList<>();
            for (ShopCartItemData item : data) {
                if (item.isChecked()) {
                    ids.add(item.detail.bookId + "");
                }
            }
            if (ids.size() == 0) {
                ToastUtil.showToast(ResManager.getString(R.string.no_selected));
                return;
            }

            String[] bookIds = new String[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                bookIds[i] = ids.get(i);
            }
            getOrderInfo(bookIds);
        }
    }

    private void getOrderInfo(String[] bookIds) {
        if (bookIds != null) {
            GetOrderInfoAction action = new GetOrderInfoAction(bookIds);
            action.execute(ShopDataBundle.getInstance(), new RxCallback<GetOrderInfoAction>() {
                @Override
                public void onNext(GetOrderInfoAction getOrderInfoAction) {
                    GetOrderInfoResultBean.DataBean dataBean = getOrderInfoAction.getDataBean();
                    if (dataBean != null) {
                        TopUpDialog dialog = new TopUpDialog();
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.PAY_DIALOG_TYPE, Constants.PAY_DIALOG_TYPE_PAY_ORDER);
                        bundle.putSerializable(Constants.ORDER_INFO, dataBean);
                        dialog.setArguments(bundle);
                        dialog.show(getActivity().getFragmentManager(), "");
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                }
            });
        }
    }

    private void deleteItems() {
        if (shopCartAdapter == null) {
            return;
        }
        List<ShopCartItemData> data = shopCartAdapter.getData();
        if (data != null && data.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (ShopCartItemData item : data) {
                if (item.isChecked()) {
                    sb.append(item.detail.bookId + ",");
                }
            }
            if (sb.length() == 0) {
                ToastUtil.showToast(ResManager.getString(R.string.no_selected));
                return;
            }
            String s = sb.deleteCharAt(sb.length() - 1).toString();
            String[] ids = CommonUtils.string2Arr(s);
            final AddOrDeleteCartAction addOrDeleteCartAction = new AddOrDeleteCartAction(ids, Constants.CART_TYPE_DEL);
            addOrDeleteCartAction.execute(ShopDataBundle.getInstance(), new RxCallback() {
                @Override
                public void onNext(Object o) {
                    UpdateBean updateBean = addOrDeleteCartAction.getData();
                    if (updateBean != null) {
                        List<BookCartBean> ebooks = updateBean.ebooks;
                        if (ebooks != null) {
                            getCartItems(ebooks);
                        }
                    }
                }
            });
        }
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
        shopCartModel.setSettlementEnable(selected == 0 ? false : true);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookClickEvent(CartBookItemClickEvent event) {
        gotoBookDetailPage(Long.parseLong(event.getBookBean().getDetail().bookId));
    }

    private void gotoBookDetailPage(long ebookId) {
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.SP_KEY_BOOK_ID, ebookId);
        getViewEventCallBack().gotoView(BookDetailFragment.class.getName(), bundle);
    }
}
