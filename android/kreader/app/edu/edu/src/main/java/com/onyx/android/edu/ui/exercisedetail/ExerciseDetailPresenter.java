package com.onyx.android.edu.ui.exercisedetail;

/**
 * Created by ming on 16/8/18.
 */
public class ExerciseDetailPresenter implements ExerciseDetailContract.ExerciseDetailPresenter{

    private ExerciseDetailContract.ExerciseDetailView exerciseDetailView;

    public ExerciseDetailPresenter(ExerciseDetailContract.ExerciseDetailView exerciseDetailView){
        this.exerciseDetailView = exerciseDetailView;
        exerciseDetailView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {

    }
}
