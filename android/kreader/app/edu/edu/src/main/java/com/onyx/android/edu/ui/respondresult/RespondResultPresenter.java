package com.onyx.android.edu.ui.respondresult;


import com.onyx.android.edu.EduApp;
import com.onyx.android.edu.bean.PaperResult;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.libedu.EduCloudManager;
import com.onyx.libedu.request.cloud.UpdateExaminationPaper;

/**
 * Created by ming on 16/6/28.
 */
public class RespondResultPresenter implements RespondResultContract.Presenter{

    private PaperResult mPaperResult;
    private RespondResultContract.View mView;
    private EduCloudManager eduCloudManager;

    public RespondResultPresenter(RespondResultContract.View view){
        mView = view;
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        eduCloudManager = new EduCloudManager();
        mView.showResult(mPaperResult);
    }

    @Override
    public void unSubscribe() {

    }

    public void setPaperResult(PaperResult paperResult) {
        mPaperResult = paperResult;
    }

    @Override
    public void updateExaminationPaper(Float score, int count, int errorCount) {
        UpdateExaminationPaper rq = new UpdateExaminationPaper(score, count, errorCount,
                EduApp.instance().getBookId(), EduApp.instance().getAnswerPaperList());
        eduCloudManager.submitRequest(EduApp.instance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }
}
