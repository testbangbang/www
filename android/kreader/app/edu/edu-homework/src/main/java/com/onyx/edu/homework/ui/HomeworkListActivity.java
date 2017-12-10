package com.onyx.edu.homework.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.data.model.QuestionOption;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.homework.Global;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.HomeworkListActionChain;
import com.onyx.edu.homework.action.note.PageListRenderActionChain;
import com.onyx.edu.homework.base.BaseActivity;
import com.onyx.edu.homework.data.Constant;
import com.onyx.edu.homework.data.Homework;
import com.onyx.edu.homework.databinding.ActivityHomeworkListBinding;
import com.onyx.edu.homework.utils.TextUtils;
import com.onyx.edu.homework.view.Base64ImageParser;

import java.io.File;
import java.util.List;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkListActivity extends BaseActivity {

    private ActivityHomeworkListBinding binding;
    private List<Question> questions;
    private Homework homework;
    private NoteViewHelper noteViewHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_homework_list);
        handleIntent();
        homeworkRequest();
    }

    private void handleIntent() {
        String extraData = getIntent().getStringExtra(MetadataUtils.INTENT_EXTRA_DATA_METADATA);
        if (StringUtils.isNullOrEmpty(extraData)) {
            return;
        }
        homework = JSON.parseObject(extraData, Homework.class);
    }

    public NoteViewHelper getNoteViewHelper() {
        if (noteViewHelper == null) {
            noteViewHelper = new NoteViewHelper();
        }
        return noteViewHelper;
    }

    private void homeworkRequest() {
        String libraryId = "5a276546299dfa4f05a6e85f";
        if (homework != null) {
            libraryId = homework.child._id;
        }
        Global.getInstance().setHomeworkId(libraryId);
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
        binding.list.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getDataCount() {
                return questions == null ? 0 : questions.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, null));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                final Question question = questions.get(position);
                final CommonViewHolder viewHolder = (CommonViewHolder) holder;
                int questionIndex = Math.max(question.QuesType - 1, 0);
                String questionType = getResources().getStringArray(R.array.question_type_list)[questionIndex];
                viewHolder.setText(R.id.question_type, getString(R.string.question_type_str, questionType));
                TextView content = viewHolder.getView(R.id.content);
                content.setText(TextUtils.fromHtml(question.content, new Base64ImageParser(HomeworkListActivity.this), null));
                RadioGroup group = viewHolder.getView(R.id.option);
                viewHolder.setVisibility(R.id.answer, question.isChoiceQuestion() ? View.GONE : View.VISIBLE);
                bindQuestionOption(group, question);
                viewHolder.getView(R.id.answer).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetNoteViewHelper();
                        gotoAnswerActivity(question);
                    }
                });
            }

            @Override
            public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
                final Question question = questions.get(holder.getAdapterPosition());
                final CommonViewHolder viewHolder = (CommonViewHolder) holder;
                final ImageView imageView = viewHolder.getView(R.id.scribble_image);
                loadScribbleImage(question, imageView);
            }
        });
        binding.list.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePage(position);
                Question question = questions.get(position);
                if (question.isChoiceQuestion()) {
                    return;
                }
                resetNoteViewHelper();
            }
        });
        updatePage(0);
    }

    private void updatePage(int position) {
        int current = position + 1;
        int total = questions.size();
        binding.page.setText(current + File.separator + total);
    }

    private void resetNoteViewHelper() {
        getNoteViewHelper().getNoteDocument().close(HomeworkListActivity.this);
        getNoteViewHelper().quit();
        getNoteViewHelper().recycleBitmap();
    }

    private void loadScribbleImage(final Question question, final ImageView imageView) {
        if (question.isChoiceQuestion()) {
            return;
        }
        Rect size = new Rect(0, 0, 800, 1000);
        Debug.d(getClass(), "loadScribbleImage: ");
        final PageListRenderActionChain pageAction = new PageListRenderActionChain(question._id, size);
        pageAction.execute(getNoteViewHelper(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<Bitmap> bitmaps = pageAction.getBitmaps();
                if (bitmaps != null && bitmaps.size() > 0) {
                    imageView.setImageBitmap(bitmaps.get(0));
                }else {
                    imageView.setImageResource(android.R.color.white);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetNoteViewHelper();
        binding.list.notifyDataSetChanged();
    }

    @Override
    public boolean isFullScreen() {
        return true;
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
            group.addView(createCompoundButton(option, question.isSingleChoiceQuestion()));
        }
    }

    private CompoundButton createCompoundButton(final QuestionOption option, final boolean single) {
        CompoundButton button = single ? new RadioButton(this) : new CheckBox(this);
        button.setText(Html.fromHtml(option.value, new Base64ImageParser(HomeworkListActivity.this), null));
        button.setChecked(option.checked);
        button.setTextSize(getResources().getDimension(R.dimen.question_option_text_size));
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                option.setChecked(isChecked);
            }
        });
        return button;
    }

    private void gotoAnswerActivity(final Question question) {
        Intent intent = new Intent(HomeworkListActivity.this, AnswerActivity.class);
        intent.putExtra(Constant.TAG_QUESTION, question);
        startActivity(intent);
    }

}
