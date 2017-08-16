package com.onyx.libedu.request.cloud;

import android.util.Log;

import com.onyx.libedu.BaseEduRequest;
import com.onyx.libedu.EduCloudManager;
import com.onyx.libedu.db.ExaminationPaper;

/**
 * Created by li on 2017/8/12.
 */

public class UpdateExaminationPaper extends BaseEduRequest {
    private Float score;
    private int correctCount;
    private int errorCount;
    private String bookId;

    public UpdateExaminationPaper(Float score, int correctCount, int errorCount, String bookId) {
        this.score = score;
        this.correctCount = correctCount;
        this.errorCount = errorCount;
        this.bookId = bookId;
    }

    @Override
    public void execute(EduCloudManager parent) throws Exception {
        boolean b = ExaminationPaper.updateExaminationPaper(getContext(), score, correctCount, errorCount, bookId);
        Log.d("-------------22222222", "execute: " + b);
    }
}
