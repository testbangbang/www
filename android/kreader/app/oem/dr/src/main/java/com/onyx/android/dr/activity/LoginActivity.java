package com.onyx.android.dr.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.presenter.LoginPresenter;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.HashMap;
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
    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.schoolchildren_school_name)
    EditText schoolchildrenSchoolName;
    @Bind(R.id.schoolchildren_grade)
    EditText schoolchildrenGrade;
    @Bind(R.id.english_publishers)
    EditText englishPublishers;
    @Bind(R.id.chinese_publishers)
    EditText chinesePublishers;
    @Bind(R.id.third_language_publishers)
    EditText thirdLanguagePublishers;
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

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
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
    }

    @Override
    protected void initData() {
        loginPresenter = new LoginPresenter(this);
    }

    @Override
    public void setAccountInfo(NeoAccountBase accountInfo) {
        if (accountInfo == null) {
            CommonNotices.showMessage(this, getString(R.string.username_or_password_error));
        } else {
            CommonNotices.showMessage(this, getString(R.string.login_succeed));
            // TODO: 17-6-30
        }
        collectUserInfo();
    }

    private void collectUserInfo() {
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
                break;
            case R.id.button_login:
                login();
                break;
            case R.id.login_next_button:
                nextStep();
                break;
        }
    }

    private void nextStep() {
        if (identity_layout.getVisibility() == View.VISIBLE) {
            identity = (String) userIdentity.getSelectedItem();
            readingInfo = identityMap.get(identity);
            if (readingInfo != null) {
                readingInfo.setVisibility(View.VISIBLE);
                identity_layout.setVisibility(View.GONE);
                step = STEP_SECOND;
            }
        } else {
            saveUserInfo();
            finish();
        }
    }

    private void saveUserInfo() {
        // TODO: 17-7-5  save identity info
    }

    private void login() {
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
