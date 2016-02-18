
#include <vector>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <list>
#include <map>
#include <unordered_map>


#include "com_onyx_kreader_plugins_images_ImagesJniWrapper.h"

#include "log.h"
#include "JNIUtils.h"
#include "png_wrapper.h"

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_images_ImagesJniWrapper_nativeClearBitmap
  (JNIEnv *env, jobject thiz, jobject bitmap) {

    AndroidBitmapInfo info;
	void *pixels;
	int ret;

	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return false;
	}

	if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
		LOGE("Bitmap format is not RGBA_8888 !");
		return false;
	}

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
		return false;
	}
    memset(pixels, 0xffffffff, info.stride * info.height);
    AndroidBitmap_unlockPixels(env, bitmap);
    return true;

}

/*
 * Class:     com_onyx_kreader_plugins_images_ImagesJniWrapper
 * Method:    nativePageSize
 * Signature: (Ljava/lang/String;[F)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_images_ImagesJniWrapper_nativePageSize
  (JNIEnv *env, jobject thiz, jstring jfilename, jfloatArray array) {

	const char *filename = NULL;
	filename = env->GetStringUTFChars(jfilename, NULL);
	if (filename == NULL) {
		LOGE("invalid file name");
		return false;
	}

	PNGWrapper wrapper(filename);
    jfloat size[] = {(float)wrapper.getWidth(), (float)wrapper.getHeight()};
    env->SetFloatArrayRegion(array, 0, 2, size);
	return true;
}