#include <jni.h>
#include <time.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#include <assert.h>

#include <unordered_map>

#include <miniexp.h>
#include <ddjvuapi.h>
#include "debug.h"

#include "JNIUtils.h"
#include "plugin_context_holder.h"

#include "com_onyx_android_sdk_reader_plugins_djvu_DjvuJniWrapper.h"

#include "onyx_djvu_context.h"

#define PACKAGENAME "com/onyx/android/sdk/reader/plugins/djvu"


#define LOG_TAG "djvulib"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

namespace {

PluginContextHolder<OnyxDjvuContext> contextHolder;

}

JNIEXPORT int JNICALL
Java_com_onyx_android_sdk_reader_plugins_djvu_DjvuJniWrapper_nativeOpenFile(JNIEnv * env, jobject thiz, jint id, jstring jfilePath)
{
    OnyxDjvuContext *context = contextHolder.findContext(env, id);
    if (!context) {
        context = OnyxDjvuContext::createContext(env, jfilePath);
        if (!context) {
            return 0;
        }
        contextHolder.insertContext(env, id, std::unique_ptr<OnyxDjvuContext>(context));
    }
    return context->getPageCount();
}

JNIEXPORT jboolean JNICALL
Java_com_onyx_android_sdk_reader_plugins_djvu_DjvuJniWrapper_nativeGotoPage(JNIEnv *env, jobject thiz, jint id, int pageNum)
{
    OnyxDjvuContext *context = contextHolder.findContext(env, id);
    if (!context) {
        return false;
    }
    return context->gotoPage(pageNum);
}

JNIEXPORT jboolean JNICALL
Java_com_onyx_android_sdk_reader_plugins_djvu_DjvuJniWrapper_nativeGetPageSize(JNIEnv *env, jobject thiz, jint id, int pageNum, jfloatArray size)
{
    OnyxDjvuContext *context = contextHolder.findContext(env, id);
    if (!context) {
        return false;
    }
    std::vector<float> v;
    if (!context->getPageSize(pageNum, &v)) {
        return false;
    }
    assert(v.size() == 2);
    env->SetFloatArrayRegion(size, 0, v.size(), v.data());
    return true;
}

JNIEXPORT jboolean JNICALL
Java_com_onyx_android_sdk_reader_plugins_djvu_DjvuJniWrapper_nativeExtractPageText(JNIEnv *env, jobject thiz, jint id, int pageNum, jobject textChunks)
{
    OnyxDjvuContext *context = contextHolder.findContext(env, id);
    if (!context) {
        return false;
    }
    return context->extractPageText(env, pageNum, textChunks);
}

JNIEXPORT jboolean JNICALL
Java_com_onyx_android_sdk_reader_plugins_djvu_DjvuJniWrapper_nativeDrawPage(JNIEnv *env, jobject thiz, jint id, jint pageNum, jobject bitmap, float zoom,
                                                                 int bmpWidth, int bmpHeight, int patchX, int patchY, int patchW, int patchH)
{
    OnyxDjvuContext *context = contextHolder.findContext(env, id);
    if (!context) {
        return false;
    }
    return context->draw(env, pageNum, bitmap, zoom, bmpWidth, bmpHeight, patchX, patchY, patchW, patchH);
}

JNIEXPORT void JNICALL
Java_com_onyx_android_sdk_reader_plugins_djvu_DjvuJniWrapper_nativeClose(JNIEnv * env, jobject thiz, jint id)
{
    contextHolder.eraseContext(env, id);
}

