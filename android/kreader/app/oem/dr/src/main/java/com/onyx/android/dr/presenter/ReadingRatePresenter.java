package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.ReadingRateData;
import com.onyx.android.dr.data.database.ReadingRateEntity;
import com.onyx.android.dr.interfaces.ReadingRateView;
import com.onyx.android.dr.request.local.InformalEssayQueryByTitle;
import com.onyx.android.dr.request.local.ReadingRateExport;
import com.onyx.android.dr.request.local.ReadingRateQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.dr.R.string.please_select_export_data;

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
        final ReadingRateQueryAll req = new ReadingRateQueryAll();
        readingRateData.getAllReadingRate(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                allData = req.getAllData();
                readingRateView.setReadingRateData(allData);
            }
        });
    }

    public void getInformalEssayQueryByTitle(String keyword) {
        final InformalEssayQueryByTitle req = new InformalEssayQueryByTitle(keyword);
        readingRateData.getInformalEssayQueryByTitle(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readingRateView.setInformalEssayByTitle( req.getData());
            }
        });
    }

    public ArrayList<String> getHtmlTitleData() {
        ArrayList<String> htmlTitle = readingRateData.getHtmlTitle(context);
        return htmlTitle;
    }

    public void exportDataToHtml(final Context context, ArrayList<Boolean> listCheck, ArrayList<String> dataList, List<ReadingRateEntity> list) {
        List<ReadingRateEntity> exportNewWordList = getData(listCheck, list);
        if (exportNewWordList == null || exportNewWordList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_export_data));
            return;
        }
        final ReadingRateExport req = new ReadingRateExport(context, dataList, exportNewWordList);
        readingRateData.exportReadingRate(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    private List<ReadingRateEntity> getData(ArrayList<Boolean> listCheck, List<ReadingRateEntity> list) {
        List<ReadingRateEntity> exportNewWordList = new ArrayList<>();
        for (int i = 0, j = list.size(); i < j; i++) {
            Boolean aBoolean = listCheck.get(i);
            if (aBoolean) {
                ReadingRateEntity bean = list.get(i);
                if (!exportNewWordList.contains(bean)) {
                    exportNewWordList.add(bean);
                }
            }
        }
        return exportNewWordList;
    }
}
