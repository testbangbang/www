package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.InfromalEssayBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.InfromalEssayData;
import com.onyx.android.dr.data.database.InfromalEssayEntity;
import com.onyx.android.dr.interfaces.InfromalEssayView;
import com.onyx.android.dr.request.local.InfromalEssayDelete;
import com.onyx.android.dr.request.local.InfromalEssayInsert;
import com.onyx.android.dr.request.local.InfromalEssayQueryAll;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class InfromalEssayPresenter {
    private final InfromalEssayView infromalEssayView;
    private InfromalEssayData infromalEssayData;
    private Context context;

    public InfromalEssayPresenter(Context context, InfromalEssayView infromalEssayView) {
        this.infromalEssayView = infromalEssayView;
        this.context = context;
        infromalEssayData = new InfromalEssayData();
    }

    public void getAllInfromalEssayData() {
        final InfromalEssayQueryAll req = new InfromalEssayQueryAll();
        infromalEssayData.getAllInfromalEssay(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                infromalEssayView.setInfromalEssayData(req.getAllDatas());
            }
        });
    }

    public void deleteNewWord(long time) {
        final InfromalEssayDelete req = new InfromalEssayDelete(time, true);
        infromalEssayData.deleteInfromalEssay(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void insertInfromalEssay(InfromalEssayBean infromalEssayBean) {
        InfromalEssayEntity bean = new InfromalEssayEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.title = infromalEssayBean.getTitle();
        bean.wordNumber = infromalEssayBean.getWordNumber();
        bean.content = infromalEssayBean.getContent();
        final InfromalEssayInsert req = new InfromalEssayInsert(bean);
        if (req.whetherInsert()) {
            CommonNotices.showMessage(context, context.getString(R.string.infromal_essay_already_add));
        } else {
            CommonNotices.showMessage(context, context.getString(R.string.add_infromal_essay_success));
        }
        infromalEssayData.insertInfromalEssay(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
