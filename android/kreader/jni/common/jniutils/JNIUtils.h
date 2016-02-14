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
	JNIUtils(JNIEnv * env);
	~JNIUtils();

public:
	bool findMethod(const char * className, const char * method, const char *signature);
	bool findStaticMethod(const char * className, const char * method, const char *signature);

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
	bool allocate;


public:

	JNIByteArray(JNIEnv *env, int s): myEnv(env), buffer(0), size(s), array(0), allocate(true) {
		buffer = new jbyte[size];
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

    jbyteArray getByteArray(bool sync) {
    	if (sync) {
    		copyToJavaArray();
    	}
    	return array;
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

#endif
