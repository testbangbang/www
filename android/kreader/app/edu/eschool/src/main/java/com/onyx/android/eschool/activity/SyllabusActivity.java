package com.onyx.android.eschool.activity;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CourseImageRequest;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;

import java.io.File;

import butterknife.Bind;

/**
 * Created by suicheng on 2017/6/5.
 */

public class SyllabusActivity extends BaseActivity {
    public static final String SYLLABUS_DIR = "syllabus";
    public static final String SYLLABUS_COURSE_IMAGE_NAME = "syllabus_course_image.png";

    @Bind(R.id.syllabus_iv)
    ImageView syllabusIView;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_syllabus;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        if (!AppCompatUtils.isColorDevice(this)) {
            syllabusIView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
    }

    @Override
    protected void initData() {
        loadSyllabusImage();
    }

    private String getSyllabusPath() {
        File syllabusDir = new File(EnvironmentUtil.getExternalStorageAppFilesDirectory(getPackageName()),
                SYLLABUS_DIR);
        if (!syllabusDir.exists()) {
            syllabusDir.mkdirs();
        }
        return new File(syllabusDir, SYLLABUS_COURSE_IMAGE_NAME).getAbsolutePath();
    }

    private void loadSyllabusImage() {
        String path = getSyllabusPath();
        loadSyllabusImageFromLocal(path, false);
        loadSyllabusImageFromCloud(path);
    }

    private void loadSyllabusImageFromCloud(final String path) {
        final CourseImageRequest courseImageRequest = new CourseImageRequest(path);
        SchoolApp.getSchoolCloudStore().submitRequest(getApplicationContext(), courseImageRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || !courseImageRequest.isSuccess()) {
                    return;
                }
                loadSyllabusImageFromLocal(path, true);
            }
        });
    }

    private void loadSyllabusImageFromLocal(final String filePath, final boolean fullUpdate) {
        if (syllabusIView == null) {
            return;
        }
        Glide.with(getApplicationContext())
                .load(new File(filePath))
                .error(R.drawable.syllabus)
                .dontAnimate()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new GlideDrawableImageViewTarget(syllabusIView) {
                    @Override
                    protected void setResource(GlideDrawable resource) {
                        super.setResource(resource);
                        if (fullUpdate && syllabusIView != null) {
                            EpdController.postInvalidate(syllabusIView, UpdateMode.GC);
                        }
                    }
                });
    }
}
