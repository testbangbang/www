package com.onyx.android.sun.presenter;

import com.onyx.android.sun.cloud.bean.ContentBean;
import com.onyx.android.sun.cloud.bean.HomeworkRequestBean;
import com.onyx.android.sun.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.sun.data.MainActData;
import com.onyx.android.sun.interfaces.MainView;
import com.onyx.android.sun.requests.GetNewMessageRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

import java.util.List;

/**
 * Created by hehai on 17-9-29.
 */

public class MainPresenter {

    private MainView mainView;
    private MainActData mainActData;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        mainActData = new MainActData();
    }

    public void getNewMessages(String page,String size,String studentId){

        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.page = page;
        requestBean.size = size;
        requestBean.studentId = studentId;
        final GetNewMessageRequest rq = new GetNewMessageRequest(requestBean);

        mainActData.getNewMessage(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkUnfinishedResultBean resultBean = rq.getHomeworkUnfinishedResultBean();
                if (resultBean == null || resultBean.data == null) {
                    return;
                }
                List<ContentBean> content = resultBean.data.content;
                if (content != null && content.size() > 0) {
                    mainView.setOnGetNewMessageData(content);
                }
            }
        });
    }
}
