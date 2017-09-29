package com.onyx.android.dr.request.local;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.ReadingRateBean;
import com.onyx.android.dr.util.ExportToHtmlUtils;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class ReadingRateExport extends BaseDataRequest {
    private final Context context;
    private final ArrayList<String> dataList;
    private final List<ReadingRateBean> list;

    public ReadingRateExport(Context context, ArrayList<String> dataList, List<ReadingRateBean> list) {
        this.context = context;
        this.dataList = dataList;
        this.list = list;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        exportData();
    }

    public void exportData() {
        ExportToHtmlUtils.exportReadingRateToHtml(context, dataList, context.getString(R.string.infromal_essay_html), list);
    }
}
