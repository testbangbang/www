package com.onyx.android.edu.ui.chooseexercise;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.ChooseMultiAdapter;
import com.onyx.android.edu.adapter.ExerciseAdapter;
import com.onyx.android.edu.adapter.SubjectAdapter;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.base.Config;
import com.onyx.android.edu.base.Constant;
import com.onyx.android.edu.ui.chapter.ChapterTypeActivity;
import com.onyx.android.edu.ui.exercisedetail.ExerciseDetailActivity;
import com.onyx.android.edu.ui.exercisepractise.ExercisePractiseActivity;
import com.onyx.android.edu.ui.findexercise.FindExerciseActivity;
import com.onyx.android.edu.utils.JsonUtils;
import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;
import com.onyx.libedu.model.BookNode;
import com.onyx.libedu.model.Difficult;
import com.onyx.libedu.model.Document;
import com.onyx.libedu.model.KnowledgePoint;
import com.onyx.libedu.model.QuestionType;
import com.onyx.libedu.model.Stage;
import com.onyx.libedu.model.Subject;
import com.onyx.libedu.model.Textbook;
import com.onyx.libedu.model.Version;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/6/28.
 */
public class ChooseExerciseFragment extends BaseFragment implements ChooseExerciseContract.ChooseExerciseView, View.OnClickListener {

    private static final String TAG = ChooseExerciseFragment.class.getSimpleName();

