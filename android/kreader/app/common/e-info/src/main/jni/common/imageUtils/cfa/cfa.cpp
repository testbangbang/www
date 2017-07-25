#include <jni.h>
#include <time.h>
#include <pthread.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <unistd.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <list>

#include <cassert>
#include <cstring>
#include <string>
#include <list>
#include <algorithm>
#include <mutex>
#include <thread>

#ifdef __cplusplus
extern "C" {
#endif
    
    
#ifdef __cplusplus
}
#endif

#ifdef NDK_PROFILER
#include "prof.h"
#endif

#include "com_onyx_einfo_utils_QRCodeUtil.h"

#include "JNIUtils.h"

#define MAX(a,b) (((a) > (b)) ? (a) : (b))
#define MIN(a,b) (((a) < (b)) ? (a) : (b))

#define LOG_TAG "onyx_cfa"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGT(...) __android_log_print(ANDROID_LOG_INFO,"alert",__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)



class JNIBitmap {
private:
    JNIEnv * myEnv;
    AndroidBitmapInfo info;
    int *pixels;
    
    
public:
    JNIBitmap(JNIEnv * env);
    ~JNIBitmap();
    
public:
    bool attach(jobject bitmap);
    const AndroidBitmapInfo & getInfo() const;
    int * getPixels();
    
};


JNIBitmap::JNIBitmap(JNIEnv * env) : myEnv(env), pixels(0) {
    
}

JNIBitmap::~JNIBitmap() {
}

const AndroidBitmapInfo & JNIBitmap::getInfo() const {
    return info;
}

int * JNIBitmap::getPixels() {
    return pixels;
}

bool JNIBitmap::attach(jobject bitmap) {
    int ret;
    if ((ret = AndroidBitmap_getInfo(myEnv, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return false;
    }
    
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return false;
    }
    
    if ((ret = AndroidBitmap_lockPixels(myEnv, bitmap, (void **)&pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return false;
    }
    
    return true;
}

JNIEXPORT void JNICALL Java_com_onyx_einfo_utils_QRCodeUtil_toRgbwBitmap
(JNIEnv *env, jclass thiz, jobject dstBitmap, jobject srcBitmap, jint orientation) {
    JNIBitmap dst(env);
    JNIBitmap src(env);
    if (!dst.attach(dstBitmap) || !src.attach(srcBitmap)) {
        return;
    }
    
    int sw = src.getInfo().width;
    int sh = src.getInfo().height;
    int ss = src.getInfo().stride;
    int * srcData = src.getPixels();
    
    int * dstData = dst.getPixels();
    int ds = dst.getInfo().stride;
    
    for(int y = 0; y < sh; ++y) {
        int * srcLine = srcData + ss * y / 4;
        int * dstLine1 = dstData + ds * y * 2 / 4;
        int * dstLine2 = dstLine1 + ds / 4;
        for(int x = 0; x < sw; ++x) {
            int argb = *srcLine++;
            unsigned char a = ColorUtils::alpha(argb);
            unsigned char r = ColorUtils::red(argb);
            unsigned char g = ColorUtils::green(argb);
            unsigned char b = ColorUtils::blue(argb);
            unsigned char w = ColorUtils::white(r, g, b);
            
            *dstLine1++ = ColorUtils::argb(a, r, r, r);
            *dstLine1++ = ColorUtils::argb(a, g, g, g);
            
            *dstLine2++ = ColorUtils::argb(a, w, w, w);
            *dstLine2++ = ColorUtils::argb(a, b, b, b);
        }
    }
}
