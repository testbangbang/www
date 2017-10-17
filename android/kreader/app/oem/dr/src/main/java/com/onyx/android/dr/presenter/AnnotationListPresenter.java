package com.onyx.android.dr.presenter;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.AnnotationStatisticsBean;
import com.onyx.android.dr.data.AnnotationData;
import com.onyx.android.dr.interfaces.AnnotationView;
import com.onyx.android.dr.request.local.ExportAnnotationListRequest;
import com.onyx.android.dr.request.local.RequestGetAnnotationList;
import com.onyx.android.dr.request.local.RequestRemoveAnnotationList;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-9-27.
 */

public class AnnotationListPresenter {
    private AnnotationView annotationView;
    private final AnnotationData annotationData;

    public AnnotationListPresenter(AnnotationView annotationView) {
        this.annotationView = annotationView;
        annotationData = new AnnotationData();
    }

    public void getAnnotationList() {
        final RequestGetAnnotationList req = new RequestGetAnnotationList();
        annotationData.getAnnotationList(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (!CollectionUtils.isNullOrEmpty(req.getList())) {
                    annotationView.setAnnotationList(req.getList());
                }
            }
        });
    }

    public void removeAnnotation(List<AnnotationStatisticsBean> selectedList) {
        RequestRemoveAnnotationList req = new RequestRemoveAnnotationList(selectedList);
        annotationData.removeAnnotation(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getAnnotationList();
            }
        });
    }

    public ArrayList<String> getHtmlTitleData() {
        ArrayList<String> htmlTitle = annotationData.getHtmlTitle(DRApplication.getInstance());
        return htmlTitle;
    }

    public void exportDataToHtml(ArrayList<String> dataList, List<AnnotationStatisticsBean> list) {
        String title = DRApplication.getInstance().getResources().getString(R.string.postil);
        final ExportAnnotationListRequest req = new ExportAnnotationListRequest(title, dataList, list);
        annotationData.exportAnnotation(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
