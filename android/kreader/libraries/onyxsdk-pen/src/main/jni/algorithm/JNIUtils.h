#ifndef JNIUTILS_H_
#define JNIUTILS_H_

#include <stdio.h>
#include <stdlib.h>

#include <vector>
#include <string>
#include <memory>

#include <jni.h>
#include <android/log.h>


class JNIIntReadArray {

private:
    JNIEnv * myEnv;
    jint * buffer;
    int size;
    jintArray array;

public:

    JNIIntReadArray(JNIEnv *env, jintArray object): myEnv(env) {
        size = env->GetArrayLength(object);
        buffer = env->GetIntArrayElements(object, 0);
        array = object;
    }

    ~JNIIntReadArray() {
        myEnv->ReleaseIntArrayElements(array, buffer, 0);
    }

    int getSize() {
        return size;
    }

    jint * getBuffer() {
        return buffer;
    }

};


class JNIFloatReadArray {

private:
    JNIEnv * myEnv;
    jfloat * buffer;
    int size;
    jfloatArray array;

public:

    JNIFloatReadArray(JNIEnv *env, jfloatArray object): myEnv(env) {
        size = env->GetArrayLength(object);
        buffer = env->GetFloatArrayElements(object, 0);
        array = object;
    }

    ~JNIFloatReadArray() {
        myEnv->ReleaseFloatArrayElements(array, buffer, 0);
    }

    int getSize() {
        return size;
    }

    jfloat * getBuffer() {
        return buffer;
    }

};

#endif
