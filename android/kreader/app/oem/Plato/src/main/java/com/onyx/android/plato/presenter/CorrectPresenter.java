package com.onyx.android.plato.presenter;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.ExerciseMessageBean;
import com.onyx.android.plato.cloud.bean.GetCorrectedTaskBean;
import com.onyx.android.plato.cloud.bean.GetCorrectedTaskRequestBean;
import com.onyx.android.plato.cloud.bean.GetCorrectedTaskResultBean;
import com.onyx.android.plato.cloud.bean.QuestionData;
import com.onyx.android.plato.cloud.bean.QuestionViewBean;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.data.CorrectData;
import com.onyx.android.plato.event.EmptyEvent;
import com.onyx.android.plato.interfaces.CorrectView;
import com.onyx.android.plato.requests.cloud.GetCorrectedTaskRequest;
import com.onyx.android.plato.requests.local.ResolveAdapterDataRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by li on 2017/10/25.
 */

public class CorrectPresenter {
    private CorrectView correctView;
    private CorrectData correctData;

    public CorrectPresenter(CorrectView correctView) {
        this.correctView = correctView;
        correctData = new CorrectData();
    }

    public void getCorrectData(int practiceId) {
        GetCorrectedTaskRequestBean requestBean = new GetCorrectedTaskRequestBean();
        requestBean.practiceId = practiceId;
        final GetCorrectedTaskRequest rq = new GetCorrectedTaskRequest(requestBean);
        correctData.getCorrectData(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetCorrectedTaskResultBean taskBean = rq.getTaskBean();
                if (taskBean == null) {
                    EventBus.getDefault().post(new EmptyEvent());
                    return;
                }

                GetCorrectedTaskBean data = taskBean.data;
                if (data != null) {
                    correctView.setCorrectData(data);
                }
            }
        });
    }

    public void resolveAdapterData(List<QuestionData> questionDataList, List<ExerciseMessageBean> questionMessages, int taskId) {
        final ResolveAdapterDataRequest rq = new ResolveAdapterDataRequest(questionDataList, questionMessages, taskId);
        correctData.resolveData(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<QuestionViewBean> questionList = rq.getQuestionList();
                if (questionList != null && questionList.size() > 0) {
                    correctView.setQuestionBeanList(questionList);
                } else {
                    correctView.clearAdapter();
                }
            }
        });
    }
}
