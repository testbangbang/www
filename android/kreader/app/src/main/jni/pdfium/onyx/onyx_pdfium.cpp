
#include <vector>

#include "onyx_context.h"
#include "JNIUtils.h"

#include <memory>

static const char * selectionClassName = "com/onyx/kreader/plugins/pdfium/PdfiumSelection";
static const char * splitterClassName = "com/onyx/kreader/api/ReaderTextSplitter";
static const char * stringUtilsClassName = "com/onyx/kreader/utils/StringUtils";

// http://cdn01.foxitsoftware.com/pub/foxit/manual/enu/FoxitPDF_SDK20_Guide.pdf

PluginContextHolder<OnyxPdfiumContext> OnyxPdfiumManager::contextHolder;

OnyxPdfiumContext * OnyxPdfiumManager::getContext(JNIEnv *env, jint id) {
    return contextHolder.findContext(env, id);
}

OnyxPdfiumContext * OnyxPdfiumManager::createContext(JNIEnv *env, jint id, FPDF_DOCUMENT document) {
    OnyxPdfiumContext *context = new OnyxPdfiumContext(document);
    contextHolder.insertContext(env, id, std::unique_ptr<OnyxPdfiumContext>(context));
    return context;
}

void OnyxPdfiumManager::releaseContext(JNIEnv *env, jint id) {
    contextHolder.eraseContext(env, id);
}


