package com.onyx.android.dr.request.local;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.util.ExportToHtmlUtils;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class GoodSentenceExport extends BaseDataRequest {
    private final Context context;
    private final ArrayList<String> dataList;
    private final List<GoodSentenceNoteEntity> goodSentenceList;

    public GoodSentenceExport(Context context, ArrayList<String> dataList, List<GoodSentenceNoteEntity> goodSentenceList) {
        this.context = context;
        this.dataList = dataList;
        this.goodSentenceList = goodSentenceList;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        exportData();
    }

    public void exportData() {
        ExportToHtmlUtils.exportGoodSentenceToHtml(context, dataList, context.getString(R.string.good_sentence_notebook_html), goodSentenceList);
    }
}
