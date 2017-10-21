package com.onyx.android.sun.presenter;

import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sun.cloud.bean.HomeworkRequestBean;
import com.onyx.android.sun.cloud.bean.PersonalAbilityResultBean;
import com.onyx.android.sun.data.MainFragmentData;
import com.onyx.android.sun.interfaces.MainFragmentView;
import com.onyx.android.sun.requests.cloud.HomeworkUnfinishedRequest;
import com.onyx.android.sun.requests.cloud.SubjectAbilityRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hehai on 17-9-29.
 */

public class MainFragmentPresenter {
    private MainFragmentView mainFragmentView;
    private MainFragmentData mainFragmentData;

    public MainFragmentPresenter(MainFragmentView mainFragmentView) {
        this.mainFragmentView = mainFragmentView;
        mainFragmentData = new MainFragmentData();
    }

    public void getPractices() {
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.course = "1";
        Date date = new Date(System.currentTimeMillis());
        requestBean.endtime = DateTimeUtil.formatDate(date, new SimpleDateFormat("yy-MM-dd", Locale.getDefault()));
        requestBean.page = "1";
        requestBean.size = "10";
        requestBean.starttime = "2017-02-02";
        requestBean.status = "tbd";
        requestBean.studentId = "2";
        requestBean.type = "all";
        final HomeworkUnfinishedRequest req = new HomeworkUnfinishedRequest(requestBean);
        mainFragmentData.getPractices(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (req.getResultBean() != null && req.getResultBean().data != null && !CollectionUtils.isNullOrEmpty(req.getResultBean().data.content)) {
                    mainFragmentView.setPractices(req.getResultBean().data.content);
                }
            }
        });
    }

    public void getSubjectScore() {
        final SubjectAbilityRequest req = new SubjectAbilityRequest("1");
        mainFragmentData.getSubjectScore(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                PersonalAbilityResultBean resultBean = req.getResultBean();
                if (resultBean != null && resultBean.data != null) {
                    mainFragmentView.setSubjectScore(resultBean.data, mainFragmentData.getSubjectScoreMap());
                }
            }
        });
    }
}
