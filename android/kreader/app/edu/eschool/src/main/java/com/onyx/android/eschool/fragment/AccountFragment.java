package com.onyx.android.eschool.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.model.StudentAccount;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/5/17.
 */

public class AccountFragment extends Fragment {

    @Bind(R.id.student_name)
    TextView studentName;
    @Bind(R.id.student_grade)
    TextView studentGrade;
    @Bind(R.id.student_phone_number)
    TextView studentPhone;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, view);
        initView((ViewGroup) view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(ViewGroup viewGroup) {
        updateView(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @SuppressLint("SetTextI18n")
    public void updateView(Context context) {
        StudentAccount studentAccount = StudentAccount.loadAccount(context);
        studentName.setText(studentAccount.getName());
        studentGrade.setText("年级：" + studentAccount.getFirstGroup());
        studentPhone.setText("电话：" + studentAccount.getPhone());
    }
}
