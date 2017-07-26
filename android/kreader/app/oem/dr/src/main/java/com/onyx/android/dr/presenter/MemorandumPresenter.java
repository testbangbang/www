package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.adapter.MemorandumAdapter;
import com.onyx.android.dr.data.MemorandumConfig;
import com.onyx.android.dr.data.MemorandumData;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.interfaces.MemorandumView;
import com.onyx.android.dr.request.local.MemorandumDelete;
import com.onyx.android.dr.request.local.MemorandumQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class MemorandumPresenter {
    private final MemorandumView memorandumView;
    private MemorandumData memorandumData;
    private MemorandumConfig memorandumConfig;
    private Context context;
    public List<MemorandumEntity> allDatas;

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
                allDatas = req.getAllDatas();
                ArrayList<Boolean> checkList = req.getCheckList();
                memorandumView.setMemorandumData(req.getAllDatas(), checkList);
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

    public void remoteAdapterDatas(ArrayList<Boolean> listCheck, MemorandumAdapter adapter) {
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
}
