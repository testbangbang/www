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
#include <queue>

#ifdef NDK_PROFILER
#include "prof.h"
#endif

#include "plugin_context_holder.h"

#include "log.h"
#include "com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper.h"
#include "fpdf_doc.h"
#include "fpdfview.h"
#include "fpdf_text.h"
#include "fpdf_formfill.h"

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
    FPDF_FORMHANDLE formHandle;
    FPDF_BITMAP bitmap;
    std::list<std::pair<int, OnyxPdfiumPage *>> pageQueue; // use std::queue to simulate FIFO queue
    std::unordered_map<void *, FPDF_BITMAP> bitmapMap;

public:
    OnyxPdfiumContext(FPDF_DOCUMENT doc, FPDF_FORMHANDLE formHandle)
        : document(doc), formHandle(formHandle) {
    }
    ~OnyxPdfiumContext() {
        document = NULL;
        formHandle = NULL;
        clearBitmaps();
        clearPages();
    }

public:
    FPDF_DOCUMENT getDocument() {
        return document;
    }

    FPDF_FORMHANDLE getFormHandle() {
        return formHandle;
    }

    FPDF_BITMAP getBitmap(int width, int height, void * pixels, int stride) {
        FPDF_BITMAP bitmap = NULL;
        std::unordered_map<void *, FPDF_BITMAP>::iterator iterator = bitmapMap.find(pixels);
        if (iterator != bitmapMap.end()) {
            bitmap = iterator->second;
            if (FPDFBitmap_GetWidth(bitmap) != width || FPDFBitmap_GetHeight(bitmap) != height) {
                FPDFBitmap_Destroy(iterator->second);
                bitmapMap.erase(iterator);
                iterator = bitmapMap.end();
            }
        }
        if (iterator == bitmapMap.end()) {
            bitmap = FPDFBitmap_CreateEx(width, height, FPDFBitmap_BGRA, pixels, stride);
            bitmapMap[pixels] = bitmap;
        }
        return bitmap;
    }

    OnyxPdfiumPage * getPdfiumPage(int pageIndex) {
        auto iterator = pageQueue.begin();
        while (iterator != pageQueue.end() && iterator->first != pageIndex) {
            ++iterator;
        }
        OnyxPdfiumPage * page = NULL;
        if (iterator == pageQueue.end()) {
            page = new OnyxPdfiumPage(getDocument(), pageIndex);
            pageQueue.push_back(std::pair<int, OnyxPdfiumPage*>(pageIndex, page));
            const int MAX_QUEUE_SIZE = 3;
            if (pageQueue.size() > MAX_QUEUE_SIZE) {
                delete pageQueue.front().second;
                pageQueue.pop_front();
            }
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
        for(auto iterator = pageQueue.begin(); iterator != pageQueue.end(); ++iterator) {
            delete iterator->second;
        }
        pageQueue.clear();
    }

    void clearBitmaps() {
        for(std::unordered_map<void *, FPDF_BITMAP>::iterator iterator = bitmapMap.begin(); iterator != bitmapMap.end(); ++iterator) {
            FPDFBitmap_Destroy(iterator->second);
        }
        bitmapMap.clear();
    }
};


class OnyxPdfiumManager {

private:
    static PluginContextHolder<OnyxPdfiumContext> contextHolder;

public:
    static OnyxPdfiumContext * getContext(JNIEnv *env, jint id);
    static OnyxPdfiumContext * createContext(JNIEnv *env, jint id,
                                             FPDF_DOCUMENT document,
                                             FPDF_FORMHANDLE formHandle);
    static void releaseContext(JNIEnv *env, jint id);

    static FPDF_DOCUMENT getDocument(JNIEnv *env, jint id) {
        OnyxPdfiumContext * context = getContext(env, id);
        if (context == NULL) {
            return NULL;
        }
        return context->getDocument();
    }

    static FPDF_FORMHANDLE getFormHandle(JNIEnv *env, jint id) {
        OnyxPdfiumContext * context = getContext(env, id);
        if (context == NULL) {
            return NULL;
        }
        return context->getFormHandle();
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

