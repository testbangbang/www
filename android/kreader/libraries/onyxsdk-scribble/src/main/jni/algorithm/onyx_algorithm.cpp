
#include <vector>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <list>
#include <map>


#include "JNIUtils.h"

#include "log.h"
#include "pen.h"
#include "com_hanvon_core_Algorithm.h"

JNIEXPORT jboolean JNICALL Java_com_hanvon_core_Algorithm_initializeEx
  (JNIEnv *env, jclass thiz, jint width, jint height, jobject bitmap) {
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

    bool value = initializeEx(width, height, (int *)pixels);
    AndroidBitmap_unlockPixels(env, bitmap);
    return value;
}

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_setPen
  (JNIEnv * env, jclass thiz, jint antiLevel, jint penStyle, jint colorType, jint penWidth, jint colorRate) {
    setPen(antiLevel, penStyle, colorType, penWidth, colorRate);
}

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_setPenColor
  (JNIEnv *env, jclass thiz, jintArray array) {
    JNIIntReadArray jniArray(env, array);
    setPenColor(jniArray.getBuffer());
}

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_drawLine
  (JNIEnv *env, jclass thiz, jint x, jint y, jfloat pressure, jintArray inRect, jintArray drawMemory) {
    JNIIntReadArray rect(env, inRect);
    JNIIntReadArray bitmap(env, drawMemory);
    drawLine(x, y, pressure, rect.getBuffer(), bitmap.getBuffer());
}

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_drawLineEx
  (JNIEnv *env, jclass thiz, jint x, jint y, jfloat pressure, jintArray inRect, jfloatArray pointArray) {
    JNIIntReadArray rect(env, inRect);
    JNIFloatReadArray points(env, pointArray);
    drawLineEx(x, y, pressure, rect.getBuffer(), points.getBuffer());
}

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_setClipRegion
  (JNIEnv *, jclass, jintArray) {
  }

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_addClipRegion
  (JNIEnv *, jclass, jintArray) {
  }

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_reSet
  (JNIEnv *, jclass)  {
  }

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_clear
  (JNIEnv *, jclass, jint) {
  }

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_clearBackground
  (JNIEnv *, jclass, jintArray) {
}

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_clearBackgroundByte
  (JNIEnv *, jclass, jbyteArray) {
  }

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_setBackground
  (JNIEnv *, jclass, jintArray) {
  }

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_reDrawLine
  (JNIEnv *, jclass, jintArray, jfloatArray, jint, jintArray) {
  }

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_reDrawLineEx
  (JNIEnv *, jclass, jfloatArray, jint, jboolean) {
  }

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_reDraw
  (JNIEnv *, jclass, jintArray, jfloatArray, jint) {
  }

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_interpolate
  (JNIEnv *env, jclass thiz, jintArray pointArray, jfloatArray pressureArray, jintArray inRect, jfloatArray pathData, jboolean isErase) {
}

JNIEXPORT void JNICALL Java_com_hanvon_core_Algorithm_setSimulatePressure
  (JNIEnv *env, jclass thiz, jboolean isSimulatePressure) {
    setSimulatePressure(isSimulatePressure);
}