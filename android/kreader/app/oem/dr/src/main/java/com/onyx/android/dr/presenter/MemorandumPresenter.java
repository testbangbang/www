package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.adapter.MemorandumAdapter;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.MemorandumConfig;
import com.onyx.android.dr.data.MemorandumData;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.interfaces.MemorandumView;
import com.onyx.android.dr.request.local.MemorandumDelete;
import com.onyx.android.dr.request.local.MemorandumExport;
import com.onyx.android.dr.request.local.MemorandumQueryAll;
import com.onyx.android.dr.request.local.MemorandumQueryByTime;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.dr.R.string.please_select_delete_data;
import static com.onyx.android.dr.R.string.please_select_export_data;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class MemorandumPresenter {
    private final MemorandumView memorandumView;
    private MemorandumData memorandumData;
    private MemorandumConfig memorandumConfig;
    private Context context;
    public List<MemorandumEntity> allDatas;
    private String tag = "";

    public MemorandumPresenter(Context context, MemorandumView memorandumView) {
        this.memorandumView = memorandumView;
        this.context = context;
        memorandumData = new MemorandumData();
        memorandumConfig = new MemorandumConfig();
    }

    public void getAllMemorandumData() {
        final MemorandumQueryAll req = new MemorandumQueryAll();
        memorandumData.getAllMemorandum(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void getMemorandumDataByTime(long startDate, long endDate) {
        final MemorandumQueryByTime req = new MemorandumQueryByTime(startDate, endDate);
        memorandumData.getMemorandumByTime(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                allDatas = req.getAllData();
                memorandumView.setMemorandumData(req.getAllData());
            }
        });
    }

    public void deleteMemorandum(long time) {
        final MemorandumDelete req = new MemorandumDelete(time, true);
        memorandumData.deleteMemorandum(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public ArrayList<String> getHtmlTitle() {
        ArrayList<String> stringList = memorandumData.setHtmlTitle(context);
        return stringList;
    }

    public void remoteAdapterData(ArrayList<Boolean> listCheck, MemorandumAdapter adapter, List<MemorandumEntity> newWordList) {
        List<MemorandumEntity> exportNewWordList = getData(listCheck, newWordList);
        if (exportNewWordList == null || exportNewWordList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_delete_data));
            return;
        }
        int length = listCheck.size();
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                //delete basedata data
                MemorandumEntity bean = allDatas.get(i);
                deleteMemorandum(bean.currentTime);
                allDatas.remove(i);
                listCheck.remove(i);
                adapter.notifyItemRemoved(i);
                adapter.notifyItemRangeChanged(0, allDatas.size());
            }
        }
    }

    public void exportDataToHtml(final Context context, ArrayList<Boolean> listCheck, ArrayList<String> dataList, List<MemorandumEntity> list) {
        List<MemorandumEntity> exportNewWordList = getData(listCheck, list);
        if (exportNewWordList == null || exportNewWordList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_export_data));
            return;
        }
        final MemorandumExport req = new MemorandumExport(context, dataList, exportNewWordList);
        memorandumData.exportMemorandum(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    private List<MemorandumEntity> getData(ArrayList<Boolean> listCheck, List<MemorandumEntity> list) {
        List<MemorandumEntity> exportNewWordList = new ArrayList<>();
        for (int i = 0, j = list.size(); i < j; i++) {
            Boolean aBoolean = listCheck.get(i);
            if (aBoolean) {
                MemorandumEntity bean = list.get(i);
                if (!exportNewWordList.contains(bean)) {
                    exportNewWordList.add(bean);
                }
            }
        }
        return exportNewWordList;
    }
}
