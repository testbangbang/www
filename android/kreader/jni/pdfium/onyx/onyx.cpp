#include <jni.h>
#include <time.h>
#include <pthread.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <list>

#ifdef NDK_PROFILER
#include "prof.h"
#endif

#include "log.h"
#include "com_onyx_reader_plugins_pdfium_PdfiumJniWrapper.h"
#include "fpdfview.h"

// http://cdn01.foxitsoftware.com/pub/foxit/manual/enu/FoxitPDF_SDK20_Guide.pdf

static FPDF_DOCUMENT document = NULL;
/*
 * Class:     com_onyx_reader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeInitLibrary
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_pdfium_PdfiumJniWrapper_nativeInitLibrary
  (JNIEnv *, jobject) {
    FPDF_InitLibrary(NULL);
    return true;
}

/*
 * Class:     com_onyx_reader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeOpenDocument
 * Signature: (Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_onyx_reader_plugins_pdfium_PdfiumJniWrapper_nativeOpenDocument
  (JNIEnv *env, jobject thiz, jstring jfilename, jstring jpassword) {

    const char *filename;
  	const char *password;
  	filename = env->GetStringUTFChars(jfilename, NULL);
  	if (filename == NULL) {
  	    LOGE("invalid file name");
  		return -1;
  	}

    password = env->GetStringUTFChars(jpassword, NULL);
    document =  FPDF_LoadDocument(filename, NULL);
    if (document == NULL) {
        LOGE("load document failed");
        return -1;
    }
    int count = FPDF_GetPageCount(document);
    LOGE("page count %d", count);
    for(int i = 0; i < count; ++i) {
        double width, height;
        FPDF_GetPageSizeByIndex(document, i, &width, &height);
        LOGE("page %d width %lf height %lf", i, width, height);

        FPDF_PAGE page = FPDF_LoadPage(document, i);
        LOGE("load page %d width %lf height %lf", i, FPDF_GetPageWidth(page), FPDF_GetPageHeight(page));
    }
    return 0;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_pdfium_PdfiumJniWrapper_nativeRenderPage
  (JNIEnv * env, jobject clazz, jint pageIndex, jobject bitmap) {
    LOGE("going to load page %d", pageIndex);

    FPDF_PAGE page = FPDF_LoadPage(document, pageIndex);
    if (page == NULL) {
        LOGE("invalid page %d", pageIndex);
        return false;
    }
    LOGE("load page %d finished", pageIndex);

    AndroidBitmapInfo info;
	void *pixels;
	int ret;

	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return false;
	}

	if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
		LOGE("Bitmap format is not RGBA_8888 !");
		return false;
	}

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
		return false;
	}
    LOGE("going to create bitmap");
    FPDF_BITMAP pdfBitmap = FPDFBitmap_CreateEx(info.width, info.height, FPDFBitmap_BGRA, pixels, info.stride);
    if (pdfBitmap == NULL) {
    	LOGE("create bitmap failed");
        return false;
    }
    LOGE("going to render page");
    FPDF_RenderPageBitmap(pdfBitmap, page, 0, 0, info.width, info.height, 0, FPDF_LCD_TEXT);
    LOGE("render page finished");
    return true;
}