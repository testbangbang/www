#include "com_onyx_android_sdk_scribble_touch_RawInputProcessor.h"
#include "JNIUtils.h"
#include "touch_reader.h"
#include "log.h"

static const char * rawTouchClassName = "com/onyx/android/sdk/scribble/touch/RawInputProcessor";

TouchReader* touchReader = new TouchReader();
JNIEnv *readerEnv;
jobject readerObject;

static void reportTouchPoint(JNIEnv *env, jobject thiz, int px, int py, int pressure, long ts, bool erasing, int state) {
    JNIUtils utils(env);
    utils.findMethod(rawTouchClassName, "onTouchPointReceived", "(IIIJZI)V");
    env->CallVoidMethod(thiz, utils.getMethodId(), px, py, pressure, ts, erasing, state);
}

static void onTouchPointReceived(int px, int py, int pressure, long ts, bool erasing, int state) {
    LOGI("onTouchPointReceived x y %d, %d\n", px, py);
    reportTouchPoint(readerEnv, readerObject, px, py, pressure, ts, erasing, state);
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputProcessor_nativeRawReader
  (JNIEnv *env, jobject thiz) {
    readerEnv = env;
    readerObject = thiz;
    std::string path = touchReader->findDevice();
    std::string deviceName;
    touchReader->openDevice(path, deviceName);
    TouchReader::onTouchPointReceived callback = onTouchPointReceived;
    touchReader->readTouchEventLoop(callback);
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputProcessor_nativeRawClose
  (JNIEnv *env, jobject) {
    touchReader->closeDevice();
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputProcessor_nativeSetStrokeWidth
  (JNIEnv *env, jobject, jfloat strokeWidth) {
    touchReader->setStrokeWidth(strokeWidth);
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_touch_RawInputProcessor_nativeSetLimitRegion
  (JNIEnv *env, jobject, jintArray limitRegion) {
    int len = env->GetArrayLength(limitRegion);
    jboolean isCopy = false;
    jint *array = env->GetIntArrayElements(limitRegion, &isCopy);
    touchReader->setLimitRegion(array, len);
}
