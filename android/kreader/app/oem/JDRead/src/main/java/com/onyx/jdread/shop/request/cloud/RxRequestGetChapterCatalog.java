package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.GetChapterGroupInfoRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BaseResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetChapterCatalogResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jackdeng on 2018/3/9.
 */

public class RxRequestGetChapterCatalog extends RxBaseCloudRequest {
    private GetChapterGroupInfoRequestBean requestBean;
    private GetChapterCatalogResultBean resultBean;
    private String chapterIds;

    public String getChapterIds() {
        return chapterIds;
    }

    public GetChapterCatalogResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(GetChapterGroupInfoRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
        Call<GetChapterCatalogResultBean> call = getCall(getCommonService);
        resultBean = done(call);
        checkResult();
    }

    private void checkResult() {
        if (resultBean != null) {
            if (BaseResultBean.checkSuccess(resultBean)) {
                GetChapterCatalogResultBean.DataBean data = resultBean.data;
                if (data != null) {
                    List<String> chapterIdList = new ArrayList<>();
                    if (data.has_volume) {
                        List<GetChapterCatalogResultBean.DataBean.VolumesBean> volumes = data.volumes;
                        for (GetChapterCatalogResultBean.DataBean.VolumesBean volumesBean : volumes) {
                            List<GetChapterCatalogResultBean.DataBean.VolumesBean.ChaptersBean> chapters = volumesBean.chapters;
                            for (GetChapterCatalogResultBean.DataBean.VolumesBean.ChaptersBean chaptersBean : chapters) {
                                if (chaptersBean.vip_flag == -1 || chaptersBean.buy) {
                                    chapterIdList.add(chaptersBean.id);
                                }
                            }
                        }
                    } else {
                        List<GetChapterCatalogResultBean.DataBean.VolumesBean.ChaptersBean> chapters = data.chapters;
                        for (GetChapterCatalogResultBean.DataBean.VolumesBean.ChaptersBean chaptersBean : chapters) {
                            if (chaptersBean.vip_flag == -1 || chaptersBean.buy) {
                                chapterIdList.add(chaptersBean.id);
                            }
                        }
                    }
                    StringBuffer chapterIdBuffer = new StringBuffer("[");
                    for (int i = 0; i < chapterIdList.size(); i++) {
                        String chapterId = chapterIdList.get(i);
                        if (i == chapterIdList.size() - 1) {
                            chapterIdBuffer.append(chapterId + "]");
                        } else {
                            chapterIdBuffer.append(chapterId + ",");
                        }
                    }
                    chapterIds = chapterIdBuffer.toString();
                    String path = getPath();
                    if (!FileUtils.fileExist(path)) {
                        FileUtils.mkdirs(path);
                    }
                    File dstFile = new File(path, String.valueOf(requestBean.bookId) + ".jdnovel");
                    FileUtils.saveContentToFile(JSONObjectParseUtils.toJson(data), dstFile);
                }
            } else {
                ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
            }
        }
    }

    private String getPath() {
        return CommonUtils.getJDNetBooksPath() + requestBean.bookId + "_" + requestBean.bookName;
    }

    private GetChapterCatalogResultBean done(Call<GetChapterCatalogResultBean> call) {
        EnhancedCall<GetChapterCatalogResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, GetChapterCatalogResultBean.class);
    }

    private Call<GetChapterCatalogResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getChapterCatalog(requestBean.bookId, requestBean.getBaseInfo().getRequestParamsMap());
    }
}
