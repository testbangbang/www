#ifndef ONYXDJVUCONTEXT_H
#define ONYXDJVUCONTEXT_H

#include <math.h>
#include <string>
#include <unordered_map>

#include <jni.h>

#include <ddjvuapi.h>
#include "JNIUtils.h"

class OnyxDjvuContext
{
public:
    static OnyxDjvuContext *createContext(JNIEnv *env, jstring filePath);

private:
    OnyxDjvuContext(char *filePath, int pageCount,
                ddjvu_context_t *context, ddjvu_document_t *doc);

public:
    virtual ~OnyxDjvuContext();

public:
    int getPageCount();
    bool gotoPage(int pageNum);
    bool getPageSize(int pageNum, std::vector<jfloat> *size);
    bool extractPageText(JNIEnv *env, int pageNum, jobject textChunks);
    bool draw(JNIEnv *env, int pageNum, jobject bitmap, float zoom, int bmpWidth, int bmpHeight,
              int patchX, int patchY, int patchW, int patchH);
    void close();

private:
    char *filePath_;
    int pageCount_;
    ddjvu_context_t *context_;
    ddjvu_document_t *doc_;
    std::unordered_map<int, ddjvu_page_t*> pageMap;
};

#endif // ONYXDJVUCONTEXT_H
