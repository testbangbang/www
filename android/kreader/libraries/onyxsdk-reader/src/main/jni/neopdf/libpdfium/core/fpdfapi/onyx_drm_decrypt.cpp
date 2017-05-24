#include "onyx_drm_decrypt.h"

#include <iostream>
#include <string>
#include <vector>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <arpa/inet.h>

#include <openssl/rsa.h>
#include <openssl/engine.h>
#include <openssl/pem.h>
#include <openssl/evp.h>

#include <third_party/zlib_v128/zlib.h>

#include "jsonxx.h"

#define LOGW std::cerr
#define LOGD std::cout

using namespace onyx;

namespace {

static uint32_t crc_32_tab[] =
{ /* CRC polynomial 0xedb88320 */
  0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419, 0x706af48f,
  0xe963a535, 0x9e6495a3, 0x0edb8832, 0x79dcb8a4, 0xe0d5e91e, 0x97d2d988,
  0x09b64c2b, 0x7eb17cbd, 0xe7b82d07, 0x90bf1d91, 0x1db71064, 0x6ab020f2,
  0xf3b97148, 0x84be41de, 0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7,
  0x136c9856, 0x646ba8c0, 0xfd62f97a, 0x8a65c9ec, 0x14015c4f, 0x63066cd9,
  0xfa0f3d63, 0x8d080df5, 0x3b6e20c8, 0x4c69105e, 0xd56041e4, 0xa2677172,
  0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b, 0x35b5a8fa, 0x42b2986c,
  0xdbbbc9d6, 0xacbcf940, 0x32d86ce3, 0x45df5c75, 0xdcd60dcf, 0xabd13d59,
  0x26d930ac, 0x51de003a, 0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423,
  0xcfba9599, 0xb8bda50f, 0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924,
  0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d, 0x76dc4190, 0x01db7106,
  0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f, 0x9fbfe4a5, 0xe8b8d433,
  0x7807c9a2, 0x0f00f934, 0x9609a88e, 0xe10e9818, 0x7f6a0dbb, 0x086d3d2d,
  0x91646c97, 0xe6635c01, 0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e,
  0x6c0695ed, 0x1b01a57b, 0x8208f4c1, 0xf50fc457, 0x65b0d9c6, 0x12b7e950,
  0x8bbeb8ea, 0xfcb9887c, 0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65,
  0x4db26158, 0x3ab551ce, 0xa3bc0074, 0xd4bb30e2, 0x4adfa541, 0x3dd895d7,
  0xa4d1c46d, 0xd3d6f4fb, 0x4369e96a, 0x346ed9fc, 0xad678846, 0xda60b8d0,
  0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9, 0x5005713c, 0x270241aa,
  0xbe0b1010, 0xc90c2086, 0x5768b525, 0x206f85b3, 0xb966d409, 0xce61e49f,
  0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4, 0x59b33d17, 0x2eb40d81,
  0xb7bd5c3b, 0xc0ba6cad, 0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a,
  0xead54739, 0x9dd277af, 0x04db2615, 0x73dc1683, 0xe3630b12, 0x94643b84,
  0x0d6d6a3e, 0x7a6a5aa8, 0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1,
  0xf00f9344, 0x8708a3d2, 0x1e01f268, 0x6906c2fe, 0xf762575d, 0x806567cb,
  0x196c3671, 0x6e6b06e7, 0xfed41b76, 0x89d32be0, 0x10da7a5a, 0x67dd4acc,
  0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5, 0xd6d6a3e8, 0xa1d1937e,
  0x38d8c2c4, 0x4fdff252, 0xd1bb67f1, 0xa6bc5767, 0x3fb506dd, 0x48b2364b,
  0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60, 0xdf60efc3, 0xa867df55,
  0x316e8eef, 0x4669be79, 0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236,
  0xcc0c7795, 0xbb0b4703, 0x220216b9, 0x5505262f, 0xc5ba3bbe, 0xb2bd0b28,
  0x2bb45a92, 0x5cb36a04, 0xc2d7ffa7, 0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d,
  0x9b64c2b0, 0xec63f226, 0x756aa39c, 0x026d930a, 0x9c0906a9, 0xeb0e363f,
  0x72076785, 0x05005713, 0x95bf4a82, 0xe2b87a14, 0x7bb12bae, 0x0cb61b38,
  0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21, 0x86d3d2d4, 0xf1d4e242,
  0x68ddb3f8, 0x1fda836e, 0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777,
  0x88085ae6, 0xff0f6a70, 0x66063bca, 0x11010b5c, 0x8f659eff, 0xf862ae69,
  0x616bffd3, 0x166ccf45, 0xa00ae278, 0xd70dd2ee, 0x4e048354, 0x3903b3c2,
  0xa7672661, 0xd06016f7, 0x4969474d, 0x3e6e77db, 0xaed16a4a, 0xd9d65adc,
  0x40df0b66, 0x37d83bf0, 0xa9bcae53, 0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9,
  0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6, 0xbad03605, 0xcdd70693,
  0x54de5729, 0x23d967bf, 0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94,
  0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d
};

#define UPDC32(octet,crc) (crc_32_tab[((crc)\
     ^ ((uint8_t)octet)) & 0xff] ^ ((crc) >> 8))

