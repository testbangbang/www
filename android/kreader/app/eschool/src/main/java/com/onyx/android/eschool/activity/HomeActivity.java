package com.onyx.android.eschool.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.model.AppConfig;
import com.onyx.android.eschool.model.StudentAccount;
import com.onyx.android.eschool.utils.AvatarUtils;
import com.onyx.android.eschool.utils.ResourceUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
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
    private static final String EDU_INTENT_CONTENT_TYPE_PRACTISE = "practice";

    private SimpleDateFormat dateFormat;

    // about notify panel
    TextView date;
    TextView location;
    TextView weatherText;
    TextView notifyText;
    ImageView weatherImage;

    private String picDisplayPath = "/mnt/sdcard/slide/sample-cfa_01.png";
    private String videoDisplayPath = "/mnt/sdcard/slide/video_display.flv";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotifyPanel();
        loadTeachingData();
    }

    @Override
    protected Integer getLayoutId() {
        return getLayoutIdByConfig();
    }

    private int getLayoutIdByConfig() {
        AppConfig config = AppConfig.sharedInstance(this);
        int layoutId = ResourceUtils.getLayoutResIdByName(this, config.getHomeLayout());
        return layoutId <= 0 ? R.layout.activity_home : layoutId;
    }

    @Override
    protected void initView() {
        initNormalItem();
        initViewByConfig();
    }

    protected void initViewByConfig() {
        if (AppConfig.sharedInstance(this).isForDisplayHomeLayout()) {
            initDisplayItemView();
        } else {
            initWeatherNotifyPanel();
        }
    }

    @Override
    protected void initData() {

    }

    protected void initConfig() {
        dateFormat = new SimpleDateFormat(getString(R.string.home_time_date_format));
        initConfigByAppConfig();
    }

    private void initConfigByAppConfig() {
        if (AppConfig.sharedInstance(this).isForDisplayHomeLayout()) {
            String path = AppConfig.sharedInstance(this).getHomePicDisplayFilePath();
            if (StringUtils.isNotBlank(path)) {
                picDisplayPath = path;
            }
            path = AppConfig.sharedInstance(this).getHomeVideoDisplayFilePath();
            if (StringUtils.isNotBlank(path)) {
                videoDisplayPath = path;
            }
        }
    }

    private void updateNotifyPanel() {
        if (AppConfig.sharedInstance(this).isForDisplayHomeLayout()) {
            return;
        }
        date.setText(dateFormat.format(new Date()));
        loadWeatherInfo();
        loadNotifyMessage();
    }

    private void initWeatherNotifyPanel() {
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.home_weather_notify);
        weatherImage = (ImageView) viewGroup.findViewById(R.id.imageView_weather_image);
        weatherText = (TextView) viewGroup.findViewById(R.id.textView_weather_text);
        date = (TextView) viewGroup.findViewById(R.id.text_date);
        location = (TextView) viewGroup.findViewById(R.id.textView_location);
        notifyText = (TextView) viewGroup.findViewById(R.id.textView_notify);
    }

    private void initNormalItem() {
        initNormalView(R.id.home_dictionary_item, R.string.home_item_application_text, R.drawable.home_application, getApplicationListIntent());
        initNormalView(R.id.home_setting_item, R.string.home_item_setting_text, R.drawable.home_setting, getSettingIntent());
    }

    private void initDisplayItemView() {
        initNormalView(R.id.home_pic_display_item, R.string.home_item_pic_display_text, R.drawable.home_pic_display, getPicDisplayIntent());
        //initNormalView(R.id.home_video_display_item, R.string.home_item_video_display_text, R.drawable.home_video_display, getVideoPlayIntent());
    }

    private void initNormalView(int layoutResId, int textResId, int imageResId, final Intent intent) {
        ViewGroup viewGroup = (ViewGroup) findViewById(layoutResId);
        TextView textView = (TextView) viewGroup.findViewById(R.id.textView_category_text);
        textView.setText(textResId);
        ImageView imageView = (ImageView) viewGroup.findViewById(R.id.imageView_category_image);
        imageView.setImageResource(imageResId);
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNormalViewClick(intent);
            }
        });
    }

    private void processNormalViewClick(Intent intent) {
        ActivityUtil.startActivitySafely(this, intent);
    }

    private void loadWeatherInfo() {
    }

    private void loadNotifyMessage() {
    }

    private void loadTeachingData() {
    }


    @OnClick(R.id.home_main_item)
    void onTeachingMaterialClick() {
        ActivityUtil.startActivitySafely(this, getPackageManager().getLaunchIntentForPackage("com.youngy.ui"));
    }

    private Intent getPicDisplayIntent() {
        Intent intent = ViewDocumentUtils.viewActionIntentWithMimeType(new File(picDisplayPath));
        ComponentName component = ViewDocumentUtils.getReaderComponentName(this);
        if (component != null) {
            intent.setComponent(component);
        }
        return intent;
    }

    private Intent getVideoPlayIntent() {
        return ViewDocumentUtils.viewActionIntentWithMimeType(new File(videoDisplayPath));
    }

    private Intent getNoteIntent() {
        return getPackageManager().getLaunchIntentForPackage("com.onyx.android.note");
    }

    private Intent getDictIntent() {
        return getPackageManager().getLaunchIntentForPackage("com.onyx.dict");
    }

    private Intent getApplicationListIntent() {
        return new Intent(this, ApplicationsActivity.class);
    }

    private Intent getEduIntent() {
        Intent intent = new Intent();
        intent.setClassName("com.onyx.android.edu", "com.onyx.android.edu.ui.chooseexercise.ChooseExerciseActivity");
        return intent;
    }

    private Intent getSettingIntent() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.onyx.android.settings",
                "com.onyx.android.libsetting.view.activity.DeviceMainSettingActivity"));
        return intent;
    }
}
