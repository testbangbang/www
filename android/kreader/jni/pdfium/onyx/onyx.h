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
#include "fpdfview.h"


class OnyxPdfiumContext;

class OnyxPdfiumContext {

private:
    static std::map<jobject, OnyxPdfiumContext *> contextMap;
    FPDF_DOCUMENT document;

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

public:
    OnyxPdfiumContext() : document(NULL) {
    }

public:
    FPDF_DOCUMENT getDocument() {
        return document;
    }
};


#endif

