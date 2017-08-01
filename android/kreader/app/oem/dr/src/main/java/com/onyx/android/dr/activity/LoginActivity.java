package com.onyx.android.dr.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.InterestAdapter;
import com.onyx.android.dr.bean.CityBean;
import com.onyx.android.dr.bean.InterestBean;
import com.onyx.android.dr.bean.ProvinceBean;
import com.onyx.android.dr.bean.SignUpInfo;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.AccountAvailableEvent;
import com.onyx.android.dr.presenter.LoginPresenter;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.util.RegularUtil;
import com.onyx.android.dr.view.CustomPopupWindow;
import com.onyx.android.sdk.data.model.v2.GroupBean;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements LoginView {
    private static final int STEP_SECOND = 2;
    private static final int STEP_FIRST = 1;
    @Bind(R.id.editText_account)
    EditText editTextAccount;
    @Bind(R.id.editText_password)
    EditText editTextPassword;
    @Bind(R.id.button_register)
    Button buttonRegister;
    @Bind(R.id.button_login)
    Button buttonLogin;
    @Bind(R.id.login_title)
    TextView loginTitle;
    @Bind(R.id.identity_username)
    EditText signUpUsername;
    @Bind(R.id.identity_email)
    EditText identity_email;
    @Bind(R.id.schoolchildren_school_name)
    EditText schoolchildrenSchoolName;
    @Bind(R.id.schoolchildren_grade)
    Spinner schoolchildrenGrade;
    @Bind(R.id.english_publisher)
    Spinner englishPublisher;
    @Bind(R.id.chinese_publisher)
    Spinner chinesePublisher;
    @Bind(R.id.third_language_publisher)
    EditText thirdLanguagePublisher;
    @Bind(R.id.teach_subject)
    EditText teachSubject;
    @Bind(R.id.teacher_interested_subject)
    PageRecyclerView teacherInterestedSubject;
    @Bind(R.id.college_student_grade)
    EditText collegeStudentGrade;
    @Bind(R.id.college_student_discipline)
    EditText collegeStudentDiscipline;
    @Bind(R.id.college_student_interested_subject)
    PageRecyclerView collegeStudentInterestedSubject;
    @Bind(R.id.login_next_button)
    Button loginNextButton;
    @Bind(R.id.user_identity)
    Spinner userIdentity;
    @Bind(R.id.other_identity_interested_subject)
    PageRecyclerView otherIdentityInterestedSubject;
    @Bind(R.id.sign_up_password)
    EditText signUpPassword;
    @Bind(R.id.identity_phone)
    EditText identityPhone;
    @Bind(R.id.spinner_province)
    EditText spinnerProvince;
    @Bind(R.id.spinner_city)
    EditText spinnerCity;
    @Bind(R.id.spinner_county)
    EditText spinnerCounty;
    @Bind(R.id.schoolchildren_class)
    EditText schoolchildrenClass;

    private LoginPresenter loginPresenter;
    private View identity_layout;
    private View login_layout;
    private View schoolchildren_layout;
    private View teacher_info_layout;
    private View college_students_info_layout;
    private View other_identity_info_layout;
    private Map<String, View> identityMap = new HashMap<>();
    private int step = 0;
    private View readingInfo;
    private String identity;
    private TextView login_title;
    private List<GroupBean> groups = new ArrayList<>();
    private boolean stepNotComplete;
    private InterestAdapter teacherInterestAdapter;
    private InterestAdapter collegeInterestAdapter;
    private InterestAdapter otherInterestAdapter;
    private List<String> provinceNames;
    private List<ProvinceBean> provinces;
    private List<String> cityNames;
    private List<CityBean> citys;
    private List<String> zoneNames;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        login_title = (TextView) findViewById(R.id.login_title);
        login_layout = findViewById(R.id.login_layout);
        identity_layout = findViewById(R.id.identity_layout);
        schoolchildren_layout = findViewById(R.id.schoolchildren_layout);
        teacher_info_layout = findViewById(R.id.teacher_info_layout);
        college_students_info_layout = findViewById(R.id.college_students_info_layout);
        other_identity_info_layout = findViewById(R.id.other_identity_info_layout);
        identityMap.put(getString(R.string.Schoolchildren), schoolchildren_layout);
        identityMap.put(getString(R.string.teacher), teacher_info_layout);
        identityMap.put(getString(R.string.College_Students), college_students_info_layout);
        identityMap.put(getString(R.string.other), other_identity_info_layout);
        readingInfo = other_identity_info_layout;

        collegeInterestAdapter = new InterestAdapter();
        collegeStudentInterestedSubject.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        collegeStudentInterestedSubject.setAdapter(collegeInterestAdapter);

        teacherInterestAdapter = new InterestAdapter();
        teacherInterestedSubject.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        teacherInterestedSubject.setAdapter(teacherInterestAdapter);

        otherInterestAdapter = new InterestAdapter();
        otherIdentityInterestedSubject.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        otherIdentityInterestedSubject.setAdapter(otherInterestAdapter);

        spinnerProvince.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showProvinceList();
                }
            }
        });
        spinnerCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showCityList();
                }
            }
        });
        spinnerCounty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showZone();
                }
            }
        });
    }

    @Override
    protected void initData() {
        loginPresenter = new LoginPresenter(this);
        loginPresenter.getRootGroups();
        loginPresenter.getInterestList();
        loginPresenter.queryProvince();
    }

    @Override
    public void setAccountInfo(NeoAccountBase accountInfo) {
        if (accountInfo != null) {
            CommonNotices.showMessage(this, getString(R.string.login_succeed));
            DRPreferenceManager.saveLibraryParentId(this, accountInfo.library);
            EventBus.getDefault().post(new AccountAvailableEvent());
            finish();
        } else {
            CommonNotices.showMessage(this, getString(R.string.username_or_password_error));
        }
    }

    @Override
    public void setGroups(List<GroupBean> groups) {
        this.groups.clear();
        if (groups != null) {
            this.groups.addAll(groups);
        }
    }

    @Override
    public void setInterestList(List<InterestBean> interestList) {
        if (interestList != null && interestList.size() > 0) {
            collegeInterestAdapter.setList(interestList);
            teacherInterestAdapter.setList(interestList);
            otherInterestAdapter.setList(interestList);
        }
    }

    @Override
    public void setSignUpResult(boolean result) {
        if (result) {
            login_title.setVisibility(View.GONE);
            step = 0;
            login_layout.setVisibility(View.VISIBLE);
            college_students_info_layout.setVisibility(View.GONE);
            other_identity_info_layout.setVisibility(View.GONE);
            schoolchildren_layout.setVisibility(View.GONE);
            loginNextButton.setVisibility(View.GONE);
            CommonNotices.showMessage(this, getString(R.string.sign_up_succeed));
        }
    }

    @Override
    public void setProvince(List<String> provinceNames, List<ProvinceBean> provinces) {
        this.provinceNames = provinceNames;
        this.provinces = provinces;
        spinnerProvince.setText(provinceNames.get(0));
        loginPresenter.queryCity(provinces.get(0).proSort);
    }

    @Override
    public void setCitys(List<String> cityNames, List<CityBean> citys) {
        this.cityNames = cityNames;
        this.citys = citys;
        if (cityNames != null) {
            spinnerCity.setText(cityNames.get(0));
            loginPresenter.queryZone(citys.get(0).citySort);
        }
    }

    @Override
    public void setZone(List<String> zoneNames) {
        this.zoneNames = zoneNames;
        if (zoneNames != null) {
            spinnerCounty.setText(zoneNames.get(0));
        }
    }

    private void collectUserInfo() {
        if (!NetworkUtil.isWiFiConnected(this)) {
            connectNetwork();
            return;
        }
        login_title.setVisibility(View.VISIBLE);
        step = STEP_FIRST;
        login_layout.setVisibility(View.GONE);
        identity_layout.setVisibility(View.VISIBLE);
        loginNextButton.setVisibility(View.VISIBLE);
        readingInfo.setVisibility(View.GONE);
    }

    @OnClick({R.id.button_register, R.id.button_login, R.id.login_next_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register:
                collectUserInfo();
                break;
            case R.id.button_login:
                login();
                break;
            case R.id.login_next_button:
                nextStep();
                break;
        }
    }

    private void showZone() {
        CustomPopupWindow customPopupWindow = new CustomPopupWindow(this);
        customPopupWindow.setOnItemClickListener(new CustomPopupWindow.OnItemClickListener() {
            @Override
            public void onClick(int position, String string) {
                spinnerCounty.setText(string);
            }
        });
        if (zoneNames != null) {
            customPopupWindow.showPopupWindow(spinnerCounty, zoneNames);
        }
    }

    private void showProvinceList() {
        CustomPopupWindow customPopupWindow = new CustomPopupWindow(this);
        customPopupWindow.setOnItemClickListener(new CustomPopupWindow.OnItemClickListener() {
            @Override
            public void onClick(int position, String string) {
                spinnerProvince.setText(string);
                loginPresenter.queryCity(provinces.get(position).proSort);
            }
        });
        if (provinceNames != null) {
            customPopupWindow.showPopupWindow(spinnerProvince, provinceNames);
        }
    }

    private void showCityList() {
        CustomPopupWindow customPopupWindow = new CustomPopupWindow(this);
        customPopupWindow.setOnItemClickListener(new CustomPopupWindow.OnItemClickListener() {
            @Override
            public void onClick(int position, String string) {
                spinnerCity.setText(string);
                loginPresenter.queryZone(citys.get(position).citySort);
            }
        });
        if (cityNames != null) {
            customPopupWindow.showPopupWindow(spinnerCity, cityNames);
        }
    }

    private void nextStep() {
        if (identity_layout.getVisibility() == View.VISIBLE) {
            String prompt = checkIdentityComplete();
            if (StringUtils.isNotBlank(prompt)) {
                CommonNotices.showMessage(this, prompt);
                return;
            }
            identity = (String) userIdentity.getSelectedItem();
            readingInfo = identityMap.get(identity);
            if (readingInfo != null) {
                readingInfo.setVisibility(View.VISIBLE);
                identity_layout.setVisibility(View.GONE);
                step = STEP_SECOND;
            }
        } else {
            SignUpInfo signUpInfo = new SignUpInfo();
            getUserInfo(signUpInfo);
            if (!stepNotComplete) {
                loginPresenter.signUp(signUpInfo);
            }
            stepNotComplete = false;
        }
    }

    private String checkIdentityComplete() {
        if (StringUtils.isNullOrEmpty(signUpUsername.getText().toString())) {
            signUpUsername.setText(Constants.EMPTY_STRING);
            signUpUsername.requestFocus();
            return getString(R.string.account_can_not_be_empty);
        }
        if (StringUtils.isNullOrEmpty(signUpPassword.getText().toString())) {
            signUpPassword.setText(Constants.EMPTY_STRING);
            signUpPassword.requestFocus();
            return getString(R.string.password_can_not_be_empty);
        }
        String email = identity_email.getText().toString();
        if (StringUtils.isNullOrEmpty(email) || !RegularUtil.isEmail(email)) {
            identity_email.setText(Constants.EMPTY_STRING);
            identity_email.requestFocus();
            return getString(R.string.please_enter_your_effective_email);
        }
        String phone = identityPhone.getText().toString();
        if (StringUtils.isNullOrEmpty(phone) || !RegularUtil.isMobile(phone)) {
            identityPhone.setText(Constants.EMPTY_STRING);
            identityPhone.requestFocus();
            return getString(R.string.please_fill_in_the_correct_phone_number);
        }
        return null;
    }

    private void getUserInfo(SignUpInfo signUpInfo) {
        signUpInfo.email = identity_email.getText().toString();
        signUpInfo.name = signUpUsername.getText().toString();
        signUpInfo.password = signUpPassword.getText().toString();
        signUpInfo.info = new SignUpInfo.InfoBean();
        signUpInfo.info.phone = identityPhone.getText().toString();
        signUpInfo.info.address = new SignUpInfo.InfoBean.AddressBean();
        signUpInfo.info.address.province = spinnerProvince.getText().toString();
        signUpInfo.info.address.city = spinnerCity.getText().toString();
        signUpInfo.info.address.district = spinnerCounty.getText().toString();
        String identity = userIdentity.getSelectedItem().toString();
        signUpInfo.info.name = getGroupID(identity);
        if (getString(R.string.Schoolchildren).equals(identity)) {
            schoolchildrenInfo(signUpInfo);
        } else if (getString(R.string.teacher).equals(identity)) {
            teacherInfo(signUpInfo);
        } else if (getString(R.string.College_Students).equals(identity)) {
            collegeStudentInfo(signUpInfo);
        } else {
            otherInfo(signUpInfo);
        }
    }

    private void otherInfo(SignUpInfo signUpInfo) {
        signUpInfo.info.interest = otherInterestAdapter.getSelectedInterest();
    }

    private void collegeStudentInfo(SignUpInfo signUpInfo) {
        signUpInfo.info.subject = collegeStudentDiscipline.getText().toString();
        signUpInfo.info.grade = collegeStudentGrade.getText().toString();
        signUpInfo.info.interest = collegeInterestAdapter.getSelectedInterest();
    }

    private void teacherInfo(SignUpInfo signUpInfo) {
        signUpInfo.info.subject = teachSubject.getText().toString();
        signUpInfo.info.interest = teacherInterestAdapter.getSelectedInterest();
    }

    private void schoolchildrenInfo(SignUpInfo signUpInfo) {
        String prompt = checkSchoolchildrenInfo();
        if (StringUtils.isNotBlank(prompt)) {
            CommonNotices.showMessage(this, prompt);
            stepNotComplete = true;
            return;
        }
        signUpInfo.info.school = schoolchildrenSchoolName.getText().toString();
        signUpInfo.info.grade = schoolchildrenGrade.getSelectedItem().toString();
        signUpInfo.info.classX = schoolchildrenClass.getText().toString();
        signUpInfo.info.subject = collegeStudentDiscipline.getText().toString();
        signUpInfo.info.textBooks = new ArrayList<>();
        SignUpInfo.InfoBean.TextBooksBean textBooksBean = new SignUpInfo.InfoBean.TextBooksBean();
        textBooksBean.course = getString(R.string.english);
        textBooksBean.textBook = englishPublisher.getSelectedItem().toString();
        signUpInfo.info.textBooks.add(textBooksBean);
        textBooksBean = new SignUpInfo.InfoBean.TextBooksBean();
        textBooksBean.course = getString(R.string.chinese);
        textBooksBean.textBook = chinesePublisher.getSelectedItem().toString();
        signUpInfo.info.textBooks.add(textBooksBean);
        textBooksBean = new SignUpInfo.InfoBean.TextBooksBean();
        textBooksBean.course = getString(R.string.third_language);
        textBooksBean.textBook = thirdLanguagePublisher.getText().toString();
        signUpInfo.info.textBooks.add(textBooksBean);
    }

    private String checkSchoolchildrenInfo() {
        if (StringUtils.isNullOrEmpty(schoolchildrenSchoolName.getText().toString())) {
            schoolchildrenSchoolName.requestFocus();
            return getString(R.string.school_is_required);
        }
        if (StringUtils.isNullOrEmpty(schoolchildrenClass.getText().toString())) {
            schoolchildrenClass.requestFocus();
            return getString(R.string.class_is_required);
        }
        return null;
    }

    private String getGroupID(String selectedItem) {
        String groupID = "";
        for (GroupBean group : groups) {
            if (group.name.equals(selectedItem)) {
                groupID = group._id;
            }
        }
        return groupID;
    }

    private void login() {
        if (!NetworkUtil.isWiFiConnected(this)) {
            connectNetwork();
            return;
        }
        String account = editTextAccount.getText().toString();
        String password = editTextPassword.getText().toString();
        if (StringUtils.isNullOrEmpty(account)) {
            CommonNotices.showMessage(this, getString(R.string.account_can_not_be_empty));
            editTextAccount.requestFocus();
            return;
        }
        if (StringUtils.isNullOrEmpty(password)) {
            CommonNotices.showMessage(this, getString(R.string.password_can_not_be_empty));
            editTextPassword.requestFocus();
            return;
        }
        loginPresenter.login(account, password);
    }

    private void connectNetwork() {
        Device.currentDevice().enableWifiDetect(this);
        NetworkUtil.enableWiFi(this, true);
        CommonNotices.showMessage(this,getString(R.string.network_not_connected));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (step == STEP_SECOND) {
                collectUserInfo();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
