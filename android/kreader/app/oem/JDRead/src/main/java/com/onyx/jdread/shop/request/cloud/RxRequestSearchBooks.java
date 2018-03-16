package com.onyx.jdread.shop.request.cloud;

import android.graphics.Bitmap;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.common.util.UriUtil;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.SearchBooksRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.utils.CutBitmapTransformation;
import com.onyx.jdread.util.DataModelTranslateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by hehai on 17-3-30.
 */

public class RxRequestSearchBooks extends RxBaseCloudRequest {
    private SearchBooksRequestBean requestBean;
    private BookModelBooksResultBean resultBean;

    private boolean mapToDataModel = false;
    private boolean loadCover = false;
    private List<DataModel> dataModelList = new ArrayList<>();

    public BookModelBooksResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(SearchBooksRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public void setMapToDataModel(boolean mapTo) {
        this.mapToDataModel = mapTo;
    }

    public void setLoadCover(boolean loadCover) {
        this.loadCover = loadCover;
    }

    public List<DataModel> getDataModelList() {
        return dataModelList;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
        Call<BookModelBooksResultBean> call = getCall(getCommonService);
        resultBean = done(call);
        boolean valid = checkRequestResult();
        if (valid) {
            mapResultToDataModel(resultBean);
        }
    }

    private BookModelBooksResultBean done(Call<BookModelBooksResultBean> call) {
        EnhancedCall<BookModelBooksResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookModelBooksResultBean.class);
    }

    private boolean checkRequestResult() {
        if (resultBean != null && resultBean.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
            return false;
        }
        return true;
    }

    private Call<BookModelBooksResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getSearchBooks(requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    private void mapResultToDataModel(BookModelBooksResultBean resultBean) {
        if (!mapToDataModel) {
            return;
        }
        if (resultBean.data != null && !CollectionUtils.isNullOrEmpty(resultBean.data.items)) {
            DataModelTranslateUtils.cloudBookMapToDataModel(ShopDataBundle.getInstance().getEventBus(),
                    dataModelList, resultBean.data.items, loadCover(resultBean.data.items));
        }
    }

    private void ensureCoverUrl(ResultBookBean item) {
        if (StringUtils.isNotBlank(item.image_url) && !UriUtil.isNetworkUri(Uri.parse(item.image_url))) {
            item.image_url = CloudApiContext.DEFAULT_COVER_PRE_FIX + item.image_url;
        }
    }

    private Map<String, CloseableReference<Bitmap>> loadCover(List<ResultBookBean> items) {
        Map<String, CloseableReference<Bitmap>> thumbnailMap = new HashMap<>();
        if (!loadCover) {
            return thumbnailMap;
        }
        for (ResultBookBean item : items) {
            ensureCoverUrl(item);
            CloseableReference<Bitmap> refBitmap = CloseableReference.of(loadCoverBitmap(item.image_url), new ResourceReleaser<Bitmap>() {
                @Override
                public void release(Bitmap value) {
                }
            });
            if (refBitmap != null) {
                thumbnailMap.put(String.valueOf(item.ebook_id), refBitmap);
            }
        }
        return thumbnailMap;
    }

    private Bitmap loadCoverBitmap(String url) {
        try {
            return Glide.with(getAppContext())
                    .load(url)
                    .asBitmap()
                    .transform(CutBitmapTransformation.getInstance(getAppContext()))
                    .into(ViewTarget.SIZE_ORIGINAL, ViewTarget.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            return null;
        }
    }
}
