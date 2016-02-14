#ifndef JNIUTILS_H_
#define JNIUTILS_H_

#include <jni.h>
#include <android/log.h>


class JNIUtils {

private:
	JNIEnv * myEnv;
	jclass clazz;
	jmethodID methodId;

public:
	JNIUtils(JNIEnv * env, const char * className, const char * method, const char *signature);
	~JNIUtils();

public:
	jclass getClazz() {
		return clazz;
	}

	jmethodID getMethodId() {
		return methodId;
	}

	void invokeStaticMethod(JNIEnv *env, ...);

};

class JNIByteArray {

private:
	JNIEnv * myEnv;
    jbyteArray array;

public:

	JNIByteArray(JNIEnv *env, int size, jbyte * buffer): myEnv(env), array(0) {
	 	array = env->NewByteArray(size);
        env->SetByteArrayRegion(array, 0, size, buffer);

	}
    ~JNIByteArray() {
    	if (array != 0) {
			myEnv->DeleteLocalRef(array);
		}
    }

    jbyteArray getByteArray() {
    	return array;
    }
};

#endif
