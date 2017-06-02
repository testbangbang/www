package com.onyx.android.edu.ui.chooseexercise;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.ChooseMultiAdapter;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.base.Config;
import com.onyx.android.sdk.ui.compat.AppCompatLinearLayout;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.libedu.model.BookNode;
import com.onyx.libedu.model.Document;
import com.onyx.libedu.model.KnowledgePoint;
import com.onyx.libedu.model.QuestionType;
import com.onyx.libedu.model.Stage;
import com.onyx.libedu.model.Subject;
import com.onyx.libedu.model.Textbook;
import com.onyx.libedu.model.Version;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2016/11/24.
 */

public class ChooseExerciseColorFragment extends BaseFragment implements ChooseExerciseContract.ChooseExerciseView {

    @Bind(R.id.exercise_list)
    PageRecyclerView exerciseList;
    @Bind(R.id.subject_group)
    DynamicMultiRadioGroupView subjectGroup;
    @Bind(R.id.subject_group_layout)
    LinearLayout subjectGroupLayout;
    @Bind(R.id.stage_group)
    DynamicMultiRadioGroupView stageGroup;
    @Bind(R.id.stage_layout)
    LinearLayout stageLayout;
    @Bind(R.id.textbook_group)
    DynamicMultiRadioGroupView textbookGroup;
    @Bind(R.id.version_group)
    DynamicMultiRadioGroupView versionGroup;
    @Bind(R.id.text_subject)
    TextView textSubject;
    @Bind(R.id.text_version)
    TextView textVersion;
    @Bind(R.id.text_textbook)
    TextView textTextbook;
    @Bind(R.id.dismiss_layout)
    LinearLayout dismissLayout;
    @Bind(R.id.btn_sync)
    Button btnSync;
    @Bind(R.id.btn_test)
    Button btnTest;
    @Bind(R.id.btn_composite)
    Button btnComposite;


    private ChooseExerciseContract.ChooseExercisePresenter presenter;
    private String documentDisplayPath = "/mnt/sdcard/edu/reading.pdf";

    public static ChooseExerciseColorFragment newInstance() {
        return new ChooseExerciseColorFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_color_choose_exercise;
    }

    @Override
    public void showSubjects(final List<Subject> subjects) {
        if (subjects == null || subjects.size() <= 0) {
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
        subjectGroup.getMultiAdapter().setItemChecked(true, 0);
        presenter.loadVersions(subjects.get(0));
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
        stageGroup.setMultiAdapter(new ChooseMultiAdapter(names, rows, columns, R.drawable.rectangle_radio));
        stageGroup.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {
                    presenter.loadSubjects(stages.get(position));
                }
            }
        });
        stageGroup.getMultiAdapter().setItemChecked(true, 0);
        selectGroupView(subjectGroup);
    }

    @Override
    public void showVersions(final List<Version> versions) {
        if (versions == null || versions.size() <= 0) {
            return;
        }
        final int columns = 2;
        final int rows = (int) Math.ceil((float) versions.size() / columns);
        versionGroup.setMultiAdapter(new DynamicMultiRadioGroupView.MultiAdapter() {
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
                return versions.size();
            }

            @Override
            public void bindView(CompoundButton button, int position) {
                final Version version = versions.get(position);
                button.setText(version.getVersionName());
                button.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            }
        });
        versionGroup.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {
                    presenter.loadTextbooks(versions.get(position));
                }
            }
        });
        versionGroup.getMultiAdapter().setItemChecked(true, 0);
        presenter.loadTextbooks(versions.get(0));
    }

    @Override
    public void showTextbooks(final List<Textbook> textbooks) {
        if (textbooks == null || textbooks.size() <= 0) {
            return;
        }
        final int columns = 3;
        final int rows = (int) Math.ceil((float) textbooks.size() / columns);
        textbookGroup.setMultiAdapter(new DynamicMultiRadioGroupView.MultiAdapter() {
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
                return textbooks.size();
            }

            @Override
            public void bindView(CompoundButton button, int position) {
                final Textbook textbook = textbooks.get(position);
                button.setText(textbook.getBookName());
                button.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            }
        });
        textbookGroup.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {
                    presenter.chooseTextbook(textbooks.get(position));
                }
            }
        });
        textbookGroup.getMultiAdapter().setItemChecked(true, 0);
        presenter.chooseTextbook(textbooks.get(0));
        presenter.loadDocuments();
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
        subjectGroupLayout.setVisibility(View.GONE);
        btnComposite.setVisibility(presenter.isPractice() ? View.VISIBLE :View.GONE);
        btnSync.setVisibility(presenter.isPractice() ? View.VISIBLE :View.GONE);
        btnTest.setVisibility(presenter.isPractice() ? View.INVISIBLE :View.VISIBLE);

        initExerciseList();
        initStageView();
        setupListener();
        presenter.loadDocuments();
    }

    private void setupListener() {
        textSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGroupView(subjectGroup);
            }
        });

        textVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGroupView(versionGroup);
            }
        });

        textTextbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGroupView(textbookGroup);
            }
        });

        dismissLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSubjectView();
            }
        });
    }

    private void selectGroupView(DynamicMultiRadioGroupView showView) {
        boolean showVersion = versionGroup.equals(showView);
        boolean showSubject = subjectGroup.equals(showView);
        boolean showTextBook = textbookGroup.equals(showView);

        versionGroup.setVisibility(showVersion ? View.VISIBLE : View.GONE);
        subjectGroup.setVisibility(showSubject ? View.VISIBLE : View.GONE);
        textbookGroup.setVisibility(showTextBook ? View.VISIBLE : View.GONE);
        stageLayout.setVisibility(showSubject ? View.VISIBLE : View.GONE);

        textSubject.setActivated(showSubject);
        textVersion.setActivated(showVersion);
        textTextbook.setActivated(showTextBook);

        textSubject.setTextColor(showSubject ? Color.WHITE : Color.BLACK);
        textVersion.setTextColor(showVersion ? Color.WHITE : Color.BLACK);
        textTextbook.setTextColor(showTextBook ? Color.WHITE : Color.BLACK);
    }

    private void initExerciseList() {
        exerciseList.setLayoutManager(new DisableScrollGridManager(getActivity()));
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

    @Override
    public void showDocuments(final List<Document> documents) {
        exerciseList.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return 3;
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public int getDataCount() {
                //return 17;
                return 1;
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                CommonViewHolder viewHolder = (CommonViewHolder)holder;
                ImageView imageView = viewHolder.getView(R.id.practice_image);
                String imageName = String.format("practice_bg%d", position + 1);
                imageView.setImageResource(getImageResourceId(imageName));
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openDocumentFile(position);
                    }
                });
            }
        });
    }

    private void openDocumentFile(final int position) {
        ActivityUtil.startActivitySafely(getActivity(),
                ViewDocumentUtils.viewActionIntentWithMimeType(new File(documentDisplayPath)),
                ViewDocumentUtils.getReaderComponentName(getActivity()));
    }

    public int getImageResourceId(String name) {
        R.drawable drawables=new R.drawable();
        int resId= R.drawable.practice_bg1;
        try {
            java.lang.reflect.Field field=R.drawable.class.getField(name);
            resId=(Integer)field.get(drawables);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resId;
    }

}
