package com.onyx.android.edu.ui.chooseexercise;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.TestAdapter;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.base.Config;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.libedu.model.BookNode;
import com.onyx.libedu.model.KnowledgePoint;
import com.onyx.libedu.model.QuestionType;
import com.onyx.libedu.model.Stage;
import com.onyx.libedu.model.Subject;
import com.onyx.libedu.model.Textbook;
import com.onyx.libedu.model.Version;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2016/11/24.
 */

public class ChooseExerciseColorFragment extends BaseFragment implements ChooseExerciseContract.ChooseExerciseView {

    @Bind(R.id.more)
    TextView more;
    @Bind(R.id.exercise_list)
    PageRecyclerView exerciseList;
    @Bind(R.id.btn_goto)
    Button btnGoto;
    @Bind(R.id.subject_group)
    DynamicMultiRadioGroupView subjectGroup;
    @Bind(R.id.subject_group_layout)
    LinearLayout subjectGroupLayout;

    private ChooseExerciseContract.ChooseExercisePresenter presenter;

    public static ChooseExerciseColorFragment newInstance() {
        return new ChooseExerciseColorFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_color_choose_exercise;
    }

    @Override
    public void showSubjects(final List<Subject> subjects) {
        if (subjects == null || subjects.size() <=0) {
            return;
        }
        final int columns = Config.COMMON_COLUMNS;
        final int rows = (int) Math.ceil((float) subjects.size() / columns);
        subjectGroup.setMultiAdapter(new DynamicMultiRadioGroupView.MultiAdapter() {
            @Override
            public int getRows() {
                return rows;
            }

            @Override
            public int getColumns() {
                return columns;
            }

            @Override
            public int getItemCount() {
                return subjects.size();
            }

            @Override
            public void bindView(CompoundButton button, int position) {
                final Subject subject = subjects.get(position);
                button.setText(subject.getSubjectName());
                button.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            }
        });
        subjectGroup.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {
                    subjectGroupLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void showVersions(List<Version> versions) {

    }

    @Override
    public void showTextbooks(List<Textbook> textbooks) {

    }

    @Override
    public void showBookNodes(List<BookNode> bookNodes) {

    }

    @Override
    public void showKnowledgePoints(List<KnowledgePoint> knowledgePoints) {

    }

    @Override
    public void showQuestionType(List<QuestionType> questionTypes) {

    }

    @Override
    public void setPresenter(ChooseExerciseContract.ChooseExercisePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        presenter.subscribe();
        presenter.loadSubjects(Stage.create(1, "小学"));
        subjectGroupLayout.setVisibility(View.GONE);

        initExerciseList();
    }

    private void initExerciseList() {
        exerciseList.setLayoutManager(new DisableScrollGridManager(getActivity()));
        exerciseList.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return 2;
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public int getDataCount() {
                return 13;
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_test, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void changeSubjectView() {
        int visibility = subjectGroupLayout.getVisibility();
        subjectGroupLayout.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
 }
