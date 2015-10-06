#include <jni.h>
#include <time.h>
#include <pthread.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <list>

#ifdef NDK_PROFILER
#include "prof.h"
#endif

#include "com_onyx_reader_adobe_AdobeReaderPlugin.h"

#include "onyx_client.h"
#include "onyx_adobe_backend.h"
#include "onyx_drm_callback.h"


/* Set to 1 to enable debug log traces. */
#define DEBUG 1


/* Enable to log rendering times (render each frame 100 times and time) */
static onyx::AdobeLibrary & library(JNIEnv *env) {
    static onyx::AdobeLibrary * instance = 0;
    if (instance == 0) {
        instance = new onyx::AdobeLibrary(env);
    }
    return *instance;
}

static const char * getUtf8String(JNIEnv * env, jstring string) {
    if (string != NULL) {
        return env->GetStringUTFChars(string, NULL);
    }
    return NULL;
}

JNIEXPORT jlong JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_openFile(JNIEnv *env, jobject thiz, jstring jfilename,
        jstring jpassword, jstring jZipPassword) {
	const char *filename;
	const char *password;
    const char *zipPassword;

#ifdef NDK_PROFILER
	monstartup("libonyx_pdf.so");
#endif

	filename = env->GetStringUTFChars(jfilename, NULL);
	if (filename == NULL) {
		LOGE("Failed to get filename");
		return -1;
	}

    password = env->GetStringUTFChars(jpassword, NULL);
    zipPassword = env->GetStringUTFChars(jZipPassword, NULL);
    return library(env).createDocument(filename, password, zipPassword);
}

JNIEXPORT void JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_closeFile
  (JNIEnv * env, jobject) {
    library(env).closeDocument();
}

JNIEXPORT jlong JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_drawPage(JNIEnv *env, jobject thiz, jint pageNumber,
    jobject bitmap, int displayLeft, int displayTop, int displayWidth, int displayHeight,  jboolean fill) {
    AndroidBitmapInfo info;
	void *pixels;
	int ret;

	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return -1;
	}

	if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
		LOGE("Bitmap format is not RGBA_8888 !");
		return -1;
	}

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
		return -1;
	}

    library(env).drawPage(pageNumber, info, pixels, displayLeft, displayTop, displayWidth, displayHeight);
    return 1;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_drawVisiblePages
