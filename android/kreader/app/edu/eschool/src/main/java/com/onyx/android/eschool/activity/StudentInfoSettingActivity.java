package com.onyx.android.eschool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.adapter.ChooseItemViewHolder;
import com.onyx.android.eschool.model.StudentAccount;
import com.onyx.android.eschool.utils.AvatarUtils;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by suicheng on 2016/11/25.
 */

public class StudentInfoSettingActivity extends BaseActivity {

    @Bind(R.id.edit_student_name)
    EditText studentName;
    @Bind(R.id.edit_student_id)
    EditText studentId;
    @Bind(R.id.edit_student_grade_class)
    EditText studentGradeClass;
    @Bind(R.id.imageView_user_image)
    ImageView userProfileAvatar;

    @Bind(R.id.student_grade_panel)
    ViewGroup gradePanel;
    @Bind(R.id.publisher_page_view)
    PageRecyclerView publisherPageView;
    @Bind(R.id.grade_page_view)
    PageRecyclerView gradePageView;

    @Bind(R.id.student_grade)
    CheckedTextView studentGrade;
    @Bind(R.id.material_publisher)
    CheckedTextView materialPublisher;

    @Bind(R.id.textView_primary_school)
    CheckedTextView primarySchool;
    @Bind(R.id.textView_secondary_school)
    CheckedTextView secondarySchool;
    @Bind(R.id.textView_high_school)
    CheckedTextView highSchool;

    @Bind({R.id.textView_primary_school, R.id.textView_secondary_school, R.id.textView_high_school})
    List<CheckedTextView> checkedTextViews;

    private Map<String, List<String>> gradeMap = new LinkedHashMap<>();
    private List<String> gradeList = new ArrayList<>();
    private List<String> materialList = new ArrayList<>();
    private String schoolSelected;
    private String publisherSelected;
    private String gradeSelected;