uint32_t crc32buf(const unsigned char *buf, size_t len)
{
    uint32_t oldcrc32 = 0xFFFFFFFF;
    for ( ; len; --len, ++buf) {
        oldcrc32 = UPDC32(*buf, oldcrc32);
    }

    return ~oldcrc32;
}

const static char* b64="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/" ;

// maps A=>0,B=>1..
const static unsigned char unb64[]={
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //10
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //20
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //30
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //40
  0,   0,   0,  62,   0,   0,   0,  63,  52,  53, //50
 54,  55,  56,  57,  58,  59,  60,  61,   0,   0, //60
  0,   0,   0,   0,   0,   0,   1,   2,   3,   4, //70
  5,   6,   7,   8,   9,  10,  11,  12,  13,  14, //80
 15,  16,  17,  18,  19,  20,  21,  22,  23,  24, //90
 25,   0,   0,   0,   0,   0,   0,  26,  27,  28, //100
 29,  30,  31,  32,  33,  34,  35,  36,  37,  38, //110
 39,  40,  41,  42,  43,  44,  45,  46,  47,  48, //120
 49,  50,  51,   0,   0,   0,   0,   0,   0,   0, //130
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //140
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //150
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //160
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //170
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //180
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //190
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //200
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //210
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //220
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //230
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //240
  0,   0,   0,   0,   0,   0,   0,   0,   0,   0, //250
  0,   0,   0,   0,   0,   0,
}; // This array has 256 elements

// Converts binary data of length=len to base64 characters.
// Length of the resultant string is stored in flen
// (you must pass pointer flen).
char* base64( const void* binaryData, int len, int *flen )
{
  const unsigned char* bin = (const unsigned char*) binaryData ;
  char* res ;

  int rc = 0 ; // result counter
  int byteNo ; // I need this after the loop

  int modulusLen = len % 3 ;
  int pad = ((modulusLen&1)<<1) + ((modulusLen&2)>>1) ; // 2 gives 1 and 1 gives 2, but 0 gives 0.

  *flen = 4*(len + pad)/3 ;
  res = (char*) malloc( *flen + 1 ) ; // and one for the null
  if( !res )
  {
    puts( "ERROR: base64 could not allocate enough memory." ) ;
    puts( "I must stop because I could not get enough" ) ;
    return 0;
  }

  for( byteNo = 0 ; byteNo <= len-3 ; byteNo+=3 )
  {
    unsigned char BYTE0=bin[byteNo];
    unsigned char BYTE1=bin[byteNo+1];
    unsigned char BYTE2=bin[byteNo+2];
    res[rc++]  = b64[ BYTE0 >> 2 ] ;
    res[rc++]  = b64[ ((0x3&BYTE0)<<4) + (BYTE1 >> 4) ] ;
    res[rc++]  = b64[ ((0x0f&BYTE1)<<2) + (BYTE2>>6) ] ;
    res[rc++]  = b64[ 0x3f&BYTE2 ] ;
  }

  if( pad==2 )
  {
    res[rc++] = b64[ bin[byteNo] >> 2 ] ;
    res[rc++] = b64[ (0x3&bin[byteNo])<<4 ] ;
    res[rc++] = '=';
    res[rc++] = '=';
  }
  else if( pad==1 )
  {
    res[rc++]  = b64[ bin[byteNo] >> 2 ] ;
    res[rc++]  = b64[ ((0x3&bin[byteNo])<<4)   +   (bin[byteNo+1] >> 4) ] ;
    res[rc++]  = b64[ (0x0f&bin[byteNo+1])<<2 ] ;
    res[rc++] = '=';
  }

  res[rc]=0; // NULL TERMINATOR! ;)
  return res ;
}

