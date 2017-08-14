package com.onyx.android.edu.ui.respondresult;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.edu.EduApp;
import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.bean.PaperResult;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by li on 2017/8/10.
 */

public class ExaminationResultFragment extends BaseFragment implements RespondResultContract.View {
    @Bind(R.id.examination_book_name)
    TextView bookName;
    @Bind(R.id.examination_correct_rate)
    TextView correctRate;
    @Bind(R.id.examination_time_use)
    TextView timeUsed;
    @Bind(R.id.examination_score)
    TextView score;

    private RespondResultContract.Presenter mPresenter;

    public static ExaminationResultFragment newInstance() {
        return new ExaminationResultFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mPresenter.subscribe();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_examination_result;
    }

    @Override
    public void showResult(PaperResult paperResult) {
        List<Boolean> result = paperResult.getResult();
        int count = 0;
        if (result != null && result.size() > 0) {
            for (Boolean bool : result) {
                if (bool) {
                    count++;
                }
            }
        }
        correctRate.setText(count * 100f / result.size() + "%");
        score.setText(String.valueOf(paperResult.getScore()));
        String format = String.format("用时%d秒", EduApp.instance().getUseTime());
        timeUsed.setText(format);
        bookName.setText(EduApp.instance().getBookName());
        mPresenter.updateExaminationPaper(paperResult.getScore(), count, result.size() - count);
    }

    @Override
    public void setPresenter(RespondResultContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unSubscribe();
        ButterKnife.unbind(this);
    }
}
