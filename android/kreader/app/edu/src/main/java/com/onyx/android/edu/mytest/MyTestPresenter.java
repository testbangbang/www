package com.onyx.android.edu.mytest;

/**
 * Created by ming on 16/6/28.
 */
public class MyTestPresenter implements MyTestContract.Presenter{

    private MyTestContract.View mView;

    public MyTestPresenter(MyTestContract.View view){
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
