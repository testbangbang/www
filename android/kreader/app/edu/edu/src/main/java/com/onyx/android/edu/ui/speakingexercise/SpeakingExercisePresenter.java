package com.onyx.android.edu.ui.speakingexercise;

/**
 * Created by ming on 16/6/28.
 */
public class SpeakingExercisePresenter implements SpeakingExerciseContract.Presenter{

    private SpeakingExerciseContract.View mView;

    public SpeakingExercisePresenter(SpeakingExerciseContract.View view){
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
