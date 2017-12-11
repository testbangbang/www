package com.onyx.edu.homework.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.CheckAnswerAction;
import com.onyx.edu.homework.action.HomeworkListActionChain;
import com.onyx.edu.homework.base.BaseActivity;
import com.onyx.edu.homework.data.Homework;
import com.onyx.edu.homework.databinding.ActivityHomeworkListBinding;
import com.onyx.edu.homework.event.DoneAnswerEvent;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkListActivity extends BaseActivity {

    private ActivityHomeworkListBinding binding;
    private List<Question> questions;
    private Homework homework;
    private List<QuestionFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_homework_list);
        DataBundle.getInstance().register(this);
        initView();
        handleIntent();
        homeworkRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataBundle.getInstance().unregister(this);
    }

    private void initView() {
        binding.prevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int prev = binding.list.getCurrentItem() - 1;
                prev = Math.max(0, prev);
                binding.list.setCurrentItem(prev,false);
            }
        });
        binding.nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int next = binding.list.getCurrentItem() + 1;
                next = Math.min(fragments.size() - 1, next);
                binding.list.setCurrentItem(next,false);
            }
        });
        binding.total.setText(getString(R.string.total, 0));
        binding.hasAnswer.setText(getString(R.string.has_answer, 0));
        binding.notAnswer.setText(getString(R.string.not_answer, 0));
        binding.submit.setText(R.string.submit);
        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SubmitDialog(HomeworkListActivity.this, questions).show();
            }
        });
    }

    private void handleIntent() {
        String extraData = getIntent().getStringExtra(MetadataUtils.INTENT_EXTRA_DATA_METADATA);
        if (StringUtils.isNullOrEmpty(extraData)) {
            return;
        }
        homework = JSON.parseObject(extraData, Homework.class);
    }

    private void homeworkRequest() {
        if (homework == null || homework.child == null) {
            System.exit(0);
            return;
        }
        String libraryId = homework.child._id;
        DataBundle.getInstance().setHomeworkId(libraryId);
        final HomeworkListActionChain actionChain = new HomeworkListActionChain(libraryId);
        actionChain.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    Toast.makeText(HomeworkListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                questions = actionChain.getHomeworkRequestModel().questions;
                initListView(questions);
            }

        });
    }

    private void initListView(final List<Question> questions) {
        if (questions == null) {
            return;
        }
        for (Question question : questions) {
            fragments.add(QuestionFragment.newInstance(question));
        }
        binding.list.setPagingEnabled(false);
        binding.list.setUseKeyPage(true);
        binding.list.setUseGesturesPage(true);
        binding.list.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public QuestionFragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        binding.list.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updatePage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        updatePage(0);
        updateShowInfo();
        new CheckAnswerAction(questions).execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                updateAnswerInfo();
            }
        });
    }

    private void updateShowInfo() {
        binding.total.setText(getString(R.string.total, questions.size()));
    }

    @Subscribe
    public void onDoneAnswerEvent(DoneAnswerEvent event) {
        if (event.question.isChoiceQuestion()) {
            updateAnswerInfo();
            return;
        }
        new CheckAnswerAction(questions).execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                updateAnswerInfo();
            }
        });
    }

    private void updateAnswerInfo() {
        if (questions == null) {
            return;
        }
        int hasAnswerCount = 0;
        for (Question question : questions) {
            if (question.doneAnswer) {
                hasAnswerCount++;
            }
        }
        int notAnswerCount = questions.size() - hasAnswerCount;
        binding.hasAnswer.setText(getString(R.string.has_answer, hasAnswerCount));
        binding.notAnswer.setText(getString(R.string.not_answer, notAnswerCount));
    }

    private void updateCurrentQuestion() {
        int current = binding.list.getCurrentItem();
        if (fragments.size() <= current) {
            return;
        }
        fragments.get(current).updateScribbleImage();
    }

    private void updatePage(int position) {
        int current = position + 1;
        int total = questions.size();
        binding.page.setText(current + File.separator + total);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCurrentQuestion();
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

}
