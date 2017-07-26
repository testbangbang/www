package com.onyx.android.dr.request.local;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.util.ExportToHtmlUtils;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class InformalEssayExport extends BaseDataRequest {
    private final Context context;
    private final ArrayList<String> dataList;
    private final List<InformalEssayEntity> infromalEssayList;

    public InformalEssayExport(Context context, ArrayList<String> dataList, List<InformalEssayEntity> infromalEssayList) {
        this.context = context;
        this.dataList = dataList;
        this.infromalEssayList = infromalEssayList;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        exportData();
    }

    public void exportData() {
        ExportToHtmlUtils.exportInfromalEssayToHtml(dataList, context.getString(R.string.infromal_essay_html), infromalEssayList);
    }
}
