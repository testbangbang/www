package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfo;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.cloud.entity.ShopMainConfigRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.BaseSubjectViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.VipUserInfoViewModel;
import com.onyx.jdread.shop.request.cloud.RxRequestShopMainConfig;
import com.onyx.jdread.shop.utils.ViewHelper;

import java.util.List;

/**
 * Created by jackdeng on 2018/1/10.
 */

public class ShopMainConfigAction extends BaseAction {

    private BookModelConfigResultBean resultBean;
    private List<BaseSubjectViewModel> commonSubjcet;
    private int cid;

    public ShopMainConfigAction(int cid) {
        this.cid = cid;
    }

    public BookModelConfigResultBean getResultBean() {
        return resultBean;
    }

    public List<BaseSubjectViewModel> getCommonSubjcet() {
        return commonSubjcet;
    }

    @Override
    public void execute(final ShopDataBundle dataBundle, final RxCallback rxCallback) {
        ShopMainConfigRequestBean requestBean = new ShopMainConfigRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        String uri = String.format(CloudApiContext.BookShopURI.SHOP_MAIN_CONFIG_URI, String.valueOf(cid));
        appBaseInfo.setSign(appBaseInfo.getSignValue(uri));
        requestBean.setAppBaseInfo(appBaseInfo);
        requestBean.setCid(cid);
        RxRequestShopMainConfig request = new RxRequestShopMainConfig();
        request.setRequestBean(requestBean);
        RxRequestShopMainConfig.setAppContext(JDReadApplication.getInstance().getApplicationContext());
        request.execute(new RxCallback<RxRequestShopMainConfig>() {

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showLoadingDialog(dataBundle, R.string.loading);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                hideLoadingDialog(dataBundle);
            }

            @Override
            public void onNext(RxRequestShopMainConfig request) {
                resultBean = request.getResultBean();
                commonSubjcet = request.getSubjectDataList();
                setResult(dataBundle);
                if (rxCallback != null) {
                    rxCallback.onNext(ShopMainConfigAction.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
                if (rxCallback != null) {
                    rxCallback.onError(throwable);
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                if (rxCallback != null) {
                    rxCallback.onComplete();
                }
            }
        });
    }

    private void setResult(ShopDataBundle dataBundle) {
        if (commonSubjcet != null) {
            if (cid == Constants.BOOK_SHOP_NEW_BOOK_CONFIG_CID) {
                dataBundle.getNewBookViewModel().setSubjectModels(commonSubjcet);
                int totalPages = ViewHelper.calculateTotalPages(commonSubjcet, Constants.COMMOM_SUBJECT_RECYCLE_HEIGHT);
                dataBundle.getNewBookViewModel().setTotalPages(totalPages);
            } else if (cid == Constants.BOOK_SHOP_SALE_BOOK_CONFIG_CID) {
                dataBundle.getBookSaleViewModel().setSubjectModels(commonSubjcet);
                int totalPages = ViewHelper.calculateTotalPages(commonSubjcet, Constants.COMMOM_SUBJECT_RECYCLE_HEIGHT);
                dataBundle.getBookSaleViewModel().setTotalPages(totalPages);
            } else if (cid == Constants.BOOK_SHOP_VIP_CONFIG_CID) {
                VipUserInfoViewModel vipUserInfoViewModel = dataBundle.getVipUserInfoViewModel();
                setVipUserInfo(vipUserInfoViewModel);
                commonSubjcet.add(0, vipUserInfoViewModel);
                dataBundle.getVipReadViewModel().setSubjectModels(commonSubjcet);
                int totalPages = ViewHelper.calculateTotalPages(commonSubjcet, Constants.COMMOM_SUBJECT_RECYCLE_HEIGHT);
                dataBundle.getVipReadViewModel().setTotalPages(totalPages);
            }
        }
    }

    private void setVipUserInfo(VipUserInfoViewModel vipUserInfoViewModel) {
        UserInfo userInfo = PersonalDataBundle.getInstance().getUserInfo();
        boolean login = JDReadApplication.getInstance().getLogin();
        vipUserInfoViewModel.showLoginButton.set(!login);
        String imgUrl = "";
        String userName = "";
        String buttonContent = "";
        String vipStatus = "";
        if (login) { //login
            imgUrl = LoginHelper.getImgUrl();
            userName = LoginHelper.getUserName();
            if (userInfo != null) {
                if(userInfo.vip_remain_days > 0) { //vip
                    vipStatus = String.format(ResManager.getString(R.string.vip_read_days), userInfo.vip_remain_days);
                    buttonContent = ResManager.getString(R.string.renew_vip_read);
                } else { //not vip
                    vipStatus = ResManager.getString(R.string.not_open_vip_read);
                    buttonContent = ResManager.getString(R.string.open_vip_read);
                }
            }
        }
        vipUserInfoViewModel.name.set(userName);
        vipUserInfoViewModel.vipStatus.set(vipStatus);
        vipUserInfoViewModel.imageUrl.set(imgUrl);
        vipUserInfoViewModel.buttonContent.set(buttonContent);
    }
}
