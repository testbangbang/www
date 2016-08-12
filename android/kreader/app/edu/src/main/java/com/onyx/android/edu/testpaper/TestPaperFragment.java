package com.onyx.android.edu.testpaper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.QuestionsPagerAdapter;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.base.BaseQuestionView;
import com.onyx.android.edu.bean.PaperResult;
import com.onyx.android.edu.db.model.Chapter;
import com.onyx.android.edu.result.ResultActivity;
import com.onyx.android.edu.utils.JsonUtils;
import com.onyx.android.edu.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ming on 16/6/24.
 */
public class TestPaperFragment extends BaseFragment implements View.OnClickListener,TestPaperContract.View {

    @Bind(R.id.paper_pager)
    CustomViewPager mPaperPager;
    @Bind(R.id.left_arrow)
    ImageView mLeftArrow;
    @Bind(R.id.paper_index)
    TextView mPaperIndex;
    @Bind(R.id.right_arrow)
    ImageView mRightArrow;

    private QuestionsPagerAdapter mQuestionsPagerAdapter;

    private TestPaperContract.Presenter mPresenter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test_paper;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {
        mLeftArrow.setOnClickListener(this);
        mRightArrow.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mPresenter.subscribe();
    }

    public static TestPaperFragment newInstance() {
        return new TestPaperFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unSubscribe();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.left_arrow:{
                int index = mPaperPager.getCurrentItem();
                int last = index - 1;
                if (index > 0){
                    mPaperPager.setCurrentItem(last,false);
                    updatePaperIndex();
                }
            }
            break;
            case R.id.right_arrow:{
                int index = mPaperPager.getCurrentItem();
                int count = mQuestionsPagerAdapter.getCount();
                int next = index + 1;
                if (next < count){
                    BaseQuestionView selectView = mQuestionsPagerAdapter.getViewList().get(index);
                    if (selectView.hasAnswers() || selectView.isShowAnswer()){
                        mPaperPager.setCurrentItem(next,false);
                        updatePaperIndex();
                    }else {
                        showToast(getString(R.string.ask_select_answer));
                    }
                }else {
                    enterResultActivity();
                }
            }
            break;
        }
    }

    private void updatePaperIndex(){
        int index = mPaperPager.getCurrentItem() + 1;
        int count = mQuestionsPagerAdapter.getCount();
        String str = String.format("%d/%d",index,count);
        mPaperIndex.setText(str);
    }

    @Override
    public void setPresenter(TestPaperContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void enterResultActivity(){
        PaperResult paperResult = mPresenter.getPaperResult(mQuestionsPagerAdapter.getViewList());
        getActivity().finish();
        Intent intent = new Intent(getActivity(), ResultActivity.class);
        intent.putExtra(ResultActivity.RESULT, JsonUtils.toJson(paperResult));
        startActivity(intent);
    }

    @Override
    public void showPaper(Chapter chapter, boolean showAnswer) {
        List<BaseQuestionView> selectViews = new ArrayList<>();
        mQuestionsPagerAdapter = new QuestionsPagerAdapter(selectViews);
        mPaperPager.setAdapter(mQuestionsPagerAdapter);
        mPaperPager.setPagingEnabled(false);

        updatePaperIndex();
    }
}
