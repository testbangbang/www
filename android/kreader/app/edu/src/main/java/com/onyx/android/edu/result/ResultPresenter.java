package com.onyx.android.edu.result;


import com.onyx.android.edu.bean.PaperResult;

/**
 * Created by ming on 16/6/28.
 */
public class ResultPresenter implements ResultContract.Presenter{

    private PaperResult mPaperResult;
    private ResultContract.View mView;

    public ResultPresenter(ResultContract.View view){
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
