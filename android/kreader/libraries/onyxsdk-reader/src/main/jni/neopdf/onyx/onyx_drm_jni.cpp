#include "com_onyx_android_sdk_reader_utils_OnyxDrmUtils.h"

#include "JNIUtils.h"

#include <fpdf_onyx_ext.h>
#include "onyx_drm_decrypt.h"

#include <android/log.h>

#define LOG_TAG "neo_drm"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGT(...) __android_log_print(ANDROID_LOG_INFO,"alert",__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_OnyxDrmUtils_setup
  (JNIEnv *env, jclass, jstring deviceId, jstring drmCertificate, jstring manifestBase64, jstring additionalDataBase64) {
    std::string id = "";
    std::string certificate = "";
    std::string manifest = "";
    std::string oad = "";

    if (deviceId) {
        JNIString strDeviceId(env, deviceId);
        id = strDeviceId.getLocalString();
    }
    if (drmCertificate) {
        JNIString strCertificate(env, drmCertificate);
        certificate = strCertificate.getLocalString();
    }
    if (manifestBase64) {
        JNIString strManifest(env, manifestBase64);
        manifest = strManifest.getLocalString();
    }
    if (additionalDataBase64) {
        JNIString strOad(env, additionalDataBase64);
        oad = strOad.getLocalString();
    }

    return static_cast<jboolean>(DrmDecryptManager::singleton().setupWithManifest(id.c_str(), certificate.c_str(), manifest.c_str(), oad.c_str()));
}

JNIEXPORT jint JNICALL Java_com_onyx_android_sdk_reader_utils_OnyxDrmUtils_decrypt
  (JNIEnv *env, jclass, jbyteArray encryptedData, jint encryptedSize, jbyteArray result) {
    if (!DrmDecryptManager::singleton().isEncrypted()) {
        LOGE("doc is not DRM protected!");
        return 0;
    }

    jsize len = env->GetArrayLength(encryptedData);
    JNIByteArray array(env, static_cast<size_t>(len));
    env->GetByteArrayRegion(encryptedData, 0, len, array.getBuffer());

    size_t resultLen = 0;
    unsigned char *dec = DrmDecryptManager::singleton().aesDecrypt(reinterpret_cast<unsigned char *>(array.getBuffer()),
                                                                   static_cast<size_t>(encryptedSize),
                                                                   &resultLen);
    if (!dec) {
        LOGE("DRM decrypt failed!");
        return 0;
    }

    jsize resultSize = env->GetArrayLength(result);
    if (resultSize < static_cast<jsize>(resultLen)) {
        LOGE("DRM decrypt out of result range!");
        return 0;
    }

    env->SetByteArrayRegion(result, 0, static_cast<jsize>(resultLen), reinterpret_cast<jbyte *>(dec));
    free(dec);

    return static_cast<jint>(resultLen);
}

