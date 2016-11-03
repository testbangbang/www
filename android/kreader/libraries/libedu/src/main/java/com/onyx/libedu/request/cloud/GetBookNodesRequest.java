package com.onyx.libedu.request.cloud;import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;import com.onyx.libedu.BaseEduRequest;import com.onyx.libedu.EduCloudManager;import com.onyx.libedu.EduServiceFactory;import com.onyx.libedu.model.BookNode;import com.onyx.libedu.model.Result;import com.onyx.libedu.model.Version;import org.json.JSONObject;import java.util.List;import java.util.Map;import retrofit2.Call;import retrofit2.Response;/** * Created by ming on 2016/11/1. */public class GetBookNodesRequest extends BaseEduRequest{    private long textbookId;    private List<BookNode> bookNodes;    public GetBookNodesRequest(long textbookId) {        this.textbookId = textbookId;    }    @Override    public void execute(EduCloudManager parent) throws Exception {        Map<String, Object> paramMap = parent.createParamMap();        paramMap.put("textbookId", textbookId);        parent.sign(paramMap);        Call<Result<List<BookNode>>> call = EduServiceFactory.getEduService(parent.getEduConf().getApiBase()).getBookNodeList(paramMap);        Response<Result<List<BookNode>>> response = call.execute();        if (response.isSuccessful()) {            Result<List<BookNode>> result = response.body();            bookNodes = result.getData();        } else {            String errorCode = JSONObjectParseUtils.httpStatus(response.code(), new JSONObject(response.errorBody().string()));            throw new Exception(errorCode);        }    }    public List<BookNode> getBookNodes() {        return bookNodes;    }}