package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BaseRequestInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.SubjectViewModel;
import com.onyx.jdread.shop.request.cloud.RxRequestBookRank;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class BookRankAction extends BaseAction<ShopDataBundle> {

    private BookModelConfigResultBean resultBean;

    public BookModelConfigResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        BaseRequestInfo requestBean = new BaseRequestInfo();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        appBaseInfo.setSign(appBaseInfo.getSignValue(CloudApiContext.BookShopURI.BOOK_RANK_URI));
        requestBean.setAppBaseInfo(appBaseInfo);
        RxRequestBookRank request = new RxRequestBookRank();
        request.setRequestBean(requestBean);
        request.execute(new RxCallback<RxRequestBookRank>() {

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showLoadingDialog(shopDataBundle, R.string.loading);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                hideLoadingDialog(shopDataBundle);
            }

            @Override
            public void onNext(RxRequestBookRank request) {
                resultBean = request.getResultBean();
                setResult(shopDataBundle);
                if (rxCallback != null) {
                    rxCallback.onNext(BookRankAction.this);
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

    private void setResult(ShopDataBundle shopDataBundle) {
        BookModelConfigResultBean resultBean = getResultBean();
        if (resultBean != null) {
            if (resultBean != null) {
                BookModelConfigResultBean.DataBean data = resultBean.data;
                List<SubjectViewModel> subjectViewModelList = new ArrayList<>();
                for (int i = 0; i < data.modules.size(); i++) {
                    BookModelConfigResultBean.DataBean.ModulesBean modulesBean = data.modules.get(i);
                    if (filterRankList(modulesBean)) {
                        SubjectViewModel subjectViewModel = new SubjectViewModel();
                        subjectViewModel.setDataBean(data, i);
                        subjectViewModel.setEventBus(shopDataBundle.getEventBus());
                        subjectViewModelList.add(subjectViewModel);
                        if (subjectViewModelList.size() == Constants.RANK_LIST_SIZE) {
                            break;
                        }
                    }
                }
                shopDataBundle.getRankViewModel().setRankItems(subjectViewModelList);
            }
        }
    }

    private boolean filterRankList(BookModelConfigResultBean.DataBean.ModulesBean modulesBean) {
        int id = modulesBean.id;
        //TODO filter the special ranking list
        return true;
    }
}
