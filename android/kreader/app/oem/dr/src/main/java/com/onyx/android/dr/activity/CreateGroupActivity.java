package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.CreateGroupBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.interfaces.CreateGroupView;
import com.onyx.android.dr.presenter.CreateGroupPresenter;
import com.onyx.android.dr.view.SecondCustomPopupWindow;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/8/28.
 */
public class CreateGroupActivity extends BaseActivity implements CreateGroupView {
    @Bind(R.id.create_group_activity_recommend_group_name)
    EditText recommendGroupName;
    @Bind(R.id.create_group_activity_custom_group_name)
    EditText customGroupName;
    @Bind(R.id.create_group_activity_year)
    TextView yearName;
    @Bind(R.id.create_group_activity_grade)
    TextView gradeName;
    @Bind(R.id.create_group_activity_class)
    TextView className;
    @Bind(R.id.create_group_activity_create)
    TextView createGroup;
    @Bind(R.id.create_group_activity_rollback)
    TextView rollback;
    @Bind(R.id.create_group_activity_recommend_check)
    CheckBox recommendCheck;
    @Bind(R.id.create_group_activity_custom_check)
    CheckBox customCheck;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    private CreateGroupPresenter createGroupPresenter;
    private List<String> gradeData;
    private SecondCustomPopupWindow customPopupWindow;
    private final int YEAR_TAG = 0;
    private final int GRADE_TAG = 1;
    private final int CLASS_TAG = 2;
    public String classType = "";
    public String year = "";
    public String grade = "";

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_create_group;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        createGroupPresenter = new CreateGroupPresenter(this);
        gradeData = createGroupPresenter.getGradeData();
        customPopupWindow = new SecondCustomPopupWindow(this);
        initTitleData();
        initEvent();
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.ic_group);
        title.setText(getString(R.string.group_home_page_activity_create));
    }

    public void initEvent() {
        yearName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommonPopupWindow(yearName, gradeData, YEAR_TAG);
            }
        });
        gradeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommonPopupWindow(gradeName, gradeData, GRADE_TAG);
            }
        });
        className.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommonPopupWindow(className, gradeData, CLASS_TAG);
            }
        });
        recommendCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    customCheck.setChecked(false);
                }
            }
        });
        customCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    recommendCheck.setChecked(false);
                }
            }
        });
    }

    private void showCommonPopupWindow(final TextView textView, List<String> list, final int tag) {
        customPopupWindow.setOnItemClickListener(new SecondCustomPopupWindow.OnItemClickListener() {

            @Override
            public void onClick(int position, String string) {
                if (tag == GRADE_TAG) {
                    grade = string;
                } else if (tag == YEAR_TAG) {
                    year = string;
                } else if (tag == CLASS_TAG) {
                    classType = string;
                }
                textView.setText(string);
                if (customPopupWindow.isShowing()) {
                    customPopupWindow.dismiss();
                }
            }
        });
        if (list != null && list.size() > 0) {
            customPopupWindow.showPopupWindow(textView, list, Constants.SCHOOL_CHILDREN);
        }
    }

    @OnClick({R.id.create_group_activity_create,
            R.id.image_view_back,
            R.id.create_group_activity_rollback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.create_group_activity_create:
                createGroupRequest();
                break;
            case R.id.create_group_activity_rollback:
                finish();
                break;
        }
    }

    private void createGroupRequest() {
        if (recommendCheck.isChecked()) {
            if (StringUtils.isNullOrEmpty(year)) {
                CommonNotices.showMessage(this, getString(R.string.select_vintage));
                return;
            }
            if (StringUtils.isNullOrEmpty(grade)) {
                CommonNotices.showMessage(this, getString(R.string.select_grade));
                return;
            }
            if (StringUtils.isNullOrEmpty(classType)) {
                CommonNotices.showMessage(this, getString(R.string.select_class));
                return;
            }
            String recommendName = recommendGroupName.getText().toString();
            if (StringUtils.isNullOrEmpty(recommendName)) {
                CommonNotices.showMessage(this, getString(R.string.input_recommend_group_name));
                return;
            }
            CreateGroupBean bean = new CreateGroupBean();
            bean.setGroupName(recommendName);
            bean.setMultiYear(year);
            bean.setGroupName(grade);
            bean.setClassName(classType);
            createGroupPresenter.createGroup(bean);
        } else if (customCheck.isChecked()) {
            String customName = customGroupName.getText().toString();
            if (StringUtils.isNullOrEmpty(customName)) {
                CommonNotices.showMessage(this, getString(R.string.input_custom_group_name));
                return;
            }
            CreateGroupBean bean = new CreateGroupBean();
            bean.setGroupName(customName);
            createGroupPresenter.createGroup(bean);
        } else {
            CommonNotices.showMessage(this, getString(R.string.select_group_name_type_hint));
        }
    }

    @Override
    public void setCreateGroupResult(boolean result) {
        if (result) {
            ActivityManager.startGroupHomePageActivity(this);
            CommonNotices.showMessage(this, getString(R.string.create_group_success));
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
