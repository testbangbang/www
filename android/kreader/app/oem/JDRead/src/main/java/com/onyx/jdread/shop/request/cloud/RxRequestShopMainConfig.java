package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.ShopMainConfigRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class RxRequestShopMainConfig extends RxBaseCloudRequest {

    private ShopMainConfigRequestBean requestBean;
    private BookModelConfigResultBean resultBean;
    private List<BookModelConfigResultBean.DataBean.AdvBean> bannerList;
    private List<BookModelConfigResultBean.DataBean.ModulesBean> subjectDataList;

    public List<BookModelConfigResultBean.DataBean.AdvBean> getBannerList() {
        return bannerList;
    }

    public List<BookModelConfigResultBean.DataBean.ModulesBean> getSubjectDataList() {
        return subjectDataList;
    }

    public BookModelConfigResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(ShopMainConfigRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
        Call<BookModelConfigResultBean> call = getCall(getCommonService);
        resultBean = done(call);
        checkRequestResult();
    }

    private void checkRequestResult() {
        if (resultBean != null) {
            parseResult();
        }
    }

    private void parseResult() {
        BookModelConfigResultBean resultBean = getResultBean();
        if (resultBean != null) {
            BookModelConfigResultBean.DataBean data = resultBean.data;
            initDataContainer();
            if (requestBean.getCid() == Constants.BOOK_SHOP_MAIN_CONFIG_CID) {
                parseAdvBeanList(data);
                for (int i = Constants.SHOP_MAIN_INDEX_THREE; i < data.modules.size(); i++) {
                    parseMainSubjectDataList(data, i);
                }
            } else {
                for (int i = 0; i < data.modules.size(); i++) {
                    parseCommonSubjectDataList(data,i);
                }
            }
        }
    }

    private void initDataContainer() {
        if (subjectDataList == null) {
            subjectDataList = new ArrayList<>();
        } else {
            subjectDataList.clear();
        }
    }

    private void parseCommonSubjectDataList(BookModelConfigResultBean.DataBean dataBean, int index) {
        if (dataBean.ebook != null && dataBean.modules != null) {
            ArrayList<ResultBookBean> bookList = new ArrayList<>();
            BookModelConfigResultBean.DataBean.ModulesBean modulesBean = dataBean.modules.get(index);
            List<BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean> items = modulesBean.items;
            for (BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean itemsBean : items) {
                ResultBookBean bookBean = dataBean.ebook.get(itemsBean.id);
                bookList.add(bookBean);
            }
            modulesBean.bookList = bookList;
            if (index % 2 == 1) {
                if (dataBean.modules.size() - 1 >= (index + 1)) {
                    BookModelConfigResultBean.DataBean.ModulesBean modulesBeanNext = dataBean.modules.get(index + 1);
                    modulesBean.show_name_next = modulesBeanNext.show_name;
                    modulesBean.f_type_next = modulesBeanNext.f_type;
                    modulesBean.id_next = modulesBeanNext.id;
                    modulesBean.showNextTitle = true;
                }
            }
            subjectDataList.add(modulesBean);
        }
    }

    private void parseAdvBeanList(BookModelConfigResultBean.DataBean dataBean) {
        if (dataBean.modules != null && dataBean.ebook != null) {
            if (bannerList == null) {
                bannerList = new ArrayList<>();
            } else {
                bannerList.clear();
            }
            List<BookModelConfigResultBean.DataBean.ModulesBean> bannerModules = dataBean.modules.subList(0, Constants.SHOP_MAIN_INDEX_TWO);
            for (BookModelConfigResultBean.DataBean.ModulesBean modulesBean : bannerModules) {
                List<BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean> items = modulesBean.items;
                for (BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean itemsBean : items) {
                    BookModelConfigResultBean.DataBean.AdvBean advBean = dataBean.adv.get(itemsBean.id);
                    bannerList.add(advBean);
                }
            }
        }
    }

    public void parseMainSubjectDataList(BookModelConfigResultBean.DataBean dataBean, int index) {
        if (dataBean.ebook != null && dataBean.modules != null) {
            ArrayList<ResultBookBean> bookList = new ArrayList<>();
            BookModelConfigResultBean.DataBean.ModulesBean modulesBean = dataBean.modules.get(index);
            List<BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean> items = modulesBean.items;
            for (BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean itemsBean : items) {
                ResultBookBean bookBean = dataBean.ebook.get(itemsBean.id);
                bookList.add(bookBean);
            }
            modulesBean.bookList = bookList;
            if (index >= Constants.SHOP_MAIN_INDEX_SIX && index <= Constants.SHOP_MAIN_INDEX_TEN && index % 2 == 0) {
                if (dataBean.modules.size() - 1 >= (index + 1)) {
                    BookModelConfigResultBean.DataBean.ModulesBean modulesBeanNext = dataBean.modules.get(index + 1);
                    modulesBean.show_name_next = modulesBeanNext.show_name;
                    modulesBean.f_type_next = modulesBeanNext.f_type;
                    modulesBean.id_next = modulesBeanNext.id;
                    modulesBean.showNextTitle = true;
                }
            }
            subjectDataList.add(modulesBean);
        }
    }

    private BookModelConfigResultBean done(Call<BookModelConfigResultBean> call) {
        EnhancedCall<BookModelConfigResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookModelConfigResultBean.class);
    }

    private Call<BookModelConfigResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getShopMainConfig(requestBean.getCid(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
