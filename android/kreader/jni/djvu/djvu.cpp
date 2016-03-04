#include <jni.h>
#include <time.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#include <miniexp.h>
#include <ddjvuapi.h>
#include "debug.h"

#include "base_geometry.c"

#include "JNIUtils.h"

#include "com_onyx_kreader_plugins_djvu_DjvuJniWrapper.h"

#define JNI_FN(A) Java_com_onyx_kreader_plugins_djvu_DjvuJniWrapper_ ## A
#define PACKAGENAME "com/onyx/kreader/plugins/djvu"


#define LOG_TAG "djvulib"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


/* Globals */
ddjvu_context_t *context = NULL;
ddjvu_document_t *doc = NULL;
ddjvu_page_t *page = NULL;

static int extractText(JNIEnv *env, miniexp_t item, fz_bbox * target, JNIUtils *utils, int pageWidth, int pageHeight, jobject textChunk);


JNIEXPORT int JNICALL
JNI_FN(nativeOpenFile)(JNIEnv * env, jobject thiz, jstring jfileName)
{
    page = NULL;
    context = ddjvu_context_create("neo");
    const char * fileName = env->GetStringUTFChars(jfileName, 0);

    doc = ddjvu_document_create_by_filename_utf8(context, fileName, 0);
    int pageNum = 0;
    if (doc) {
        pageNum =  ddjvu_document_get_pagenum(doc);
    }
    return 	pageNum;
}


JNIEXPORT void JNICALL
JNI_FN(nativeGotoPage)(JNIEnv *env, jobject thiz, int pageNum)
{
    if (page != NULL) {
        ddjvu_page_release(page);
        page = NULL;
    }
    page = ddjvu_page_create_by_pageno(doc, pageNum);
}

JNIEXPORT void JNICALL
JNI_FN(nativeGetPageSize)(JNIEnv *env, jobject thiz, int pageNum, jfloatArray size)
{

    clock_t start, end;
    start = clock();

    /*
    ddjvu_page_t * mypage = ddjvu_page_create_by_pageno(doc, pageNum);
    int pageWidth =  ddjvu_page_get_width(mypage);
    int pageHeight = ddjvu_page_get_height(mypage);
    //LOGI("mypage: %x", mypage);

    ddjvu_page_release(mypage);
    */

    ddjvu_pageinfo_t dinfo;
    ddjvu_document_get_pageinfo(doc, pageNum, &dinfo);

    //jfloat s[2] = { (float)dinfo.width, (float)dinfo.height };
    jfloat s[2];
    s[0] = (float)dinfo.width;
    s[1] = (float)dinfo.height;
    env->SetFloatArrayRegion(size, 0, 2, s);
    end = clock();
}

JNIEXPORT jboolean JNICALL
JNI_FN(nativeDrawPage)(JNIEnv *env, jobject thiz, jobject bitmap, float zoom,
        int bmpWidth, int bmpHeight, int patchX, int patchY, int patchW, int patchH)
{
    int ret;
    AndroidBitmapInfo info;
    void *pixels;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return 0;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return 0;
    }
    int numPixels = info.height * info.stride;

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return 0;
    }

    //float zoom = 0.0001f * zoom10000;
    int pageWidth =  ddjvu_page_get_width(page);
    int pageHeight = ddjvu_page_get_height(page);

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
    //LOGI("going to render page %d %d %d %d pageRect %d %d %d %d.", targetRect.x, targetRect.y, targetRect.w, targetRect.h, pageRect.x, pageRect.y, pageRect.w, pageRect.h);
    jboolean result = ddjvu_page_render(page, DDJVU_RENDER_COLOR, &pageRect, &targetRect, pixelFormat, info.stride, buffer);
    ddjvu_format_release(pixelFormat);
    AndroidBitmap_unlockPixels(env, bitmap);
    return 1;
}


JNIEXPORT void JNICALL
JNI_FN(nativeClose)(JNIEnv * env, jobject thiz)
{
    if (page != NULL) {
        ddjvu_page_release(page);
        page = NULL;
    }
    if (doc != NULL) {
        ddjvu_document_release(doc);
        doc = NULL;
    }
    if (context != NULL) {
        ddjvu_context_release(context);
        context = NULL;
    }
}

JNIEXPORT jboolean JNICALL
JNI_FN(nativeExtractPageText)(JNIEnv *env, jobject thiz, int pageNumber, jobject textChunks)
{

    miniexp_t pagetext;
    while ((pagetext=ddjvu_document_get_pagetext(doc,pageNumber,0))==miniexp_dummy) {
      //handle_ddjvu_messages(ctx, TRUE);
    }

    if (miniexp_nil == pagetext) {
        return false;
    }

    ddjvu_status_t status;
    ddjvu_pageinfo_t info;
    while ((status = ddjvu_document_get_pageinfo(doc, pageNumber, &info)) < DDJVU_JOB_OK) {
        //nothing
    }

    JNIUtils utils(env);
    utils.findStaticMethod("com/onyx/kreader/plugins/djvu/DjvuSelection",
                           "addToSelectionList", "(Ljava/util/List;Ljava/lang/String;[I)V");
    int w = info.width;
    int h = info.height;
    fz_bbox target = { 0, 0, w, h };
    extractText(env, pagetext, &target, &utils, w, h, textChunks);

    return true;
}


//sumatrapdf code
int extractText(JNIEnv *env, miniexp_t item, fz_bbox * target, JNIUtils *utils, int pageWidth, int pageHeight, jobject textChunks) {
    miniexp_t type = miniexp_car(item);

    if (!miniexp_symbolp(type))
        return 0;

    item = miniexp_cdr(item);

    if (!miniexp_numberp(miniexp_car(item))) return 0;
    int x0 = miniexp_to_int(miniexp_car(item)); item = miniexp_cdr(item);
    if (!miniexp_numberp(miniexp_car(item))) return 0;
    int y0 = miniexp_to_int(miniexp_car(item)); item = miniexp_cdr(item);
    if (!miniexp_numberp(miniexp_car(item))) return 0;
    int x1 = miniexp_to_int(miniexp_car(item)); item = miniexp_cdr(item);
    if (!miniexp_numberp(miniexp_car(item))) return 0;
    int y1 = miniexp_to_int(miniexp_car(item)); item = miniexp_cdr(item);

    fz_bbox rect = {x0 , y0 , x1 , y1};

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
