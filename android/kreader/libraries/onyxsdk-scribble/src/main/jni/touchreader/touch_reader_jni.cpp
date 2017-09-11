#include "com_onyx_android_sdk_scribble_touch_RawInputProcessor.h"
#include "JNIUtils.h"
#include "touch_reader.h"
#include "log.h"

static const char * rawTouchClassName = "com/onyx/android/sdk/scribble/touch/RawInputProcessor";

TouchReader touchReader;
static jobject readerObject;
static bool debug = false;

static void reportTouchPoint(JNIEnv *env, jobject thiz, int px, int py, int pressure, bool erasing, int state, long ts) {
    JNIUtils utils(env);
    utils.findMethod(rawTouchClassName, "onTouchPointReceived", "(IIIZIJ)V");
    env->CallVoidMethod(thiz, utils.getMethodId(), px, py, pressure, erasing, state, ts);
}

static void onTouchPointReceived(void * userData, int px, int py, int pressure, long ts, bool erasing, int state) {
    if(debug) {
       LOGI("onTouchPointReceived x y pressure ts erasing state %d %d %d %d %d %d \n", px, py, pressure, ts, erasing, state);
    }
    JNIEnv *readerEnv = (JNIEnv *)userData;
    reportTouchPoint(readerEnv, readerObject, px, py, pressure, erasing, state, ts);
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputProcessor_nativeRawReader
  (JNIEnv *env, jobject thiz) {
    readerObject = thiz;
    std::string path = touchReader.findDevice();
    std::string deviceName;
    touchReader.openDevice(path, deviceName);
    TouchReader::onTouchPointReceived callback = onTouchPointReceived;
    touchReader.readTouchEventLoop(env, callback);
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputProcessor_nativeRawClose
  (JNIEnv *env, jobject) {
    touchReader.closeDevice();
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputProcessor_nativeSetStrokeWidth
  (JNIEnv *env, jobject, jfloat strokeWidth) {
    touchReader.setStrokeWidth(strokeWidth);
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputProcessor_nativeSetLimitRegion
  (JNIEnv *env, jobject, jfloatArray limitRegion) {
    int len = env->GetArrayLength(limitRegion);
    jboolean isCopy = false;
    jfloat *array = env->GetFloatArrayElements(limitRegion, &isCopy);
    touchReader.setLimitRegion(array, len);
}
