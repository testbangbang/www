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
    OnyxPdfiumPage(FPDF_DOCUMENT document, int pageIndex, bool loadTextPage) : page(NULL), textPage(NULL) {
        if (document != NULL) {
            page = FPDF_LoadPage(document, pageIndex);
            if (loadTextPage && page != NULL) {
                textPage = FPDFText_LoadPage(page);
            }
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
        return textPage;
    }

};

class OnyxPdfiumContext;

class OnyxPdfiumContext {

private:
    static std::map<jobject, OnyxPdfiumContext *> contextMap;
    FPDF_DOCUMENT document;
    FPDF_BITMAP bitmap;
    std::unordered_map<int, OnyxPdfiumPage *> pageMap;

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

    static FPDF_PAGE getPage(jobject thiz, int pageIndex) {
        OnyxPdfiumContext * context = getContext(thiz);
        if (context == NULL) {
            return NULL;
        }
        return context->getPage(pageIndex);
    }

    static FPDF_PAGE getTextPage(jobject thiz, int pageIndex) {
        OnyxPdfiumContext * context = getContext(thiz);
        if (context == NULL) {
            return NULL;
        }
        return context->getTextPage(pageIndex);
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
        // clear page hash map.
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
            page = new OnyxPdfiumPage(getDocument(), pageIndex, true);
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

};



#endif

