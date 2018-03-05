package com.onyx.jdread.personal.model;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/1/3.
 */

public class PersonalTaskModel {
    private List<PersonalTaskData> list = new ArrayList<>();

    public void loadData() {
        if (list.size() > 0) {
            list.clear();
        }
        PersonalTaskData bean = new PersonalTaskData();
        bean.setTaskName(ResManager.getString(R.string.sign_in_vouchers));
        bean.setTaskStatus(ResManager.getString(R.string.have_receive));
        list.add(bean);

        bean = new PersonalTaskData();
        bean.setTaskName(ResManager.getString(R.string.read_thirty));
        if (TimeUtils.getCurrentDataInString().equals(PersonalDataBundle.getInstance().getReceiveReadVoucherTime())) {
            bean.setTaskStatus(ResManager.getString(R.string.have_receive));
        } else if (JDReadApplication.getInstance().canSignForRead()) {
            bean.setTaskStatus(ResManager.getString(R.string.allow_get));
        } else {
            bean.setTaskStatus(ResManager.getString(R.string.cannot_get));
        }
        list.add(bean);
    }

    public List<PersonalTaskData> getTasks() {
        return list;
    }
}
