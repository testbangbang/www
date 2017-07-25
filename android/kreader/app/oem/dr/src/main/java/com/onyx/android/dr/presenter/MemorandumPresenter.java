package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.MemorandumBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.MemorandumData;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.interfaces.MemorandumView;
import com.onyx.android.dr.request.local.MemorandumDelete;
import com.onyx.android.dr.request.local.MemorandumInsert;
import com.onyx.android.dr.request.local.MemorandumQueryAll;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class MemorandumPresenter {
    private final MemorandumView memorandumView;
    private MemorandumData memorandumData;
    private Context context;

    public MemorandumPresenter(Context context, MemorandumView memorandumView) {
        this.memorandumView = memorandumView;
        this.context = context;
        memorandumData = new MemorandumData();
    }

    public void getAllMemorandumData() {
        final MemorandumQueryAll req = new MemorandumQueryAll();
        memorandumData.getAllMemorandum(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                memorandumView.setMemorandumData(req.getAllDatas());
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
