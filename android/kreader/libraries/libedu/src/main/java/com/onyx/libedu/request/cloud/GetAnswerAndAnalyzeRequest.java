package com.onyx.libedu.request.cloud;import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;import com.onyx.libedu.BaseEduRequest;import com.onyx.libedu.EduCloudManager;import com.onyx.libedu.EduServiceFactory;import com.onyx.libedu.model.QuestionAnalytical;import com.onyx.libedu.model.Result;import com.onyx.libedu.model.Version;import org.json.JSONObject;import java.util.List;import java.util.Map;import retrofit2.Call;import retrofit2.Response;/** * Created by ming on 2016/11/1. */public class GetAnswerAndAnalyzeRequest extends BaseEduRequest{    private long subjectId;    private long questionId;    private QuestionAnalytical analytical;    public GetAnswerAndAnalyzeRequest(long subjectId, long questionId) {        this.subjectId = subjectId;        this.questionId = questionId;    }    @Override    public void execute(EduCloudManager parent) throws Exception {        Map<String, Object> paramMap = parent.createParamMap();        paramMap.put("subjectId", subjectId);        paramMap.put("questionId", questionId);        parent.sign(paramMap);//        Call<Result<QuestionAnalytical>> call = EduServiceFactory.getEduService(parent.getEduConf().getApiBase()).getAnswerAndAnalyze(paramMap);//        Response<Result<QuestionAnalytical>> response = call.execute();//        if (response.isSuccessful()) {//            Result<QuestionAnalytical> result = response.body();////            analytical = result.getData();//        } else {//            String errorCode = JSONObjectParseUtils.httpStatus(response.code(), new JSONObject(response.errorBody().string()));////            throw new Exception(errorCode);//        }        analytical = createTestData();    }    private QuestionAnalytical createTestData() {        QuestionAnalytical analytical = new QuestionAnalytical();        analytical.setAnswer("v1");        analytical.setQuestionAnalyze("analyse1");        return analytical;    }    public QuestionAnalytical getAnalytical() {        return analytical;    }}