unsigned char* unbase64( const char* ascii, size_t len, int *flen )
{
  const unsigned char *safeAsciiPtr = (const unsigned char*)ascii ;
  unsigned char *bin ;
  int cb=0;
  int charNo;
  int pad = 0 ;

  if( len < 2 ) { // 2 accesses below would be OOB.
    // catch empty string, return NULL as result.
    puts( "ERROR: You passed an invalid base64 string (too short). You get NULL back." ) ;
    *flen=0;
    return 0 ;
  }
  if( safeAsciiPtr[ len-1 ]=='=' )  ++pad ;
  if( safeAsciiPtr[ len-2 ]=='=' )  ++pad ;

  *flen = 3*len/4 - pad ;
  bin = (unsigned char*)malloc( *flen ) ;
  if( !bin )
  {
    puts( "ERROR: unbase64 could not allocate enough memory." ) ;
    puts( "I must stop because I could not get enough" ) ;
    return 0;
  }

  for( charNo=0; charNo <= len - 4 - pad ; charNo+=4 )
  {
    int A=unb64[safeAsciiPtr[charNo]];
    int B=unb64[safeAsciiPtr[charNo+1]];
    int C=unb64[safeAsciiPtr[charNo+2]];
    int D=unb64[safeAsciiPtr[charNo+3]];

    bin[cb++] = (A<<2) | (B>>4) ;
    bin[cb++] = (B<<4) | (C>>2) ;
    bin[cb++] = (C<<6) | (D) ;
  }

  if( pad==1 )
  {
    int A=unb64[safeAsciiPtr[charNo]];
    int B=unb64[safeAsciiPtr[charNo+1]];
    int C=unb64[safeAsciiPtr[charNo+2]];

    bin[cb++] = (A<<2) | (B>>4) ;
    bin[cb++] = (B<<4) | (C>>2) ;
  }
  else if( pad==2 )
  {
    int A=unb64[safeAsciiPtr[charNo]];
    int B=unb64[safeAsciiPtr[charNo+1]];

    bin[cb++] = (A<<2) | (B>>4) ;
  }

  return bin ;
}

RSA* loadPublicKeyFromString(const char* publicKeyStr)
{
    BIO *bio = BIO_new_mem_buf(static_cast<const void *>(publicKeyStr), -1);
    RSA* rsaPublicKey = PEM_read_bio_RSA_PUBKEY(bio, NULL, NULL, NULL) ;
    if (!rsaPublicKey) {
        LOGW << "ERROR: Could not load public KEY! PEM_read_bio_RSAPublicKey FAILED: " <<
                ERR_error_string(ERR_get_error(), NULL) << std::endl;
    }

    BIO_free(bio) ;
    return rsaPublicKey ;
}

unsigned char* rsaDecrypt(RSA *publicKey, const unsigned char* encryptedData, int dataLen, size_t *outLen)
{
    const int PADDING = RSA_PKCS1_PADDING;

    int rsaLen = RSA_size(publicKey) ; // That's how many bytes the decrypted data would be

    unsigned char *decryptedBin = (unsigned char*)malloc(dataLen);
    int encOffset = 0;
    for (;;) {
        if (encOffset >= dataLen) {
            break;
        }
        int result = RSA_public_decrypt(rsaLen, encryptedData + encOffset,
                                         decryptedBin + *outLen, publicKey, PADDING);
        if(result == -1) {
            LOGW << "ERROR: RSA_public_decrypt: " << ERR_error_string(ERR_get_error(), NULL) << std::endl;
            free(decryptedBin);
            return NULL;
        }
        *outLen += static_cast<size_t>(result);
        encOffset += rsaLen;
    }

    return decryptedBin;
}

