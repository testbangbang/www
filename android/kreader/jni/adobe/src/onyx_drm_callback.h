#ifndef ONYX_DRM_CALLBACK_H_
#define ONYX_DRM_CALLBACK_H_


#include <jni.h>
#include <time.h>
#include <pthread.h>
#include <android/log.h>
#include <android/bitmap.h>

#include "types.h"
#include "dp_all.h"
#include "scoped_ptr.h"
#include "shared_ptr.h"
#include <vector>


class HostDRMCallback
{
public:
    HostDRMCallback();
    ~HostDRMCallback();

public:
    bool init(JNIEnv * env, jclass thiz, jobject jCallback);

public:
    virtual void reportActivationDone();
    virtual void reportAuthSignInDone();
    virtual void reportDownloadDone();
    virtual void reportLoanReturnDone();
    virtual void reportFulfillDone();


    virtual void reportDownloadProgress(double progress);


    virtual void reportActivationFailed(const QString &message);
    virtual void reportSignInFailed(const QString &message);
    virtual void reportDownloadFailed(const QString &message);
    virtual void reportLoanReturnFailed(const QString &message);
    virtual void reportFulfillFailed(const QString &message);


    virtual void reportDRMAuthenticateFailed(const QString &message);
    virtual void reportDRMActivationResult(bool succeeded, const QString &message);
    virtual void reportDRMFulfillmentResult(bool succeeded, const QString &message);
    virtual void reportDRMDownloadProgress(double progress);
    virtual void reportDRMFulfillContentPath(const QString &fulfill_content_path);

private:
    JNIEnv *getJNIEnv();
    jmethodID getMethod(const char * name, const char * parameters);

    void callVoidMethod(const char * name, const char * parameters);
    void callStringMethod(const char * name, const char * parameters, const QString &message);
    void callDoubleMethod(const char * name, const char * parameters, double value);

private:
    JavaVM *jvm;
    jobject callbackObject;

};


#endif