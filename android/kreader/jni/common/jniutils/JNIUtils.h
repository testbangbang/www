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
    jbyte * buffer;
    int size;
    jbyteArray array;


public:

	JNIByteArray(JNIEnv *env, int s): myEnv(env), buffer(0), size(s), array(0) {
		buffer = new jbyte[size];
		array = env->NewByteArray(size);
	}

    ~JNIByteArray() {
    	if (buffer != 0) {
    		delete [] buffer;
    	}
    	if (array != 0) {
			myEnv->DeleteLocalRef(array);
		}
    }

    jbyte * getBuffer() {
    	return buffer;
    }

    jbyteArray getByteArray() {
    	return array;
    }

    void copyToJavaArray() {
		myEnv->SetByteArrayRegion(array, 0, size, buffer);
    }
};

#endif