unsigned char* rsaDecryptThisBase64(RSA *privKey, const char* base64String, int inLen, size_t *outLen)
{
  int encBinLen;
  unsigned char* encBin = unbase64(base64String, inLen, &encBinLen);

  // rsaDecrypt assumes length of encBin based on privKey
  unsigned char *decryptedBin = rsaDecrypt(privKey, encBin, encBinLen, outLen) ;
  free(encBin) ;

  return decryptedBin;
}

unsigned char *aesDecrypt(const unsigned char *key, const unsigned char *data, int dataLen, int *outLen)
{
    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    EVP_CIPHER_CTX_init(ctx);

    int ret = EVP_DecryptInit_ex(ctx, EVP_aes_128_ecb(), NULL, key, NULL);
    if (ret != 1) {
        LOGW << "EVP_DecryptUpdate failed!" << std::endl;
        EVP_CIPHER_CTX_free(ctx);
        return NULL;
    }

    unsigned char* result = new unsigned char[dataLen];

    int len1 = 0;
    ret = EVP_DecryptUpdate(ctx, result, &len1, data, dataLen);
    if (ret != 1) {
        LOGW << "EVP_DecryptUpdate failed!" << std::endl;
        EVP_CIPHER_CTX_free(ctx);
        delete result;
        return NULL;
    }

    int len2 = 0;
    ret = EVP_DecryptFinal_ex(ctx, result+len1, &len2);
    if (ret != 1) {
        LOGW << "EVP_DecryptFinal_ex failed!" << std::endl;
        EVP_CIPHER_CTX_free(ctx);
        delete result;
        return NULL;
    }

    ret = EVP_CIPHER_CTX_cleanup(ctx);
    if (ret != 1) {
        LOGW << "EVP_CIPHER_CTX_cleanup failed!";
        EVP_CIPHER_CTX_free(ctx);
        delete result;
        return NULL;
    }

    EVP_CIPHER_CTX_free(ctx);

    *outLen = len1 + len2;
    return result;
}

unsigned char *rsaDecryptBase64WithPublicKey(const char *key,
                                       const char *dataBase64,
                                       const size_t dataLen,
                                       size_t *resultLen) {
    RSA* publicKey = loadPublicKeyFromString(key);
    if (!publicKey) {
        return nullptr;
    }

    unsigned char *result = rsaDecryptThisBase64(publicKey, dataBase64,
                                                 static_cast<int>(dataLen),
                                                 resultLen);

    RSA_free(publicKey);
    return result;
}

unsigned char *rsaDecryptWithPublicKey(const char *key,
                                       const unsigned char *data,
                                       const size_t dataLen,
                                       size_t *resultLen) {
    RSA* publicKey = loadPublicKeyFromString(key);
    if (!publicKey) {
        return nullptr;
    }

    unsigned char *result = rsaDecrypt(publicKey,
                                       data,
                                       static_cast<int>(dataLen),
                                       resultLen);

    RSA_free(publicKey);
    return result;
}

bool inflateZip(const unsigned char *src, size_t srcLen, std::vector<unsigned char> *result) {
    z_stream zs;
    memset(&zs, 0, sizeof(zs));

    int ret = inflateInit2(&zs, MAX_WBITS + 16); // tells zlib to to detect if using gzip or zlib
    if (ret != Z_OK) {
        LOGW << "init zlib failed: " << ret << std::endl;
        return false;
    }

    zs.next_in = const_cast<Bytef*>(src);
    zs.avail_in = srcLen;

    unsigned char buf[4 * 4096];
    do {
        zs.next_out = reinterpret_cast<Bytef*>(buf);
        zs.avail_out = sizeof(buf);

        ret = inflate(&zs, Z_NO_FLUSH);
        if (result->size() < zs.total_out) {
            result->insert(result->end(), buf, buf + sizeof(buf));
        }
    } while (ret == Z_OK);

    inflateEnd(&zs);

    if (ret != Z_STREAM_END) {
        LOGW << "zlib inflate failed: " << ret << std::endl;
        return false;
    }

    return true;
}

const int MANIFEST_HEADER_LENGTH = 512;

}

class DrmDecryptV1 : public DrmDecrypt {

    ~DrmDecryptV1() {
    }

