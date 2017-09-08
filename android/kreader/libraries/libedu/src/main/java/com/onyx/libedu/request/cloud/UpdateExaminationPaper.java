package com.onyx.libedu.request.cloud;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.libedu.BaseEduRequest;
import com.onyx.libedu.EduCloudManager;
import com.onyx.libedu.TimeComparator;
import com.onyx.libedu.db.ExaminationPaper;
import com.onyx.libedu.db.PaperQuestionAndAnswer;

import java.util.Collections;
import java.util.List;

/**
 * Created by li on 2017/8/12.
 */

public class UpdateExaminationPaper extends BaseEduRequest {
    private List<PaperQuestionAndAnswer> answerPaperList;
    private Float score;
    private int correctCount;
    private int errorCount;
    private String bookId;
    private int limits;

    public UpdateExaminationPaper(Float score, int correctCount, int errorCount, String bookId,
                                  List<PaperQuestionAndAnswer> answerPaperList) {
        this.score = score;
        this.correctCount = correctCount;
        this.errorCount = errorCount;
        this.bookId = bookId;
        this.answerPaperList = answerPaperList;
    }

    @Override
    public void execute(EduCloudManager parent) throws Exception {
        List<ExaminationPaper> list = ExaminationPaper.getExaminationPaperByBookId(getContext(), bookId);
        if (list == null || list.size() == 0) {
            return;
        }

        ExaminationPaper exPaper = list.get(0);
        limits = StringUtils.isNullOrEmpty(exPaper.limits) ? 5 : Integer.parseInt(exPaper.limits);
        if (list.size() <= limits) {
            insertPaper(list, list.size());
        } else {
            Collections.sort(list, new TimeComparator());
            ExaminationPaper paper = list.get(1);
            ExaminationPaper.deletePaper(getContext(), paper);
            PaperQuestionAndAnswer.deleteAnswerPaperByTimes(getContext(), paper.paperId, paper.times);

            for (int i = 0; i <= limits; i++) {
                if (i < 2) {
                    continue;
                }
                ExaminationPaper eachPaper = list.get(i);
                List<PaperQuestionAndAnswer> answerPaperByTimes = PaperQuestionAndAnswer.getAnswerPaperByTimes(getContext(), eachPaper.paperId, eachPaper.times);
                PaperQuestionAndAnswer.deleteAnswerPaperByTimes(getContext(), eachPaper.paperId, eachPaper.times);

                for (PaperQuestionAndAnswer paperByTimes : answerPaperByTimes) {
                    paperByTimes.times = String.valueOf(Integer.parseInt(paperByTimes.times) - 1);
                    PaperQuestionAndAnswer.insertPaperQuestionAndAnswer(getContext(), paperByTimes);
                }
                eachPaper.times = String.valueOf(Integer.parseInt(eachPaper.times) - 1);
                ExaminationPaper.updateExaminationPaper(getContext(), eachPaper);
            }
            insertPaper(list, list.size() - 1);
        }
    }

    private void insertPaper(List<ExaminationPaper> list, int size) {
        ExaminationPaper paper = list.get(0);
        paper.score = String.valueOf(score);
        paper.correctCount = String.valueOf(correctCount);
        paper.errorCount = String.valueOf(errorCount);
        paper.times = String.valueOf(size);
        paper.modifyTime = System.currentTimeMillis();
        ExaminationPaper.insertExaminationPaper(getContext(), paper);

        for (PaperQuestionAndAnswer answerPaper : answerPaperList) {
            answerPaper.times = String.valueOf(size);
            PaperQuestionAndAnswer.insertPaperQuestionAndAnswer(getContext(), answerPaper);
        }
    }
}
