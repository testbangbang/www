package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.adapter.NewWordAdapter;
import com.onyx.android.dr.data.NewWordData;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.interfaces.NewWordView;
import com.onyx.android.dr.request.local.NewWordDelete;
import com.onyx.android.dr.request.local.NewWordExport;
import com.onyx.android.dr.request.local.NewWordQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class NewWordPresenter {
    private final NewWordData newWordData;
    private NewWordView newWordView;
    private Context context;
    public List<NewWordNoteBookEntity> allData;

    public NewWordPresenter(Context context, NewWordView newWordView) {
        this.newWordView = newWordView;
        this.context = context;
        newWordData = new NewWordData();
    }

    public void getAllNewWordData() {
        final NewWordQueryAll req = new NewWordQueryAll();
        newWordData.getAllNewWord(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                allData = req.getNewWordList();
                ArrayList<Boolean> checkList = req.getCheckList();
                newWordView.setNewWordData(allData, checkList);
            }
        });
    }

    public void deleteNewWord(long time) {
        final NewWordDelete req = new NewWordDelete(time, true);
        newWordData.deleteNewWord(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void remoteAdapterDatas(ArrayList<Boolean> listCheck, NewWordAdapter adapter) {
        int length = listCheck.size();
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                //delete basedata data
                NewWordNoteBookEntity bean = allData.get(i);
                deleteNewWord(bean.currentTime);
                allData.remove(i);
                listCheck.remove(i);
                adapter.notifyItemRemoved(i);
                adapter.notifyItemRangeChanged(0, allData.size());
            }
        }
    }

    public void getHtmlTitle() {
        newWordData.setHtmlTitle(context);
    }

    public void exportDataToHtml(Context context, ArrayList<String> dataList, List<NewWordNoteBookEntity> list) {
        final NewWordExport req = new NewWordExport(context, dataList, list);
        newWordData.exportInformalEssay(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
