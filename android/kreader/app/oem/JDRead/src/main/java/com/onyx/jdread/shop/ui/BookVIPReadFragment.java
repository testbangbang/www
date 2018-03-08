package com.onyx.jdread.shop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentBookVipReadBinding;
import com.onyx.jdread.library.ui.SearchBookFragment;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.personal.event.UserInfoEvent;
import com.onyx.jdread.personal.event.UserLoginResultEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.action.ShopMainConfigAction;
import com.onyx.jdread.shop.adapter.ShopMainConfigAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.event.BookItemClickEvent;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.TopRightTitleEvent;
import com.onyx.jdread.shop.event.ViewAllClickEvent;
import com.onyx.jdread.shop.event.VipButtonClickEvent;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.VipReadViewModel;
import com.onyx.jdread.shop.view.CustomRecycleView;
import com.onyx.jdread.shop.view.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by jackdeng on 2018/1/16.
 */

public class BookVIPReadFragment extends BaseFragment {
    private FragmentBookVipReadBinding bookVipReadBinding;
    private int space = ResManager.getInteger(R.integer.custom_recycle_view_space);
    private CustomRecycleView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookVipReadBinding = FragmentBookVipReadBinding.inflate(inflater, container, false);
        initView();
        initLibrary();
        initData();
        return bookVipReadBinding.getRoot();
    }

    private void initLibrary() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    private void initData() {
        getBookConfigData();
    }

    private void getBookConfigData() {
        ShopMainConfigAction configAction = new ShopMainConfigAction(Constants.BOOK_SHOP_VIP_CONFIG_CID);
        configAction.execute(getShopDataBundle(), new RxCallback<ShopMainConfigAction>() {
            @Override
            public void onNext(ShopMainConfigAction configAction) {
                bookVipReadBinding.scrollBar.setTotal(getVipReadViewModel().getTotalPages());
                if (recyclerView != null) {
                    recyclerView.setTotalPages(getVipReadViewModel().getTotalPages());
                }
                scrollToTop();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    private void scrollToTop() {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    private void initView() {
        setRecycleView();
        bookVipReadBinding.setViewModel(getVipReadViewModel());
        bookVipReadBinding.layoutTitleBar.titleBarRightIcon.setImageResource(R.mipmap.ic_shelf_search);
        getVipReadViewModel().getTitleBarViewModel().leftText = ResManager.getString(R.string.read_vip);
        getVipReadViewModel().getTitleBarViewModel().showRightText = true;
    }

    private void setRecycleView() {
        ShopMainConfigAdapter adapter = new ShopMainConfigAdapter();
        recyclerView = bookVipReadBinding.vipSubjectRecycleView;
        recyclerView.setPageTurningCycled(true);
        recyclerView.setLayoutManager(new DisableScrollLinearManager(JDReadApplication.getInstance()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(space));
        recyclerView.setAdapter(adapter);
        recyclerView.setOnPagingListener(new CustomRecycleView.OnPagingListener() {
            @Override
            public void onPageChange(int position) {
                bookVipReadBinding.scrollBar.setFocusPosition(position);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initLibrary();
        checkWifi();
    }

    private void checkWifi() {
        if (checkWifiAndGoNetWorkErrorFragment()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.NET_ERROR_TITLE, ResManager.getString(R.string.read_vip));
            setBundle(bundle);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    private EventBus getEventBus() {
        return getShopDataBundle().getEventBus();
    }

    public ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    private VipReadViewModel getVipReadViewModel() {
        return getShopDataBundle().getVipReadViewModel();
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
    public void onTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewAllClickEvent(ViewAllClickEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        BookModelConfigResultBean.DataBean.ModulesBean modulesBean = event.modulesBean;
        if (modulesBean != null) {
            JDPreferenceManager.setStringValue(Constants.SP_KEY_SUBJECT_NAME, modulesBean.show_name);
            JDPreferenceManager.setIntValue(Constants.SP_KEY_BOOK_LIST_TYPE, Constants.BOOK_LIST_TYPE_BOOK_MODEL);
            JDPreferenceManager.setIntValue(Constants.SP_KEY_SUBJECT_MODEL_ID, modulesBean.id);
            JDPreferenceManager.setIntValue(Constants.SP_KEY_SUBJECT_MODEL_TYPE, modulesBean.f_type);
            if (getViewEventCallBack() != null) {
                getViewEventCallBack().gotoView(ViewAllBooksFragment.class.getName());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookItemClickEvent(BookItemClickEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        JDPreferenceManager.setLongValue(Constants.SP_KEY_BOOK_ID, event.getBookBean().ebook_id);
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().gotoView(BookDetailFragment.class.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVipButtonClickEvent(VipButtonClickEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        if (getViewEventCallBack() != null) {
            String buttonContent = event.buttonContent;
            if (!StringUtils.isNullOrEmpty(buttonContent)) {
                if (ResManager.getString(R.string.login_immediately).equals(buttonContent)) {
                    LoginHelper.showUserLoginDialog(PersonalDataBundle.getInstance().getPersonalViewModel().getUserLoginViewModel(), getActivity());
                } else {
                    getViewEventCallBack().gotoView(BuyReadVIPFragment.class.getName());
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginResultEvent(UserLoginResultEvent event) {
        getBookConfigData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoEvent(UserInfoEvent event) {
        getBookConfigData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopRightTitleEvent(TopRightTitleEvent event) {
        getViewEventCallBack().gotoView(SearchBookFragment.class.getName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideLoadingDialog();
    }
}
