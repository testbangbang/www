package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.MemorandumConfig;
import com.onyx.android.dr.data.MemorandumData;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.interfaces.AddMemorandumView;
import com.onyx.android.dr.request.local.MemorandumDelete;
import com.onyx.android.dr.request.local.MemorandumInsert;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/7/26.
 */
public class AddMemorandumPresenter {
    private final AddMemorandumView addMemorandumView;
    private final MemorandumData memorandumData;
    private MemorandumConfig memorandumConfig;
    private Context context;

    public AddMemorandumPresenter(Context context, AddMemorandumView addMemorandumView) {
        this.addMemorandumView = addMemorandumView;
        this.context = context;
        memorandumConfig = new MemorandumConfig();
        memorandumData = new MemorandumData();
    }

    public void getHourDatas() {
        addMemorandumView.setHourData(memorandumConfig.loadHourDatas());
    }

    public void getMinuteDatas() {
        addMemorandumView.setMinuteData(memorandumConfig.loadMinuteDatas());
    }

    public void insertMemorandum(MemorandumEntity bean) {
        final MemorandumInsert req = new MemorandumInsert(bean);
        memorandumData.insertMemorandum(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
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
}
