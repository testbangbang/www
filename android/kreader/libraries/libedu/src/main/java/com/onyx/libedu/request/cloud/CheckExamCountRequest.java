package com.onyx.libedu.request.cloud;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.libedu.BaseEduRequest;
import com.onyx.libedu.EduCloudManager;
import com.onyx.libedu.db.ExaminationPaper;

import java.util.List;

/**
 * Created by li on 2017/9/19.
 */

public class CheckExamCountRequest extends BaseEduRequest {
    private String bookId;
    private String result;

    public CheckExamCountRequest(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public void execute(EduCloudManager parent) throws Exception {
        result = checkExamCount();
    }

    private String checkExamCount() {
        List<ExaminationPaper> list = ExaminationPaper.getExaminationPaperByBookId(getContext(), bookId);
        if (list == null || list.size() == 0) {
            return "试题不存在，请上传试题！";
        }

        ExaminationPaper exPaper = list.get(0);
        int limits = StringUtils.isNullOrEmpty(exPaper.limits) ? 5 : Integer.parseInt(exPaper.limits);
        if(list.size() > limits) {
            return "已达最大考试次数";
        }

        return null;
    }

    public String getResult() {
        return result;
    }
}
