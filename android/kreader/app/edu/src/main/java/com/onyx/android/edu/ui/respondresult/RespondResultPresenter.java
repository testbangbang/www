package com.onyx.android.edu.ui.respondresult;


import com.onyx.android.edu.bean.PaperResult;

/**
 * Created by ming on 16/6/28.
 */
public class RespondResultPresenter implements RespondResultContract.Presenter{

    private PaperResult mPaperResult;
    private RespondResultContract.View mView;

    public RespondResultPresenter(RespondResultContract.View view){
        mView = view;
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        mView.showResult(mPaperResult);
    }

    @Override
    public void unSubscribe() {

    }

    public void setPaperResult(PaperResult paperResult) {
        mPaperResult = paperResult;
    }
}
