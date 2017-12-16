package com.onyx.edu.homework.ui;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.data.model.QuestionOption;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.note.HomeworkPagesRenderActionChain;
import com.onyx.edu.homework.base.BaseFragment;
import com.onyx.edu.homework.databinding.FragmentRecordBinding;
import com.onyx.edu.homework.event.GotoQuestionPageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lxm on 2017/12/13.
 */

public class RecordFragment extends BaseFragment {

    private FragmentRecordBinding binding;
    private List<Question> questions;

    private List<Question> subjectives = new ArrayList<>();
    private List<Question> objectives = new ArrayList<>();

    public static RecordFragment newInstance(List<Question> questions) {
        RecordFragment fragment = new RecordFragment();
        fragment.setQuestions(questions);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListView();
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    private void initListView() {
        if (questions == null) {
            return;
        }
        for (Question question : questions) {
            if (question.isChoiceQuestion()) {
                objectives.add(question);
            }else {
                subjectives.add(question);
            }
        }
        initObjectiveList();
        initSubjectiveList();
    }

    private String getQuestionAnswer(Question question) {
        StringBuilder answer = new StringBuilder();
        List<QuestionOption> options = question.options;
        if (options != null) {
            String[] optionIndexs = getResources().getStringArray(R.array.question_option);
            int size = options.size();
            for (int i = 0; i < size; i++) {
                QuestionOption option = options.get(i);
                if (option.checked) {
                    String index = i >= optionIndexs.length ? optionIndexs[0] : optionIndexs[i];
                    answer.append(index).append(" ");
                }

            }
        }
        return answer.toString();
    }

    private void initObjectiveList() {
        if (objectives.isEmpty()) {
            return;
        }
        final int column = 5;
        final int row = 2;
        binding.objectiveList.setLayoutManager(new DisableScrollGridManager(getContext()));
        binding.objectiveList.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return row;
            }

            @Override
            public int getColumnCount() {
                return column;
            }

            @Override
            public int getDataCount() {
                return objectives.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.objective_record_item, null));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                CommonViewHolder viewHolder = (CommonViewHolder) holder;
                Question question = objectives.get(position);
                final int page = getQuestionPosition(question);
                viewHolder.setText(R.id.position, String.valueOf(page + 1));
                viewHolder.setVisibility(R.id.no_answer, question.doneAnswer ? View.GONE : View.VISIBLE);
                viewHolder.getView(R.id.answer_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoQuestionPage(page);
                    }
                });
                if (question.doneAnswer) {
                    viewHolder.setText(R.id.answer, getQuestionAnswer(question));
                }
                boolean showRightLine = ((position + 1) % column) == 0
                        || (position < column && position == (getDataCount() - 1));
                viewHolder.setVisibility(R.id.right_line, showRightLine ? View.VISIBLE : View.GONE);
            }
        });

    }

    private void gotoQuestionPage(int position) {
        GotoQuestionPageEvent event = new GotoQuestionPageEvent(position, true);
        DataBundle.getInstance().post(event);
    }

    private int getQuestionPosition(Question question) {
        int size = questions.size();
        for (int i = 0; i < size; i++) {
            if (question == questions.get(i)) {
                return i;
            }
        }
        return 0;
    }

    private void initSubjectiveList() {
        if (subjectives.isEmpty()) {
            return;
        }
        binding.subjectiveList.setLayoutManager(new DisableScrollGridManager(getContext()));
        binding.subjectiveList.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return 2;
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public int getDataCount() {
                return subjectives.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.subjective_record_item, null));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                CommonViewHolder viewHolder = (CommonViewHolder) holder;
                Question question = subjectives.get(position);
                final int page = getQuestionPosition(question);
                viewHolder.setText(R.id.position, String.valueOf(page + 1));
                loadScribbleImage(question, viewHolder);
                viewHolder.getView(R.id.answer_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoQuestionPage(page);
                    }
                });
            }
        });
    }

    public NoteViewHelper getNoteViewHelper() {
        return DataBundle.getInstance().getNoteViewHelper();
    }

    private void loadScribbleImage(final Question question, final CommonViewHolder viewHolder) {
        if (question.isChoiceQuestion()) {
            return;
        }
        int width = (int) getResources().getDimension(R.dimen.scribble_view_width);
        int height = (int) getResources().getDimension(R.dimen.scribble_view_height);
        Rect size = new Rect(0, 0, width, height);
        final HomeworkPagesRenderActionChain pageAction = new HomeworkPagesRenderActionChain(question.getUniqueId(), size, 3);
        pageAction.execute(getNoteViewHelper(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<Bitmap> bitmaps = pageAction.getPageMap().get(question.getUniqueId());
                setImageView(bitmaps, (ImageView) viewHolder.getView(R.id.image0), 0);
                setImageView(bitmaps, (ImageView) viewHolder.getView(R.id.image1), 1);
                setImageView(bitmaps, (ImageView) viewHolder.getView(R.id.image2), 2);
                viewHolder.setVisibility(R.id.no_answer, bitmaps == null ? View.VISIBLE : View.GONE);
                viewHolder.setVisibility(R.id.image_layout, bitmaps == null ? View.GONE : View.VISIBLE);
                showMore(question, pageAction.getUnRenderPageUniqueMap(), viewHolder);
            }
        });
    }

    private void showMore(Question question, Map<String, List<String>> unRenderPageUniqueMap, CommonViewHolder viewHolder) {
        boolean more = false;
        if (!CollectionUtils.isNullOrEmpty(unRenderPageUniqueMap)) {
            List<String> unRenderPageUniques = unRenderPageUniqueMap.get(question.getUniqueId());
            more = !CollectionUtils.isNullOrEmpty(unRenderPageUniques);
        }
        viewHolder.setVisibility(R.id.more, more ? View.VISIBLE : View.GONE);
        viewHolder.setVisibility(R.id.image2, more ? View.GONE : View.VISIBLE);
    }

    private void setImageView(List<Bitmap> bitmaps, ImageView imageView, int index) {
        if (bitmaps != null && bitmaps.size() > index) {
            imageView.setImageBitmap(bitmaps.get(index));
            imageView.setBackgroundResource(R.drawable.round_bg);
        }else {
            imageView.setImageResource(android.R.color.white);
            imageView.setBackgroundResource(android.R.color.white);
        }
    }
}
