#ifndef ONYXDJVUCONTEXT_H
#define ONYXDJVUCONTEXT_H

#include <math.h>
#include <string>

#include <jni.h>

#include <ddjvuapi.h>
#include "JNIUtils.h"

class OnyxDjvuContext
{
public:
    static OnyxDjvuContext *createContext(std::string filePath);

private:
    OnyxDjvuContext(const std::string &filePath, int pageCount,
                ddjvu_context_t *context, ddjvu_document_t *doc);

public:
    virtual ~OnyxDjvuContext();

public:
    int getPageCount();
    bool gotoPage(int pageNum);
    bool getPageSize(int pageNum, std::vector<jfloat> *size);
    bool extractPageText(JNIEnv *env, int pageNum, jobject textChunks);
    bool draw(JNIEnv *env, jobject bitmap, float zoom, int bmpWidth, int bmpHeight,
              int patchX, int patchY, int patchW, int patchH);
    void close();

private:
    std::string filePath_;
    int pageCount_;
    ddjvu_context_t *context_;
    ddjvu_document_t *doc_;
    ddjvu_page_t *currentPage_;
};

#endif // ONYXDJVUCONTEXT_H
