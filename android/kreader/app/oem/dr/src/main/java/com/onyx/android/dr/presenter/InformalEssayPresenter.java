package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.InformalEssayAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.InformalEssayData;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.interfaces.InformalEssayView;
import com.onyx.android.dr.request.cloud.CreateInformalEssayRequest;
import com.onyx.android.dr.request.cloud.RequestGetInformalEssay;
import com.onyx.android.dr.request.local.InformalEssayDelete;
import com.onyx.android.dr.request.local.InformalEssayExport;
import com.onyx.android.dr.request.local.InformalEssayInsert;
import com.onyx.android.dr.request.local.InformalEssayQueryAll;
import com.onyx.android.dr.request.local.InformalEssayQueryByTime;
import com.onyx.android.dr.request.local.InformalEssayQueryByTitle;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.data.model.InformalEssayBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.onyx.android.dr.R.string.please_select_export_data;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class InformalEssayPresenter {
    private final InformalEssayView informalEssayView;
    private InformalEssayData infromalEssayData;
    private Context context;
    private String tag = "";

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
            }
        });
    }

    public void getInformalEssay(String param) {
        final RequestGetInformalEssay req = new RequestGetInformalEssay(infromalEssayData, param);
        infromalEssayData.getInformalEssay(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                informalEssayView.setInformalEssayData(req.getGroup(), req.getCheckList());
            }
        });
    }

    public void getInformalEssayQueryByTitle(String keyword) {
        final InformalEssayQueryByTitle req = new InformalEssayQueryByTitle(keyword);
        infromalEssayData.getInformalEssayQueryByTitle(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                informalEssayView.setInformalEssayByTitle(req.getData());
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

    public void createInformalEssay(InformalEssayBean param) {
        final CreateInformalEssayRequest req = new CreateInformalEssayRequest(param);
        infromalEssayData.createInformalEssay(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                informalEssayView.createInformalEssay(req.getGroup() != null);
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

    public void remoteAdapterData(ArrayList<Boolean> listCheck, InformalEssayAdapter informalEssayAdapter, List<CreateInformalEssayBean> list) {
        List<CreateInformalEssayBean> exportList = getData(listCheck, list);
        if (exportList == null || exportList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_export_data));
            return;
        }
        int length = listCheck.size();
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                //delete basedata data
                CreateInformalEssayBean bean = exportList.get(i);
                deleteNewWord(bean.currentTime);
                exportList.remove(i);
                listCheck.remove(i);
                informalEssayAdapter.notifyItemRemoved(i);
                informalEssayAdapter.notifyItemRangeChanged(0, exportList.size());
            }
        }
    }

    public void shareInformalEssay(ArrayList<Boolean> listCheck, List<CreateInformalEssayBean> list) {
        List<CreateInformalEssayBean> exportList = getData(listCheck, list);
        if (exportList == null || exportList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_export_data));
            return;
        }
        int length = exportList.size();
        String[] array = new String[]{};
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                CreateInformalEssayBean bean = exportList.get(i);
                array = Arrays.copyOf(array, array.length + 1);
                array[array.length - 1] = bean._id;
            }
        }
        DRPreferenceManager.saveShareType(DRApplication.getInstance(), Constants.INFORMAL_ESSAY);
        ActivityManager.startShareBookReportActivity(DRApplication.getInstance(), "", array);
    }

    public void exportDataToHtml(final Context context, ArrayList<Boolean> listCheck, ArrayList<String> dataList, List<CreateInformalEssayBean> list) {
        List<CreateInformalEssayBean> exportNewWordList = getData(listCheck, list);
        if (exportNewWordList == null || exportNewWordList.isEmpty()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(please_select_export_data));
            return;
        }
        final InformalEssayExport req = new InformalEssayExport(context, dataList, exportNewWordList);
        infromalEssayData.exportInformalEssay(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    private List<CreateInformalEssayBean> getData(ArrayList<Boolean> listCheck, List<CreateInformalEssayBean> list) {
        List<CreateInformalEssayBean> exportList = new ArrayList<>();
        for (int i = 0, j = list.size(); i < j; i++) {
            Boolean aBoolean = listCheck.get(i);
            if (aBoolean) {
                CreateInformalEssayBean bean = list.get(i);
                if (!exportList.contains(bean)) {
                    exportList.add(bean);
                }
            }
        }
        return exportList;
    }
}
