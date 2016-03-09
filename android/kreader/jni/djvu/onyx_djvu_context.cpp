#include "onyx_djvu_context.h"

#include <math.h>
#include <malloc.h>

#include <string>
#include <vector>

#include <jni.h>

#include <android/log.h>
#include <android/bitmap.h>

#include <miniexp.h>
#include <ddjvuapi.h>

#include "base_geometry.h"

#include "JNIUtils.h"

#define LOG_TAG "djvulib"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

namespace {

bool handle_ddjvu_messages(ddjvu_context_t *context, bool wait)
{
    const ddjvu_message_t *msg;
    if (!context)
        return false;
    if (wait)
        msg = ddjvu_message_wait(context);
    while ((msg = ddjvu_message_peek(context)))
    {
      switch(msg->m_any.tag)
        {
        case DDJVU_ERROR:
          break;
        default:
          break;
        }
      ddjvu_message_pop(context);
    }

    return true;
}

bool extractText(JNIEnv *env, miniexp_t item, fz_bbox *target, JNIUtils *utils, int pageWidth, int pageHeight, jobject textChunks)
{
    miniexp_t type = miniexp_car(item);
    if (!miniexp_symbolp(type)) {
        return false;
    }
    item = miniexp_cdr(item);

    if (!miniexp_numberp(miniexp_car(item))) return 0;
    int x0 = miniexp_to_int(miniexp_car(item)); item = miniexp_cdr(item);
    if (!miniexp_numberp(miniexp_car(item))) return 0;
    int y0 = miniexp_to_int(miniexp_car(item)); item = miniexp_cdr(item);
    if (!miniexp_numberp(miniexp_car(item))) return 0;
    int x1 = miniexp_to_int(miniexp_car(item)); item = miniexp_cdr(item);
    if (!miniexp_numberp(miniexp_car(item))) return 0;
    int y1 = miniexp_to_int(miniexp_car(item)); item = miniexp_cdr(item);

    fz_bbox rect = { x0 , y0 , x1 , y1 };
    miniexp_t str = miniexp_car(item);

    if (miniexp_stringp(str) && !miniexp_cdr(item)) {
        fz_bbox inters = fz_intersect_bbox(rect, *target);
        if (!fz_is_empty_bbox(inters)) {
            const char *content = miniexp_to_str(str);
            //LOGI("Start text extraction: rectangle=[%d,%d,%d,%d] %s", rect.x0, rect.y0, rect.x1, rect.y1, content);
            int list[] = { rect.x0, pageHeight - rect.y1, rect.x1, pageHeight - rect.y0 }; // patching y coordinates
            JNIIntArray intArray(env, 4, &list[0]);
            env->CallStaticVoidMethod(utils->getClazz(),
                                      utils->getMethodId(),
                                      textChunks,
                                      env->NewStringUTF(content),
                                      intArray.getIntArray(true));
        }
        item = miniexp_cdr(item);
    }

    while (miniexp_consp(str)) {
        extractText(env, str, target, utils, pageWidth, pageHeight, textChunks);
        item = miniexp_cdr(item);
        str = miniexp_car(item);
    }

    return !item;
}

}

OnyxDjvuContext *OnyxDjvuContext::createContext(JNIEnv *env, jstring filePath)
{
    ddjvu_context_t *context = ddjvu_context_create("neo");
    if (!context) {
        LOGE("creating djvu context failed!");
        return nullptr;
    }

    std::string str(JNIString(env, filePath).getLocalString());
    char *pFilePath = (char *)calloc(str.size() + 1, 1);
    str.copy(pFilePath, str.size());

    // djvulibre will keep pFilePath pointer, so we use heap memory to store it
    ddjvu_document_t *doc = ddjvu_document_create_by_filename_utf8(context, pFilePath, 0);
    if (!doc) {
        LOGE("creating djvu document failed!");
        ddjvu_context_release(context);
        return nullptr;
    }

    while (!ddjvu_document_decoding_done(doc)) {
        handle_ddjvu_messages(context, true);
    }

    ddjvu_status_t status = ddjvu_document_decoding_status(doc);
    if (status >= DDJVU_JOB_FAILED) {
        LOGE("decoding djvu document failed!");
        ddjvu_document_release(doc);
        ddjvu_context_release(context);
        return nullptr;
    }

    int pageCount = ddjvu_document_get_pagenum(doc);
    return new OnyxDjvuContext(pFilePath, pageCount, context, doc);
}

OnyxDjvuContext::OnyxDjvuContext(char *filePath, int pageCount,
            ddjvu_context_t *context, ddjvu_document_t *doc)
    : filePath_(filePath), pageCount_(pageCount),
      context_(context), doc_(doc), currentPage_(nullptr) {
}

OnyxDjvuContext::~OnyxDjvuContext()
{
    close();
}

int OnyxDjvuContext::getPageCount()
{
    return pageCount_;
}

