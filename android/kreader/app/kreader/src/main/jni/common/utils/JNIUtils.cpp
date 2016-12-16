#include "JNIUtils.h"

#define LOG_TAG "JNIUtils"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGT(...) __android_log_print(ANDROID_LOG_INFO,"alert",__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)



JNIUtils::JNIUtils(JNIEnv * env) : myEnv(env), clazz(0) {
}

bool JNIUtils::findClass(const char *className)
{
    clazz = myEnv->FindClass(className);
    if (clazz == 0) {
        LOGE("Could not find class: %s", className);
        return false;
    }
    return true;
}

bool JNIUtils::getObjectClass(const jobject object)
{
    clazz = myEnv->GetObjectClass(object);
    if (clazz == 0) {
        LOGE("Could not find class of object");
        return false;
    }
    return true;
}

bool JNIUtils::findMethod(const char *method, const char *signature)
{
    if (clazz == 0) {
        return false;
    }

    methodId = myEnv->GetMethodID(clazz, method, signature);
    if (methodId == 0) {
        LOGE("Find method %s failed", method);
        return false;
    }
    return true;
}

bool JNIUtils::findMethod(const char * className, const char * method, const char *signature) {
    if (clazz != 0) {
        myEnv->DeleteLocalRef(clazz);
    }

    clazz = myEnv->FindClass(className);
    if (clazz == 0) {
        LOGE("Could not find class: %s", className);
        return false;
    }

    methodId = myEnv->GetMethodID(clazz, method, signature);
    if (methodId == 0) {
        LOGE("Find method %s failed", method);
        return false;
    }
    return true;
}

bool JNIUtils::findStaticMethod(const char * className, const char * method, const char *signature) {
    clazz = myEnv->FindClass(className);
    if (clazz == 0) {
        LOGE("Could not find class: %s", className);
        return false;
    }

    methodId = myEnv->GetStaticMethodID(clazz, method, signature);
    if (methodId == 0) {
        LOGE("Find method %s failed", method);
        return false;
    }
    return true;
}

JNIUtils::~JNIUtils() {
    if (clazz != 0) {
        myEnv->DeleteLocalRef(clazz);
    }
}

jint JNIUtils::hashcode(const jobject object)
{
    jclass clazz = myEnv->GetObjectClass(object);
    jmethodID methodIDHashcode = myEnv->GetMethodID(clazz, "hashCode", "()I");
    jint hashCode = myEnv->CallIntMethod(object, methodIDHashcode);
    myEnv->DeleteLocalRef(clazz);
    return hashCode;
}

void JNIUtils::invokeStaticMethod(JNIEnv *env, ...) {
    va_list args;
    va_start(args, env);
    env->CallStaticVoidMethod(clazz, methodId, args);
    va_end(args);
}

unsigned char ColorUtils::red(int rgba) {
    return (unsigned char)(rgba & 0xff);
}

unsigned char ColorUtils::green(int rgba) {
    return (unsigned char)((rgba >> 8) & 0xff);
}

unsigned char ColorUtils::blue(int rgba) {
    return (unsigned char)((rgba >> 16) & 0xff);
}

unsigned char ColorUtils::gray(int rgba) {
    return (unsigned char) (0.299 * red(rgba) + 0.587 * green(rgba) + 0.114 * blue(rgba));
}

unsigned char ColorUtils::white(int red, int green, int blue) {
    return (unsigned char)((((red * 299) + (green * 587) + (blue * 114)) / 1000) & 0xff);
}

int ColorUtils::argb(int a, int r, int g, int b) {
    return a << 24 | b << 16 | g << 8 | r;
}

int ColorUtils::toRed(int gray) {
    return gray << 24;
}

int ColorUtils::toGreen(int gray) {
    return gray << 16;
}

int ColorUtils::toBlue(int gray) {
    return gray << 8;
}

int ColorUtils::toWhite(int white) {
    return white | white << 8 | white << 16 | white << 24;
}

