package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.interfaces.InformalEssayView;
import com.onyx.android.dr.presenter.InformalEssayPresenter;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.dr.view.DefaultEditText;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.data.model.InformalEssayBean;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/7/21.
 */
public class AddInformalEssayActivity extends BaseActivity implements InformalEssayView {
    @Bind(R.id.add_infromal_essay_activity_title)
    EditText titleEditText;
    @Bind(R.id.add_infromal_essay_activity_file)
    TextView correctFile;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.add_infromal_essay_activity_content)
    DefaultEditText contentEditText;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    private InformalEssayPresenter informalEssayPresenter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_add_infromal_essay;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        informalEssayPresenter = new InformalEssayPresenter(getApplicationContext(), this);
        getIntentData();
        setTitleData();
    }

    private void getIntentData() {
        String informalEssayContent = getIntent().getStringExtra(Constants.INFORMAL_ESSAY_CONTENT);
        String informalEssayTitle = getIntent().getStringExtra(Constants.INFORMAL_ESSAY_TITLE);
        titleEditText.setText(informalEssayTitle);
        contentEditText.setText(informalEssayContent);
        Utils.movingCursor(titleEditText);
    }

    private void setTitleData() {
        iconFour.setVisibility(View.VISIBLE);
        iconFour.setImageResource(R.drawable.ic_reader_note_diary_save);
        image.setImageResource(R.drawable.informal_essay);
        title.setText(getString(R.string.informal_essay));
    }

    @Override
    public void setInformalEssayData(List<CreateInformalEssayBean> dataList, ArrayList<Boolean>listCheck) {
    }

    @Override
    public void setInformalEssayByTime(List<InformalEssayEntity> dataList) {
    }

    @Override
    public void setInformalEssayByTitle(List<InformalEssayEntity> dataList) {
    }

    @OnClick({R.id.add_infromal_essay_activity_file,
            R.id.menu_back,
            R.id.title_bar_right_icon_four})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.title_bar_right_icon_four:
                uploadData();
                break;
            case R.id.add_infromal_essay_activity_file:
                break;
        }
    }

    private void insertData() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        if (StringUtils.isNullOrEmpty(title)) {
            CommonNotices.showMessage(this, getString(R.string.input_title));
            return;
        }
        if (StringUtils.isNullOrEmpty(content)) {
            CommonNotices.showMessage(this, getString(R.string.input_infromal_essay_content));
            return;
        }
        InformalEssayBean bean = new InformalEssayBean();
        bean.setTitle(title);
        bean.setContent(content);
        bean.setWordNumber(Utils.getStringLength(content));
        informalEssayPresenter.insertInformalEssay(bean);
        finish();
    }

    private void uploadData() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        if (StringUtils.isNullOrEmpty(title)) {
            CommonNotices.showMessage(this, getString(R.string.input_title));
            return;
        }
        if (StringUtils.isNullOrEmpty(content)) {
            CommonNotices.showMessage(this, getString(R.string.input_infromal_essay_content));
            return;
        }
        InformalEssayBean bean = new InformalEssayBean();
        bean.setTitle(title);
        bean.setContent(content);
        bean.setWordNumber(Utils.getStringLength(content));
        bean.setCurrentTime(TimeUtils.getCurrentTimeMillis());
        informalEssayPresenter.createInformalEssay(bean);
    }

    @Override
    public void createInformalEssay(boolean tag) {
        if (tag) {
            finish();
        }else{
            CommonNotices.showMessage(this, getString(R.string.save_failed));
        }
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
