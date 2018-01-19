package com.onyx.jdread.personal.model;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;

/**
 * Created by li on 2018/1/3.
 */

public class PersonalTaskModel {
    private String[] tasks = new String[]{ResManager.getString(R.string.sign_in_vouchers),
            ResManager.getString(R.string.read_thirty) + "(" + ResManager.getString(R.string.add_ten_vouchers) + ")"};

    public String[] getTasks() {
        return tasks;
    }
}
