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
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.action.GetOrderUrlAction;
import com.onyx.jdread.personal.adapter.ShopCartAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetOrderUrlResultBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.action.AddOrDeleteCartAction;
import com.onyx.jdread.shop.action.GetShopCartItemsAction;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCartBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.UpdateBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.event.BookItemClickEvent;
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
        OnyxPageDividerItemDecoration decoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.shopCartRecycler.addItemDecoration(decoration);
        shopCartAdapter = new ShopCartAdapter();
        binding.shopCartRecycler.setAdapter(shopCartAdapter);
        paginator = binding.shopCartRecycler.getPaginator();
    }

    private void initData() {
        shopCartModel = ShopDataBundle.getInstance().getShopCartModel();
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
            final GetOrderUrlAction orderUrlAction = new GetOrderUrlAction(ids);
            orderUrlAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
                @Override
                public void onNext(Object o) {
                    GetOrderUrlResultBean orderUrlResultBean = PersonalDataBundle.getInstance().getOrderUrlResultBean();
                    if (orderUrlResultBean != null) {
                        String url = CloudApiContext.JD_BOOK_ORDER_URL + CloudApiContext.GotoOrder.ORDER_ORDERSTEP1_ACTION;
                        String tokenKey = CloudApiContext.GotoOrder.TOKENKEY;
                        String payUrl = url + tokenKey + orderUrlResultBean.getTokenKey();
                        PayFragment payFragment = new PayFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.PAY_URL, payUrl);
                        payFragment.setArguments(bundle);
                        payFragment.show(getActivity().getFragmentManager(), "");
                    }
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
        if (checkWfiDisConnected()) {
            return;
        }
        gotoBookDetailPage(Long.parseLong(event.getBookBean().getDetail().bookId));
    }

    private void gotoBookDetailPage(long ebookId) {
        JDPreferenceManager.setLongValue(Constants.SP_KEY_BOOK_ID, ebookId);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName());
        }
    }
}
