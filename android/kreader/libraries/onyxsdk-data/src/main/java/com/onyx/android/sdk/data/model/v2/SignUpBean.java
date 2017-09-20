package com.onyx.android.sdk.data.model.v2;

/**
 * Created by hehai on 17-7-27.
 */

public class SignUpBean {
    public String name;
    public String email;
    public String password;
    public String groupId;
    public String info;

    public static SignUpBean create(String username, String email, String password, String groupId, String info) {
        SignUpBean signUpBean = new SignUpBean();
        signUpBean.name = username;
        signUpBean.email = email;
        signUpBean.password = password;
        signUpBean.groupId = groupId;
        signUpBean.info = info;
        return signUpBean;
    }
}