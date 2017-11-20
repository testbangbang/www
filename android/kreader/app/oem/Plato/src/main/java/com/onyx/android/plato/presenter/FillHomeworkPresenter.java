package com.onyx.android.plato.presenter;

import com.alibaba.fastjson.JSON;
import com.onyx.android.plato.cloud.bean.PracticeFavoriteBean;
import com.onyx.android.plato.cloud.bean.PracticeFavoriteOrDeleteBean;
import com.onyx.android.plato.cloud.bean.QuestionViewBean;
import com.onyx.android.plato.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.common.Constants;
import com.onyx.android.plato.data.FillHomeworkData;
import com.onyx.android.plato.requests.cloud.FavoriteOrDeletePracticeRequest;
import com.onyx.android.plato.requests.local.FillAnswerRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by li on 2017/10/20.
 */

public class FillHomeworkPresenter {
    private FillHomeworkData fillHomeworkData;

    public FillHomeworkPresenter() {
        fillHomeworkData = new FillHomeworkData();
    }

    public void insertAnswer(int taskId, QuestionViewBean questionViewBean) {
        FillAnswerRequest rq = new FillAnswerRequest(taskId + "", questionViewBean.getId() + "", questionViewBean.getContent(), questionViewBean.getShowType(), questionViewBean.getUserAnswer());
        fillHomeworkData.insertAnswer(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

    public void deleteOrFavorite(int taskId, int questionId, int studentId) {
        PracticeFavoriteBean bean = new PracticeFavoriteBean();
        bean.id = questionId;
        bean.pid = taskId;
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constants.REQUEST_HEAD), JSON.toJSONString(bean));
        PracticeFavoriteOrDeleteBean requestBean = new PracticeFavoriteOrDeleteBean();
        requestBean.studentId = studentId;
        requestBean.requestBody = requestBody;
        final FavoriteOrDeletePracticeRequest rq = new FavoriteOrDeletePracticeRequest(requestBean);
        fillHomeworkData.deleteOrFavorite(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SubmitPracticeResultBean resultBean = rq.getResultBean();
                if (resultBean != null) {
                    CommonNotices.show(resultBean.msg);
                }
            }
        });
    }
}
