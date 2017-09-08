package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.InformalEssayAdapter;
import com.onyx.android.dr.bean.InformalEssayBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.InformalEssayData;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.interfaces.InformalEssayView;
import com.onyx.android.dr.request.local.InformalEssayDelete;
import com.onyx.android.dr.request.local.InformalEssayExport;
import com.onyx.android.dr.request.local.InformalEssayInsert;
import com.onyx.android.dr.request.local.InformalEssayQueryAll;
import com.onyx.android.dr.request.local.InformalEssayQueryByTime;
import com.onyx.android.dr.request.local.InformalEssayQueryByTitle;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class InformalEssayPresenter {
    private final InformalEssayView informalEssayView;
    private InformalEssayData infromalEssayData;
    private Context context;
    public List<InformalEssayEntity> allDatas;

    public InformalEssayPresenter(Context context, InformalEssayView informalEssayView) {
        this.informalEssayView = informalEssayView;
        this.context = context;
        infromalEssayData = new InformalEssayData();
    }

    public void getAllInformalEssayData() {
        final InformalEssayQueryAll req = new InformalEssayQueryAll();
        infromalEssayData.getAllInformalEssay(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                allDatas = req.getAllDatas();
                ArrayList<Boolean> checkList = req.getCheckList();
                informalEssayView.setInformalEssayData(allDatas, checkList);
            }
        });
    }

    public void getInformalEssayQueryByTitle(String keyword) {
        final InformalEssayQueryByTitle req = new InformalEssayQueryByTitle(keyword);
        infromalEssayData.getInformalEssayQueryByTitle(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                allDatas = req.getData();
                informalEssayView.setInformalEssayByTitle(allDatas);
            }
        });
    }

    public void getInformalEssayByTime(long startDate, long endDate) {
        final InformalEssayQueryByTime req = new InformalEssayQueryByTime(startDate, endDate);
        infromalEssayData.getInformalEssayByTime(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                informalEssayView.setInformalEssayByTime(req.getData());
            }
        });
    }

    public void deleteNewWord(long time) {
        final InformalEssayDelete req = new InformalEssayDelete(time, true);
        infromalEssayData.deleteInformalEssay(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public ArrayList<String> getHtmlTitleData() {
        ArrayList<String> htmlTitle = infromalEssayData.getHtmlTitle(context);
        return htmlTitle;
    }

    public void exportDataToHtml(final Context context, ArrayList<String> dataList, List<InformalEssayEntity> infromalEssayList) {
        final InformalEssayExport req = new InformalEssayExport(context, dataList, infromalEssayList);
        infromalEssayData.exportInformalEssay(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void insertInformalEssay(InformalEssayBean infromalEssayBean) {
        InformalEssayEntity bean = new InformalEssayEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.title = infromalEssayBean.getTitle();
        bean.wordNumber = infromalEssayBean.getWordNumber();
        bean.content = infromalEssayBean.getContent();
        final InformalEssayInsert req = new InformalEssayInsert(bean);
        if (req.whetherInsert()) {
            CommonNotices.showMessage(context, context.getString(R.string.infromal_essay_already_add));
        } else {
            CommonNotices.showMessage(context, context.getString(R.string.add_infromal_essay_success));
        }
        infromalEssayData.insertInformalEssay(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void remoteAdapterDatas(ArrayList<Boolean> listCheck, InformalEssayAdapter infromalEssayAdapter) {
        int length = listCheck.size();
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                //delete basedata data
                InformalEssayEntity bean = allDatas.get(i);
                deleteNewWord(bean.currentTime);
                allDatas.remove(i);
                listCheck.remove(i);
                infromalEssayAdapter.notifyItemRemoved(i);
                infromalEssayAdapter.notifyItemRangeChanged(0, allDatas.size());
            }
        }
    }
}
