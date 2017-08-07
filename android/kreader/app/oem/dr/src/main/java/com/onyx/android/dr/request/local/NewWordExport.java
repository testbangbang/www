package com.onyx.android.dr.request.local;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.util.ExportToHtmlUtils;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class NewWordExport extends BaseDataRequest {
    private final Context context;
    private final ArrayList<String> dataList;
    private final List<NewWordNoteBookEntity> newWordList;

    public NewWordExport(Context context, ArrayList<String> dataList, List<NewWordNoteBookEntity> newWordList) {
        this.context = context;
        this.dataList = dataList;
        this.newWordList = newWordList;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        exportData();
    }

    public void exportData() {
        ExportToHtmlUtils.exportNewWordToHtml(context, dataList, context.getString(R.string.new_word_notebook_html), newWordList);
    }
}
