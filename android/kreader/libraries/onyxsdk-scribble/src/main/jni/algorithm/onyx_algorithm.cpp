
#include <vector>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <list>
#include <map>
#include <math.h>

#include "JNIUtils.h"

#include "log.h"

JNIEXPORT jfloat JNICALL Java_com_hanvon_core_Algorithm_distance
  (JNIEnv *env, jclass thiz, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jfloat x, jfloat y) {
    float A = x - x1;
    float B = y - y1;
    float C = x2 - x1;
    float D = y2 - y1;

    float dot = A * C + B * D;
    float lenSq = C * C + D * D;
    float param = -1.0f;
    if (lenSq != 0) {
        param = dot / lenSq;
    }

    float xx, yy;

    if (param < 0) {
        xx = x1;
        yy = y1;
    } else if (param > 1) {
        xx = x2;
        yy = y2;
    } else {
        xx = x1 + param * C;
        yy = y1 + param * D;
    }

    float dx = x - xx;
    float dy = y - yy;
    return sqrtf(dx * dx + dy * dy);
}
