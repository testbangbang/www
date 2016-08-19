package com.onyx.android.edu.ui.wrongquestions;

/**
 * Created by ming on 16/6/28.
 */
public class WrongQuestionPresenter implements WrongQuestionContract.Presenter{

    private WrongQuestionContract.View mView;

    public WrongQuestionPresenter(WrongQuestionContract.View view){
        mView = view;
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unSubscribe() {

    }

}
