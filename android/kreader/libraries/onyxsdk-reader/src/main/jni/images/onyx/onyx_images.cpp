
#include <vector>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <list>
#include <map>



#include "com_onyx_android_sdk_reader_plugins_images_ImagesJniWrapper.h"

#include "log.h"
#include "JNIUtils.h"
#include "image_wrapper.h"


static ImageManager imageManager;

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_images_ImagesJniWrapper_nativeClearBitmap
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
 * Class:     com_onyx_android_sdk_reader_plugins_images_ImagesJniWrapper
 * Method:    nativePageSize
 * Signature: (Ljava/lang/String;[F)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_images_ImagesJniWrapper_nativePageSize
  (JNIEnv *env, jobject thiz, jstring jfilename, jfloatArray array) {

	JNIString stringWrapper(env, jfilename);
	const char * filename = stringWrapper.getLocalString();
	if (filename == NULL) {
		LOGE("Invalid file name");
		return false;
	}

	ImageWrapper * imageWrapper = imageManager.getImage(filename);
	if (imageWrapper == 0) {
	    LOGE("Cloud not create corresponding image instance for %s", filename);
	    return false;
	}
    jfloat size[] = {(float)imageWrapper->getWidth(), (float)imageWrapper->getHeight()};
    env->SetFloatArrayRegion(array, 0, 2, size);
	return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_images_ImagesJniWrapper_nativeDrawImage
  (JNIEnv *env, jobject thiz, jstring jfilename, jint x, jint y, jint width, jint height, jint rotation, jobject bitmap) {

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

    JNIString stringWrapper(env, jfilename);
    ImageWrapper * imageWrapper = imageManager.getImage(stringWrapper.getLocalString());
    if (imageWrapper != 0) {
	    imageWrapper->draw(pixels, x, y, width, height, info.width, info.height, info.stride);
	}
    AndroidBitmap_unlockPixels(env, bitmap);
	return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_images_ImagesJniWrapper_nativeCloseImage
  (JNIEnv *env, jobject thiz, jstring jfilename) {
  	JNIString stringWrapper(env, jfilename);
  	imageManager.releaseImage(stringWrapper.getLocalString());
	return true;
}

/*
 * Class:     com_onyx_android_sdk_reader_plugins_images_ImagesJniWrapper
 * Method:    nativeCloseAll
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_onyx_android_sdk_reader_plugins_images_ImagesJniWrapper_nativeCloseAll
  (JNIEnv *env, jobject thiz) {
	imageManager.clear();
}
