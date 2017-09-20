package com.onyx.android.dr.activity;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.onyx.android.dr.util.Utils;
import com.onyx.android.dr.view.SecondCustomPopupWindow;
import com.onyx.android.sdk.data.model.GroupNameExistBean;
import com.onyx.android.sdk.data.model.v2.CreateGroupCommonBean;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/8/28.
 */
public class CreateGroupActivity extends BaseActivity implements CreateGroupView {
    @Bind(R.id.create_group_activity_recommend_school)
    EditText recommendGroupName;
    @Bind(R.id.create_group_activity_recommend_school_container)
    RelativeLayout schoolContainer;
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
    @Bind(R.id.create_group_activity_group_name_hint)
    TextView hint;
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
    private List<CreateGroupCommonBean> annualData;
    private List<CreateGroupCommonBean> gradeData;
    private List<CreateGroupCommonBean> classData;
    private List<CreateGroupCommonBean> schoolData;
    private SecondCustomPopupWindow customPopupWindow;
    private final int YEAR_TAG = 0;
    private final int GRADE_TAG = 1;
    private final int CLASS_TAG = 2;
    private final int SCHOOL_TAG = 3;
    private final String EXIST_FALSE = "false";
    private final String EXIST_TRUE = "true";
    public String classType = "";
    public String year = "";
    public String grade = "";
    private String parentId = "";
    private String customName;

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
        annualData = new ArrayList<>();
        classData = new ArrayList<>();
        gradeData = new ArrayList<>();
        schoolData = new ArrayList<>();
        createGroupPresenter = new CreateGroupPresenter(this);
        customPopupWindow = new SecondCustomPopupWindow(this);
        parentId = DRPreferenceManager.loadParentId(this, "");
        setData();
        initEvent();
    }

    private void setData() {
        image.setImageResource(R.drawable.ic_group);
        title.setText(getString(R.string.group_home_page_activity_create));
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
        recommendGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence string, int start, int before, int count) {
                Utils.movingCursor(recommendGroupName);
                createGroupPresenter.getSchoolData(string.toString(), "");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showCommonPopupWindow(final RelativeLayout view, final List<CreateGroupCommonBean> list, final int tag) {
        customPopupWindow.setOnItemClickListener(new SecondCustomPopupWindow.OnItemClickListener() {
            @Override
            public void onClick(int position, CreateGroupCommonBean bean) {
                if (tag == GRADE_TAG) {
                    gradeName.setText(bean.name);
                    createGroupPresenter.getClassData(bean._id);
                } else if (tag == YEAR_TAG) {
                    yearName.setText(bean.name);
                    createGroupPresenter.getGradeData(bean._id);
                } else if (tag == CLASS_TAG) {
                    className.setText(bean.name);
                } else if (tag == SCHOOL_TAG) {
                    recommendGroupName.setText(bean.name);
                    Utils.movingCursor(recommendGroupName);
                    createGroupPresenter.getYearData(bean._id);
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
        customName = customGroupName.getText().toString();
        if (StringUtils.isNullOrEmpty(customName)) {
            CommonNotices.showMessage(this, getString(R.string.input_custom_group_name));
            return;
        }
        createGroupPresenter.checkGroupName(customName, parentId);
    }

    @Override
    public void setSchoolInfo(List<CreateGroupCommonBean> list) {
        if (list == null && list.size() <= 0) {
            return;
        }
        schoolData = list;
        if (customPopupWindow.isShowing()) {
            customPopupWindow.dismiss();
        }
        showCommonPopupWindow(schoolContainer, schoolData, SCHOOL_TAG);
    }

    @Override
    public void setYearInfo(CreateGroupCommonBean bean) {
        if (bean.children == null && bean.children.size() <= 0) {
            return;
        }
        annualData = bean.children;
        if (customPopupWindow.isShowing()) {
            customPopupWindow.dismiss();
        }
        showCommonPopupWindow(yearContainer, annualData, YEAR_TAG);
    }

    @Override
    public void setGradeInfo(CreateGroupCommonBean bean) {
        if (bean.children == null && bean.children.size() <= 0) {
            return;
        }
        gradeData = bean.children;
        if (customPopupWindow.isShowing()) {
            customPopupWindow.dismiss();
        }
        showCommonPopupWindow(gradeContainer, gradeData, GRADE_TAG);
    }

    @Override
    public void setClassInfo(CreateGroupCommonBean bean) {
        if (bean.children == null && bean.children.size() <= 0) {
            return;
        }
        classData = bean.children;
        if (customPopupWindow.isShowing()) {
            customPopupWindow.dismiss();
        }
        showCommonPopupWindow(classContainer, classData, CLASS_TAG);
    }

    @Override
    public void setCreateGroupResult(CreateGroupCommonBean bean) {
        if (bean != null) {
            ActivityManager.startGroupHomePageActivity(this);
            CommonNotices.showMessage(this, getString(R.string.create_group_success));
            finish();
        } else {
            CommonNotices.showMessage(this, getString(R.string.create_group_failed));
        }
    }

    @Override
    public void setCheckGroupNameResult(GroupNameExistBean bean) {
        if (bean.isExists.equals(EXIST_FALSE)) {
            hint.setVisibility(View.GONE);
            whetherExist.setImageResource(R.drawable.ic_reader_group_set_right);
            CreateGroupCommonBean groupBean = new CreateGroupCommonBean();
            groupBean.name = customName;
            groupBean.parent = parentId;
            createGroupPresenter.createGroup(groupBean);
        } else if (bean.isExists.equals(EXIST_TRUE)) {
            hint.setVisibility(View.VISIBLE);
            whetherExist.setImageResource(R.drawable.ic_reader_group_set_wrong);
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
