package com.onyx.android.eschool.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.eschool.R;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2016/11/15.
 */
public class HomeActivity extends OnyxAppCompatActivity {
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
    @Bind(R.id.text_time)
    DigitalClock digitalClock;
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
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        initConfig();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotifyPanel();
        loadTeachingData();
    }

    private void initConfig() {
        dateFormat = new SimpleDateFormat(getString(R.string.home_time_date_format));
    }

    private void initViews() {
        initNormalItem();
    }

    private void updateNotifyPanel() {
        date.setText(dateFormat.format(new Date()));
        loadWeatherInfo();
        loadNotifyMessage();
    }

    private void initNormalItem() {
        initNormalView(R.id.home_note_item, R.string.home_item_note_text, R.drawable.home_note);
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

    @OnClick(R.id.imageView_user_image)
    void onProfileImageClick(View view) {
        showToast("onProfileImageClick", Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.textView_study_process)
    void onStudyScheduleClick(View view) {
        showToast("onStudyScheduleClick", Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.home_teaching_material_item)
    void onTeachingMaterialClick() {
        showToast("onTeachingMaterialClick", Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.home_teaching_auxiliary_item)
    void onTeachingAuxiliaryClick() {
        showToast("onTeachingAuxiliaryClick", Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.home_note_item)
    void onNoteItemClick() {
        showToast("onNoteItemClick", Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.home_exam_item)
    void onExamItemClick() {
        showToast("onExamItemClick", Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.home_practice_item)
    void onPracticeItemClick() {
        showToast("onPracticeItemClick", Toast.LENGTH_SHORT);
    }

    private void showToast(int resId, int duration) {
        showToast(getString(resId), duration);
    }

    private void showToast(String message, int duration) {
        Toast.makeText(this, message, duration).show();
    }
}
