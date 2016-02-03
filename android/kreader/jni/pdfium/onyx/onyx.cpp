
#include "onyx.h"

// http://cdn01.foxitsoftware.com/pub/foxit/manual/enu/FoxitPDF_SDK20_Guide.pdf

std::map<jobject, OnyxPdfiumContext *> OnyxPdfiumContext::contextMap;

OnyxPdfiumContext * OnyxPdfiumContext::getContext(jobject thiz) {
    std::map<jobject, OnyxPdfiumContext *>::iterator iterator = contextMap.find(thiz);
    if (iterator != contextMap.end()) {
        return iterator->second;
    }
    return NULL;
}

OnyxPdfiumContext * OnyxPdfiumContext::createContext(jobject object, FPDF_DOCUMENT document) {
    OnyxPdfiumContext * context = new OnyxPdfiumContext();
    context->document = document;
    contextMap.insert(std::pair<jobject, OnyxPdfiumContext *>(object, context));
    return context;
}

void OnyxPdfiumContext::releaseContext(jobject thiz) {
    std::map<jobject, OnyxPdfiumContext *>::iterator iterator = contextMap.find(thiz);
    if (iterator != contextMap.end()) {
        delete iterator->second;
        contextMap.erase(iterator);
    }
}

/*
 * Class:     com_onyx_reader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeInitLibrary
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_pdfium_PdfiumJniWrapper_nativeInitLibrary
  (JNIEnv *, jobject thiz) {
    FPDF_InitLibrary(NULL);
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_pdfium_PdfiumJniWrapper_nativeDestroyLibrary
  (JNIEnv *env, jobject thiz) {
    FPDF_DestroyLibrary();
    return true;
}

/*
 * Class:     com_onyx_reader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeOpenDocument
 * Signature: (Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_onyx_reader_plugins_pdfium_PdfiumJniWrapper_nativeOpenDocument
  (JNIEnv *env, jobject thiz, jstring jfilename, jstring jpassword) {
    const char *filename = NULL;
  	const char *password = NULL;
  	filename = env->GetStringUTFChars(jfilename, NULL);
  	if (filename == NULL) {
  	    LOGE("invalid file name");
  		return -1;
  	}

    if (jpassword != NULL) {
        password = env->GetStringUTFChars(jpassword, NULL);
    }
    FPDF_DOCUMENT document =  FPDF_LoadDocument(filename, password);
    if (document == NULL) {
        LOGE("load document failed");
        return -1;
    }
    OnyxPdfiumContext::createContext(thiz, document);
    return 0;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_pdfium_PdfiumJniWrapper_nativeCloseDocument
  (JNIEnv *env, jobject thiz) {
    FPDF_DOCUMENT document = OnyxPdfiumContext::getDocument(thiz);
    if (document == NULL) {
        return false;
    }
    FPDF_CloseDocument(document);
    OnyxPdfiumContext::releaseContext(thiz);
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_pdfium_PdfiumJniWrapper_nativePageSize
  (JNIEnv *env, jobject thiz, jint pageIndex, jfloatArray array) {
    FPDF_DOCUMENT document = OnyxPdfiumContext::getDocument(thiz);
    if (document == NULL) {
        return false;
    }
    double width = 0, height = 0;
    if (!FPDF_GetPageSizeByIndex(document, pageIndex, &width, &height)) {
        return false;
    }
    jfloat size[] = {(float)width, (float)height};
    env->SetFloatArrayRegion(array, 0, 2, size);
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_pdfium_PdfiumJniWrapper_nativeRenderPage
  (JNIEnv * env, jobject thiz, jint pageIndex, jobject bitmap) {

    FPDF_DOCUMENT document = OnyxPdfiumContext::getDocument(thiz);
    FPDF_PAGE page = FPDF_LoadPage(document, pageIndex);
    if (page == NULL) {
        LOGE("invalid page %d", pageIndex);
        return false;
    }
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

JNIEXPORT jint JNICALL Java_com_onyx_reader_plugins_pdfium_PdfiumJniWrapper_nativePageCount
  (JNIEnv * env, jobject thiz) {
    FPDF_DOCUMENT document = OnyxPdfiumContext::getDocument(thiz);
    int count = FPDF_GetPageCount(document);
    return count;
}
