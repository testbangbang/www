package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.dialog.TimePickerDialog;
import com.onyx.android.dr.event.HourEvent;
import com.onyx.android.dr.interfaces.AddMemorandumView;
import com.onyx.android.dr.presenter.AddMemorandumPresenter;
import com.onyx.android.dr.util.DictPreference;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.dr.view.DefaultEditText;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/7/21.
 */
public class AddMemorandumActivity extends BaseActivity implements AddMemorandumView, TimePickerDialog.TimePickerDialogInterface {
    @Bind(R.id.add_memorandum_activity_day_of_week)
    TextView dayOfWeek;
    @Bind(R.id.add_memorandum_activity_time)
    TextView time;
    @Bind(R.id.add_memorandum_activity_content)
    DefaultEditText contentEditText;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.title_bar_right_icon_three)
    ImageView iconThree;
    private AddMemorandumPresenter addMemorandumPresenter;
    private TimePickerDialog timePickerDialog;
    private String dateAndTimeHorizon;
    private String date;
    private long currentTime = 0;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_add_memorandum;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        DictPreference.init(this);
        loadMemorandumData();
        setTitleData();
        initEvent();
    }

    private void loadMemorandumData() {
        addMemorandumPresenter = new AddMemorandumPresenter(getApplicationContext(), this);
        timePickerDialog = new TimePickerDialog(this);
        String day = getIntent().getStringExtra(Constants.MEMORANDUM_DAY_OF_WEEK);
        date = getIntent().getStringExtra(Constants.MEMORANDUM_TIME);
        currentTime = getIntent().getLongExtra(Constants.MEMORANDUM_CURRENT_TIME, 0);
        String matter = getIntent().getStringExtra(Constants.MEMORANDUM_MATTER);
        dayOfWeek.setText(day);
        time.setText(date);
        contentEditText.setText(matter);
        Utils.movingCursor(contentEditText);
    }

    private void setTitleData() {
        iconThree.setVisibility(View.VISIBLE);
        iconThree.setImageResource(R.drawable.ic_reader_note_diary_save);
        iconFour.setVisibility(View.VISIBLE);
        iconFour.setImageResource(R.drawable.ic_reader_note_delet);
        image.setImageResource(R.drawable.memorandum);
        title.setText(getString(R.string.memorandum));
    }

    @Override
    public void setHourData(List<String> dataList) {
    }

    @Override
    public void setMinuteData(List<String> dataList) {
    }

    private void initEvent() {
    }

    @OnClick({R.id.menu_back,
            R.id.title_bar_right_icon_four,
            R.id.title_bar_right_icon_three})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.title_bar_right_icon_three:
                insertData();
                break;
            case R.id.title_bar_right_icon_four:
                deleteData();
                break;
        }
    }

    @Override
    public void positiveListener() {
        dateAndTimeHorizon = timePickerDialog.getDateAndTimeHorizon();
        EventBus.getDefault().post(new HourEvent());
    }

    private void insertData() {
        String content = contentEditText.getText().toString();
        if (StringUtils.isNullOrEmpty(content)) {
            CommonNotices.showMessage(this, getString(R.string.input_memorandum));
            return;
        }
        MemorandumEntity bean = new MemorandumEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.setMatter(content);
        bean.setDate(date);
        addMemorandumPresenter.insertMemorandum(bean);
        finish();
        ActivityManager.startMemorandumActivity(this);
    }

    private void deleteData() {
        String content = contentEditText.getText().toString();
        if (StringUtils.isNullOrEmpty(content) || currentTime == 0) {
            CommonNotices.showMessage(this, getString(R.string.no_memorandum_content));
            return;
        }
        addMemorandumPresenter.deleteMemorandum(currentTime);
        finish();
        ActivityManager.startMemorandumActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHourEvent(HourEvent event) {
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
