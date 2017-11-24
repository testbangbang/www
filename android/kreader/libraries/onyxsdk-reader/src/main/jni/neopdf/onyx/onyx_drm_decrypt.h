#ifndef DRM_DECRYPT_H
#define DRM_DECRYPT_H

#include <string>
#include <memory>
#include <vector>

#include "fpdf_onyx_ext.h"

class DrmDecrypt
{
public:
    virtual ~DrmDecrypt() {}

    virtual bool setupWithManifest(const std::string &deviceId,
                                   const std::string &drmCertificate,
                                   const std::vector<unsigned char> &manifest,
                                   const std::string &additionalData) = 0;

    virtual unsigned char *decryptData(const unsigned char *data,
                                       const int dataLen,
                                       size_t *resultLen) = 0;
};

class DrmDecryptManager {
public:
    DrmDecryptManager();
    ~DrmDecryptManager();

    static DrmDecryptManager &singleton();

    void reset();

    bool setupWithManifest(const std::string &deviceId,
                           const std::string &drmCertificate,
                           const std::string &manifestBase64,
                           const std::string &additionalDataBase64);

    bool isEncrypted();

    unsigned char *aesDecrypt(const unsigned char *data,
                              const size_t dataLen,
                              size_t *resultLen);

    IPDF_ONYX_DRMCALLBACK &getDrmCallback();

private:
    static bool isEncryptedStatic();
    static unsigned char *aesDecryptStatic(const unsigned char *data,
                                    const size_t dataLen,
                                    size_t *resultLen);

private:
    bool encrypted;
    size_t drmVersion;
    std::unique_ptr<DrmDecrypt> drmDecrypt;
    IPDF_ONYX_DRMCALLBACK drmCallback;
};

#endif // DRM_DECRYPT_H
