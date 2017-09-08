package com.onyx.libedu;

import com.onyx.libedu.db.ExaminationPaper;

import java.util.Comparator;

/**
 * Created by li on 2017/9/7.
 */

public class TimeComparator implements Comparator<ExaminationPaper> {
    @Override
    public int compare(ExaminationPaper lhs, ExaminationPaper rhs) {
        return (int) (lhs.modifyTime - rhs.modifyTime);
    }
}
