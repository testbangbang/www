package com.onyx.android.eschool.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.model.StudentAccount;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.eschool.utils.AvatarUtils;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by suicheng on 2016/11/19.
 */

public class StudyPreviewActivity extends BaseActivity {

    TextView materialProgressView;
    TextView auxiliaryProgressView;
    TextView practiceProgressView;
    TextView personProgressView;

    @Bind(R.id.study_preview_total_progress)
    TextView totalProgressView;

    @Bind(R.id.imageView_user_image)
    ImageView profileAvatar;
    @Bind(R.id.textView_user_name)
    TextView profileName;
    @Bind(R.id.textView_user_class)
    TextView profileClass;
    @Bind(R.id.textView_user_student_id)
    TextView profileStudentId;

    @Bind(R.id.textView_user_grade)
    TextView studentGrade;
    @Bind(R.id.textView_teaching_material)
    TextView studentMaterial;

    private int materialProgress;
    private int auxiliaryProgress;
    private int practiceProgress;
    private int personProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_study_preview;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initCategoryViews();
        initCategoryProgressViews();
    }

    private void initCategoryViews() {
        initCategoryView(R.id.study_preview_category_item_material,
                getPL107CategoryImageDrawableRes(R.drawable.study_preview_material_without_text, R.drawable.home_teaching_material_without_text),
                R.string.home_item_teaching_materials_text);
        initCategoryView(R.id.study_preview_category_item_auxiliary,
                getPL107CategoryImageDrawableRes(R.drawable.study_preview_teaching_auxiliary, R.drawable.home_teaching_auxiliary),
                R.string.home_item_teaching_auxiliary_text);
        initCategoryView(R.id.study_preview_category_item_practice,
                getPL107CategoryImageDrawableRes(R.drawable.study_preview_practice, R.drawable.home_practice),
                R.string.home_item_practice_text);
        initCategoryView(R.id.study_preview_category_item_person,
                getPL107CategoryImageDrawableRes(R.drawable.study_preview_material_person, R.drawable.material_person),
                R.string.study_preview_person);
    }

    private int getPL107CategoryImageDrawableRes(int resId, int fallbackResId) {
        return AppCompatUtils.isColorDevice(this) ? resId : fallbackResId;
    }

    private void initCategoryView(int layoutResId, int imageResId, int textResId) {
        ViewGroup viewGroup = (ViewGroup) findViewById(layoutResId);
        ImageView imageView = (ImageView) viewGroup.findViewById(R.id.imageView_category_image);
        imageView.setImageResource(imageResId);
        TextView textView = (TextView) viewGroup.findViewById(R.id.textView_category_text);
        if (textResId > 0) {
            textView.setText(textResId);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void initCategoryProgressViews() {
        materialProgressView = initProgressView(R.id.study_preview_progress_item_material, Color.GREEN);
        auxiliaryProgressView = initProgressView(R.id.study_preview_progress_item_material_auxiliary, Color.RED);
        practiceProgressView = initProgressView(R.id.study_preview_progress_item_material_practice, Color.BLUE);
        personProgressView = initProgressView(R.id.study_preview_progress_item_material_person, Color.GREEN);
    }

    private TextView initProgressView(int layoutResId, int color) {
        ViewGroup viewGroup = (ViewGroup) findViewById(layoutResId);
        View view = viewGroup.findViewById(R.id.textView_study_color_view);
        view.setBackgroundColor(color);
        return (TextView) viewGroup.findViewById(R.id.textView_study_process);
    }

    private String getTextViewString(TextView textView) {
        String text = textView.getText().toString();
        if (StringUtils.isNotBlank(text)) {
            text = text.substring(0, 3);
        }
        return text;
    }

    private void updateUserProfileInfo() {
        AvatarUtils.loadAvatar(this, profileAvatar, AvatarUtils.getSpecifyArrayAvatarPath(this, R.array.study_preview_user_avatar_array));
        StudentAccount account = StudentAccount.loadAccount(this);

        profileName.setText(getTextViewString(profileName) + account.name);
        profileClass.setText(getTextViewString(profileClass) + account.gradeClass);
        profileStudentId.setText(getTextViewString(profileStudentId) + account.studentId);

        studentGrade.setText(StudentPreferenceManager.loadGradeSelected(this, ""));
        studentMaterial.setText(StudentPreferenceManager.loadPublisherSelected(this, ""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserProfileInfo();
    }

    @Override
    protected void initData() {
        loadStudyScheduleProgress();
    }

    private void loadStudyScheduleProgress() {
        materialProgress = TestUtils.randInt(0, 100);
        auxiliaryProgress = TestUtils.randInt(0, 100);
        practiceProgress = TestUtils.randInt(0, 100);
        personProgress = TestUtils.randInt(0, 100);
        updateScheduleProgress();
    }

    private void updateScheduleProgress() {
        materialProgressView.setText(getProgress(materialProgress));
        auxiliaryProgressView.setText(getProgress(auxiliaryProgress));
        practiceProgressView.setText(getProgress(practiceProgress));
        personProgressView.setText(getProgress(personProgress));
        totalProgressView.setText(String.valueOf((materialProgress + auxiliaryProgress + practiceProgress + personProgress) / 4));
    }

    private String getProgress(int progress) {
        return progress + "%";
    }

    @OnClick(R.id.imageView_user_setting)
    void settingOnClick() {
        ActivityUtil.startActivitySafely(this, new Intent(this, StudentInfoSettingActivity.class));
    }
}
