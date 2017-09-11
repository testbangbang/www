package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.NewWordAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.NewWordData;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.interfaces.NewWordView;
import com.onyx.android.dr.request.local.NewWordDelete;
import com.onyx.android.dr.request.local.NewWordExport;
import com.onyx.android.dr.request.local.NewWordQueryByTime;
import com.onyx.android.dr.request.local.NewWordQueryByType;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.dr.R.string.please_select_delete_data;
import static com.onyx.android.dr.R.string.please_select_export_data;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class NewWordPresenter {
    private final NewWordData newWordData;
    private NewWordView newWordView;
    private Context context;
    private String tag = "";
    public List<NewWordNoteBookEntity> allData;

    public NewWordPresenter(Context context, NewWordView newWordView) {
        this.newWordView = newWordView;
        this.context = context;
        newWordData = new NewWordData();
    }

    public void getAllNewWordByType(int type) {
        final NewWordQueryByType req = new NewWordQueryByType(type);
        newWordData.getAllNewWordByType(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                allData = req.getNewWordList();
                ArrayList<Boolean> checkList = req.getCheckList();
                newWordView.setNewWordData(allData, checkList);
            }
        });
    }

    public void getNewWordByTime(int type, long startDate, long endDate) {
        final NewWordQueryByTime req = new NewWordQueryByTime(type, startDate, endDate);
        newWordData.getNewWordByTime(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                allData = req.getData();
                ArrayList<Boolean> checkList = req.getCheckList();
                newWordView.setNewWordByTime(allData, checkList);
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

    public void remoteAdapterData(ArrayList<Boolean> listCheck, NewWordAdapter adapter, List<NewWordNoteBookEntity> newWordList) {
        List<NewWordNoteBookEntity> exportNewWordList = getData(listCheck, newWordList);
        if (exportNewWordList == null || exportNewWordList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_delete_data));
            return;
        }
        int length = listCheck.size();
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                //delete basedata data
                NewWordNoteBookEntity bean = allData.get(i);
                deleteNewWord(bean.currentTime);
                allData.remove(i);
                listCheck.remove(i);
                adapter.notifyItemRemoved(i);
            }
        }
        adapter.notifyItemRangeChanged(0, allData.size());
    }

    public ArrayList<String> getHtmlTitle() {
        ArrayList<String> stringList = newWordData.setHtmlTitle(context);
        return stringList;
    }

    public void exportDataToHtml(final Context context, ArrayList<Boolean> listCheck, ArrayList<String> dataList, List<NewWordNoteBookEntity> list) {
        List<NewWordNoteBookEntity> exportNewWordList = getData(listCheck, list);
        if (exportNewWordList == null || exportNewWordList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_export_data));
            return;
        }
        final NewWordExport req = new NewWordExport(context, dataList, exportNewWordList);
        newWordData.exportNewWord(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (StringUtils.isNullOrEmpty(e + tag)) {
                    CommonNotices.showMessage(context, context.getString(R.string.export_failed));
                } else {
                    CommonNotices.showMessage(context, context.getString(R.string.export_success));
                }
            }
        });
    }

    private List<NewWordNoteBookEntity> getData(ArrayList<Boolean> listCheck, List<NewWordNoteBookEntity> list) {
        List<NewWordNoteBookEntity> exportNewWordList = new ArrayList<>();
        for (int i = 0, j = list.size(); i < j; i++) {
            Boolean aBoolean = listCheck.get(i);
            if (aBoolean) {
                NewWordNoteBookEntity newWordNoteBookEntity = list.get(i);
                if (!exportNewWordList.contains(newWordNoteBookEntity)) {
                    exportNewWordList.add(newWordNoteBookEntity);
                }
            }
        }
        return exportNewWordList;
    }
}
