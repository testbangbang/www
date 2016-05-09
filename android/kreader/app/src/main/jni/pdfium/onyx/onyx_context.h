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
#include <unordered_map>



#ifdef NDK_PROFILER
#include "prof.h"
#endif

#include "plugin_context_holder.h"

#include "log.h"
#include "com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper.h"
#include "fpdf_doc.h"
#include "fpdfview.h"
#include "fpdf_text.h"

class OnyxPdfiumPage {

private:
    FPDF_PAGE page;
    FPDF_TEXTPAGE textPage;

public:
    OnyxPdfiumPage(FPDF_DOCUMENT document, int pageIndex) : page(NULL), textPage(NULL) {
        if (document != NULL) {
            page = FPDF_LoadPage(document, pageIndex);
        }
    }

    ~OnyxPdfiumPage() {
        if (textPage != NULL) {
            FPDFText_ClosePage(textPage);
            textPage = NULL;
        }
        if (page != NULL) {
            FPDF_ClosePage(page);
            page = NULL;
        }
    }

    FPDF_PAGE getPage() {
        return page;
    }

    FPDF_TEXTPAGE getTextPage() {
        if (textPage == NULL && page != NULL) {
            textPage = FPDFText_LoadPage(page);
        }
        return textPage;
    }

};

class OnyxPdfiumContext {

private:
    FPDF_DOCUMENT document;
    FPDF_BITMAP bitmap;
    std::unordered_map<int, OnyxPdfiumPage *> pageMap;

public:
    OnyxPdfiumContext(FPDF_DOCUMENT doc)
        : document(doc)
        , bitmap(NULL) {
    }
    ~OnyxPdfiumContext() {
        document = NULL;
        if (bitmap != NULL) {
            FPDFBitmap_Destroy(bitmap);
            bitmap = NULL;
        }
        clearPages();
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

    OnyxPdfiumPage * getPdfiumPage(int pageIndex) {
        std::unordered_map<int, OnyxPdfiumPage *>::iterator iterator = pageMap.find(pageIndex);
        OnyxPdfiumPage * page = NULL;
        if (iterator == pageMap.end()) {
            page = new OnyxPdfiumPage(getDocument(), pageIndex);
            pageMap[pageIndex] = page;
        } else {
            page = iterator->second;
        }
        return page;
    }

    FPDF_PAGE getPage(int pageIndex) {
        OnyxPdfiumPage * page = getPdfiumPage(pageIndex);
        return page->getPage();
    }

    FPDF_TEXTPAGE getTextPage(int pageIndex) {
        OnyxPdfiumPage * page = getPdfiumPage(pageIndex);
        return page->getTextPage();
    }

private:
    void clearPages() {
        for(std::unordered_map<int, OnyxPdfiumPage *>::iterator iterator = pageMap.begin(); iterator != pageMap.end(); ++iterator) {
            delete iterator->second;
        }
        pageMap.clear();
    }
};


class OnyxPdfiumManager {

private:
    static PluginContextHolder<OnyxPdfiumContext> contextHolder;

public:
    static OnyxPdfiumContext * getContext(JNIEnv *env, jint id);
    static OnyxPdfiumContext * createContext(JNIEnv *env, jint id, FPDF_DOCUMENT document);
    static void releaseContext(JNIEnv *env, jint id);

    static FPDF_DOCUMENT getDocument(JNIEnv *env, jint id) {
        OnyxPdfiumContext * context = getContext(env, id);
        if (context == NULL) {
            return NULL;
        }
        return context->getDocument();
    }

    static FPDF_BITMAP getBitmap(JNIEnv *env, jint id, int width, int height, void * pixels, int stride) {
        OnyxPdfiumContext * context = getContext(env, id);
        if (context == NULL) {
            return NULL;
        }
        return context->getBitmap(width, height, pixels, stride);
    }

    static FPDF_PAGE getPage(JNIEnv *env, jint id, int pageIndex) {
        OnyxPdfiumContext * context = getContext(env, id);
        if (context == NULL) {
            return NULL;
        }
        return context->getPage(pageIndex);
    }

    static FPDF_PAGE getTextPage(JNIEnv *env, jint id, int pageIndex) {
        OnyxPdfiumContext * context = getContext(env, id);
        if (context == NULL) {
            return NULL;
        }
        return context->getTextPage(pageIndex);
    }

};


#endif

