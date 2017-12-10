package com.onyx.edu.homework.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.TextView;

import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.base.BaseActivity;
import com.onyx.edu.homework.data.Constant;
import com.onyx.edu.homework.databinding.ActivityAnswerBinding;
import com.onyx.edu.homework.event.CloseScribbleEvent;
import com.onyx.edu.homework.utils.TextUtils;
import com.onyx.edu.homework.view.Base64ImageParser;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lxm on 2017/12/6.
 */

public class AnswerActivity extends BaseActivity {

    private ActivityAnswerBinding binding;
    private Question question;
    private ScribbleFragment scribbleFragment;
    private NoteToolFragment toolFragment;
    private NoteViewHelper noteViewHelper = new NoteViewHelper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_answer);
        question = (Question) getIntent().getSerializableExtra(Constant.TAG_QUESTION);
        initView(question);
    }

    private void initView(final Question question) {
        int questionIndex = Math.max(question.QuesType - 1, 0);
        String questionType = getResources().getStringArray(R.array.question_type_list)[questionIndex];
        binding.questionType.setText(getString(R.string.question_type_str, questionType));
        binding.content.setText(TextUtils.fromHtml(question.content, new Base64ImageParser(this), null));
        scribbleFragment = ScribbleFragment.create(getNoteViewHelper(), question);
        toolFragment = NoteToolFragment.create(binding.subMenuLayout, getNoteViewHelper());
        getFragmentManager().beginTransaction().replace(R.id.scribble_layout, scribbleFragment).commit();
        getFragmentManager().beginTransaction().replace(R.id.tool_layout, toolFragment).commit();
        getNoteViewHelper().register(this);
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getNoteViewHelper().unregister(this);
    }

    public NoteViewHelper getNoteViewHelper() {
        return noteViewHelper;
    }

    @Override
    public void onBackPressed() {
        scribbleFragment.save(true, false);
    }

    @Subscribe
    public void onCloseScribbleEvent(CloseScribbleEvent event) {
        finish();
    }
}
