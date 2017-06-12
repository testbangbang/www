package com.onyx.android.edu.ui.myexercise;

/**
 * Created by ming on 16/6/28.
 */
public class MyExercisePresenter implements MyExerciseContract.Presenter{

    private MyExerciseContract.View mView;

    public MyExercisePresenter(MyExerciseContract.View view){
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