    private StudentAccount account;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_user_info_setting;
    }

    @Override
    protected void initConfig() {
        gradeSelected = StudentPreferenceManager.loadGradeSelected(this, "");
        publisherSelected = StudentPreferenceManager.loadPublisherSelected(this, "");
        schoolSelected = StudentPreferenceManager.loadSchoolSelected(this, "");
        account = StudentAccount.loadAccount(this);
    }

    @Override
    protected void initView() {
        initAccountInfoView();
        initGradePageView();
        initPublisherPageView();
    }

    private void initAccountInfoView() {
        studentName.setText(account.name);
        studentId.setText(account.studentId);
        studentGradeClass.setText(account.gradeClass);
    }

    private void initGradePageView() {
        gradePageView.setLayoutManager(new DisableScrollGridManager(this));
        gradePageView.setAdapter(new PageRecyclerView.PageAdapter<ChooseItemViewHolder>() {
            @Override
            public int getRowCount() {
                return 6;
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public int getDataCount() {
                return getDataListSize(gradeList);
            }

            @Override
            public ChooseItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                ChooseItemViewHolder viewHolder = ChooseItemViewHolder.create(StudentInfoSettingActivity.this, parent);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processGradeClick((Integer) v.getTag());
                    }
                });
                return viewHolder;
            }

            @Override
            public void onPageBindViewHolder(ChooseItemViewHolder holder, int position) {
                holder.itemView.setTag(position);

                holder.titleTextView.setText(gradeList.get(position));
                boolean choose = StringUtils.isNullOrEmpty(gradeSelected) ? (position == 0) : (gradeSelected.equalsIgnoreCase(gradeList.get(position)));
                holder.chooseImageView.setImageResource(choose ? R.drawable.delivery_dot_green : R.drawable.delivery_dot_grey);
            }
        });
    }

    private void initPublisherPageView() {
        publisherPageView.setLayoutManager(new DisableScrollGridManager(this));
        publisherPageView.setAdapter(new PageRecyclerView.PageAdapter<ChooseItemViewHolder>() {
            @Override
            public int getRowCount() {
                return 3;
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public int getDataCount() {
                return getDataListSize(materialList);
            }

            @Override
            public ChooseItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                ChooseItemViewHolder viewHolder = ChooseItemViewHolder.create(StudentInfoSettingActivity.this, parent);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processMaterialClick((Integer) v.getTag());
                    }
                });
                return viewHolder;
            }

            @Override
            public void onPageBindViewHolder(ChooseItemViewHolder holder, int position) {
                holder.itemView.setTag(position);

                holder.titleTextView.setText(materialList.get(position));
                boolean choose = StringUtils.isNullOrEmpty(publisherSelected) ? (position == 0) : (publisherSelected.equalsIgnoreCase(materialList.get(position)));
                holder.chooseImageView.setImageResource(choose ? R.drawable.delivery_dot_green : R.drawable.delivery_dot_grey);
            }
        });
    }

    private <T> int getDataListSize(List<T> list) {
        return CollectionUtils.isNullOrEmpty(list) ? 0 : list.size();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AvatarUtils.loadAvatar(this, userProfileAvatar, StudentAccount.loadAvatarPath(this));
    }

    @Override
    protected void initData() {
        loadGradeData();
        loadTeachingMaterialData();
        updateCheckTextViews();
    }

    private void loadGradeData() {
        String content = RawResourceUtil.contentOfRawResource(this, R.raw.student_grade);
        Map<String, List<String>> map = JSON.parseObject(content, new TypeReference<Map<String, List<String>>>() {
        });
        if (!CollectionUtils.isNullOrEmpty(map)) {
            List<String> list = Arrays.asList(map.keySet().toArray(new String[]{}));
            for (String key : sortSchoolList(list)) {
                gradeMap.put(key, sortGradeList(map.get(key)));
            }
            if (!CollectionUtils.isNullOrEmpty(list)) {
                if (StringUtils.isNullOrEmpty(schoolSelected)) {
                    schoolSelected = list.get(0);
                    updateCheckTextViews();
                }
                gradeList = gradeMap.get(schoolSelected);
                if (!CollectionUtils.isNullOrEmpty(gradeList)) {
                    gradeSelected = gradeList.get(0);
                }
            }
        }
        gradePageView.getAdapter().notifyDataSetChanged();
    }

    private void loadTeachingMaterialData() {
        String content = RawResourceUtil.contentOfRawResource(this, R.raw.teaching_material);
        materialList = JSON.parseObject(content, new TypeReference<List<String>>() {
        });
        if (StringUtils.isNullOrEmpty(publisherSelected) && !CollectionUtils.isNullOrEmpty(materialList)) {
            publisherSelected = materialList.get(0);
        }
    }

    private int getSortLevel(String s) {
        if (s.contains("小")) {
            return 0;
        }
        if (s.contains("初")) {
            return 1;
        }
        if (s.contains("高")) {
            return 2;
        }
        if (s.contains("大")) {
            return 3;
        }
        return 0;
    }

    private int getSortGrade(String s) {
        if (s.contains("一")) {
            return 0;
        }
        if (s.contains("二")) {
            return 1;
        }
        if (s.contains("三")) {
            return 2;
        }
        if (s.contains("四")) {
            return 3;
        }
        if (s.contains("五")) {
            return 4;
        }
        if (s.contains("六")) {
            return 5;
        }
        return 0;
    }

    private List<String> sortSchoolList(List<String> originList) {
        sortList(originList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return compareSortNumber(getSortLevel(lhs), getSortLevel(rhs));
            }
        });
        return originList;
    }

    private List<String> sortGradeList(List<String> originList) {
        sortList(originList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return compareSortNumber(getSortGrade(lhs), getSortGrade(rhs));
            }
        });
        return originList;
    }

    private List<String> sortList(List<String> originList, Comparator<String> comparator) {
        Collections.sort(originList, comparator);
        return originList;
    }

    private int compareSortNumber(int left, int right) {
        if (left == right) {
            return 0;
        }
        return left > right ? 1 : -1;
    }

    private void processMaterialClick(int position) {
        publisherSelected = materialList.get(position);
        publisherPageView.getAdapter().notifyDataSetChanged();
    }

    private void processGradeClick(int position) {
        gradeSelected = gradeList.get(position);
        gradePageView.getAdapter().notifyDataSetChanged();
    }

    private void updateCheckTextViews() {
        String[] schoolArray;
        if (CollectionUtils.isNullOrEmpty(gradeMap)) {
            return;
        }
        schoolArray = gradeMap.keySet().toArray(new String[]{});
        if (schoolArray.length <= 0) {
            return;
        }
        int selectPosition = 0;
        for (int i = 0; i < checkedTextViews.size(); i++) {
            String school = schoolArray[i];
            if (school.equals(schoolSelected)) {
                selectPosition = i;
            }
            checkedTextViews.get(i).setText(schoolArray[i]);
        }
        processSchoolOnClick(selectPosition);
    }

    private void toggleSelectView(boolean showGrade) {
        gradePanel.setVisibility(showGrade ? View.VISIBLE : View.INVISIBLE);
        publisherPageView.setVisibility(!showGrade ? View.VISIBLE : View.INVISIBLE);
        studentGrade.setChecked(showGrade);
        materialPublisher.setChecked(!showGrade);
    }

    @OnClick(R.id.student_grade)
    void studentGradeOnClick() {
        toggleSelectView(true);
    }

    @OnClick(R.id.material_publisher)
    void materialPublisherOnClick() {
        toggleSelectView(false);
    }

    private void checkSelectedSchoolView(int position) {
        for (int i = 0; i < checkedTextViews.size(); i++) {
            checkedTextViews.get(i).setChecked(position == i);
        }
    }

    private void processSchoolOnClick(int position) {
        checkSelectedSchoolView(position);
        gradeList = gradeMap.get(schoolSelected = gradeMap.keySet().toArray(new String[]{})[position]);
        gradePageView.getAdapter().notifyDataSetChanged();
    }

    @OnClick({R.id.textView_primary_school, R.id.textView_secondary_school, R.id.textView_high_school})
    void schoolOnClick(CheckedTextView checkedTextView) {
        switch (checkedTextView.getId()) {
            case R.id.textView_primary_school:
                processSchoolOnClick(0);
                break;
            case R.id.textView_secondary_school:
                processSchoolOnClick(1);
                break;
            case R.id.textView_high_school:
                processSchoolOnClick(2);
                break;
        }
    }

    @OnClick(R.id.imageView_user_image)
    void userAvatarOnClick() {
        ActivityUtil.startActivitySafely(this, new Intent(this, AvatarSelectActivity.class));
    }

    private String getEditTextString(EditText editText) {
        return editText.getText().toString().trim();
    }

    private void saveStudentAccountInfo() {
        account.name = getEditTextString(studentName);
        account.studentId = getEditTextString(studentId);
        account.gradeClass = getEditTextString(studentGradeClass);
        account.saveAccount(this);
    }

    @OnClick(R.id.confirm_save)
    void confirmSaveOnClick() {
        StudentPreferenceManager.saveGradeSelected(this, gradeSelected);
        StudentPreferenceManager.savePublisherSelected(this, publisherSelected);
        StudentPreferenceManager.saveSchoolSelected(this, schoolSelected);
        saveStudentAccountInfo();
        showToast(R.string.save_success, Toast.LENGTH_SHORT);
    }
}
