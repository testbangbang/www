package com.onyx.jdread.personal.model;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;

/**
 * Created by li on 2018/1/3.
 */

public class PersonalTaskModel {
    private String[] tasks = new String[]{JDReadApplication.getInstance().getResources().getString(R.string.sign_in_vouchers),
    JDReadApplication.getInstance().getResources().getString(R.string.read_thirty) + "(" + JDReadApplication.getInstance()
    .getResources().getString(R.string.add_ten_vouchers) + ")"};

    public String[] getTasks() {
        return tasks;
    }
}
