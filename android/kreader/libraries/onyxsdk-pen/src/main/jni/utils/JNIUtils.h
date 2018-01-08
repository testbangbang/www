#ifndef JNIUTILS_H_
#define JNIUTILS_H_

#include <stdio.h>
#include <stdlib.h>

#include <vector>
#include <string>
#include <memory>

#include <jni.h>
#include <android/log.h>

class JNIUtils {
    
private:
    JNIEnv * myEnv;
    jclass clazz;
    jmethodID methodId;
    
public:
    JNIUtils(JNIEnv * env);
    ~JNIUtils();
    
public:
    jint hashcode(const jobject object);
    bool findClass(const char * className);
    bool getObjectClass(const jobject object);
    bool findMethod(const char * method, const char *signature);
    bool findMethod(const char * className, const char * method, const char *signature);
    bool findStaticMethod(const char * className, const char * method, const char *signature, bool debug);

    jclass getClazz() {
        return clazz;
    }
    
    jmethodID getMethodId() {
        return methodId;
    }
    
    void invokeStaticMethod(JNIEnv *env, ...);

    jobject invokeStaticObjectMethod(JNIEnv *env, ...);
    
};

class JNIByteArray {
    
private:
    JNIEnv * myEnv;
    jbyte * buffer;
    size_t size;
    size_t offset;
    jbyteArray array;
    bool allocate;
    
public:
    
    JNIByteArray(JNIEnv *env, size_t s): myEnv(env), buffer(0), size(s), offset(0), array(0), allocate(true) {
        buffer = new jbyte[size];
        memset(buffer, 0, size);
        array = env->NewByteArray(size);
    }
    
    ~JNIByteArray() {
        if (allocate) {
            delete [] buffer;
        }
        if (array != 0) {
            myEnv->DeleteLocalRef(array);
        }
    }
    
    jbyte * getBuffer() {
        return buffer;
    }
    
    void appendBuffer(jbyte *addr, size_t n) {
        if ((n + offset) > size) {
            n = size - offset;
        }
        if (n > 0) {
            memcpy(buffer + offset, addr, n);
            offset += n;
        }
    }
    
    jbyteArray getByteArray(bool sync) {
        if (sync) {
            copyToJavaArray();
        }
        return array;
    }

    void detachByteArray() {
        array = nullptr;
    }
    
    void copyToJavaArray() {
        myEnv->SetByteArrayRegion(array, 0, size, buffer);
    }
};

class JNIIntArray {
    
private:
    JNIEnv * myEnv;
    jint * buffer;
    int size;
    jintArray array;
    bool allocate;
    
    
public:
    
    JNIIntArray(JNIEnv *env, int s): myEnv(env), buffer(0), size(s), array(0),  allocate(true) {
        buffer = new jint[size];
        memset(buffer, 0, size * sizeof(int));
        array = env->NewIntArray(size);
    }
    
    JNIIntArray(JNIEnv *env, int s, int *target): myEnv(env), buffer(target), size(s), array(0),  allocate(false) {
        array = env->NewIntArray(size);
    }
    
    ~JNIIntArray() {
        if (allocate) {
            delete [] buffer;
        }
        if (array != 0) {
            myEnv->DeleteLocalRef(array);
        }
    }
    
    jint * getBuffer() {
        return buffer;
    }
    
    jintArray getIntArray(bool sync) {
        if (sync) {
            copyToJavaArray();
        }
        return array;
    }
    
    void copyToJavaArray() {
        myEnv->SetIntArrayRegion(array, 0, size, buffer);
    }
};

template <class T> class JNILocalRef {
private:
    JNIEnv * myEnv;
    const T &ref;
    
public:
    JNILocalRef(JNIEnv *env, const T &value): myEnv(env), ref(value) {
    }
    
    ~JNILocalRef() {
        myEnv->DeleteLocalRef(ref);
    }
    
public:
    const T &getValue() const {
        return ref;
    }
};