static int libraryReference = 0;
/*
 * Class:     com_onyx_reader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeInitLibrary
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeInitLibrary
  (JNIEnv *, jobject thiz) {
    if (libraryReference++ <= 0) {
        FPDF_InitLibrary();
    }
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeDestroyLibrary
  (JNIEnv *env, jobject thiz) {
    if (--libraryReference <= 0) {
        FPDF_DestroyLibrary();
        libraryReference = 0;
    }
    return true;
}

/*
 * Class:     com_onyx_reader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeOpenDocument
 * Signature: (Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeOpenDocument
  (JNIEnv *env, jobject thiz, jint id, jstring jfilename, jstring jpassword) {
    const char *filename = NULL;
  	const char *password = NULL;
  	JNIString fileString(env, jfilename);
  	filename = fileString.getLocalString();
  	if (filename == NULL) {
  	    LOGE("invalid file name");
  		return -1;
  	}

  	JNIString passwordString(env, jpassword);
  	password = passwordString.getLocalString();
    FPDF_DOCUMENT document =  FPDF_LoadDocument(filename, password);
    if (document == NULL) {
        int errorCode = FPDF_GetLastError();
        LOGE("load document failed error code %d", errorCode);
        return errorCode;
    }
    OnyxPdfiumManager::createContext(env, id, document);
    return 0;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeCloseDocument
  (JNIEnv *env, jobject thiz, jint id) {
    FPDF_DOCUMENT document = OnyxPdfiumManager::getDocument(env, id);
    if (document == NULL) {
        return false;
    }

    // make sure we close all pages before closing document.
    OnyxPdfiumManager::releaseContext(env, id);
    FPDF_CloseDocument(document);
    return true;
}

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeMetadata
  (JNIEnv *env, jobject thiz, jint id, jstring jtag, jbyteArray array) {
    FPDF_DOCUMENT document = OnyxPdfiumManager::getDocument(env, id);
    if (document == NULL) {
        return 0;
    }
    JNIString tagString(env, jtag);
    const char * tag = tagString.getLocalString();
    const unsigned long limit = 4096;
    JByteArray buffer(limit);
    unsigned long size = FPDF_GetMetaText(document, tag, buffer.getRawBuffer(), limit);
    env->SetByteArrayRegion(array, 0, limit - 1, buffer.getRawBuffer());
    return size;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativePageSize
  (JNIEnv *env, jobject thiz, jint id, jint pageIndex, jfloatArray array) {
    FPDF_DOCUMENT document = OnyxPdfiumManager::getDocument(env, id);
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

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeClearBitmap
  (JNIEnv *env, jobject thiz, jint id, jobject bitmap) {

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
    memset(pixels, 0xffffffff, info.stride * info.height);
    AndroidBitmap_unlockPixels(env, bitmap);
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeRenderPage
  (JNIEnv * env, jobject thiz, jint id, jint pageIndex, jint x, jint y, jint width, jint height, jint rotation, jobject bitmap) {

    FPDF_PAGE page = OnyxPdfiumManager::getPage(env, id, pageIndex);
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
    FPDF_BITMAP pdfBitmap = OnyxPdfiumManager::getBitmap(env, id, info.width, info.height, pixels, info.stride);
    if (pdfBitmap == NULL) {
    	LOGE("create bitmap failed");
    	AndroidBitmap_unlockPixels(env, bitmap);
        return false;
    }
    FPDF_RenderPageBitmap(pdfBitmap, page, x, y, width, height, rotation, FPDF_LCD_TEXT);
    AndroidBitmap_unlockPixels(env, bitmap);
    return true;
}

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativePageCount
  (JNIEnv * env, jobject thiz, jint id) {
    FPDF_DOCUMENT document = OnyxPdfiumManager::getDocument(env, id);
    if (document == NULL) {
        return 0;
    }
    int count = FPDF_GetPageCount(document);
    return count;
}

static int getSelectionRectangles(FPDF_PAGE page, FPDF_TEXTPAGE textPage, int x, int y, int width, int height, int rotation, int start, int end, std::vector<int> & list) {
    double left, right, bottom, top;
    int newLeft, newRight, newBottom, newTop;
    int pageHeight = FPDF_GetPageHeight(page);
    int count = end - start + 1;
    for(int i = 0; i < count; ++i) {
        FPDFText_GetCharBox(textPage, i + start, &left, &right, &bottom, &top);
        // convert page's left-bottom origin to screen's left-top origin
        newLeft = left;
        newRight = right;
        newTop = pageHeight - top;
        newBottom = pageHeight - bottom;
        if (newRight < newLeft) {
            std::swap(newRight, newLeft);
        }
        if (newBottom < newTop) {
            std::swap(newBottom, newTop);
        }
        list.push_back(newLeft);
        list.push_back(newTop);
        list.push_back(newRight);
        list.push_back(newBottom);
    }
    return count;
}

static int reportSelection(JNIEnv *env, FPDF_PAGE page, FPDF_TEXTPAGE textPage, int x, int y, int width, int height, int rotation, int start, int end, jobject selection) {
    int count = end - start + 1;
    {
        JNIUtils utils(env);
        utils.findMethod(selectionClassName, "addRectangle", "(IIII)V");
        std::vector<int> list;
        getSelectionRectangles(page, textPage, x, y, width, height, rotation, start, end, list);
        for(int i = 0; i < count; ++i) {
            env->CallVoidMethod(selection, utils.getMethodId(), list[i * 4], list[i * 4 + 1], list[i * 4 + 2], list[i * 4 + 3]);
        }
    }

    {
        int textSize = (count + 1) * sizeof(unsigned short);
        JNIByteArray arrayWrapper(env, textSize);
        FPDFText_GetText(textPage, start, count, (unsigned short *)arrayWrapper.getBuffer());
        JNIUtils utils(env);
        utils.findMethod(selectionClassName, "setText", "([B)V");
        env->CallVoidMethod(selection, utils.getMethodId(), arrayWrapper.getByteArray(true));
    }

    {
        JNIUtils utils(env);
        utils.findMethod(selectionClassName, "setRange", "(II)V");
        env->CallVoidMethod(selection, utils.getMethodId(), start, end);
    }

    return count;
}

static int getTextLeftBoundary(JNIEnv * env, jobject splitter, jstring str, jstring left, jstring right) {
    JNILocalRef<jclass> clz = JNILocalRef<jclass>(env, env->GetObjectClass(splitter));
    jmethodID method = env->GetMethodID(clz.getValue(), "getTextLeftBoundary", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I");
    if (!method) {
        LOGE("find method getTextLeftBoundary failed!");
        return -1;
    }
    return env->CallIntMethod(splitter, method, str, left, right);
}

static int getTextRightBoundary(JNIEnv * env, jobject splitter, jstring str, jstring left, jstring right) {
    JNILocalRef<jclass> clz = JNILocalRef<jclass>(env, env->GetObjectClass(splitter));
    jmethodID method = env->GetMethodID(clz.getValue(), "getTextRightBoundary", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I");
    if (!method) {
        LOGE("find method getTextRightBoundary failed!");
        return -1;
    }
    return env->CallIntMethod(splitter, method, str, left, right);
}

static std::shared_ptr<_jstring> getJStringText(JNIEnv *env, FPDF_TEXTPAGE page, jobject splitter, int start, int end) {
    const int count = end - start + 1;
    int textSize = (count + 1) * sizeof(unsigned short);
    JNIByteArray arrayWrapper(env, textSize);
    FPDFText_GetText(page, start, count, (unsigned short *)(arrayWrapper.getBuffer()));
    
    jclass clz = env->FindClass(stringUtilsClassName);
    jmethodID method = env->GetStaticMethodID(clz, "utf16le", "([B)Ljava/lang/String;");
    jstring str = (jstring)env->CallStaticObjectMethod(clz, method, arrayWrapper.getByteArray(true));
    return std::shared_ptr<_jstring>(str, [=](_jstring *s) {
        env->DeleteLocalRef(s);
    });
}

static void selectByWord(JNIEnv *env, FPDF_TEXTPAGE page, jobject splitter, int start, int end, int *newStart, int *newEnd) {
    const int count = FPDFText_CountChars(page);
    if (count <= 0) {
        LOGE("selectByWord, FPDFText_CountChars failed.");
        return;
    }
    
    const int extend = 50;
    const int left = std::max(start - extend, 0);
    const int right = std::min(end + extend, count - 1);
    LOGE("selectByWord: start %d, end %d, left %d, right %d", start, end, left, right);
    
    std::shared_ptr<_jstring> word = getJStringText(env, page, splitter, start, end);
    std::shared_ptr<_jstring> leftStr = getJStringText(env, page, splitter, left, start);
    std::shared_ptr<_jstring> rightStr = getJStringText(env, page, splitter, end, right);
    
    int leftBoundary = getTextLeftBoundary(env, splitter, word.get(), leftStr.get(), rightStr.get());
    int rightBoundary = getTextRightBoundary(env, splitter, word.get(), leftStr.get(), rightStr.get());
    if (leftBoundary > 0) {
        *newStart = start - leftBoundary;
    }
    if (rightBoundary > 0) {
        *newEnd = end + rightBoundary;
    }
    LOGE("result left %d, right %d", *newStart, *newEnd);
}

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeHitTest
  (JNIEnv *env, jobject thiz, jint id, jint pageIndex,  jint x, jint y, jint width, jint height, jint rotation, jint sx, jint sy, jint ex, jint ey, jobject splitter, jobject selection) {
    FPDF_PAGE page = OnyxPdfiumManager::getPage(env, id, pageIndex);
    FPDF_TEXTPAGE textPage = OnyxPdfiumManager::getTextPage(env, id, pageIndex);
    if (page == NULL || textPage == NULL) {
        return 0;
    }

    double tolerance = 10;
    double startPageX, startPageY, endPageX, endPageY;

    // convert from screen to page
    FPDF_DeviceToPage(page, x, y, width, height, rotation, sx, sy, &startPageX, &startPageY);
    FPDF_DeviceToPage(page, x, y, width, height, rotation, ex, ey, &endPageX, &endPageY);

    // find char index in page
    int startIndex = FPDFText_GetCharIndexAtPos(textPage, startPageX, startPageY, tolerance, tolerance);
    int endIndex = FPDFText_GetCharIndexAtPos(textPage, endPageX, endPageY, tolerance, tolerance);

    if (startIndex < 0 || endIndex < 0) {
        LOGE("No selection %d %d", startIndex, endIndex);
        return 0;
    }

    // normalize
    int start = startIndex < endIndex ? startIndex : endIndex;
    int end = startIndex < endIndex ? endIndex : startIndex;
    
    int newStart = start;
    int newEnd = end;
    selectByWord(env, textPage, splitter, start, end, &newStart, &newEnd);
    LOGE("selectByWord finished");
    
    return reportSelection(env, page, textPage, x, y, width, height, rotation, newStart, newEnd, selection);
}

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeSelection
  (JNIEnv *env, jobject thiz, jint id, jint pageIndex, jint x, jint y, jint width, jint height, jint rotation, jint startIndex, jint endIndex, jobject selection) {
    FPDF_PAGE page = OnyxPdfiumManager::getPage(env, id, pageIndex);
    FPDF_TEXTPAGE textPage = OnyxPdfiumManager::getTextPage(env, id, pageIndex);
    if (page == NULL || textPage == NULL) {
        return 0;
    }
    return reportSelection(env, page, textPage, x, y, width, height, rotation, startIndex, endIndex, selection);
}

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeSearchInPage
  (JNIEnv *env, jobject thiz, jint id, jint pageIndex, jint x, jint y, jint width, jint height, int rotation, jbyteArray array, jboolean caseSensitive, jboolean matchWholeWord, jobject objectList) {

    FPDF_PAGE page = OnyxPdfiumManager::getPage(env, id, pageIndex);
    FPDF_TEXTPAGE textPage = OnyxPdfiumManager::getTextPage(env, id, pageIndex);
    if (textPage == NULL) {
        return -1;
    }
    jboolean isCopy = false;
    jbyte* temp = env->GetByteArrayElements(array, &isCopy);
    if (temp == NULL) {
        return -1;
    }

    int length = env->GetArrayLength(array);
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

    int count = 0;
    FPDF_SCHHANDLE searchHandle = FPDFText_FindStart(textPage, (unsigned short *)stringData, flags, 0);
    JNIUtils utils(env);
    utils.findStaticMethod(selectionClassName, "addToSelectionList", "(Ljava/util/List;I[I[BII)V");
    while (searchHandle != NULL && FPDFText_FindNext(searchHandle)) {
        ++count;
        int startIndex = FPDFText_GetSchResultIndex(searchHandle);
        int endIndex = startIndex + FPDFText_GetSchCount(searchHandle);
        std::vector<int> list;
        getSelectionRectangles(page, textPage, x, y, width, height, rotation, startIndex, endIndex, list);
        JNIIntArray intArray(env, list.size(), &list[0]);
        env->CallStaticVoidMethod(utils.getClazz(), utils.getMethodId(), objectList, pageIndex, intArray.getIntArray(true), array, startIndex, endIndex);
    }
    FPDFText_FindClose(searchHandle);
    delete [] stringData;
    return count;
}

JNIEXPORT jbyteArray JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeGetPageText
  (JNIEnv *env, jobject thiz, jint id, jint pageIndex) {
    FPDF_TEXTPAGE textPage = OnyxPdfiumManager::getTextPage(env, id, pageIndex);
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
    FPDFText_GetText(textPage, 0,  count, (unsigned short *)data);
    jbyteArray array = env->NewByteArray(size);
    env->SetByteArrayRegion(array, 0, size, data);
    delete [] data;
    return array;
}
