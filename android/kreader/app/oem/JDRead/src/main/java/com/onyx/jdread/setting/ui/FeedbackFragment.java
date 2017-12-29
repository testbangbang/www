package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.FeedbackModel;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentFeedbackBinding;
import com.onyx.jdread.model.TitleBarModel;

/**
 * Created by hehai on 17-12-28.
 */

public class FeedbackFragment extends BaseFragment {

    private FragmentFeedbackBinding feedbackBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        feedbackBinding = FragmentFeedbackBinding.inflate(inflater, container, false);
        loadData();
        return feedbackBinding.getRoot();
    }

    private void loadData() {
        FeedbackModel feedbackModel = new FeedbackModel();
        feedbackModel.titleBarModel.title.set(getString(R.string.feedback));
        feedbackBinding.feedbackTitle.setTitleModel(feedbackModel.titleBarModel);
        feedbackBinding.setFeedbackModel(feedbackModel);
    }
}
