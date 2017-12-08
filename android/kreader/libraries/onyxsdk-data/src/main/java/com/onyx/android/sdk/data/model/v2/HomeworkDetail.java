package com.onyx.android.sdk.data.model.v2;

import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/12/7.
 */
public class HomeworkDetail {
    public String _id;
    public String title;
    public Subject subject;
    public NeoAccountBase creator;
    public CloudMetadata file;
    public Map<String, Integer> difficultyCount;
    public Map<String, Integer> quesTypeCount;
    public List<String> questions;

    public String getSubjectName() {
        if (subject == null) {
            return "";
        }
        return subject.name;
    }
}