    @Bind(R.id.choose_type_layout)
    LinearLayout chooseTypeLayout;
    @Bind(R.id.subject_recyclerview)
    RecyclerView subjectRecyclerview;
    @Bind(R.id.textbook_view)
    DynamicMultiRadioGroupView textbookView;
    @Bind(R.id.question_type_view)
    DynamicMultiRadioGroupView questionTypeView;
    @Bind(R.id.difficulty_levels_view)
    DynamicMultiRadioGroupView difficultyLevelsView;
    @Bind(R.id.stage_title)
    TextView stageTitle;
    @Bind(R.id.select_learning_stage_text)
    TextView selectLearningStageText;
    @Bind(R.id.select_learning_stage_image)
    ImageButton selectLearningStageImage;
    @Bind(R.id.check_detail_text)
    TextView checkDetailText;
    @Bind(R.id.check_detail_image)
    ImageButton checkDetailImage;
    @Bind(R.id.exercise_test)
    ImageView exerciseTest;
    @Bind(R.id.find_more)
    TextView findMore;
    @Bind(R.id.exercise_arrow_go)
    ImageView exerciseArrowGo;
    @Bind(R.id.exercise_layout)
    RelativeLayout exerciseLayout;
    @Bind(R.id.exercise_list)
    RecyclerView exerciseList;
    @Bind(R.id.sync_practice)
    Button syncPractice;
    @Bind(R.id.general_practice)
    Button generalPractice;
    @Bind(R.id.version_view)
    DynamicMultiRadioGroupView versionView;
    @Bind(R.id.stage_view)
    DynamicMultiRadioGroupView stageView;
    @Bind(R.id.choose_chapter)
    Button chooseChapter;
    @Bind(R.id.question_type_view_line)
    View questionTypeViewLine;
    @Bind(R.id.question_type_layout)
    LinearLayout questionTypeLayout;
    @Bind(R.id.textbook_line)
    View textbookLine;
    @Bind(R.id.textbook_layout)
    LinearLayout textbookLayout;
    @Bind(R.id.difficulty_levels_line)
    View difficultyLevelsLine;
    @Bind(R.id.difficulty_levels_layout)
    LinearLayout difficultyLevelsLayout;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.subject_line)
    View subjectLine;
    @Bind(R.id.subject_layout)
    FrameLayout subjectLayout;
    @Bind(R.id.version_line)
    View versionLine;
    @Bind(R.id.version_layout)
    LinearLayout versionLayout;
    @Bind(R.id.choose_knowPoint)
    Button chooseKnowPoint;

    private ChooseExerciseContract.ChooseExercisePresenter mPresenter;

    public static ChooseExerciseFragment newInstance() {
        return new ChooseExerciseFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_choose_exercise;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    protected void initView() {
        mPresenter.subscribe();

        syncPractice.setOnClickListener(this);
        generalPractice.setOnClickListener(this);
        selectLearningStageText.setOnClickListener(this);
        selectLearningStageImage.setOnClickListener(this);
        findMore.setOnClickListener(this);
        exerciseArrowGo.setOnClickListener(this);
        checkDetailText.setOnClickListener(this);
        checkDetailImage.setOnClickListener(this);
        chooseChapter.setOnClickListener(this);

        setupListener();
        initStageView();
        initDifficultyLevelsView();
        initExerciseView();
    }

    private void setupListener() {
        chooseKnowPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter.getChooseQuestionVariable().getSubject() == null) {
                    Toast.makeText(getActivity(), getString(R.string.no_choose_subject), Toast.LENGTH_SHORT).show();
                }else {
                    mPresenter.loadKnowledgePoints();
                }
            }
        });
    }

    private void initStageView() {
        final List<Stage> stages = Stage.createCommonStages(getActivity());
        if (stages == null || stages.size() <= 0) {
            return;
        }
        List<String> names = new ArrayList<>();
        for (Stage stage : stages) {
            names.add(stage.getStageName());
        }
        int columns = Config.COMMON_COLUMNS;
        int rows = (int) Math.ceil((float) stages.size() / columns);
        stageView.setMultiAdapter(new ChooseMultiAdapter(names, rows, columns, R.drawable.rectangle_radio));
        stageView.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {
                    mPresenter.loadSubjects(stages.get(position));
                    subjectLayout.setVisibility(View.GONE);
                    subjectLine.setVisibility(View.GONE);
                }
            }
        });
        stageView.getMultiAdapter().setItemChecked(true, 0);
    }

    private void initDifficultyLevelsView() {
        final List<Difficult> difficults = Difficult.createCommonDifficults(getActivity());
        if (difficults == null || difficults.size() <= 0) {
            return;
        }
        difficultyLevelsLayout.setVisibility(View.VISIBLE);
        difficultyLevelsLine.setVisibility(View.VISIBLE);
        List<String> names = new ArrayList<>();
        for (Difficult difficult : difficults) {
            names.add(difficult.getDifficultName());
        }
        int columns = Config.COMMON_COLUMNS;
        int rows = (int) Math.ceil((float) difficults.size() / columns);
        difficultyLevelsView.setMultiAdapter(new ChooseMultiAdapter(names, rows, columns, R.drawable.rectangle_radio));
        difficultyLevelsView.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {
                    // FIXME: 2016/11/2 Write difficult to death
                    mPresenter.chooseDifficult(difficults.get(1));
                }
            }
        });
        difficultyLevelsView.getMultiAdapter().setItemChecked(true, 0);
    }

    private void initExerciseView() {
        ExerciseAdapter exerciseAdapter = new ExerciseAdapter();
        exerciseList.setLayoutManager(new GridLayoutManager(getActivity(), Config.COMMON_COLUMNS));
        exerciseList.setAdapter(exerciseAdapter);
        exerciseAdapter.setCallBack(new ExerciseAdapter.CallBack() {
            @Override
            public void OnClickItemListener() {
                startActivity(new Intent(getActivity(), ExerciseDetailActivity.class));
            }
        });
    }

    @Override
    public void setPresenter(ChooseExerciseContract.ChooseExercisePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unSubscribe();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(syncPractice) || v.equals(generalPractice)) {
            startActivity(new Intent(getActivity(), ExercisePractiseActivity.class));
        } else if (v.equals(selectLearningStageImage) || v.equals(selectLearningStageText)) {
            startActivity(new Intent(getActivity(), ChooseStudyingStageActivity.class));
        } else if (v.equals(findMore) || v.equals(exerciseArrowGo)) {
            startActivity(new Intent(getActivity(), FindExerciseActivity.class));
        } else if (v.equals(checkDetailImage) || v.equals(checkDetailText)) {
            startActivity(new Intent(getActivity(), ExercisePractiseActivity.class));
        } else if (v.equals(chooseChapter)) {
            if (mPresenter.getChooseQuestionVariable().getTextbook() == null) {
                Toast.makeText(getActivity(), getString(R.string.no_choose_textbook), Toast.LENGTH_SHORT).show();
            } else {
                mPresenter.loadBookNodes();
            }
        }
    }

    @Override
    public void showSubjects(List<Subject> subjects) {
        if (subjects == null || subjects.size() <= 0) {
            return;
        }
        subjectLayout.setVisibility(View.VISIBLE);
        subjectLine.setVisibility(View.VISIBLE);
        SubjectAdapter subjectAdapter = new SubjectAdapter(subjects, Config.subjectResIds);
        subjectRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        subjectRecyclerview.setAdapter(subjectAdapter);
        subjectAdapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Subject subject) {
                changeSubject(subject);
            }
        });
        changeSubject(subjects.get(0));
    }

    private void changeSubject(Subject subject) {
        mPresenter.loadVersions(subject);
        versionLayout.setVisibility(View.GONE);
        versionLine.setVisibility(View.GONE);

        mPresenter.loadQuestionType(subject);
        questionTypeLayout.setVisibility(View.GONE);
        questionTypeViewLine.setVisibility(View.GONE);
    }

    @Override
    public void showVersions(final List<Version> versions) {
        if (versions == null || versions.size() <= 0) {
            return;
        }
        versionLayout.setVisibility(View.VISIBLE);
        versionLine.setVisibility(View.VISIBLE);
        List<String> names = new ArrayList<>();
        for (Version version : versions) {
            names.add(version.getVersionName());
        }
        int columns = Config.COMMON_COLUMNS;
        int rows = (int) Math.ceil((float) versions.size() / columns);
        versionView.setMultiAdapter(new ChooseMultiAdapter(names, rows, columns, R.drawable.rectangle_radio));
        versionView.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {
                    mPresenter.loadTextbooks(versions.get(position));
                    textbookLayout.setVisibility(View.GONE);
                    textbookLine.setVisibility(View.GONE);
                }
            }
        });
        versionView.getMultiAdapter().setItemChecked(true, 0);
    }

    @Override
    public void showTextbooks(final List<Textbook> textbooks) {
        if (textbooks == null || textbooks.size() <= 0) {
            return;
        }
        textbookLayout.setVisibility(View.VISIBLE);
        textbookLine.setVisibility(View.VISIBLE);
        List<String> names = new ArrayList<>();
        for (Textbook textbook : textbooks) {
            names.add(textbook.getBookName());
        }
        int columns = Config.COMMON_COLUMNS;
        int rows = (int) Math.ceil((float) textbooks.size() / columns);
        textbookView.setMultiAdapter(new ChooseMultiAdapter(names, rows, columns, R.drawable.rectangle_radio));
        textbookView.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {
                    mPresenter.chooseTextbook(textbooks.get(position));
                    mPresenter.loadDocuments();
                }
            }
        });
        textbookView.getMultiAdapter().setItemChecked(true, 0);
    }

    @Override
    public void showBookNodes(List<BookNode> bookNodes) {
        if (bookNodes == null || bookNodes.size() <= 0) {
            Toast.makeText(getActivity(), getString(R.string.no_find_chapter), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), ChapterTypeActivity.class);
        intent.putExtra(Constant.BOOK_NODE, JSON.toJSONString(bookNodes));
        intent.putExtra(Constant.CHOOSE_QUESTION_VARIABLE, JSON.toJSONString(mPresenter.getChooseQuestionVariable()));
        startActivity(intent);
    }

    @Override
    public void showKnowledgePoints(List<KnowledgePoint> knowledgePoints) {
        if (knowledgePoints == null || knowledgePoints.size() <= 0) {
            Toast.makeText(getActivity(), getString(R.string.no_find_knowledgePoint), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), ChapterTypeActivity.class);
        intent.putExtra(Constant.KNOW_LEDGE_POINT, JSON.toJSONString(knowledgePoints));
        intent.putExtra(Constant.CHOOSE_QUESTION_VARIABLE, JSON.toJSONString(mPresenter.getChooseQuestionVariable()));
        startActivity(intent);
    }

    @Override
    public void showQuestionType(final List<QuestionType> questionTypes) {
        if (questionTypes == null || questionTypes.size() <= 0) {
            return;
        }
        questionTypeLayout.setVisibility(View.VISIBLE);
        questionTypeViewLine.setVisibility(View.VISIBLE);
        List<String> names = new ArrayList<>();
        for (QuestionType questionType : questionTypes) {
            names.add(questionType.getQuestionTypeName());
        }
        int columns = Config.COMMON_COLUMNS;
        int rows = (int) Math.ceil((float) questionTypes.size() / columns);
        questionTypeView.setMultiAdapter(new ChooseMultiAdapter(names, rows, columns, R.drawable.rectangle_radio));
        questionTypeView.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {
                    mPresenter.chooseQuestionType(questionTypes.get(position));
                }
            }
        });
        questionTypeView.getMultiAdapter().setItemChecked(true, 0);
    }

    @Override
    public void showDocuments(List<Document> documents) {
        Log.d(TAG, JsonUtils.toJson(documents));
    }
}
