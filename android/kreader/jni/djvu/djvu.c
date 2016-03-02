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

#include "list.c"
#include "base_geometry.c"

#define JNI_FN(A) Java_com_onyx_kreader_plugins_djvu_DjvuJniWrapper_ ## A
#define PACKAGENAME "com/onyx/kreader/plugins/djvu"


#define LOG_TAG "djvulib"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


/* Globals */
ddjvu_context_t *context = NULL;
ddjvu_document_t *doc = NULL;
ddjvu_page_t *page = NULL;


JNIEXPORT int JNICALL
JNI_FN(nativeOpenFile)(JNIEnv * env, jobject thiz, jstring jfileName)
{
    page = NULL;
    context = ddjvu_context_create("neo");
    const char * fileName = (*env)->GetStringUTFChars(env, jfileName, 0);

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
    (*env)->SetFloatArrayRegion(env, size, 0, 2, s);
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
    char * buffer = &(((unsigned char *)pixels)[shift*4]);
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


struct list_el {
  jobject item;
  struct list_item * next;
};

typedef struct list_el list_item;

struct list_st {
  list_item * head;
  list_item * tail;
};

typedef struct list_st list;

struct OutlineItem_s {
  char * title;
  int level;
  int page;
};
typedef struct OutlineItem_s OutlineItem;

JNIEXPORT jstring JNICALL
JNI_FN(DjvuDocument_getText)(JNIEnv *env, jobject thiz, int pageNumber,
        int startX, int startY, int width, int height)
{

    miniexp_t pagetext;
    while ((pagetext=ddjvu_document_get_pagetext(doc,pageNumber,0))==miniexp_dummy) {
      //handle_ddjvu_messages(ctx, TRUE);
    }

    if (miniexp_nil == pagetext) {
        return NULL;
    }
//
    ddjvu_status_t status;
    ddjvu_pageinfo_t info;
    while ((status = ddjvu_document_get_pageinfo(doc, pageNumber, &info)) < DDJVU_JOB_OK) {
        //nothing
    }

    Arraylist values = arraylist_create();

    int w = info.width;
    int h = info.height;
    fz_bbox target = {startX, h - startY - height, startX + width, h - startY};
    extractText(pagetext, values, &target);

    arraylist_add(values, 0);
    jstring result = (*env)->NewStringUTF(env, arraylist_getData(values));
    arraylist_free(values);

    return result;
}


//sumatrapdf code
int extractText(miniexp_t item, Arraylist list, fz_bbox * target) {
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
    //RectI rect = RectI::FromXY(x0, y0, x1, y1);
    fz_bbox rect = {x0 , y0 , x1 , y1};

    miniexp_t str = miniexp_car(item);

    if (miniexp_stringp(str) && !miniexp_cdr(item)) {
        fz_bbox inters = fz_intersect_bbox(rect, *target);
            //LOGI("Start text extraction: rectangle=[%d,%d,%d,%d] %s", rect.x0, rect.y0, rect.x1, rect.y1, content);
        if (!fz_is_empty_bbox(inters)) {
            const char *content = miniexp_to_str(str);

            while (*content) {
                arraylist_add(list, *content++);
            }

    //        if (value) {
    //            size_t len = str::Len(value);
    //            // TODO: split the rectangle into individual parts per glyph
    //            for (size_t i = 0; i < len; i++)
    //                coords.Append(RectI(rect.x, rect.y, rect.dx, rect.dy));
    //            extracted.AppendAndFree(value);
    //        }
            if (miniexp_symbol("word") == type) {
                arraylist_add(list, ' ');
                //coords.Append(RectI(rect.x + rect.dx, rect.y, 2, rect.dy));
            }
            else if (miniexp_symbol("char") != type) {
                arraylist_add(list, '\n');
    //            extracted.Append(lineSep);
    //            for (size_t i = 0; i < str::Len(lineSep); i++)
    //                coords.Append(RectI());
            }
        }
        item = miniexp_cdr(item);
    }

    while (miniexp_consp(str)) {
        extractText(str, list, target);
        item = miniexp_cdr(item);
        str = miniexp_car(item);
    }

    return !item;
}
