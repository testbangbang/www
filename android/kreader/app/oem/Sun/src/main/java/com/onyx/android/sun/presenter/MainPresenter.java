package com.onyx.android.sun.presenter;

import android.util.Log;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.ContentBean;
import com.onyx.android.sun.cloud.bean.HomeworkRequestBean;
import com.onyx.android.sun.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.sun.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.data.MainActivityData;
import com.onyx.android.sun.interfaces.MainView;
import com.onyx.android.sun.requests.cloud.DeleteNewMessageRequest;
import com.onyx.android.sun.requests.cloud.GetNewMessageRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

import java.util.List;

/**
 * Created by hehai on 17-9-29.
 */

public class MainPresenter {
    private MainView mainView;
    private MainActivityData mainActivityData;

    public MainPresenter(MainView mainView) {
        mainActivityData = new MainActivityData();
        this.mainView = mainView;
    }

    public void getNewMessage(String studentId) {
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.page = Constants.DEFAULT_PAGE;
        requestBean.size = Constants.DEFAULT_PAGE_SIZE;
        requestBean.studentId = studentId;
        final GetNewMessageRequest rq = new GetNewMessageRequest(requestBean);
        mainActivityData.getNewMessage(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkUnfinishedResultBean resultBean = rq.getHomeworkUnfinishedResultBean();
                if (resultBean == null) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.login_activity_request_failed));
                    return;
                }

                List<ContentBean> content = resultBean.data.content;
                if (content != null && content.size() > 0) {
                    mainView.setRemindContent(content);
                } else {
                    mainView.setRemindView();
                }
            }
        });
    }

    public void deleteRemindMessage(final String messageId, final String studentId) {
        final DeleteNewMessageRequest rq = new DeleteNewMessageRequest(messageId, studentId);
        mainActivityData.deleteMessage(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SubmitPracticeResultBean resultBean = rq.getResultBean();
                if (resultBean == null) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.login_activity_request_failed));
                    return;
                }
                if (Constants.OK.equals(resultBean.msg)) {
                    getNewMessage(studentId);
                } else {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.refresh_failed));
                }
            }
        });
    }
}
