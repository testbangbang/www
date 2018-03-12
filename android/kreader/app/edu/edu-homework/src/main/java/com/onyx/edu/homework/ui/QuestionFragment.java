package com.onyx.edu.homework.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.data.model.homework.QuestionOption;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TextLayoutArgs;
import com.onyx.android.sdk.utils.Base64ImageParser;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.DoAnswerAction;
import com.onyx.edu.homework.action.note.HomeworkPagesRenderActionChain;
import com.onyx.edu.homework.base.BaseFragment;
import com.onyx.edu.homework.data.Constant;
import com.onyx.edu.homework.data.SaveDocumentOption;
import com.onyx.edu.homework.databinding.FragmentQuestionBinding;
import com.onyx.edu.homework.event.DoneAnswerEvent;
import com.onyx.edu.homework.event.OpenDraftEvent;
import com.onyx.edu.homework.utils.TextUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by lxm on 2017/12/9.
 */

public class QuestionFragment extends BaseFragment {

    private FragmentQuestionBinding binding;
    private Question question;

    private ScribbleFragment scribbleFragment;
    private NoteToolFragment toolFragment;
    private ReviewFragment reviewFragment;
    private TopicFragment topicFragment;

    public static QuestionFragment newInstance(Question question) {
        QuestionFragment fragment = new QuestionFragment();
        fragment.setQuestion(question);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataBundle.getInstance().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question, container, false);
        DataBundle.getInstance().register(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(question);
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    private void init(Question question) {
        initView(question);
        initFragment();
        updateViewState();
        loadDraftView();
    }

    private void initView(final Question question) {
        initViewVisibility();
        Spanned content = question.isChoiceQuestion() ? TextUtils.fromHtml(question.content, new Base64ImageParser(getActivity()), null)
                : null;
        binding.content.setText(content);
        bindQuestionOption(binding.option, question);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenDraftEvent(OpenDraftEvent event) {
        gotoDraft();
    }

    private void gotoDraft() {
        binding.imageView.setVisibility(View.GONE);
        Intent intent = new Intent(getActivity(), DraftActivity.class);
        intent.putExtra(Constant.TAG_QUESTION, question);
        startActivity(intent);
    }

    private void initViewVisibility() {
        binding.content.setVisibility(question.isChoiceQuestion() ? View.VISIBLE : View.GONE);

        boolean showScribble = !question.isChoiceQuestion();
        binding.scribble.setVisibility(showScribble ? View.VISIBLE : View.GONE);
        binding.scribbleLine.setVisibility(showScribble ? View.VISIBLE : View.GONE);
        binding.toolLayout.setVisibility(showScribble ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        initViewVisibility();
    }

    public NoteViewHelper getNoteViewHelper() {
        return getDataBundle().getNoteViewHelper();
    }

    private void bindQuestionOption(RadioGroup group, Question question) {
        group.removeAllViews();
        if (!question.isChoiceQuestion()) {
            group.setVisibility(View.GONE);
            return;
        }
        group.setVisibility(View.VISIBLE);
        List<QuestionOption> options = question.options;
        for (QuestionOption option : options) {
            CompoundButton button = createCompoundButton(question, option);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin = (int) getResources().getDimension(R.dimen.question_option_margin);
            lp.setMargins(0, margin, 0, margin);
            group.addView(button, lp);
            if (option.checked) {
                group.check(button.getId());
            }
        }
    }

    private CompoundButton createCompoundButton(final Question question, final QuestionOption option) {
        final boolean single = question.isSingleChoiceQuestion();
        final CompoundButton button = single ? new RadioButton(getActivity()) : new CheckBox(getActivity());
        button.setText(Html.fromHtml(StringUtils.filterHtmlWrapChar(option.value), new Base64ImageParser(getActivity()), null));
        button.setTextSize(getResources().getDimension(R.dimen.question_option_text_size));
        button.setChecked(option.checked);
        button.setEnabled(radioButtonIsEnabled());
        button.setGravity(Gravity.TOP);
        button.setTextColor(Color.BLACK);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (single && isChecked) {
                    unCheckOption(question);
                }
                option.setChecked(isChecked);
                if (buttonView.isPressed()) {
                    new DoAnswerAction(question, DataBundle.getInstance().getPersonalHomeworkId()).execute(getActivity(), new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            DataBundle.getInstance().post(new DoneAnswerEvent(question));
                        }
                    });
                }
            }
        });
        return button;
    }

    private void unCheckOption(Question question) {
        List<QuestionOption> options = question.options;
        if (options == null) {
            return;
        }
        for (QuestionOption option : options) {
            option.setChecked(false);
        }
    }

    private boolean radioButtonIsEnabled() {
        //        return !getDataBundle().isDoingAndExpired() && !getDataBundle().canCheckAnswer();
        return !getDataBundle().isDoingAndExpired();
    }

    public void initFragment() {
        if (question.isChoiceQuestion()) {
            return;
        }
        getDataBundle().resetNoteViewHelper();
        if (!question.isChoiceQuestion()) {
            getDataBundle().getNoteViewHelper().setTextLayoutArgs(TextLayoutArgs.create(question.content, TextUtils.getTextSpacingAdd(question)));
        }
        scribbleFragment = ScribbleFragment.newInstance(question);
        loadRootFragment(R.id.scribble_layout, scribbleFragment);
        toolFragment = NoteToolFragment.newInstance(binding.subMenuLayout, 1, RelativeLayout.ALIGN_PARENT_TOP);
        loadRootFragment(R.id.tool_layout, toolFragment);
    }

    private void removeFragment() {
        if (reviewFragment != null) {
            getChildFragmentManager().beginTransaction().remove(reviewFragment).commit();
            reviewFragment = null;
        }
        if (scribbleFragment != null) {
            getChildFragmentManager().beginTransaction().remove(scribbleFragment).commit();
            scribbleFragment = null;
        }
        if (toolFragment != null) {
            getChildFragmentManager().beginTransaction().remove(toolFragment).commit();
            toolFragment = null;
        }
        if (topicFragment != null) {
            getChildFragmentManager().beginTransaction().remove(topicFragment).commit();
            topicFragment = null;
        }
    }

    public void reloadQuestion(Question question) {
        this.question = question;
        removeDraftView();
        removeFragment();
        init(question);
    }

    public DataBundle getDataBundle() {
        return DataBundle.getInstance();
    }

    public void updateViewState() {
        int size = binding.option.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = binding.option.getChildAt(i);
            view.setEnabled(radioButtonIsEnabled());
        }
        binding.option.setClickable(radioButtonIsEnabled());
    }

    public void saveQuestion(@NonNull SaveDocumentOption option, BaseCallback callback) {
        if (question.isChoiceQuestion() || scribbleFragment == null) {
            BaseCallback.invoke(callback, null, null);
            return;
        }
        scribbleFragment.saveDocument(option.finishAfterSave,
                option.resumeDrawing,
                option.render,
                option.showLoading,
                callback);
    }

    private void removeDraftView() {
        binding.imageView.setImageResource(android.R.color.transparent);
        binding.imageView.setVisibility(View.GONE);
    }

    private void loadDraftView() {
        if (!question.isChoiceQuestion()) {
            return;
        }
        binding.imageView.setVisibility(View.VISIBLE);
        binding.imageView.post(new Runnable() {
            @Override
            public void run() {
                renderDraftBitmap();
            }
        });

    }

    private void renderDraftBitmap() {
        Rect size =  new Rect(0, 0, binding.imageView.getWidth(), binding.imageView.getHeight());
        final HomeworkPagesRenderActionChain pageAction = new HomeworkPagesRenderActionChain(question.getUniqueId(),
                null,
                size,
                1);
        pageAction.execute(getNoteViewHelper(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<Bitmap> bitmaps = pageAction.getPageMap().get(question.getUniqueId());
                if (CollectionUtils.isNullOrEmpty(bitmaps)) {
                    return;
                }
                binding.imageView.setImageBitmap(bitmaps.get(0));
            }
        });
    }
}
