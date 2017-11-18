package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.manager.OperatingDataManager;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.GetBookReportList;
import com.onyx.android.sdk.data.model.v2.GetBookReportListRequestBean;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;

import retrofit2.Response;

/**
 * Created by li on 2017/9/19.
 */

public class GetBookReportListRequest extends AutoNetWorkConnectionBaseCloudRequest {
    private GetBookReportListRequestBean requestBean;
    private GetBookReportList bookReportList;
    private ArrayList<Boolean> listCheck = new ArrayList<>();

    public GetBookReportListRequest(GetBookReportListRequestBean requstBean) {
        this.requestBean = requstBean;
    }

    public GetBookReportList getBookReportList() {
        return bookReportList;
    }

    public ArrayList<Boolean> getCheckList() {
        return listCheck;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        try {
            Response<GetBookReportList> response = executeCall(ServiceFactory.getContentService(parent.
                    getCloudConf().getApiBase()).getImpressionsList(requestBean.offset,
                    requestBean.limit, requestBean.sortBy, requestBean.order));

            if (response != null) {
                bookReportList = response.body();
            }
            listCheck.clear();
            if (bookReportList.list != null && bookReportList.list.size() > 0) {
                for (int i = 0; i < bookReportList.list.size(); i++) {
                    listCheck.add(false);
                    OperatingDataManager.getInstance().insertReaderResponse(bookReportList.list.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
