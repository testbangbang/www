package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.dataprovider.ExaminationPaper;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

/**
 * Created by li on 2017/9/21.
 */

public class CheckExaminataionRequest extends BaseReaderRequest {
    private String id;
    private String result;

    public CheckExaminataionRequest(String id) {
        this.id = id;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        result = checkExamCount();
    }

    private String checkExamCount() {
        List<ExaminationPaper> list = ExaminationPaper.getExaminationPaperByBookId(getContext(), id);
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
