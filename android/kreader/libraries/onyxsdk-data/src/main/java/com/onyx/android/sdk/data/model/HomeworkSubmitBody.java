package com.onyx.android.sdk.data.model;

import java.util.List;

/**
 * Created by lxm on 2017/12/9.
 */

public class HomeworkSubmitBody {

    public List<HomeworkSubmitAnswer> anwsers;

    public HomeworkSubmitBody() {
    }

    public HomeworkSubmitBody(List<HomeworkSubmitAnswer> anwsers) {
        this.anwsers = anwsers;
    }
}
