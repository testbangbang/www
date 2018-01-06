package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.BaseData;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by suicheng on 2017/12/7.
 */
public class HomeworkDetail extends BaseData implements Serializable {

    public String _id;
    public String title;
    public String info;
    public String type;
    public Subject subject;
    public NeoAccountBase creator;
    public Map<String, Integer> difficultyCount;
    public Map<String, Integer> quesTypeCount;

    public Date beginTime;
    public Date endTime;

    public boolean needReply;

    public int status;

    public String getSubjectName() {
        return subject == null ? "" : subject.name;
    }
}
