package com.onyx.android.edu.mytest;


import com.onyx.android.edu.base.BasePresenter;
import com.onyx.android.edu.base.BaseView;

/**
 * Created by ming on 16/6/28.
 */
public interface MyTestContract {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {

    }
}
