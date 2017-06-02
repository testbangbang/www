#include "com_onyx_zip_ZipDecryption.h"
#include "onyx_zip_file_stream.h"

#include <map>
#include "JNIUtils.h"
#include "log.h"

static const char * zipDecryptionClassName = "com/onyx/zip/ZipDecryption";

static std::map<int, OnyxZipFileStream *> zipFileStreamMap;
static std::map<int, jlong> seekOffsetMap;

static int getHashCode(JNIEnv *env, jobject obj) {
    JNIUtils utils(env);
    utils.findMethod(zipDecryptionClassName, "hashCode", "()I");
    return env->CallIntMethod(obj, utils.getMethodId());
}

static OnyxZipFileStream * findZipFileStream(JNIEnv *env, jobject thiz) {
    std::map<int, OnyxZipFileStream *>::iterator iter = zipFileStreamMap.find(getHashCode(env, thiz));
    OnyxZipFileStream *stream = NULL;
    if (iter != zipFileStreamMap.end()) {
        stream = iter->second;
    }
    return stream;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_zip_ZipDecryption_init
  (JNIEnv *env, jobject thiz, jstring jpath, jstring jpassword) {
    const char *path = NULL;
    const char *password = NULL;
    JNIString pathString(env, jpath);
    path = pathString.getLocalString();
    if (path == NULL) {
        LOGE("invalid path");
        return false;
    }
    JNIString passwordString(env, jpassword);
    password = passwordString.getLocalString();
    LOGI("at ZipDecryption init, path: %s, password: %s", path, password);

    int hashCode = getHashCode(env, thiz);
    OnyxZipFileStream *stream = new OnyxZipFileStream(path, password);
    zipFileStreamMap.insert(std::pair<int, OnyxZipFileStream *>(hashCode, stream));
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_zip_ZipDecryption_open
  (JNIEnv *env, jobject thiz) {
    int hashCode = getHashCode(env, thiz);
    LOGI("at ZipDecryption open, object hash code is: %d", hashCode);
    OnyxZipFileStream *stream = findZipFileStream(env, thiz);
    if (stream != NULL) {
        return stream->open();
    }
    return false;
}

JNIEXPORT jint JNICALL Java_com_onyx_zip_ZipDecryption_size
  (JNIEnv *env, jobject thiz) {
    OnyxZipFileStream *stream = findZipFileStream(env, thiz);
    LOGI("at ZipDecryption size, stream is null? %d", (stream == NULL));
    if (stream != NULL) {
        return stream->getSize();
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_onyx_zip_ZipDecryption_seek
  (JNIEnv *env, jobject thiz, jlong joffset) {
    OnyxZipFileStream *stream = findZipFileStream(env, thiz);
    if (stream != NULL) {
        if (joffset < stream->getSize()) {
            seekOffsetMap.insert(std::pair<int, jlong>(getHashCode(env, thiz), joffset));
            return joffset;
        }
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_onyx_zip_ZipDecryption_readContent
  (JNIEnv *env, jobject thiz, jbyteArray jbuffer, jint jbufOffset, jint jlength) {
    std::map<int, jlong>::iterator iter = seekOffsetMap.find(getHashCode(env, thiz));
    int offset = -1;
    if (iter != seekOffsetMap.end()) {
        offset = iter->second;
    }
    if (offset < 0) {
        return -1;
    }

    OnyxZipFileStream *stream = findZipFileStream(env, thiz);
    if (stream != NULL) {
        JByteArray buffer(jlength);
        stream->requestBytes(offset, (unsigned char *)buffer.getRawBuffer(), jlength);
        env->SetByteArrayRegion(jbuffer, 0, jlength, buffer.getRawBuffer());
    }
    return -1;
}

JNIEXPORT void JNICALL Java_com_onyx_zip_ZipDecryption_closeZip
  (JNIEnv *env, jobject thiz) {
    OnyxZipFileStream *stream = findZipFileStream(env, thiz);
    if (stream != NULL) {
        stream->close();
    }
}