    bool setupWithManifest(const std::string &deviceId,
                           const std::string &drmCertificate,
                           const std::vector<unsigned char> &manifest,
                           const std::string &additionalData)
    {
        const char *manifestBody = reinterpret_cast<const char *>(manifest.data() + MANIFEST_HEADER_LENGTH);
        size_t dataLen = manifest.size() - MANIFEST_HEADER_LENGTH;

        size_t resultLen = 0;
        unsigned char *result = rsaDecryptBase64WithPublicKey(drmCertificate.c_str(), manifestBody, dataLen, &resultLen);
        if (!result) {
            LOGW << "invalid metadata!" << std::endl;
            return false;
        }

        jsonxx::Object object;
        bool succ = object.parse(reinterpret_cast<char *>(result));
        free(result);
        if (!succ) {
            LOGW << "invalid metadata!" << std::endl;
            return false;
        }

        if (!object.has<std::string>("publish") || !object.has<std::string>("stamp")) {
            LOGW << "invalid metadata!" << std::endl;
            return false;
        }
        std::string aesKey = object.get<std::string>("publish");
        std::string aesGuard = object.get<std::string>("stamp");

        int keyLen;
        unsigned char *key = nullptr;
        key = computeKeyFromGuard(aesKey, aesGuard, &keyLen);
        if (!key) {
            LOGW << "invalid metadata!" << std::endl;
            return false;
        }
        std::vector<unsigned char> vec(key, key + keyLen);
        free(key);

        this->aesKey.assign(vec.begin(), vec.end());

        return true;
    }

    unsigned char *decryptData(const unsigned char *data,
                               const int dataLen,
                               size_t *resultLen)
    {
        unsigned char *decrypted = aesDecrypt(aesKey.data(), data, dataLen,
                                              reinterpret_cast<int *>(resultLen));
        if (!decrypted) {
            return nullptr;
        }

        unsigned char *result = static_cast<unsigned char *>(calloc(*resultLen, sizeof(unsigned char)));
        if (!result) {
            free(decrypted);
            return nullptr;
        }
        memcpy(result, decrypted, *resultLen);
        free(decrypted);

        return result;
    }

private:
    unsigned char *computeKeyFromGuard(const std::string &cipherBase64,
                                          const std::string &guardBase64,
                                          int *resultLen) {
        int keyLen;
        unsigned char* cipher = unbase64(cipherBase64.c_str(), cipherBase64.length(), &keyLen);

        int guardLen;
        unsigned char* guard = unbase64(guardBase64.c_str(), guardBase64.length(), &guardLen);

        unsigned char digest[SHA512_DIGEST_LENGTH];

        SHA512_CTX ctx;
        SHA512_Init(&ctx);
        SHA512_Update(&ctx, guard, guardLen);
        SHA512_Final(digest, &ctx);

        unsigned char guardKey[16];
        memcpy(guardKey, &digest[9], 4);
        memcpy(&guardKey[4], &digest[18], 4);
        memcpy(&guardKey[8], &digest[24], 4);
        memcpy(&guardKey[12], &digest[35], 4);

        unsigned char *decrypted = ::aesDecrypt(guardKey, cipher, keyLen, resultLen);
        free(cipher);
        free(guard);

        return decrypted;
    }

private:
    std::vector<unsigned char> aesKey;

};

class DrmDecryptV2 : public DrmDecrypt {

    ~DrmDecryptV2() {
    }

    bool setupWithManifest(const std::string &deviceId,
                           const std::string &drmCertificate,
                           const std::vector<unsigned char> &manifest,
                           const std::string &additionalData)
    {
        size_t bodyLen = 0;
        if (!checkManifest(manifest, &bodyLen)) {
            return false;
        }

        std::vector<unsigned char> inflated;
        if (!decryptManifestBody(drmCertificate, additionalData, manifest, bodyLen, &inflated)) {
            LOGW << "invalid metadata!" << std::endl;
            return false;
        }

        if (!computeKeyFromManifestBody(deviceId, inflated)) {
            LOGW << "invalid metadata!" << std::endl;
            return false;
        }

        return true;
    }

