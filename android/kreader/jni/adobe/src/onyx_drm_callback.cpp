
#include "onyx_drm_callback.h"


HostDRMCallback::HostDRMCallback() {
}

HostDRMCallback::~HostDRMCallback() {
    JNIEnv *env = 0;
    env = getJNIEnv();
    if (!env) {
        LOGE("getJNIEnv failed");
        return;
    }
    env->DeleteGlobalRef(callbackObject);
}

bool HostDRMCallback::init(JNIEnv * env, jclass thiz, jobject jCallback) {
    if (env->GetJavaVM(&jvm) != 0) {
        LOGE("GetJavaVM failed!");
        return false;
    }

    callbackObject = env->NewGlobalRef(jCallback);
    return true;
}

JNIEnv *HostDRMCallback::getJNIEnv() {
    JNIEnv *env = 0;
    jvm->GetEnv((void **)&env, JNI_VERSION_1_6);
    return env;
}

jmethodID HostDRMCallback::getMethod(const char * name, const char * parameters) {
    JNIEnv * env = getJNIEnv();
    if (env == 0) {
        return 0;
    }
    jobject object = callbackObject;
    jclass myClass = env->GetObjectClass(object);
    if (myClass == 0) {
        LOGE("Find class com/onyx/android/onyxpdf/IDRMCallback failed");
        return 0;
    }

    jmethodID mid = env->GetMethodID(myClass, name, parameters);
    if (mid == 0) {
        LOGE("Find method %s %s failed", name, parameters);
        return 0;
    }
    env->DeleteLocalRef(myClass);
    return mid;
}

void HostDRMCallback::reportDRMAuthenticateFailed(const QString &message) {
    callStringMethod("reportDRMAuthenticateFailed", "(Ljava/lang/String;)V", message);
}

void HostDRMCallback::reportDRMActivationResult(bool succeeded, const QString &message) {
    JNIEnv *env = getJNIEnv();
    if (!env) {
        LOGE("getJNIEnv failed");
        return;
    }

    jmethodID mid = getMethod("reportDRMActivationResult", "(ZLjava/lang/String;)V");
    if (mid == 0) {
        LOGE("Find method reportDRMActivationResult() failed");
        return;
    }

    const char * data = message.c_str();
    jstring str_message = env->NewStringUTF(data);
    if (str_message == 0) {
        LOGE("reportDRMActivationResult: env->NewStringUTF failed");
        return;
    }

    env->CallVoidMethod(callbackObject, mid, succeeded, str_message);
    env->DeleteLocalRef(str_message);
}

void HostDRMCallback::reportDRMFulfillmentResult(bool succeeded, const QString &message) {
    JNIEnv *env= getJNIEnv();
    if (!env) {
        LOGE("getJNIEnv failed");
        return;
    }

    jmethodID mid = getMethod("reportDRMFulfillmentResult", "(ZLjava/lang/String;)V");
    if (mid == 0) {
        return;
    }

    const char * data = message.c_str();
    jstring str_message = env->NewStringUTF(data);
    if (str_message == 0) {
        LOGE("reportDRMFulfillmentResult: env->NewStringUTF failed");
        return;
    }

    env->CallVoidMethod(callbackObject, mid, succeeded, str_message);
    env->DeleteLocalRef(str_message);
}

void HostDRMCallback::reportDRMDownloadProgress(double progress) {
    callDoubleMethod("reportDRMDownloadProgress", "(D)V", progress);
}

void HostDRMCallback::reportDRMFulfillContentPath(const QString &path) {
    callStringMethod("reportDRMFulfillContentPath", "(Ljava/lang/String;)V", path);
}

void HostDRMCallback::reportActivationDone() {
    callVoidMethod("onActivationDone", "()V");
}

void HostDRMCallback::reportAuthSignInDone() {
    callVoidMethod("onAuthSignInDone", "()V");
}

void HostDRMCallback::reportDownloadDone(){
    callVoidMethod("onDownloadDone", "()V");
}

void HostDRMCallback::reportLoanReturnDone(){
    callVoidMethod("onLoanReturnDone", "()V");
}

void HostDRMCallback::reportFulfillDone(){
    callVoidMethod("onFulfillDone", "()V");
}

void HostDRMCallback::reportDownloadProgress(double progress){
    callDoubleMethod("onDownloadProgress", "(D)V", progress);
}

void HostDRMCallback::reportActivationFailed(const QString &message){
    callStringMethod("onActivationFailed", "(Ljava/lang/String;)V", message);
}

void HostDRMCallback::reportSignInFailed(const QString &message){
    callStringMethod("onSignInFailed", "(Ljava/lang/String;)V", message);
}

void HostDRMCallback::reportDownloadFailed(const QString &message){
    callStringMethod("onDownloadFailed", "(Ljava/lang/String;)V", message);
}

void HostDRMCallback::reportLoanReturnFailed(const QString &message){
    callStringMethod("onLoanReturnFailed", "(Ljava/lang/String;)V", message);
}

void HostDRMCallback::reportFulfillFailed(const QString &message){
    callStringMethod("onFulfillFailed", "(Ljava/lang/String;)V", message);
}

void HostDRMCallback::callVoidMethod(const char * name, const char * parameters) {
    JNIEnv *env = getJNIEnv();
    if (!env) {
        LOGE("getJNIEnv failed");
        return;
    }

    jmethodID mid = getMethod(name, parameters);
    if (mid == 0) {
        return;
    }

    env->CallVoidMethod(callbackObject, mid);
}

void HostDRMCallback::callStringMethod(const char * name, const char * parameters, const QString &message) {
    JNIEnv *env = getJNIEnv();
    if (!env) {
        LOGE("getJNIEnv failed");
        return;
    }

    jmethodID mid = getMethod(name, parameters);
    if (mid == 0) {
        return;
    }

    const char * data = message.c_str();
    jstring str = env->NewStringUTF(data);
    if (str == 0) {
        LOGE("env->NewStringUTF failed");
        return;
    }
    env->CallVoidMethod(callbackObject, mid, str);
    env->DeleteLocalRef(str);
}

void HostDRMCallback::callDoubleMethod(const char * name, const char * parameters, double value) {
    JNIEnv *env = getJNIEnv();
    if (!env) {
        LOGE("getJNIEnv failed");
        return;
    }

    jmethodID mid = getMethod(name, parameters);
    if (mid == 0) {
        return;
    }

    env->CallVoidMethod(callbackObject, mid, value);
}
