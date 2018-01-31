package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.ShopMainConfigRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
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
    private List<BookModelConfigResultBean.DataBean.AdvBean> bannerList;
    private List<BookModelConfigResultBean.DataBean.ModulesBean> subjectDataList;
    private List<SubjectViewModel> commonSubjcet;
    private int cid;

    public ShopMainConfigAction(int cid) {
        this.cid = cid;
    }

    public BookModelConfigResultBean getResultBean() {
        return resultBean;
    }

    public List<SubjectViewModel> getCommonSubjcet() {
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
                bannerList = request.getBannerList();
                subjectDataList = request.getSubjectDataList();
                setResult(cid, dataBundle);
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

    private void setResult(int cid, ShopDataBundle dataBundle) {
        if (cid == Constants.BOOK_SHOP_MAIN_CONFIG_CID) {
            if (bannerList != null) {
                dataBundle.getShopViewModel().getBannerViewModel().setBannerList(bannerList);
            }
            if (subjectDataList != null) {
                List<SubjectViewModel> commonSubjcet = dataBundle.getShopViewModel().getCommonSubjcet();
                for (int i = 0; i < subjectDataList.size(); i++) {
                    BookModelConfigResultBean.DataBean.ModulesBean modulesBean = subjectDataList.get(i);
                    commonSubjcet.get(i).setModelBean(modulesBean);
                }
            }
        } else {
            if (subjectDataList != null) {
                if (commonSubjcet != null) {
                    commonSubjcet.clear();
                } else {
                    commonSubjcet = new ArrayList<>();
                }
                for (int i = 0; i < subjectDataList.size(); i++) {
                    BookModelConfigResultBean.DataBean.ModulesBean modulesBean = subjectDataList.get(i);
                    SubjectViewModel subjectViewModel = new SubjectViewModel();
                    subjectViewModel.setEventBus(dataBundle.getEventBus());
                    subjectViewModel.setModelBean(modulesBean);
                    commonSubjcet.add(subjectViewModel);
                }
            }
        }
    }
}
