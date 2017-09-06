package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.InformalEssayBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.interfaces.InformalEssayView;
import com.onyx.android.dr.presenter.InformalEssayPresenter;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.dr.view.DefaultEditText;
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
    @Bind(R.id.title_bar_right_menu)
    TextView rightMenu;
    @Bind(R.id.add_infromal_essay_activity_content)
    DefaultEditText contentEditText;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    private InformalEssayPresenter infromalEssayPresenter;

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
        infromalEssayPresenter = new InformalEssayPresenter(getApplicationContext(), this);
        setTitleData();
    }

    private void setTitleData() {
        rightMenu.setVisibility(View.VISIBLE);
        rightMenu.setText(R.string.save_button);
        rightMenu.setTextSize(getResources().getDimension(R.dimen.level_two_font_size));
        image.setImageResource(R.drawable.informal_essay);
        title.setText(getString(R.string.informal_essay));
    }

    @Override
    public void setInformalEssayData(List<InformalEssayEntity> dataList, ArrayList<Boolean>listCheck) {
    }

    @Override
    public void setInformalEssayByTime(List<InformalEssayEntity> dataList) {
    }

    @Override
    public void setInformalEssayByTitle(List<InformalEssayEntity> dataList) {
    }

    @OnClick({R.id.add_infromal_essay_activity_file,
            R.id.image_view_back,
            R.id.title_bar_right_menu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.title_bar_right_menu:
                insertData();
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
        infromalEssayPresenter.insertInformalEssay(bean);
        finish();
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