    unsigned char *decryptData(const unsigned char *data,
                               const int dataLen,
                               size_t *resultLen)
    {
        unsigned char *decrypted = aesDecrypt(aesKey.data(), data, dataLen,
                                              reinterpret_cast<int *>(resultLen));
        if (!decrypted) {
            return nullptr;
        }

        unsigned char *result = static_cast<unsigned char *>(calloc(*resultLen, sizeof(unsigned char)));
        if (!result) {
            free(decrypted);
            return nullptr;
        }
        memcpy(result, decrypted, *resultLen);
        free(decrypted);

        return result;
    }

private:
    bool checkManifest(const std::vector<unsigned char> &manifest, size_t *bodyLength) {
        // CRC32 is represented as long in java, which takes 8 bytes,
        // so we will ignore first 4 bytes to get real CRC32 value
        const int CRC32_HEADER_LENGTH = 8;

        uint32_t headerCrc32 = 0;
        memcpy(&headerCrc32, &manifest[4], 4);
        headerCrc32 = ntohl(headerCrc32);

        uint32_t headerChecksum = crc32buf(&manifest[8], MANIFEST_HEADER_LENGTH - CRC32_HEADER_LENGTH);
        if (headerCrc32 != headerChecksum) {
            LOGW << "invalid header checksum!" << std::endl;
            return false;
        }

        uint32_t bodyLen = 0;
        memcpy(&bodyLen, &manifest[504], 4);
        bodyLen = ntohl(bodyLen);

        const size_t totalLen = MANIFEST_HEADER_LENGTH + bodyLen + CRC32_HEADER_LENGTH;
        if (totalLen != manifest.size()) {
            LOGW << "invalid manifest length!" << std::endl;
            return false;
        }

        const unsigned char *manifestBody = manifest.data() + MANIFEST_HEADER_LENGTH;

        uint32_t bodyCrc32 = 0;
        memcpy(&bodyCrc32, &manifest[MANIFEST_HEADER_LENGTH + bodyLen + 4], 4);
        bodyCrc32 = ntohl(bodyCrc32);

        uint32_t bodyChecksum = crc32buf(manifestBody, bodyLen);
        if (bodyCrc32 != bodyChecksum) {
            LOGW << "invalid body checksum!" << std::endl;
            return false;
        }

        *bodyLength = bodyLen;
        return true;
    }

    bool decryptManifestBody(const std::string &drmCertificate,
                             const std::string &additionalData,
                             const std::vector<unsigned char> &manifest,
                             const size_t bodyLength,
                             std::vector<unsigned char> *body) {
        std::string key;
        if (!getKeyFromCertificate(drmCertificate, additionalData, &key)) {
            return false;
        }

        const unsigned char *manifestBody = manifest.data() + MANIFEST_HEADER_LENGTH;

        size_t resultLen = 0;
        unsigned char *result = rsaDecryptWithPublicKey(key.c_str(), manifestBody, bodyLength, &resultLen);
        if (!result) {
            return false;
        }

        if (!inflateZip(result, resultLen, body)) {
            return false;
        }
        free(result);

        return true;
    }

    bool getKeyFromCertificate(const std::string &drmCertificate,
                               const std::string &additionalData,
                               std::string *key) {
        jsonxx::Object additionObj;
        if (additionalData.length() <= 0 || !additionObj.parse(additionalData) || !additionObj.has<std::string>("group")) {
            // to be compatible with DRM without group limitation
            jsonxx::Object certObj;
            if (!certObj.parse(drmCertificate)) {
                *key = drmCertificate;
                return true;
            }

            if (certObj.kv_map().size() <= 0) {
                return false;
            }

            auto find = certObj.kv_map().find("default");
            if (find != certObj.kv_map().end()) {
                *key = find->second->get<std::string>();
            } else {
                *key = certObj.kv_map().begin()->second->get<std::string>();
            }
            return true;
        }

        std::string group = additionObj.get<std::string>("group");

        jsonxx::Object certObj;
        if (!certObj.parse(drmCertificate)) {
            return false;
        }

        for (auto pair : certObj.kv_map()) {
            if (group.compare(pair.first) == 0) {
                *key = pair.second->get<std::string>();
                return true;
            }
        }
        return false;
    }

