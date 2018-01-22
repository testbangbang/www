package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.ShopMainConfigRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.BannerViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.SubjectViewModel;
import com.onyx.jdread.shop.request.cloud.RxRequestShopMainConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2018/1/10.
 */

public class ShopMainConfigAction extends BaseAction {

    private BookModelConfigResultBean resultBean;

    public BookModelConfigResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(final ShopDataBundle dataBundle, final RxCallback rxCallback) {
        ShopMainConfigRequestBean requestBean = new ShopMainConfigRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        String uri = String.format(CloudApiContext.BookShopURI.SHOP_MAIN_CONFIG_URI, String.valueOf(Constants.BOOK_SHOP_DEFAULT_CID));
        appBaseInfo.setSign(appBaseInfo.getSignValue(uri));
        requestBean.setAppBaseInfo(appBaseInfo);
        requestBean.setCid(Constants.BOOK_SHOP_DEFAULT_CID);
        RxRequestShopMainConfig request = new RxRequestShopMainConfig();
        request.setRequestBean(requestBean);
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
                setResult(dataBundle);
                if (rxCallback != null) {
                    rxCallback.onNext(ShopMainConfigAction.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
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
        BookModelConfigResultBean resultBean = getResultBean();
        if (resultBean != null) {
            BannerViewModel banerViewModel = new BannerViewModel();
            banerViewModel.setEventBus(dataBundle.getEventBus());
            banerViewModel.setDataBean(resultBean.data);
            dataBundle.getShopViewModel().setBannerSubjectIems(banerViewModel);
            BookModelConfigResultBean.DataBean data = resultBean.data;
            List<SubjectViewModel> subjectViewModelList = new ArrayList<>();
            for (int i = Constants.SHOP_MAIN_INDEX_THREE; i < data.modules.size(); i++) {
                SubjectViewModel subjectViewModel = new SubjectViewModel();
                subjectViewModel.setDataBean(data, i);
                subjectViewModel.setEventBus(dataBundle.getEventBus());
                subjectViewModelList.add(subjectViewModel);
            }
            dataBundle.getShopViewModel().setCommonSubjcet(subjectViewModelList);
        }
    }
}
