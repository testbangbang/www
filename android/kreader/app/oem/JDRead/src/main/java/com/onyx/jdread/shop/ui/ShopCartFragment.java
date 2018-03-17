package com.onyx.jdread.shop.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ShopCartBinding;
import com.onyx.jdread.library.view.LibraryDeleteDialog;
import com.onyx.jdread.main.common.BaseFragment;
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
import com.onyx.jdread.shop.event.BuyBookSuccessEvent;
import com.onyx.jdread.shop.event.CartBookItemClickEvent;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.model.ShopCartItemData;
import com.onyx.jdread.shop.model.ShopCartModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.utils.ViewHelper;
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
    private TopUpDialog topUpDialog;
    private boolean isBuying = false;

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
        shopCartModel.setSelectedAll(false);
        shopCartModel.setTotalAmount("0");
        shopCartModel.setOriginalPrice("0");
        shopCartModel.setCashBack("0");
        shopCartModel.setSize("0");
        binding.setModel(shopCartModel);
        binding.amountLayout.setVisibility(View.INVISIBLE);
        updateShopCartStatus(null, Constants.CART_TYPE_GET);
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
                updateShopCartView(shopCartModel.getDatas());
            }
        });
    }

    private void updateShopCartView(List<ShopCartItemData> datas) {
        if (datas == null) {
            return;
        }
        int size = datas.size();
        shopCartModel.setSize(size + "");
        shopCartModel.setSelectedAll(size > 0);
        shopCartModel.setCheckAllEnable(size > 0);
        shopCartAdapter.setData(datas);
        shopCartAdapter.notifyDataSetChanged();
        paginator.resize(shopCartAdapter.getRowCount(), shopCartAdapter.getColumnCount(), size);
        paginator.setCurrentPage(0);
        shopCartModel.setPageSize(paginator.getProgressText());
        binding.amountLayout.setVisibility(size > 0 ? View.VISIBLE : View.INVISIBLE);
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
                shopCartModel.setPageSize((position / pageSize + 1) + "/" + paginator.pages());
            }
        });

        binding.shopCartDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
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

    private void showDeleteDialog() {
        LibraryDeleteDialog.DialogModel dialogModel = new LibraryDeleteDialog.DialogModel();
        dialogModel.message.set(ResManager.getString(R.string.Delete_selected_book_from_shopp_cart) + "?");
        LibraryDeleteDialog.Builder builder = new LibraryDeleteDialog.Builder(getContext(), dialogModel);
        final LibraryDeleteDialog libraryDeleteDialog = builder.create();
        libraryDeleteDialog.show();
        dialogModel.setPositiveClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                deleteItems();
                libraryDeleteDialog.dismiss();
            }
        });
        dialogModel.setNegativeClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                libraryDeleteDialog.dismiss();
            }
        });

    }

    @Nullable
    private List<String> getSelectedBookIds() {
        if (shopCartAdapter == null || CollectionUtils.isNullOrEmpty(shopCartAdapter.getData())) {
            return null;
        }
        List<String> ids = new ArrayList<>();
        for (ShopCartItemData item : shopCartAdapter.getData()) {
            if (item.isChecked()) {
                ids.add(item.detail.bookId + "");
            }
        }
        return ids;
    }

    private void settlement() {
        List<String> ids = getSelectedBookIds();
        if (CollectionUtils.isNullOrEmpty(ids)) {
            ToastUtil.showToast(ResManager.getString(R.string.no_selected));
            return;
        }
        if (!checkWifi(ResManager.getString(R.string.cart))) {
            return;
        }
        getOrderInfo(ids.toArray(new String[0]));
    }

    private void getOrderInfo(@NonNull String[] bookIds) {
        if (ViewHelper.dialogIsShowing(topUpDialog) || isBuying()) {
            return;
        }
        setBuying(true);
        GetOrderInfoAction action = new GetOrderInfoAction(bookIds);
        action.execute(ShopDataBundle.getInstance(), new RxCallback<GetOrderInfoAction>() {
            @Override
            public void onNext(GetOrderInfoAction getOrderInfoAction) {
                showTopUpDialog(getOrderInfoAction.getDataBean());
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }

            @Override
            public void onFinally() {
                setBuying(false);
            }
        });
    }

    private void showTopUpDialog(GetOrderInfoResultBean.DataBean orderData) {
        if (orderData != null) {
            ViewHelper.dismissDialog(topUpDialog);
            topUpDialog = new TopUpDialog();
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.PAY_DIALOG_TYPE, Constants.PAY_DIALOG_TYPE_PAY_ORDER);
            bundle.putSerializable(Constants.ORDER_INFO, orderData);
            topUpDialog.setArguments(bundle);
            topUpDialog.show(getActivity().getFragmentManager(), "");
        }
    }

    private void deleteItems() {
        List<String> ids = getSelectedBookIds();
        if (CollectionUtils.isNullOrEmpty(ids)) {
            ToastUtil.showToast(ResManager.getString(R.string.no_selected));
            return;
        }
        updateShopCartStatus(ids.toArray(new String[0]), Constants.CART_TYPE_DEL);
    }

    private void updateShopCartStatus(@Nullable String[] bookIds, @NonNull String cartType) {
        final AddOrDeleteCartAction action = new AddOrDeleteCartAction(bookIds, cartType);
        action.execute(ShopDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                UpdateBean updateBean = action.getData();
                if (updateBean != null) {
                    if (!CollectionUtils.isNullOrEmpty(updateBean.ebooks)) {
                        getCartItems(updateBean.ebooks);
                    }
                }
            }
        });
    }

    private void setAmount() {
        int selected = 0;
        int cashBack = 0;
        int original = 0;
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
        shopCartModel.setTotalAmount(String.valueOf(original - cashBack));
        shopCartModel.setOriginalPrice(String.valueOf(original));
        shopCartModel.setCashBack(String.valueOf(cashBack));
        shopCartModel.setSelectedAll(selected == data.size());
        shopCartModel.setSettlementEnable(selected != 0);
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
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        if (isAdded()) {
            showLoadingDialog(getString(event.getResId()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        hideLoadingDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBuyBookSuccessEvent(BuyBookSuccessEvent event) {
        List<String> ids = getSelectedBookIds();
        if (CollectionUtils.isNullOrEmpty(ids)) {
            return;
        }
        updateShopCartStatus(ids.toArray(new String[0]), Constants.CART_TYPE_DEL);
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

    @Override
    public void onDetach() {
        super.onDetach();
        hideAllDialog();
    }

    private void hideAllDialog() {
        hideLoadingDialog();
        ViewHelper.dismissDialog(topUpDialog);
    }

    private void setBuying(boolean buying) {
        this.isBuying = buying;
    }

    private boolean isBuying() {
        return isBuying;
    }
}
