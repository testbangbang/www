/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_onyx_android_sdk_pen_RawInputReader */

#ifndef _Included_com_onyx_android_sdk_pen_RawInputReader
#define _Included_com_onyx_android_sdk_pen_RawInputReader
#ifdef __cplusplus
extern "C" {
#endif
#undef com_onyx_android_sdk_pen_RawInputReader_PEN_SIZE
#define com_onyx_android_sdk_pen_RawInputReader_PEN_SIZE 0L
#undef com_onyx_android_sdk_pen_RawInputReader_ON_PRESS
#define com_onyx_android_sdk_pen_RawInputReader_ON_PRESS 0L
#undef com_onyx_android_sdk_pen_RawInputReader_ON_MOVE
#define com_onyx_android_sdk_pen_RawInputReader_ON_MOVE 1L
#undef com_onyx_android_sdk_pen_RawInputReader_ON_RELEASE
#define com_onyx_android_sdk_pen_RawInputReader_ON_RELEASE 2L
#undef com_onyx_android_sdk_pen_RawInputReader_ON_RELEASE_OUT_LIMIT_REGION
#define com_onyx_android_sdk_pen_RawInputReader_ON_RELEASE_OUT_LIMIT_REGION 3L
/*
 * Class:     com_onyx_android_sdk_pen_RawInputReader
 * Method:    nativeRawReader
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_onyx_android_sdk_pen_RawInputReader_nativeRawReader
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_android_sdk_pen_RawInputReader
 * Method:    nativeRawClose
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_onyx_android_sdk_pen_RawInputReader_nativeRawClose
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_android_sdk_pen_RawInputReader
 * Method:    nativeSetStrokeWidth
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_com_onyx_android_sdk_pen_RawInputReader_nativeSetStrokeWidth
  (JNIEnv *, jobject, jfloat);

/*
 * Class:     com_onyx_android_sdk_pen_RawInputReader
 * Method:    nativeSetLimitRegion
 * Signature: ([F)V
 */
JNIEXPORT void JNICALL Java_com_onyx_android_sdk_pen_RawInputReader_nativeSetLimitRegion
  (JNIEnv *, jobject, jfloatArray);

/*
 * Class:     com_onyx_android_sdk_pen_RawInputReader
 * Method:    nativeSetExcludeRegion
 * Signature: ([F)V
 */
JNIEXPORT void JNICALL Java_com_onyx_android_sdk_pen_RawInputReader_nativeSetExcludeRegion
  (JNIEnv *, jobject, jfloatArray);

#ifdef __cplusplus
}
#endif
#endif
