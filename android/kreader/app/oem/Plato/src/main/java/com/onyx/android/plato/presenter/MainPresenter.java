package com.onyx.android.plato.presenter;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.cloud.bean.HomeworkRequestBean;
import com.onyx.android.plato.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.plato.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.common.Constants;
import com.onyx.android.plato.data.MainActivityData;
import com.onyx.android.plato.event.EmptyEvent;
import com.onyx.android.plato.interfaces.MainView;
import com.onyx.android.plato.requests.cloud.DeleteNewMessageRequest;
import com.onyx.android.plato.requests.cloud.GetNewMessageRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;

import org.greenrobot.eventbus.EventBus;

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

    public void getNewMessage() {
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.page = Constants.DEFAULT_PAGE;
        requestBean.size = Constants.DEFAULT_PAGE_SIZE;
        final GetNewMessageRequest rq = new GetNewMessageRequest(requestBean);
        mainActivityData.getNewMessage(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkUnfinishedResultBean resultBean = rq.getHomeworkUnfinishedResultBean();
                if (resultBean == null) {
                    EventBus.getDefault().post(new EmptyEvent());
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

    public void deleteRemindMessage(final String messageId) {
        final DeleteNewMessageRequest rq = new DeleteNewMessageRequest(messageId);
        mainActivityData.deleteMessage(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SubmitPracticeResultBean resultBean = rq.getResultBean();
                if (resultBean == null) {
                    EventBus.getDefault().post(new EmptyEvent());
                    return;
                }
                if (Constants.OK.equals(resultBean.msg)) {
                    getNewMessage();
                } else {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.refresh_failed));
                }
            }
        });
    }
}
