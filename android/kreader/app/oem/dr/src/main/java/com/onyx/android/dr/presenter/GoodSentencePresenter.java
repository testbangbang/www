package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.GoodSentenceAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.GoodSentenceData;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.interfaces.GoodSentenceView;
import com.onyx.android.dr.request.local.GoodSentenceDeleteByTime;
import com.onyx.android.dr.request.local.GoodSentenceExport;
import com.onyx.android.dr.request.local.GoodSentenceQueryByTime;
import com.onyx.android.dr.request.local.GoodSentenceQueryByType;
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
public class GoodSentencePresenter {
    private final GoodSentenceData goodSentenceData;
    private GoodSentenceView goodSentenceView;
    private Context context;
    public List<GoodSentenceNoteEntity> allData;
    public String tag = "";

    public GoodSentencePresenter(Context context, GoodSentenceView goodSentenceView) {
        this.goodSentenceView = goodSentenceView;
        this.context = context;
        goodSentenceData = new GoodSentenceData();
    }

    public void getGoodSentenceByType(int type) {
        final GoodSentenceQueryByType req = new GoodSentenceQueryByType(type);
        goodSentenceData.getGoodSentenceByType(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                allData = req.getGoodSentenceList();
                ArrayList<Boolean> checkList = req.getCheckList();
                goodSentenceView.setGoodSentenceData(allData, checkList);
            }
        });
    }

    public void getGoodSentenceByTime(int type, long startDate, long endDate) {
        final GoodSentenceQueryByTime req = new GoodSentenceQueryByTime(type, startDate, endDate);
        goodSentenceData.getGoodSentenceByTime(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                allData = req.getData();
                ArrayList<Boolean> checkList = req.getCheckList();
                goodSentenceView.setGoodSentenceByTime(allData, checkList);
            }
        });
    }

    public void deleteGoodSentence(long time) {
        final GoodSentenceDeleteByTime req = new GoodSentenceDeleteByTime(time);
        goodSentenceData.deleteGoodSentence(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void remoteAdapterData(ArrayList<Boolean> listCheck, GoodSentenceAdapter adapter, List<GoodSentenceNoteEntity> list) {
        List<GoodSentenceNoteEntity> exportNewWordList = getData(listCheck, list);
        if (exportNewWordList == null || exportNewWordList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_delete_data));
            return;
        }
        int length = listCheck.size();
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                //delete basedata data
                GoodSentenceNoteEntity bean = allData.get(i);
                deleteGoodSentence(bean.currentTime);
                allData.remove(i);
                listCheck.remove(i);
                adapter.notifyItemRemoved(i);
            }
        }
        adapter.notifyItemRangeChanged(0, allData.size());
    }

    public ArrayList<String> getHtmlTitle() {
        ArrayList<String> stringList = goodSentenceData.setHtmlTitle(context);
        return stringList;
    }

    public void exportDataToHtml(final Context context, ArrayList<Boolean> listCheck, ArrayList<String> dataList, List<GoodSentenceNoteEntity> list) {
        List<GoodSentenceNoteEntity> exportNewWordList = getData(listCheck, list);
        if (exportNewWordList == null || exportNewWordList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_export_data));
            return;
        }
        final GoodSentenceExport req = new GoodSentenceExport(context, dataList, list);
        goodSentenceData.exportGoodSentence(context, req, new BaseCallback() {
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

    private List<GoodSentenceNoteEntity> getData(ArrayList<Boolean> listCheck, List<GoodSentenceNoteEntity> list) {
        List<GoodSentenceNoteEntity> exportNewWordList = new ArrayList<>();
        for (int i = 0, j = list.size(); i < j; i++) {
            Boolean aBoolean = listCheck.get(i);
            if (aBoolean) {
                GoodSentenceNoteEntity bean = list.get(i);
                if (!exportNewWordList.contains(bean)) {
                    exportNewWordList.add(bean);
                }
            }
        }
        return exportNewWordList;
    }
}
