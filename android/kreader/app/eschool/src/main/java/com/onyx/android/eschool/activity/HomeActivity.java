package com.onyx.android.eschool.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.model.StudentAccount;
import com.onyx.android.eschool.utils.AvatarUtils;
import com.onyx.android.sdk.utils.ActivityUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by suicheng on 2016/11/15.
 */
public class HomeActivity extends BaseActivity {
    private static final String EDU_INTENT_CONTENT_TYPE = "contentType";
    private static final String EDU_INTENT_CONTENT_TYPE_EXAM = "examination";
    private static final String EDU_INTENT_CONTENT_TYPE_PRACTISE= "practice";
    private SimpleDateFormat dateFormat;

    // about userInfo
    @Bind(R.id.textView_user_name)
    TextView userName;
    @Bind(R.id.textView_user_class)
    TextView userClass;
    @Bind(R.id.imageView_user_image)
    ImageView userImage;

    // about notify panel
    @Bind(R.id.text_date)
    TextView date;
    @Bind(R.id.textView_location)
    TextView location;
    @Bind(R.id.textView_weather_text)
    TextView weatherText;
    @Bind(R.id.textView_notify)
    TextView notifyText;
    @Bind(R.id.imageView_weather_image)
    ImageView weatherImage;

    // about teaching
    @Bind(R.id.textView_material_title)
    TextView teachingMaterialText;
    @Bind(R.id.textView_auxiliary_title)
    TextView teachingAuxiliaryText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileInfo();
        updateNotifyPanel();
        loadTeachingData();
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        initNormalItem();
    }

    @Override
    protected void initData() {

    }

    protected void initConfig() {
        dateFormat = new SimpleDateFormat(getString(R.string.home_time_date_format));
    }

    private void updateNotifyPanel() {
        date.setText(dateFormat.format(new Date()));
        loadWeatherInfo();
        loadNotifyMessage();
    }

    private void updateProfileInfo() {
        AvatarUtils.loadAvatar(this, userImage, StudentAccount.loadAvatarPath(this));
        StudentAccount account = StudentAccount.loadAccount(this);
        userName.setText(account.name);
        userClass.setText(account.gradeClass);
    }

    private void initNormalItem() {
        initNormalView(R.id.home_note_item, R.string.home_item_note_text, R.drawable.home_note);
        initNormalView(R.id.home_dictionary_item, R.string.home_item_dictionary_text, R.drawable.home_dictionary);
        initNormalView(R.id.home_practice_item, R.string.home_item_practice_text, R.drawable.home_practice);
        initNormalView(R.id.home_exam_item, R.string.home_item_exam_text, R.drawable.home_exam);
    }

    private void initNormalView(int layoutResId, int textResId, int imageResId) {
        ViewGroup viewGroup = (ViewGroup) findViewById(layoutResId);
        TextView textView = (TextView) viewGroup.findViewById(R.id.textView_category_text);
        textView.setText(textResId);
        ImageView imageView = (ImageView) viewGroup.findViewById(R.id.imageView_category_image);
        imageView.setImageResource(imageResId);
    }

    private void loadWeatherInfo() {
    }

    private void loadNotifyMessage() {
    }

    private void loadTeachingData() {
    }

    @OnClick(R.id.textView_study_process)
    void onStudyScheduleClick() {
        ActivityUtil.startActivitySafely(this, new Intent(this, StudyPreviewActivity.class));
    }

    @OnClick(R.id.home_teaching_material_item)
    void onTeachingMaterialClick() {
        ActivityUtil.startActivitySafely(this, new Intent(this, TeachingMaterialActivity.class));
    }

    @OnClick(R.id.home_teaching_auxiliary_item)
    void onTeachingAuxiliaryClick() {
        ActivityUtil.startActivitySafely(this, new Intent(this, TeachingAuxiliaryActivity.class));
    }

    @OnClick(R.id.home_note_item)
    void onNoteItemClick() {
        ActivityUtil.startActivitySafely(this, getPackageManager().getLaunchIntentForPackage("com.onyx.android.note"));
    }

    @OnClick(R.id.home_dictionary_item)
    void onDictionaryItemClick() {
        ActivityUtil.startActivitySafely(this, getPackageManager().getLaunchIntentForPackage("com.onyx.dict"));
    }

    private Intent getEduIntent() {
        Intent intent = new Intent();
        intent.setClassName("com.onyx.android.edu", "com.onyx.android.edu.ChooseExerciseActivity");
        return intent;
    }

    private Intent getSettingIntent() {
        Intent intent = new Intent();
        intent.setClassName("com.onyx.android.libsetting", "con.onyx.android.libsetting.view.activity.DeviceMainSettingActivity");
        return intent;
    }

    @OnClick(R.id.home_exam_item)
    void onExamItemClick() {
        Intent intent = getEduIntent();
        intent.putExtra(EDU_INTENT_CONTENT_TYPE, EDU_INTENT_CONTENT_TYPE_EXAM);
        ActivityUtil.startActivitySafely(this, intent);
    }

    @OnClick(R.id.home_practice_item)
    void onPracticeItemClick() {
        Intent intent = getEduIntent();
        intent.putExtra(EDU_INTENT_CONTENT_TYPE, EDU_INTENT_CONTENT_TYPE_PRACTISE);
        ActivityUtil.startActivitySafely(this, intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            ActivityUtil.startActivitySafely(this, getSettingIntent());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
