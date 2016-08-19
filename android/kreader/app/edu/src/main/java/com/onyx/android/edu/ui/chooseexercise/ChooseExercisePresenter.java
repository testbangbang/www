package com.onyx.android.edu.ui.chooseexercise;

/**
 * Created by ming on 16/6/28.
 */
public class ChooseExercisePresenter implements ChooseExerciseContract.ChooseExercisePresenter {

    private ChooseExerciseContract.ChooseExerciseView chooseExerciseView;

    public ChooseExercisePresenter(ChooseExerciseContract.ChooseExerciseView chooseExerciseView){
        this.chooseExerciseView = chooseExerciseView;
        chooseExerciseView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {

    }

}
