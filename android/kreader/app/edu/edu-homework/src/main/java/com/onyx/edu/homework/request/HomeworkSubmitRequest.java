package com.onyx.edu.homework.request;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.HomeworkSubmitAnswer;
import com.onyx.android.sdk.data.model.HomeworkSubmitBody;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.edu.homework.data.Constant;
import com.onyx.edu.homework.data.HomeworkState;
import com.onyx.edu.homework.db.DBDataProvider;
import com.onyx.edu.homework.db.HomeworkModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkSubmitRequest extends BaseCloudRequest {

    private String libraryId;
    private HomeworkSubmitBody body;
    private Response<ResponseBody> response;

    public HomeworkSubmitRequest(String libraryId, HomeworkSubmitBody body) {
        this.libraryId = libraryId;
        this.body = body;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        benchmarkStart();
        response = executeCall(ServiceFactory.getHomeworkService(parent.getCloudConf().getApiBase()).submitAnswers(libraryId, body, getFilePathMap()));
        Debug.d(getClass(), "submitAnswers:" + benchmarkEnd());
        if (isSuccess()) {
            HomeworkModel model = DBDataProvider.loadHomework(libraryId);
            if (model == null) {
                model = HomeworkModel.create(libraryId);
            }
            model.setState(HomeworkState.SUBMITTED.ordinal());
            DBDataProvider.saveHomework(model);
        }
        clearFileCache();
    }

    private void clearFileCache() {
        FileUtils.deleteFile(new File(Constant.getRenderPageDir()), true);
    }

    private Map<String, RequestBody> getFilePathMap() {
        List<HomeworkSubmitAnswer> anwsers = body.anwsers;
        if (CollectionUtils.isNullOrEmpty(anwsers)) {
            return null;
        }
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        for (HomeworkSubmitAnswer anwser : anwsers) {
            List<String> paths = anwser.filePaths;
            if (CollectionUtils.isNullOrEmpty(paths)) {
                continue;
            }
            int size = paths.size();
            List<String> attachment = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                String path = paths.get(i);
                String fileName = FileUtils.getRealFileName(path);
                String key = "files\"; filename=\""+ fileName;
                requestBodyMap.put(key,
                        RequestBody.create(MediaType.parse("image/png"), new File(path)));
                attachment.add(fileName);
            }
            anwser.setAttachment(attachment);
            paths.clear();

        }
        return requestBodyMap;
    }

    public boolean isSuccess() {
        return response != null && response.isSuccessful();
    }
}
