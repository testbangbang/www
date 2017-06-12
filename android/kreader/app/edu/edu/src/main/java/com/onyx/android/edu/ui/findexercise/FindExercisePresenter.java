package com.onyx.android.edu.ui.findexercise;

/**
 * Created by ming on 16/8/18.
 */
public class FindExercisePresenter implements FindExerciseContract.FindExercisePresenter{

    FindExerciseContract.FindExerciseView findExerciseView;

    public FindExercisePresenter(FindExerciseContract.FindExerciseView findExerciseView) {
        this.findExerciseView = findExerciseView;
        findExerciseView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {

    }
}