    unsigned char *computeKeyFromGuard(const std::string &cipherBase64,
                                          const std::string &guardBase64,
                                          int *resultLen) {
        int keyLen;
        unsigned char* cipher = unbase64(cipherBase64.c_str(), cipherBase64.length(), &keyLen);

        int guardLen;
        unsigned char* guard = unbase64(guardBase64.c_str(), guardBase64.length(), &guardLen);

        unsigned char digest[SHA512_DIGEST_LENGTH];

        SHA512_CTX ctx;
        SHA512_Init(&ctx);
        SHA512_Update(&ctx, guard, guardLen);
        SHA512_Final(digest, &ctx);

        unsigned char guardKey[16];
        memcpy(guardKey, &digest[9], 4);
        memcpy(&guardKey[4], &digest[18], 4);
        memcpy(&guardKey[8], &digest[24], 4);
        memcpy(&guardKey[12], &digest[35], 4);

        unsigned char *decrypted = ::aesDecrypt(guardKey, cipher, keyLen, resultLen);
        free(cipher);
        free(guard);

        return decrypted;
    }

    bool computeKeyFromManifestBody(const std::string &deviceId,
                                    std::vector<unsigned char> body) {
        jsonxx::Object object;
        bool succ = object.parse(reinterpret_cast<char *>(body.data()));
        if (!succ) {
            return false;
        }

        // in v2, device id is mac address
        if (object.has<jsonxx::Array>("macs")) {
            bool found = false;
            jsonxx::Array macs = object.get<jsonxx::Array>("macs");
            for (size_t i = 0; i < macs.size(); i++) {
                std::string mac = macs.get<std::string>(i);
                if (mac.compare(deviceId) == 0) {
                    found = true;
                    break;
                }
            }
            if (!found && macs.size() > 0) {
                return false;
            }
        }

        if (!object.has<std::string>("publish") || !object.has<std::string>("stamp")) {
            return false;
        }
        std::string aesKey = object.get<std::string>("publish");
        std::string aesGuard = object.get<std::string>("stamp");

        int keyLen;
        unsigned char *key = nullptr;
        key = computeKeyFromGuard(aesKey, aesGuard, &keyLen);
        if (!key) {
            return false;
        }
        std::vector<unsigned char> vec(key, key + keyLen);
        free(key);

        this->aesKey.assign(vec.begin(), vec.end());
        return true;
    }

private:
    std::vector<unsigned char> aesKey;

};

DrmDecryptManager::DrmDecryptManager()
    : encrypted(false), drmVersion(0), drmDecrypt(nullptr)
{

}

DrmDecryptManager::~DrmDecryptManager()
{

}

DrmDecryptManager &DrmDecryptManager::singleton()
{
    static DrmDecryptManager instance;
    return instance;
}

void DrmDecryptManager::reset()
{
    encrypted = false;
    drmVersion = 0;
    drmDecrypt.reset(nullptr);
}

bool DrmDecryptManager::setupWithManifest(const std::string &deviceId,
                                          const std::string &drmCertificate,
                                          const std::string &manifestBase64,
                                          const std::string &additionalDataBase64)
{
    int len = 0;
    unsigned char *data = unbase64(manifestBase64.c_str(), manifestBase64.length(), &len);
    std::vector<unsigned char> manifest(data, data + len);
    free(data);

    len = 0;
    data = unbase64(additionalDataBase64.c_str(), additionalDataBase64.length(), &len);
    std::string additionalData(reinterpret_cast<char *>(data));
    free(data);

    uint32_t version = 0;
    memcpy(&version, &manifest[508], 4);
    version = ntohl(version);

    if (version == 1) {
        drmDecrypt.reset(new DrmDecryptV1());
    } else if (version == 2) {
        drmDecrypt.reset(new DrmDecryptV2());
    } else {
        LOGW << "invalid manifest version: " << version << std::endl;
        return false;
    }

    if (!drmDecrypt->setupWithManifest(deviceId, drmCertificate, manifest, additionalData)) {
        drmDecrypt.reset(nullptr);
        return false;
    }

    encrypted = true;
    drmVersion = version;
    return true;
}

bool DrmDecryptManager::isEncrypted()
{
    return encrypted;
}

unsigned char *DrmDecryptManager::aesDecrypt(const unsigned char *data, const size_t dataLen, size_t *resultLen)
{
    return drmDecrypt->decryptData(data, dataLen, resultLen);
}
