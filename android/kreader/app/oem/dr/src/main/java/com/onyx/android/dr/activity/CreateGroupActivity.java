package com.onyx.android.dr.activity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.interfaces.CreateGroupView;
import com.onyx.android.dr.presenter.CreateGroupPresenter;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.view.SecondCustomPopupWindow;
import com.onyx.android.sdk.data.model.CreateGroupBean;
import com.onyx.android.sdk.data.model.v2.CreateGroupResultBean;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/8/28.
 */
public class CreateGroupActivity extends BaseActivity implements CreateGroupView {
    @Bind(R.id.create_group_activity_recommend_school)
    EditText recommendGroupName;
    @Bind(R.id.create_group_activity_custom_group_name)
    EditText customGroupName;
    @Bind(R.id.create_group_activity_year)
    TextView yearName;
    @Bind(R.id.create_group_activity_year_container)
    RelativeLayout yearContainer;
    @Bind(R.id.create_group_activity_grade_container)
    RelativeLayout gradeContainer;
    @Bind(R.id.create_group_activity_grade)
    TextView gradeName;
    @Bind(R.id.create_group_activity_class_container)
    RelativeLayout classContainer;
    @Bind(R.id.create_group_activity_class)
    TextView className;
    @Bind(R.id.create_group_activity_create)
    TextView createGroup;
    @Bind(R.id.create_group_activity_cancel)
    TextView cancel;
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
    @Bind(R.id.create_group_activity_whether_exist)
    ImageView whetherExist;
    private CreateGroupPresenter createGroupPresenter;
    private List<String> gradeData;
    private List<String> annualData;
    private List<String> classData;
    private SecondCustomPopupWindow customPopupWindow;
    private final int YEAR_TAG = 0;
    private final int GRADE_TAG = 1;
    private final int CLASS_TAG = 2;
    public String classType = "";
    public String year = "";
    public String grade = "";
    private String parentId;

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
        annualData = createGroupPresenter.getAnnualData();
        classData = createGroupPresenter.getClassData();
        customPopupWindow = new SecondCustomPopupWindow(this);
        parentId = DRPreferenceManager.loadLibraryParentId(this, "");
        setData();
        initEvent();
    }

    private void setData() {
        image.setImageResource(R.drawable.ic_group);
        title.setText(getString(R.string.group_home_page_activity_create));
        classType = classData.get(0);
        year = annualData.get(0);
        grade = gradeData.get(0);
        yearName.setText(year);
        gradeName.setText(grade);
        className.setText(classType);
    }

    public void initEvent() {
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

    private void showCommonPopupWindow(final RelativeLayout view, List<String> list, final int tag) {
        customPopupWindow.setOnItemClickListener(new SecondCustomPopupWindow.OnItemClickListener() {
            @Override
            public void onClick(int position, String string) {
                if (tag == GRADE_TAG) {
                    grade = string;
                    gradeName.setText(string);
                } else if (tag == YEAR_TAG) {
                    year = string;
                    yearName.setText(string);
                } else if (tag == CLASS_TAG) {
                    classType = string;
                    className.setText(string);
                }
                if (customPopupWindow.isShowing()) {
                    customPopupWindow.dismiss();
                }
            }
        });
        if (list != null && list.size() > 0) {
            customPopupWindow.showPopupWindow(view, list, Constants.SCHOOL_CHILDREN);
        }
    }

    @OnClick({R.id.create_group_activity_create,
            R.id.image_view_back,
            R.id.create_group_activity_grade_container,
            R.id.create_group_activity_year_container,
            R.id.create_group_activity_class_container,
            R.id.create_group_activity_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.create_group_activity_create:
                createGroupRequest();
                break;
            case R.id.create_group_activity_cancel:
                finish();
                break;
            case R.id.create_group_activity_year_container:
                showCommonPopupWindow(yearContainer, annualData, YEAR_TAG);
                break;
            case R.id.create_group_activity_grade_container:
                showCommonPopupWindow(gradeContainer, gradeData, GRADE_TAG);
                break;
            case R.id.create_group_activity_class_container:
                showCommonPopupWindow(classContainer, classData, CLASS_TAG);
                break;
        }
    }

    private void createGroupRequest() {
        String customName = customGroupName.getText().toString();
        if (StringUtils.isNullOrEmpty(customName)) {
            CommonNotices.showMessage(this, getString(R.string.input_custom_group_name));
            return;
        }
        CreateGroupBean bean = new CreateGroupBean();
        bean.setName(customName);
        bean.setParent(parentId);
        createGroupPresenter.createGroup(bean);
    }

    @Override
    public void setCreateGroupResult(List<CreateGroupResultBean> list) {
        if (list != null && !list.isEmpty()) {
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
