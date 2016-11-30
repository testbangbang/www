package com.onyx.libedu;

import com.onyx.libedu.model.BookNode;
import com.onyx.libedu.model.Document;
import com.onyx.libedu.model.KnowledgePoint;
import com.onyx.libedu.model.Question;
import com.onyx.libedu.model.QuestionAnalytical;
import com.onyx.libedu.model.QuestionType;
import com.onyx.libedu.model.Result;
import com.onyx.libedu.model.Subject;
import com.onyx.libedu.model.Textbook;
import com.onyx.libedu.model.Version;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by ming on 2016/10/31.
 */

public interface OnyxEduService {

    @GET("getSubjectList")
    Call<Result<List<Subject>>> getSubjectList(@QueryMap Map<String, Object> paramMap);

    @GET("getQuestionTypeList")
    Call<Result<List<QuestionType>>> getQuestionTypeList(@QueryMap Map<String, Object> paramMap);

    @GET("getVersionList")
    Call<Result<List<Version>>> getVersionList(@QueryMap Map<String, Object> paramMap);

    @GET("getTextbookList")
    Call<Result<List<Textbook>>> getTextbookList(@QueryMap Map<String, Object> paramMap);

    @GET("getBookNodeList")
    Call<Result<List<BookNode>>> getBookNodeList(@QueryMap Map<String, Object> paramMap);

    @GET("getDocumentList")
    Call<Result<List<Document>>> getDocumentList(@QueryMap Map<String, Object> paramMap);

    @GET("getKnowledgePointList")
    Call<Result<List<KnowledgePoint>>> getKnowledgePointList(@QueryMap Map<String, Object> paramMap);

    @GET("getSyncChapterQuestionList")
    Call<Result<List<Question>>> getSyncChapterQuestionList(@QueryMap Map<String, Object> paramMap);

    @GET("getKnowledgePointQuestionList")
    Call<Result<List<Question>>> getKnowledgePointQuestionList(@QueryMap Map<String, Object> paramMap);

    @GET("getAnswerAndAnalyze")
    Call<Result<QuestionAnalytical>> getAnswerAndAnalyze(@QueryMap Map<String, Object> paramMap);
}
