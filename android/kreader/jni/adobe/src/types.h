

#ifndef ONYX_PDF_TYPES_H_
#define ONYX_PDF_TYPES_H_

#include <stdio.h>
#include <string.h>
#include <vector>
#include <string>

#include <android/bitmap.h>
#include <android/log.h>

#define LOG_TAG "libonyx_adobe"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGT(...) __android_log_print(ANDROID_LOG_INFO,"alert",__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef std::vector<unsigned char> QByteArray;
typedef std::string QString;
typedef unsigned int QRgb;
typedef unsigned char uchar;


namespace onyx
{

class QSizeF {
public:
    QSizeF() {}
    QSizeF(const float w, const float h) : width(w), height(h) {}
    float width, height;

};

class QRect {
public:
    int x, y, width, height;
};




}

#endif // ONYX_PDF_TYPES_H_
