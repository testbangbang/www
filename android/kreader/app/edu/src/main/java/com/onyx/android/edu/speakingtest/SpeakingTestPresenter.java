package com.onyx.android.edu.speakingtest;

/**
 * Created by ming on 16/6/28.
 */
public class SpeakingTestPresenter implements SpeakingTestContract.Presenter{

    private SpeakingTestContract.View mView;

    public SpeakingTestPresenter(SpeakingTestContract.View view){
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
