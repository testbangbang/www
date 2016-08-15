package com.onyx.android.edu.teststype;

/**
 * Created by ming on 16/6/28.
 */
public class TestsTypePresenter implements TestsTypeContract.TestsTypePresenter {

    private TestsTypeContract.TestsTypeView mTestsTypeView;

    public TestsTypePresenter(TestsTypeContract.TestsTypeView testsTypeView){
        mTestsTypeView = testsTypeView;
        testsTypeView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {

    }

}
