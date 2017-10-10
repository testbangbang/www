package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.ReadingRateData;
import com.onyx.android.dr.data.database.ReadingRateEntity;
import com.onyx.android.dr.interfaces.ReadingRateView;
import com.onyx.android.dr.request.local.ReadingRateExport;
import com.onyx.android.dr.request.local.ReadingRateQueryAll;
import com.onyx.android.dr.request.local.ReadingRateQueryByTimeAndType;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class ReadingRatePresenter {
    private final ReadingRateView readingRateView;
    private ReadingRateData readingRateData;
    private Context context;
    public List<ReadingRateEntity> allData;

    public ReadingRatePresenter(Context context, ReadingRateView readingRateView) {
        this.readingRateView = readingRateView;
        this.context = context;
        readingRateData = new ReadingRateData();
    }

    public void getAllReadingRateData() {
        final ReadingRateQueryAll req = new ReadingRateQueryAll(readingRateData);
        readingRateData.getAllReadingRate(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                allData = req.getAllData();
                readingRateView.setReadingRateData(allData);
            }
        });
    }

    public void getDataByTimeAndType(String type, long startDate, long endDate) {
        final ReadingRateQueryByTimeAndType req = new ReadingRateQueryByTimeAndType(type, startDate, endDate);
        readingRateData.getDataByTimeAndType(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ArrayList<Boolean> checkList = req.getCheckList();
                readingRateView.setDataByTimeAndType(req.getData(), checkList);
            }
        });
    }

    public ArrayList<String> getHtmlTitleData() {
        ArrayList<String> htmlTitle = readingRateData.getHtmlTitle(context);
        return htmlTitle;
    }

    public void exportDataToHtml(final Context context, ArrayList<String> dataList, List<ReadingRateEntity> list) {
        final ReadingRateExport req = new ReadingRateExport(context, dataList, list);
        readingRateData.exportReadingRate(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
