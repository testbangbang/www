package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.JsonResponse;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.utils.StringUtils;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by ming on 2017/6/12.
 */

public class GetDocumentDataFromCloudRequest extends BaseCloudRequest {

    private String url;
    private String cloudDocId;
    private String token;

    private String errorMessage;
    private String documentData;

    public GetDocumentDataFromCloudRequest(String url, String cloudDocId, String token) {
        this.url = url;
        this.cloudDocId = cloudDocId;
        this.token = token;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (StringUtils.isNullOrEmpty(url)) {
            errorMessage = "url is empty";
            return;
        }
        if (StringUtils.isNullOrEmpty(cloudDocId)) {
            errorMessage = "cloud document id is empty";
            return;
        }
        if (StringUtils.isNullOrEmpty(token)) {
            errorMessage = "token is empty";
            return;
        }
        updateTokenHeader(url, token);
        try {
            Response<JsonResponse> response = executeCall(ServiceFactory.getSyncService(url).getDocumentData(cloudDocId));
            if (response != null) {
                documentData = response.body().data;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            e.printStackTrace();
        }

    }

    public String getDocumentData() {
        return documentData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private void updateTokenHeader(final String url, String token) {
        if (StringUtils.isNotBlank(token)) {
            ServiceFactory.addRetrofitTokenHeader(url,
                    Constant.HEADER_AUTHORIZATION,
                    ContentService.CONTENT_AUTH_PREFIX + token);
        }
    }
}
