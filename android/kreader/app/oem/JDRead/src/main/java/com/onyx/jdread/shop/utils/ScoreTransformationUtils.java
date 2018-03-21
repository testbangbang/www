package com.onyx.jdread.shop.utils;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;

/**
 * Created by lmb on 2018/3/21.
 */

public class ScoreTransformationUtils {
    private static String NOSCORE = ResManager.getString(R.string.no_score);
    private static String totalscore;

    public static String changeText(float score) {
        if (score == Constants.SCORE) {
            totalscore = NOSCORE;
        } else {
            totalscore = String.valueOf(score);
        }
        return totalscore;
    }
}
