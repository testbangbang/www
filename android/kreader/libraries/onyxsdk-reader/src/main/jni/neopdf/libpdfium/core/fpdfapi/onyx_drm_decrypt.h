#ifndef DRM_DECRYPT_H
#define DRM_DECRYPT_H

#include <string>

namespace onyx {

class DrmDecrypt
{
public:
    DrmDecrypt();
    virtual ~DrmDecrypt();

    unsigned char *rsaDecryptManifest(const unsigned char *key,
                                      const char *manifestBase64String,
                                      int *resultLen);

    unsigned char *aesDecrypt(const char *keyBase64String,
                              const unsigned char *data,
                              const int dataLen,
                              int *resultLen);
};

class DrmDecryptManager {
public:
    DrmDecryptManager();
    ~DrmDecryptManager();

    static DrmDecryptManager &singleton();

    bool isEncrypted();
    void setEncrypted(bool encrypted);

    void setAESKey(const std::string &aesKeyBase64String);

    unsigned char *aesDecrypt(const unsigned char *data,
                              const int dataLen,
                              int *resultLen);

private:
    bool encrypted;
    DrmDecrypt drmDecrypt;
    std::string aesKeyBase64String;
};

}


#endif // DRM_DECRYPT_H
