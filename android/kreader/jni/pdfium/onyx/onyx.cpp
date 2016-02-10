
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
JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeInitLibrary
  (JNIEnv *, jobject thiz) {
    FPDF_InitLibrary();
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeDestroyLibrary
  (JNIEnv *env, jobject thiz) {
    FPDF_DestroyLibrary();
    return true;
}

/*
 * Class:     com_onyx_reader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeOpenDocument
 * Signature: (Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeOpenDocument
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
        int errorCode = FPDF_GetLastError();
        LOGE("load document failed error code %d", errorCode);
        return errorCode;
    }
    OnyxPdfiumContext::createContext(thiz, document);
    return 0;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeCloseDocument
  (JNIEnv *env, jobject thiz) {
    FPDF_DOCUMENT document = OnyxPdfiumContext::getDocument(thiz);
    if (document == NULL) {
        return false;
    }
    FPDF_CloseDocument(document);
    OnyxPdfiumContext::releaseContext(thiz);
    return true;
}

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeMetadata
  (JNIEnv *env, jobject thiz, jstring jtag, jbyteArray array) {
    FPDF_DOCUMENT document = OnyxPdfiumContext::getDocument(thiz);
    if (document == NULL) {
        return 0;
    }
    const char * tag = env->GetStringUTFChars(jtag, NULL);
    const unsigned long limit = 4096;
    jbyte * buffer = new jbyte[limit];
    memset(buffer, 0, limit);
    unsigned long size = FPDF_GetMetaText(document, tag, buffer, limit);
    env->SetByteArrayRegion(array, 0, limit - 1, buffer);
    delete [] buffer;
    return size;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativePageSize
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

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeRenderPage
  (JNIEnv * env, jobject thiz, jint pageIndex, jint x, jint y, jint width, jint height, jobject bitmap) {

    FPDF_DOCUMENT document = OnyxPdfiumContext::getDocument(thiz);
    OnyxPdfiumPage pageWrapper(document, pageIndex, false);
    FPDF_PAGE page = pageWrapper.getPage();
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
    FPDF_BITMAP pdfBitmap = OnyxPdfiumContext::getBitmap(thiz, info.width, info.height, pixels, info.stride);
    if (pdfBitmap == NULL) {
    	LOGE("create bitmap failed");
        return false;
    }
    FPDF_RenderPageBitmap(pdfBitmap, page, x, y, width, height, 0, FPDF_LCD_TEXT);
    return true;
}

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativePageCount
  (JNIEnv * env, jobject thiz) {
    FPDF_DOCUMENT document = OnyxPdfiumContext::getDocument(thiz);
    if (document == NULL) {
        return 0;
    }
    int count = FPDF_GetPageCount(document);
    return count;
}

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_hitTest
  (JNIEnv *env, jobject thiz, jint pageIndex,  jint x, jint y, jint width, jint height, jint sx, jint sy, jint ex, jint ey, jdoubleArray array) {
    FPDF_DOCUMENT document = OnyxPdfiumContext::getDocument(thiz);
    OnyxPdfiumPage pageWrapper(document, pageIndex, true);
    FPDF_PAGE page = pageWrapper.getPage();
    FPDF_TEXTPAGE textPage = pageWrapper.getTextPage();

    double tolerance = 10.0 / 72.0;
    int startIndex = FPDFText_GetCharIndexAtPos(textPage, sx, sy, tolerance, tolerance);
    int endIndex = FPDFText_GetCharIndexAtPos(textPage, ex, ey, tolerance, tolerance);

    if (startIndex < 0) {
        startIndex = 0;
    }
    if (endIndex < 0) {
        endIndex = FPDFText_CountChars(textPage) / 2;
    }


    int start = startIndex < endIndex ? startIndex : endIndex;
    int end = startIndex < endIndex ? endIndex : startIndex;
    double left, right, bottom, top;
    int newLeft, newRight, newBottom, newTop;
    int limit = end - start + 1;
    jdouble * buffer = new jdouble[limit * 4];
    LOGE("start index %d end index %d limit %d count %d", start, end, limit, FPDFText_CountChars(textPage));
    for(int i = start; i <= end; ++i) {
        FPDFText_GetCharBox(textPage, i, &left, &right, &bottom, &top);
        FPDF_PageToDevice(page, x, y, width, height, 0, left, top, &newLeft, &newTop);
        FPDF_PageToDevice(page, x, y, width, height, 0, right, bottom, &newRight, &newBottom);
        buffer[i * 4] = newLeft;
        buffer[i * 4 + 1] = newTop;
        buffer[i * 4 + 2] = newRight;
        buffer[i * 4 + 3] = newBottom;
    }

    env->SetDoubleArrayRegion(array, 0, limit * 4, buffer);
    delete [] buffer;
    return limit;
}

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeSearchInPage
  (JNIEnv *env, jobject thiz, jint pageIndex, jbyteArray buffer, jboolean caseSensitive, jboolean matchWholeWord) {
    FPDF_DOCUMENT document = OnyxPdfiumContext::getDocument(thiz);
    OnyxPdfiumPage pageWrapper(document, pageIndex, true);
    FPDF_TEXTPAGE textPage = pageWrapper.getTextPage();
    if (textPage == NULL) {
        return -1;
    }
    jboolean isCopy = false;
    jbyte* temp = env->GetByteArrayElements(buffer, &isCopy);
    if (temp == NULL) {
        return -1;
    }

    int length = env->GetArrayLength(buffer);
    jbyte * stringData = new jbyte[length + 2];
    memset(stringData, 0, length + 2);
    memcpy(stringData, temp, length);
    int flags = 0;
    if (caseSensitive) {
        flags |= FPDF_MATCHCASE;
    }
    if (matchWholeWord) {
        flags |= FPDF_MATCHWHOLEWORD;
    }

    LOGE("search content %d length %d", *stringData, env->GetArrayLength(buffer));
    int count = 0;
    FPDF_SCHHANDLE searchHandle = FPDFText_FindStart(textPage, (unsigned short *)stringData, flags, 0);
    LOGE("search handle %p", searchHandle);
    while (FPDFText_FindNext(searchHandle)) {
        ++count;
        int index = FPDFText_GetSchResultIndex(searchHandle);
        // collect the rectangle and others
    }
    LOGE("search count %d", count);
    FPDFText_FindClose(searchHandle);
    return count;
}

JNIEXPORT jbyteArray JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeGetPageText
  (JNIEnv *env, jobject thiz, jint pageIndex) {
    FPDF_DOCUMENT document = OnyxPdfiumContext::getDocument(thiz);
    OnyxPdfiumPage pageWrapper(document, pageIndex, true);
    FPDF_TEXTPAGE textPage = pageWrapper.getTextPage();
    if (textPage == NULL) {
       return NULL;
    }
    int count = FPDFText_CountChars(textPage);
    if (count <= 0) {
        return NULL;
    }

    int size = 2 * (count + 1);
    jbyte * data = new jbyte[size];
    memset(data, 0, size);
    int written = FPDFText_GetText(textPage, 0,  count, (unsigned short *)data);
    LOGE("written %d count %d data %d %d %d %d", count, written, *data, *(data + 1), *(data + 2), *(data + 3));
    jbyteArray array = env->NewByteArray(size);
    env->SetByteArrayRegion(array, 0, size, data);
    delete [] data;
    return array;
}