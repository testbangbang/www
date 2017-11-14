package com.onyx.android.sun.interfaces;

import com.onyx.android.sun.cloud.bean.ContentBean;

import java.util.List;

/**
 * Created by hehai on 17-9-29.
 */

public interface MainView {
    void setRemindContent(List<ContentBean> content);

    void setRemindView();
}
