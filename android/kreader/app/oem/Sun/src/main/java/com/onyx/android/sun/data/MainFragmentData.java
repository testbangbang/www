package com.onyx.android.sun.data;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hehai on 17-10-10.
 */

public class MainFragmentData {
    public Map<String,Float> getSubjectScoreMap() {
        Map<String, Float> scores = new TreeMap<>();
        scores.put("语文", 80F);
        scores.put("数学", 90F);
        scores.put("英语", 80F);
        scores.put("物理", 100F);
        scores.put("化学", 80F);
        scores.put("生物", 60F);
        scores.put("政治", 50F);
        scores.put("历史", 40F);
        scores.put("地理", 70F);
        return scores;
    }
}
