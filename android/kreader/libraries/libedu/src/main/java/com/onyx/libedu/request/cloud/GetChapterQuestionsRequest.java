package com.onyx.libedu.request.cloud;

import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.libedu.BaseEduRequest;
import com.onyx.libedu.EduCloudManager;
import com.onyx.libedu.EduServiceFactory;
import com.onyx.libedu.model.ChooseQuestionVariable;
import com.onyx.libedu.model.Question;
import com.onyx.libedu.model.Result;
import com.onyx.libedu.model.Version;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

import static com.onyx.libedu.Constant.INVALID_VALUE;

/**
 * Created by ming on 2016/11/1.
 */

public class GetChapterQuestionsRequest extends BaseEduRequest{

    private ChooseQuestionVariable variable;
    private long startPage = 0;
    private long countPerPage = 10;

    private long bookNodeId1 = INVALID_VALUE;
    private long bookNodeId2 = INVALID_VALUE;
    private long bookNodeId3 = INVALID_VALUE;

    private List<Question> questions;

    public GetChapterQuestionsRequest(ChooseQuestionVariable variable) {
        this.variable = variable;
    }

    @Override
    public void execute(EduCloudManager parent) throws Exception {
        Map<String, Object> paramMap = parent.createParamMap();
        paramMap.put("subjectId", variable.getSubject().getId());
        paramMap.put("versionId", variable.getVersion().getId());
        paramMap.put("textbookId", variable.getTextbook().getId());
        paramMap.put("questionTypeId", variable.getQuestionType().getId());
        paramMap.put("difficult", variable.getDifficult().getId());
        paramMap.put("startPage", startPage);
        paramMap.put("countPerPage", countPerPage);
        if (bookNodeId1 != INVALID_VALUE) {
            paramMap.put("bookNodeId1", bookNodeId1);
        }
        if (bookNodeId2 != INVALID_VALUE) {
            paramMap.put("bookNodeId2", bookNodeId2);
        }
        if (bookNodeId3 != INVALID_VALUE) {
            paramMap.put("bookNodeId3", bookNodeId3);
        }
        parent.sign(paramMap);

//        Call<Result<List<Question>>> call = EduServiceFactory.getEduService(parent.getEduConf().getApiBase()).getSyncChapterQuestionList(paramMap);
//        Response<Result<List<Question>>> response = call.execute();
//        if (response.isSuccessful()) {
//            Result<List<Question>> result = response.body();
////            questions = result.getData();
//        } else {
//            String errorCode = JSONObjectParseUtils.httpStatus(response.code(), new JSONObject(response.errorBody().string()));
////            throw new Exception(errorCode);
//        }
        questions = createTestData();
    }

    private List<Question> createTestData() {
        Question q1 = new Question();
        q1.setId(3);
        q1.setDifficult(0);
        q1.setStem("请回答面向对象的三大基本特性");
        q1.setAnswerCount(3);

        ArrayList<Question> list = new ArrayList<>();
        list.add(q1);
        return list;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setStartPage(long startPage) {
        this.startPage = startPage;
    }

    public void setCountPerPage(long countPerPage) {
        this.countPerPage = countPerPage;
    }

    public void setBookNodeId1(long bookNodeId1) {
        this.bookNodeId1 = bookNodeId1;
    }

    public void setBookNodeId2(long bookNodeId2) {
        this.bookNodeId2 = bookNodeId2;
    }

    public void setBookNodeId3(long bookNodeId3) {
        this.bookNodeId3 = bookNodeId3;
    }
}
