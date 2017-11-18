package com.onyx.android.dr.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by hehai on 17-7-28.
 */

public class SignUpInfo {

    public String name;
    public String email;
    public String password;
    public String groupId;
    public String role;
    public InfoBean info;

    public static class InfoBean {
        public String phone;
        public String name;
        public String organization;
        public AddressBean address;
        public String school;
        public String grade;
        @JSONField(name = "class")
        public String classX;
        public String subject;
        public List<String> interest;
        public List<TextBooksBean> textBooks;

        public static class AddressBean {
            public String province;
            public String city;
            public String district;
        }

        public static class TextBooksBean {
            public String course;
            public String textBook;
        }
    }
}
