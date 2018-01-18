package com.onyx.jdread.personal.model;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;

/**
 * Created by li on 2018/1/3.
 */

public class PersonalTaskModel {
    private String[] tasks = new String[]{ResManager.getResString(R.string.sign_in_vouchers),
            ResManager.getResString(R.string.read_thirty) + "(" + ResManager.getResString(R.string.add_ten_vouchers) + ")"};

    public String[] getTasks() {
        return tasks;
    }
}
