#include "com_onyx_android_sdk_scribble_touch_RawInputReader.h"
#include "JNIUtils.h"
#include "touch_reader.h"
#include "log.h"

static const char * rawTouchClassName = "com/onyx/android/sdk/scribble/touch/RawInputReader";

TouchReader touchReader;
static jobject readerObject;
static bool debug = false;

static void reportTouchPoint(JNIEnv *env, jobject thiz, int px, int py, int pressure, bool erasing, bool shortcutDrawing, bool shortcutErasing, int state, long ts) {
    JNIUtils utils(env);
    utils.findMethod(rawTouchClassName, "onTouchPointReceived", "(IIIZZZIJ)V");
    env->CallVoidMethod(thiz, utils.getMethodId(), px, py, pressure, erasing, shortcutDrawing, shortcutErasing, state, ts);
}

static void onTouchPointReceived(void * userData, int px, int py, int pressure, long ts, bool erasing, bool shortcutDrawing, bool shortcutErasing, int state) {
    if(debug) {
       LOGI("onTouchPointReceived x y pressure ts erasing state %d %d %d %d %d %d \n", px, py, pressure, ts, erasing, shortcutDrawing, shortcutErasing, state);
    }
    JNIEnv *readerEnv = (JNIEnv *)userData;
    reportTouchPoint(readerEnv, readerObject, px, py, pressure, erasing, shortcutDrawing, shortcutErasing, state, ts);
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputReader_nativeRawReader
  (JNIEnv *env, jobject thiz) {
    readerObject = env->NewGlobalRef(thiz);
    std::string path = touchReader.findDevice();
    std::string deviceName;
    touchReader.openDevice(path, deviceName);
    TouchReader::onTouchPointReceived callback = onTouchPointReceived;
    touchReader.readTouchEventLoop(env, callback);
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputReader_nativeRawClose
  (JNIEnv *env, jobject) {
    touchReader.closeDevice();
    env->DeleteGlobalRef(readerObject);
    readerObject = NULL;
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputReader_nativeSetStrokeWidth
  (JNIEnv *env, jobject, jfloat strokeWidth) {
    touchReader.setStrokeWidth(strokeWidth);
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputReader_nativeSetLimitRegion
  (JNIEnv *env, jobject, jfloatArray limitRegion) {
    int len = env->GetArrayLength(limitRegion);
    jfloat *buf = (jfloat *)calloc(len, sizeof(jfloat));
    jboolean isCopy = false;
    jfloat *array = env->GetFloatArrayElements(limitRegion, &isCopy);
    memcpy(buf, array, len * sizeof(jfloat));
    env->ReleaseFloatArrayElements(limitRegion, array, 0);
    touchReader.setLimitRegion(buf, len);
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputReader_nativeSetExcludeRegion
  (JNIEnv *env, jobject, jfloatArray excludeRegion) {
    int len = env->GetArrayLength(excludeRegion);
    jfloat *buf = (jfloat *)calloc(len, sizeof(jfloat));
    jboolean isCopy = false;
    jfloat *array = env->GetFloatArrayElements(excludeRegion, &isCopy);
    memcpy(buf, array, len * sizeof(jfloat));
    env->ReleaseFloatArrayElements(excludeRegion, array, 0);
    touchReader.setExcludeRegion(buf, len);
}
