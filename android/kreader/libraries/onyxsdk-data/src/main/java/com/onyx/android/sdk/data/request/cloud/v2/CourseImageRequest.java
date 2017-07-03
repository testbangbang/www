package com.onyx.android.sdk.data.request.cloud.v2;

import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ViewTarget;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.Course;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/6/29.
 */

public class CourseImageRequest extends BaseCloudRequest {

    private String courseImagePath;
    private boolean success = false;

    public CourseImageRequest(String savePath) {
        this.courseImagePath = savePath;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        List<Course> courseList = loadCourseListUrl(parent);
        if (CollectionUtils.isNullOrEmpty(courseList)) {
            return;
        }
        Bitmap bitmap = loadBitmapFromCloud(courseList.get(0).cfaFileUrl);
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        success = ThumbnailUtils.writeBitmapToThumbnailFile(new File(courseImagePath), bitmap);
    }

    private List<Course> loadCourseListUrl(CloudManager parent) {
        List<Course> courseList = new ArrayList<>();
        try {
            Response<List<Course>> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                    .getMyCourses());
            if (response.isSuccessful()) {
                courseList = response.body();
            }
        } catch (Exception e) {
        }
        return courseList;
    }

    private Bitmap loadBitmapFromCloud(String url) throws Exception {
        return Glide.with(getContext())
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(ViewTarget.SIZE_ORIGINAL, ViewTarget.SIZE_ORIGINAL)
                .get();
    }
}
