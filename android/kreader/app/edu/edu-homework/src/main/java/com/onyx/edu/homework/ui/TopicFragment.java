package com.onyx.edu.homework.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.utils.Base64ImageParser;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.base.BaseFragment;
import com.onyx.edu.homework.databinding.FragmentTopicBinding;
import com.onyx.edu.homework.event.HideTopicFragmentEvent;
import com.onyx.edu.homework.utils.TextUtils;
import com.onyx.edu.homework.view.PageMovementMethod;

/**
 * Created by lxm on 2017/12/20.
 */

public class TopicFragment extends BaseFragment {

    private FragmentTopicBinding binding;
    private Question question;

    public static TopicFragment newInstance(Question question) {
        TopicFragment fragment = new TopicFragment();
        fragment.setQuestion(question);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_topic, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(question);
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    private void initView(Question question) {
        if (question == null) {
            return;
        }
        int questionIndex = Math.max(question.QuesType - 1, 0);
        String questionType = getResources().getStringArray(R.array.question_type_list)[questionIndex];
        binding.questionType.setText(getString(R.string.question_type_str, questionType));
        final Spanned content = !question.isFillQuestion() ? TextUtils.fromHtml(question.content, new Base64ImageParser(getActivity()), null)
                : null;
        binding.content.setText(content);
        final PageMovementMethod movementMethod = new PageMovementMethod();
        binding.content.setMovementMethod(movementMethod);
        binding.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBundle.getInstance().post(new HideTopicFragmentEvent());
            }
        });
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBundle.getInstance().post(new HideTopicFragmentEvent());
            }
        });
        binding.prevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movementMethod.pageUp(binding.content, (Spannable) content);
            }
        });
        binding.nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movementMethod.pageDown(binding.content, (Spannable) content);
            }
        });
    }
}