bool OnyxDjvuContext::gotoPage(int pageNum)
{
    auto find = pageMap.find(pageNum);
    if (find != pageMap.end()) {
        currentPage_ = find->second;
        return true;
    }
    
    currentPage_ = ddjvu_page_create_by_pageno(doc_, pageNum);
    if (!currentPage_) {
        return false;
    }
    pageMap.insert({ pageNum, currentPage_ });
    return true;
}

bool OnyxDjvuContext::getPageSize(int pageNum, std::vector<jfloat> *size)
{
    ddjvu_pageinfo_t dinfo;
    ddjvu_status_t s;
    while ((s= ddjvu_document_get_pageinfo(doc_, pageNum, &dinfo)) < DDJVU_JOB_OK) {
        handle_ddjvu_messages(context_, true);
    }
    if (s >= DDJVU_JOB_FAILED) {
        LOGE("ddjvu_document_get_pageinfo failed");
        return false;
    }
    size->push_back(dinfo.width);
    size->push_back(dinfo.height);
    return true;
}

bool OnyxDjvuContext::extractPageText(JNIEnv *env, int pageNum, jobject textChunks)
{
    miniexp_t pagetext;
    while ((pagetext=ddjvu_document_get_pagetext(doc_,pageNum,0)) == miniexp_dummy) {
        //handle_ddjvu_messages(ctx, TRUE);
    }

    if (miniexp_nil == pagetext) {
        return false;
    }

    ddjvu_status_t status;
    ddjvu_pageinfo_t info;
    while ((status = ddjvu_document_get_pageinfo(doc_, pageNum, &info)) < DDJVU_JOB_OK) {
        //nothing
    }

    JNIUtils utils(env);
    utils.findStaticMethod("com/onyx/kreader/plugins/djvu/DjvuSelection",
                           "addToSelectionList", "(Ljava/util/List;Ljava/lang/String;[I)V");
    int w = info.width;
    int h = info.height;
    fz_bbox target = { 0, 0, w, h };
    return extractText(env, pagetext, &target, &utils, w, h, textChunks);
}

bool OnyxDjvuContext::draw(JNIEnv *env, jobject bitmap, float zoom, int bmpWidth, int bmpHeight, int patchX, int patchY, int patchW, int patchH)
{
    if (!currentPage_) {
        return false;
    }

    int ret;
    AndroidBitmapInfo info;
    void *pixels;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return false;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return false;
    }
    int numPixels = info.height * info.stride;

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return false;
    }

    //float zoom = 0.0001f * zoom10000;
    int pageWidth =  ddjvu_page_get_width(currentPage_);
    int pageHeight = ddjvu_page_get_height(currentPage_);

    ddjvu_rect_t pageRect;
    pageRect.x = 0;
    pageRect.y = 0;
    pageRect.w = round(zoom * pageWidth);
    pageRect.h = round(zoom * pageHeight);
    ddjvu_rect_t targetRect;
    targetRect.x = patchX;
    targetRect.y = patchY;
    targetRect.w = patchW;
    targetRect.h = patchH;

    int shift = 0;
    if (targetRect.x < 0) {
        shift = -targetRect.x;
        targetRect.w += targetRect.x;
        targetRect.x = 0;
    }
    if (targetRect.y < 0) {
        shift +=  -targetRect.y * info.width;
        targetRect.h += targetRect.y;
        targetRect.y = 0;
    }

    if (pageRect.w <  targetRect.x + targetRect.w) {
        targetRect.w = targetRect.w - (targetRect.x + targetRect.w - pageRect.w);
    }
    if (pageRect.h <  targetRect.y + targetRect.h) {
        targetRect.h = targetRect.h - (targetRect.y + targetRect.h - pageRect.h);
    }

    // we would keep old bitmap data if we want to provide continuous paging mode
    //memset(pixels, 0xffffffff, numPixels);

    unsigned int masks[4] = { 0xff, 0xff00, 0xff0000, 0xff000000 };
    ddjvu_format_t* pixelFormat = ddjvu_format_create(DDJVU_FORMAT_RGBMASK32, 4, masks);

    ddjvu_format_set_row_order(pixelFormat, TRUE);
    ddjvu_format_set_y_direction(pixelFormat, TRUE);
    char * buffer = &(((char *)pixels)[shift*4]);
    //LOGI("going to render page %d %d %d %d pageRect %d %d %d %d.", targetRect.x, targetRect.y, targetRect.w, targetRect.h, pageRect.x, pageRect.y, pa
    ret = ddjvu_page_render(currentPage_, DDJVU_RENDER_COLOR, &pageRect, &targetRect, pixelFormat, info.stride, buffer);
    ddjvu_format_release(pixelFormat);
    AndroidBitmap_unlockPixels(env, bitmap);

    return ret;
}

void OnyxDjvuContext::close()
{
    pageCount_ = 0;

    for (auto &p : pageMap) {
        ddjvu_page_release(p.second);
    }
    pageMap.clear();

    if (doc_) {
        ddjvu_document_release(doc_);
        doc_ = nullptr;
    }
    if (context_) {
        ddjvu_context_release(context_);
        context_ = nullptr;
    }
    if (filePath_) {
        free(filePath_);
        filePath_ = nullptr;
    }
}

