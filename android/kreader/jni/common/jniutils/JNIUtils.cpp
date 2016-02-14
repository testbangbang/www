#include "JNIUtils.h"

#define LOG_TAG "JNIUtils"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGT(...) __android_log_print(ANDROID_LOG_INFO,"alert",__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)



JNIUtils::JNIUtils(JNIEnv * env, const char * className, const char * method, const char * signature) : myEnv(env) {
    clazz = env->FindClass(className);
    if (clazz == 0) {
        LOGE("Could not find class: %s", className);
        return;
    }

    methodId = env->GetMethodID(clazz, method, signature);
    if (methodId == 0) {
        LOGE("Find method %s failed", method);
    }
}

JNIUtils::~JNIUtils() {
    if (clazz != 0) {
        myEnv->DeleteLocalRef(clazz);
    }
}

void JNIUtils::invokeStaticMethod(JNIEnv *env, ...) {
    va_list args;
    va_start(args, env);
    env->CallStaticVoidMethod(clazz, methodId, args);
    va_end(args);
}