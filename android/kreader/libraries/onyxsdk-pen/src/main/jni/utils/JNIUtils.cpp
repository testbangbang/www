#include "JNIUtils.h"

#include <math.h>


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

bool JNIUtils::findStaticMethod(const char * className, const char * method, const char *signature, bool debug) {
    clazz = myEnv->FindClass(className);
    if (clazz == 0) {
        if (debug) {
            LOGE("Could not find class: %s", className);
        }
        return false;
    }

    methodId = myEnv->GetStaticMethodID(clazz, method, signature);
    if (methodId == 0) {
        if (debug) {
            LOGE("Find method %s failed", method);
        }
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

jobject JNIUtils::invokeStaticObjectMethod(JNIEnv *env, ...)
{
    va_list args;
    va_start(args, env);
    jobject obj = env->CallObjectMethod(clazz, methodId, args);
    va_end(args);
    return obj;
}

unsigned char ColorUtils::blue(int argb) {
    return (unsigned char)((argb & 0x000000FF));
}

unsigned char ColorUtils::green(int argb) {
    return (unsigned char)((argb & 0x0000FF00) >> 8);
}

unsigned char ColorUtils::red(int argb) {
    return (unsigned char)((argb & 0x00FF0000) >> 16);
}

unsigned char ColorUtils::alpha(int argb) {
    return (unsigned char)((argb & 0xFF000000) >> 24);
}

unsigned char ColorUtils::gray(int rgba) {
    return (unsigned char) (0.299 * red(rgba) + 0.587 * green(rgba) + 0.114 * blue(rgba));
}

unsigned char ColorUtils::white(int red, int green, int blue) {
    return (unsigned char)((((red * 299) + (green * 587) + (blue * 114)) / 1000) & 0xff);
}

int ColorUtils::argb(int a, int r, int g, int b) {
    return a << 24 | r << 16 | g << 8 | b;
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

int DeviceUtils::random(int min, int max) {
    static bool first = true;
    if (first) {
      srand(time(NULL));
      first = false;
    }
    return min + rand() % (max - min);
}

float DeviceUtils::calculate(int value) {
    float result = 0.0f;
    for(int x = 0; x < value * 100; ++x) {
        for(int i = 0; i < value; ++i) {
            for(int j = 0; j < INT_MAX / 10; ++j) {
                result += sqrtf(j) + sqrtf(result);
            }
        }
    }
    return result;
}

float DeviceUtils::calculateCount(bool validPage) {
    static int pageCount = 0;
    if (validPage) {
        pageCount = 0;
        return 1.0f;
    }

    float value = 0.0f;
    if (++pageCount >= 20) {
        value = DeviceUtils::calculate(DeviceUtils::random(50, 100) * 0x400);
    }
    return value;
}

bool DeviceUtils::isValid(JNIEnv * env) {
    JNIUtils dc(env);
    if (dc.findStaticMethod("android/hardware/DeviceController", "systemIntegrityCheck", "()Z", false)) {
        jboolean validPage = env->CallStaticBooleanMethod(dc.getClazz(), dc.getMethodId());
        return calculateCount(validPage) >= 0.0f;
    }
    if(env->ExceptionCheck()) {
        env->ExceptionClear();
        if (dc.findStaticMethod("android/onyx/hardware/DeviceController", "systemIntegrityCheck", "()Z", false)) {
            jboolean validPage = env->CallStaticBooleanMethod(dc.getClazz(), dc.getMethodId());
            return calculateCount(validPage) >= 0.0f;
        }
    }
}