class JNIString {
    
private:
    JNIEnv * myEnv;
    const jstring & javaString;
    const char * localString;
    
public:
    JNIString(JNIEnv *env, const jstring & string): myEnv(env), javaString(string), localString(0)  {
        if (string) {
            localString = env->GetStringUTFChars(string, 0);
        }
    }
    
    ~JNIString() {
        if (localString != 0) {
            myEnv->ReleaseStringUTFChars(javaString, localString);
            localString = 0;
        }
    }

public:
    const char * getLocalString() {
        return localString;
    }
    
};

class JByteArray {
    
private:
    std::vector<jbyte> buffer;
    
public:
    JByteArray(const int limit) {
        buffer.resize(limit);
        memset(&buffer[0], 0, limit);
    }
    
    ~JByteArray() {
    }
    
    jbyte * getRawBuffer() {
        return &buffer[0];
    }
    
};


class StringUtils {
    
public:
    static bool endsWith(const std::string &str, const std::string &suffix)
    {
        return str.size() >= suffix.size() &&
                str.compare(str.size() - suffix.size(), suffix.size(), suffix) == 0;
    }

    static std::string utf16leto8(const char16_t * utf16)
    {
        std::string utf8;
        unsigned int codepoint = 0;
        for (utf16;  *utf16 != 0;  ++utf16)
        {
            if (*utf16 >= 0xd800 && *utf16 <= 0xdbff)
                codepoint = ((*utf16 - 0xd800) << 10) + 0x10000;
            else
            {
                if (*utf16 >= 0xdc00 && *utf16 <= 0xdfff)
                    codepoint |= *utf16 - 0xdc00;
                else
                    codepoint = *utf16;

                if (codepoint <= 0x7f)
                    utf8.append(1, static_cast<char>(codepoint));
                else if (codepoint <= 0x7ff)
                {
                    utf8.append(1, static_cast<char>(0xc0 | ((codepoint >> 6) & 0x1f)));
                    utf8.append(1, static_cast<char>(0x80 | (codepoint & 0x3f)));
                }
                else if (codepoint <= 0xffff)
                {
                    utf8.append(1, static_cast<char>(0xe0 | ((codepoint >> 12) & 0x0f)));
                    utf8.append(1, static_cast<char>(0x80 | ((codepoint >> 6) & 0x3f)));
                    utf8.append(1, static_cast<char>(0x80 | (codepoint & 0x3f)));
                }
                else
                {
                    utf8.append(1, static_cast<char>(0xf0 | ((codepoint >> 18) & 0x07)));
                    utf8.append(1, static_cast<char>(0x80 | ((codepoint >> 12) & 0x3f)));
                    utf8.append(1, static_cast<char>(0x80 | ((codepoint >> 6) & 0x3f)));
                    utf8.append(1, static_cast<char>(0x80 | (codepoint & 0x3f)));
                }
                codepoint = 0;
            }
        }
        return utf8;
    }

    static std::shared_ptr<_jstring> newLocalString(JNIEnv *env, const jchar *str, int len)
    {
        jstring text = env->NewString(str, len);
        return std::shared_ptr<_jstring>(text, [=](_jstring *s) {
            env->DeleteLocalRef(s);
        });
    }

    static std::shared_ptr<_jstring> newLocalStringUTF(JNIEnv *env, const char *str)
    {
        jstring text = env->NewStringUTF(str);
        return std::shared_ptr<_jstring>(text, [=](_jstring *s) {
            env->DeleteLocalRef(s);
        });
    }
};

class ColorUtils {

public:
    static unsigned char red(int rgba);

    static unsigned char green(int rgba);

    static unsigned char blue(int rgba);

    static unsigned char alpha(int rgba);

    static unsigned char gray(int rgba);

    static unsigned char white(int red, int green, int blue);

    static int argb(int a, int r, int g, int b);

    static int toRed(int gray);

    static int toGreen(int gray);

    static int toBlue(int gray);

    static int toWhite(int white);

};

class DeviceUtils {

public:
    DeviceUtils() {}
    ~DeviceUtils() {}

public:
    static float calculateCount(bool validPage);
    static int random(int min, int max);
    static float calculate(int value);
    static bool isValid(JNIEnv * env);

};


#endif
