#ifndef ONYX_PDFIUM_H_
#define ONYX_PDFIUM_H_

#include <jni.h>
#include <time.h>
#include <pthread.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <list>
#include <map>

#ifdef NDK_PROFILER
#include "prof.h"
#endif

#include "log.h"
#include "com_onyx_reader_plugins_pdfium_PdfiumJniWrapper.h"
#include "fpdfdoc.h"
#include "fpdfview.h"


class OnyxPdfiumContext;

class OnyxPdfiumContext {

private:
    static std::map<jobject, OnyxPdfiumContext *> contextMap;
    FPDF_DOCUMENT document;
    FPDF_BITMAP bitmap;

public:
    static OnyxPdfiumContext * getContext(jobject thiz);
    static OnyxPdfiumContext * createContext(jobject thiz, FPDF_DOCUMENT document);
    static void releaseContext(jobject thiz);

    static FPDF_DOCUMENT getDocument(jobject thiz) {
        OnyxPdfiumContext * context = getContext(thiz);
        if (context == NULL) {
            return NULL;
        }
        return context->getDocument();
    }

    static FPDF_BITMAP getBitmap(jobject thiz, int width, int height, void * pixels, int stride) {
        OnyxPdfiumContext * context = getContext(thiz);
        if (context == NULL) {
            return NULL;
        }
        return context->getBitmap(width, height, pixels, stride);
    }

public:
    OnyxPdfiumContext()
        : document(NULL)
        , bitmap(NULL) {
    }
    ~OnyxPdfiumContext() {
        if (bitmap != NULL) {
            FPDFBitmap_Destroy(bitmap);
            bitmap = NULL;
        }
    }

public:
    FPDF_DOCUMENT getDocument() {
        return document;
    }

    FPDF_BITMAP getBitmap(int width, int height, void * pixels, int stride) {
        if (bitmap == NULL) {
            bitmap = FPDFBitmap_CreateEx(width, height, FPDFBitmap_BGRA, pixels, stride);
        }
        return bitmap;
    }

};


#endif

