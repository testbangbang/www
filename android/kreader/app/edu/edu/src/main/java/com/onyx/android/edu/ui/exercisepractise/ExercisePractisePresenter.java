package com.onyx.android.edu.ui.exercisepractise;

/**
 * Created by ming on 16/8/18.
 */
public class ExercisePractisePresenter implements ExercisePractiseContract.ExercisePractisePresenter {

    private ExercisePractiseContract.ExercisePractiseView exercisePractiseView;

    public ExercisePractisePresenter(ExercisePractiseContract.ExercisePractiseView exercisePractiseView) {
        this.exercisePractiseView = exercisePractiseView;
        exercisePractiseView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {

    }
}
