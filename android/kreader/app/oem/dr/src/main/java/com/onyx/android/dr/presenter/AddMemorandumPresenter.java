package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.MemorandumBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.MemorandumConfig;
import com.onyx.android.dr.data.MemorandumData;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.interfaces.AddMemorandumView;
import com.onyx.android.dr.request.local.MemorandumInsert;
import com.onyx.android.dr.util.TimeUtils;
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

    public void insertMemorandum(MemorandumBean memorandumBean) {
        MemorandumEntity bean = new MemorandumEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.timeQuantum = memorandumBean.getTimeQuantum();
        bean.matter = memorandumBean.getMatter();
        final MemorandumInsert req = new MemorandumInsert(bean);
        if (req.whetherInsert()) {
            CommonNotices.showMessage(context, context.getString(R.string.memorandum_already_add));
        } else {
            CommonNotices.showMessage(context, context.getString(R.string.add_memorandum_success));
        }
        memorandumData.insertMemorandum(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
