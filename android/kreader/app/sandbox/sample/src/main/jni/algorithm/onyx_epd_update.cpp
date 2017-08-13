
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
#include "com_onyx_android_sample_utils_EpdUtils.h"

static int rand(int min, int max) {
    return rand() % (max - min + 1) + min;
}

JNIEXPORT void JNICALL Java_com_onyx_android_sample_utils_EpdUtils_test
  (JNIEnv *env, jclass thiz) {

    int width = 2200;
    int height = 1650;
    int limit = width * height;
    int updLimit = 10;
    int maxFrame = 33;

    unsigned char * wb      = new unsigned char[limit];
    unsigned char * wbIndex = new unsigned char[limit];
    unsigned char ** updList= new unsigned char *[updLimit];
    for(int i = 0; i < updLimit; ++i) {
        updList[i] = new unsigned char[limit];
    }

    int round = 0;
    do {
        int finished = 0;
        for(int i = 0; i < limit; ++i) {
            wb[i] = rand(0, 16);
            wbIndex[i] = rand(0, maxFrame);
            if (wbIndex[i] <= 0) {
                ++finished;
            }
            for(int j = 0; j < updLimit; ++j) {
                updList[j][i] = rand(0, 16);
            }
        }

        LOGE("testing step1 with finished pixels: %d", finished);
        for(int i = 0; i < limit; ++i) {
            if (wbIndex[i] > 0) {
                continue;
            }
            unsigned char src = wb[i];
            for(int j = 0; j < updLimit; ++j) {
                unsigned char value = updList[j][i];
                if (value != src) {
                    wb[i] = value;
                    wbIndex[i] = maxFrame;
                }
            }
        }
        LOGE("testing step1 done");
        for(int i = 0; i < limit; ++i) {
            wbIndex[i]--;
        }
        LOGE("testing step2 done");
    } while (round++ < 20);

}