(JNIEnv *env, jobject thiz, jobject bitmap,  int displayLeft, int displayTop, int displayWidth, int displayHeight, jboolean fill) {
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
    library(env).drawPages(info, pixels, displayLeft, displayTop, displayWidth, displayHeight);
    return 1;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_gotoLocationInternal
  (JNIEnv *env, jobject, jint page, jstring location) {
    const char * string = NULL;
    if (location != NULL) {
        string = env->GetStringUTFChars(location, NULL);
    }
    return library(env).gotoLocation(page, string);
}

JNIEXPORT void JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_pageSizeNative
  (JNIEnv *env, jobject, jint page, jfloatArray ret) {
    double width = 0.0, height = 0.0;
    library(env).pageNaturalSize(page, width, height);
    jfloat size[] = {(float)width, (float)height};
    env->SetFloatArrayRegion(ret, 0, 2, size);
}

JNIEXPORT jint JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_countPagesInternal
  (JNIEnv * env, jobject) {
    return library(env).getPageCount();
}

JNIEXPORT jobject JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_hitTestNative
  (JNIEnv * env, jobject, jfloat x, jfloat y, jint type, jobject splitter) {
    return library(env).hitTest(env, x, y, type, splitter);
}

JNIEXPORT void JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_abortCurrentTask
  (JNIEnv *, jobject) {

}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_prevScreenNative
  (JNIEnv * env, jobject) {
    return library(env).prevScreen();
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_nextScreenNative
  (JNIEnv * env, jobject) {
    return library(env).nextScreen();
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_setFontSizeNative
  (JNIEnv *env, jobject, jdouble width, jdouble height, jdouble size) {
    return library(env).setFontSize(width, height, size);
}

JNIEXPORT jstring JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_getTextNative
  (JNIEnv *env, jobject, jstring start, jstring end) {
  	const char * s = env->GetStringUTFChars(start, NULL);
  	const char * e = env->GetStringUTFChars(end, NULL);
  	dp::String string  = library(env).getText(s, e);
    return env->NewStringUTF(string.utf8());
}

JNIEXPORT jint JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_getPageNumberOfScreenPoint
  (JNIEnv *env, jobject, jdouble screenX, jdouble screenY) {
    return library(env).getPageNumberOfScreenPoint(screenX, screenY);
}

jdoubleArray doubleArrayFromPoint(JNIEnv *env, double x, double y) {
    std::vector<double> buf;
    buf.push_back(x);
    buf.push_back(y);
    jdoubleArray array = env->NewDoubleArray(buf.size());
    env->SetDoubleArrayRegion(array, 0, buf.size(), &buf[0]);
    return array;
}

JNIEXPORT jdoubleArray JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_convertPointFromDeviceSpaceToDocumentSpace
  (JNIEnv *env, jobject, jdouble screenX, jdouble screenY, jint pageNum) {
    double docX = 0;
    double docY = 0;
    if (!library(env).convertPointFromDeviceSpaceToDocumentSpace(screenX, screenY, &docX, &docY, pageNum)) {
        return 0;
    }
    return doubleArrayFromPoint(env, docX, docY);
}

JNIEXPORT jdoubleArray JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_convertPointFromDocumentSpaceToDeviceSpace
  (JNIEnv *env, jobject, jdouble docX, jdouble docY, jint pageNum) {
    double screenX = 0;
    double screenY = 0;
    if (!library(env).convertPointFromDocumentSpaceToDeviceSpace(docX, docY, &screenX, &screenY, pageNum)) {
        return 0;
    }
    return doubleArrayFromPoint(env, screenX, screenY);
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_setPageMode
  (JNIEnv *env, jobject, jint mode) {
  return library(env).setPageMode(mode);
}

jdoubleArray doubleArrayFromRectList(JNIEnv * env, std::vector<onyx::OnyxRectangle> rectangles) {
    const int COUNT = 4;
    if (rectangles.size() <= 0) {
        return NULL;
    }
    int length = rectangles.size();
    int size = length * COUNT;
	jdouble * buffer = new double[size];
	for(int i = 0; i < length; ++i) {
	    dpdoc::Rectangle item = rectangles[i];
	    buffer[i * COUNT] = item.xMin;
        buffer[i * COUNT + 1] = item.yMin;
        buffer[i * COUNT + 2] = item.xMax - item.xMin + 1;
        buffer[i * COUNT + 3] = item.yMax - item.yMin + 1;
	}

	// copy from buffer to double array.
	jdoubleArray array = env->NewDoubleArray(size);
    env->SetDoubleArrayRegion(array, 0, size, buffer);
    return array;
}

jdoubleArray doubleArrayFromMatrix(JNIEnv * env, const onyx::OnyxMatrix & matrix) {
    const int COUNT = 6;
	jdouble * buffer = new double[COUNT];
	memcpy(buffer, &matrix, sizeof(double) * COUNT);
	jdoubleArray array = env->NewDoubleArray(COUNT);
    env->SetDoubleArrayRegion(array, 0, COUNT, buffer);
    return array;
}

JNIEXPORT jdoubleArray JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_rectangles
  (JNIEnv * env, jobject, jstring start , jstring end) {
    const char * s = env->GetStringUTFChars(start, NULL);
  	const char * e = env->GetStringUTFChars(end, NULL);
  	std::vector<onyx::OnyxRectangle> rectangles;
  	library(env).getRectangles(s, e, rectangles);
    return doubleArrayFromRectList(env, rectangles);
}


JNIEXPORT jdoubleArray JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_pageDisplayRectangles
  (JNIEnv * env, jobject, jint page, jint count) {
  	std::vector<onyx::OnyxRectangle> rectangles;
  	std::vector<onyx::OnyxRectangle> size;
    library(env).getDisplayRectangles(page, count, 0, 0, rectangles, size);
  	return doubleArrayFromRectList(env, rectangles);
}

JNIEXPORT jdoubleArray JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_updateLocationNative
  (JNIEnv * env, jobject thiz) {
    onyx::OnyxMatrix matrix = library(env).getCurrentLocation();
  	return doubleArrayFromMatrix(env, matrix);
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_changeNavigationMatrix
  (JNIEnv * env, jobject thiz, jdouble scale, jdouble dx, jdouble dy) {
    return library(env).changeNavigationMatrix(scale, dx, dy);
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_setNavigationMatrix
  (JNIEnv *env, jobject thiz, jdouble scale, jdouble absX, jdouble absY) {
  return library(env).setNavigationMatrix(scale, absX, absY);
}

JNIEXPORT int JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_allVisiblePagesRectangle
  (JNIEnv * env, jobject thiz, jobject list) {
    return library(env).allVisiblePagesRectangle(env, list);
}

JNIEXPORT void JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_setAbortFlagNative
  (JNIEnv *env, jobject, jboolean value) {
    library(env).setAbortFlag(value);
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_getAbortFlagNative
  (JNIEnv *env, jobject) {
    return library(env).getAbortFlag();
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_searchNextNative
  (JNIEnv * env, jobject thiz, jstring pattern, jboolean caseSensitive, jboolean matchWholeWord, jint from, jint end, jobject list) {
  return library(env).searchNextOccur(env, getUtf8String(env, pattern), caseSensitive, matchWholeWord, from, end, list);
}

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    searchPrevNative
 * Signature: (Ljava/lang/String;ZZLjava/lang/String;Ljava/lang/String;Ljava/util/List;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_searchPrevNative
  (JNIEnv * env, jobject, jstring pattern, jboolean caseSensitive, jboolean matchWholeWord, jint from, jint end, jobject list) {
  return library(env).searchPrevOccur(env, getUtf8String(env, pattern), caseSensitive, matchWholeWord,
                                    from, end, list);
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_searchAllInPageNative
  (JNIEnv * env, jobject thiz, jstring pattern, jboolean caseSensitive, jboolean matchWholeWord, jint from, jint end, jobject list)  {
    return library(env).searchAllInPage(env, getUtf8String(env, pattern), caseSensitive, matchWholeWord, from, end, list);
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_hasTableOfContent
  (JNIEnv *env, jobject) {
    return library(env).hasTableOfContent();
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_getTableOfContent
  (JNIEnv * env, jobject thiz, jobject toc) {
    return library(env).getTableOfContent(env, toc);
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_getMetadataNative
  (JNIEnv * env, jobject thiz, jobject metadata, jobject tagList) {
    return library(env).getMetadata(env, metadata, tagList);
}

JNIEXPORT jint JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_collectVisibleLinksNative
  (JNIEnv *env, jobject thiz, jobject list) {
    return library(env).collectVisibleLinks(env, list);
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_isLocationInCurrentScreenNative
  (JNIEnv *env, jobject thiz, jstring location) {
    return library(env).isLocationInCurrentScreen(env, location);
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_setFontFaceInternal
  (JNIEnv *env, jobject thiz, jstring font) {
    if (font == NULL) {
        onyx::AdobeLibrary::setFontFace("");
        return true;
    }

    const char *jstr = env->GetStringUTFChars(font, 0);
    if (jstr == 0) {
        LOGE("GetStringUTFChars failed");
        return false;
    }

    onyx::AdobeLibrary::setFontFace(jstr);
    return true;
}

JNIEXPORT jstring JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_getPageTextNative
  (JNIEnv *env, jobject thiz, jint pageNumber) {
    dp::String text = library(env).getPageText(pageNumber);
    return env->NewStringUTF(text.utf8());
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_setDisplayMarginsNative
  (JNIEnv *env, jobject thiz, jdouble left, jdouble top, jdouble right, jdouble bottom) {
    return library(env).setDisplayMargins(left, top, right, bottom);
}

JNIEXPORT jint JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_getPageOrientationNative
  (JNIEnv *env, jobject, jint page) {
  return library(env).getPageOrientation(page);
}

JNIEXPORT void JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_initDeviceForDRM
  (JNIEnv *env, jclass thiz, jstring deviceName, jstring deviceSerial, jstring applicationPrivateStorage) {

}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_registerAdobeDRMCallback
  (JNIEnv *env, jclass thiz, jobject jCallback) {
    shared_ptr<HostDRMCallback> callback(new HostDRMCallback());
    if (!callback->init(env, thiz, jCallback)) {
        return false;
    }
    library(env).registerDrmCallback(callback);
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_fulfillByAcsm
  (JNIEnv *env, jclass thiz, jstring acsm_path) {
    const char *jstr = env->GetStringUTFChars(acsm_path, 0);
    if (jstr == 0) {
        LOGE("acsm_path, GetStringChars failed");
        return 0;
    }
    QString qstr_acsm_path(jstr);
    env->ReleaseStringUTFChars(acsm_path, jstr);
    return library(env).fulfillByAcsm(qstr_acsm_path);
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_activateDevice
  (JNIEnv *env, jclass thiz, jstring adobeId, jstring password) {
    const char *jstr = env->GetStringUTFChars(adobeId, 0);
    if (jstr == 0) {
        LOGE("adobeId, GetStringChars failed");
        return 0;
    }
    QString qstr_adobe_id(jstr);
    env->ReleaseStringUTFChars(adobeId, jstr);
    jstr = env->GetStringUTFChars(password, 0);
    if (jstr == 0) {
        LOGE("password, GetStringChars failed");
        return 0;
    }
    QString qstr_password(jstr);
    env->ReleaseStringUTFChars(password, jstr);
    return library(env).activateDevice(qstr_adobe_id, qstr_password);
}

JNIEXPORT jstring JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_getActivatedDRMAccount
  (JNIEnv *env, jclass thiz) {
    jstring str_drm_account = env->NewStringUTF(library(env).getActivatedDRMAccount().c_str());
    if (str_drm_account == 0) {
        LOGE("getActivatedDRMAccount: env->NewStringUTF failed");
        return env->NewStringUTF("");
    }
    return str_drm_account;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_deactivateDevice
  (JNIEnv *env, jclass thiz) {
    bool succeeded = library(env).deactivateDevice();
    return succeeded;
}

JNIEXPORT jint JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_getPageNumberByLocationNative
  (JNIEnv *env, jobject thiz, jstring location) {
    const char * string = NULL;
    if (location != NULL) {
        string = env->GetStringUTFChars(location, NULL);
    } else {
        return -1;
    }
    return library(env).getPageNumberByLocation(string);
}

JNIEXPORT jobject JNICALL Java_com_onyx_reader_plugins_adobe_AdobePluginImpl_getNextSentence
  (JNIEnv *env, jclass thiz, jobject splitter, jstring location) {
    if (location == NULL) {
        return NULL;
    }
    const char * tmpLocation = NULL;
    tmpLocation = env->GetStringUTFChars(location, NULL);
    std::string startLocation(tmpLocation);
    env->ReleaseStringUTFChars(location, tmpLocation);
    return library(env).getNextSentence(env, splitter, startLocation);
}

