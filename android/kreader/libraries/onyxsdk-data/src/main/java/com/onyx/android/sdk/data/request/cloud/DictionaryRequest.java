package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Dictionary;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.StringUtils;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/11/16.
 */

public class DictionaryRequest extends BaseCloudRequest {

    private String guid;
    private Dictionary dictionary;

    public DictionaryRequest(String guid) {
        this.guid = guid;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (StringUtils.isNullOrEmpty(guid)) {
            return;
        }
        Response<Dictionary> response = executeCall(ServiceFactory.getDictionaryService(parent.getCloudConf().getApiBase())
                .dictionaryItem(guid));
        if (response.isSuccessful()) {
            dictionary = response.body();
        }
    }
}
