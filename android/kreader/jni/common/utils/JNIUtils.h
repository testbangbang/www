#ifndef JNIUTILS_H_
#define JNIUTILS_H_

#include <stdio.h>
#include <stdlib.h>

#include <vector>
#include <string>

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

class JNIString {

private:
	JNIEnv * myEnv;
	const jstring & javaString;
   	const char * localString;

public:
	JNIString(JNIEnv *env, const jstring & string): myEnv(env), javaString(string), localString(0)  {
		localString = env->GetStringUTFChars(string, 0);
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
};

#endif
