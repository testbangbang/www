#include "com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper.h"

#include <cstdlib>

#include <vector>
#include <string>
#include <locale>
#include <memory>
#include <fstream>
#include <sstream>

#include "fpdf_doc.h"
#include "fpdf_formfill.h"
#include "fpdf_edit.h"

#include "core/fpdfapi/fpdf_page/include/cpdf_pageobject.h"
#include "core/fpdfapi/onyx_drm_decrypt.h"

#include "onyx_context.h"
#include "JNIUtils.h"
#include "jsonxx.h"

static const char * selectionClassName = "com/onyx/android/sdk/reader/plugins/neopdf/NeoPdfSelection";
static const char * splitterClassName = "com/onyx/android/sdk/reader/api/ReaderTextSplitter";
static const char * sentenceClassName = "com/onyx/android/sdk/reader/api/ReaderSentence";
static const char * annotationClassName = "com/onyx/android/sdk/data/model/Annotation";

// http://cdn01.foxitsoftware.com/pub/foxit/manual/enu/FoxitPDF_SDK20_Guide.pdf

PluginContextHolder<OnyxPdfiumContext> OnyxPdfiumManager::contextHolder;

OnyxPdfiumContext * OnyxPdfiumManager::getContext(JNIEnv *env, jint id) {
    return contextHolder.findContext(env, id);
}

OnyxPdfiumContext * OnyxPdfiumManager::createContext(JNIEnv *env, jint id,
                                                     FPDF_DOCUMENT document,
                                                     FPDF_FORMHANDLE formHandle) {
    OnyxPdfiumContext *context = new OnyxPdfiumContext(document, formHandle);
    contextHolder.insertContext(env, id, std::unique_ptr<OnyxPdfiumContext>(context));
    return context;
}

void OnyxPdfiumManager::releaseContext(JNIEnv *env, jint id) {
    contextHolder.eraseContext(env, id);
}

static int libraryReference = 0;
/*
 * Class:     com_onyx_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeInitLibrary
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeInitLibrary
  (JNIEnv *, jobject thiz) {
    if (libraryReference++ <= 0) {
        FPDF_InitLibrary();
    }
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeDestroyLibrary
  (JNIEnv *env, jobject thiz) {
    if (--libraryReference <= 0) {
        FPDF_DestroyLibrary();
        libraryReference = 0;
    }
    return true;
}

/*
 * Class:     com_onyx_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeOpenDocument
 * Signature: (Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeOpenDocument
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

    onyx::DrmDecryptManager::singleton().setEncrypted(false);

    char buf[4096];
    unsigned long res = FPDF_GetMetaText(document, "book", buf, 4096);
    if (res > 2) { // empty metadata text is '\0' in unicode
        LOGI("document is DRM protected, try to decrypt the document");
        std::string manifestString = StringUtils::utf16leto8(reinterpret_cast<const char16_t *>(buf));

        std::ifstream file("/sdcard/private_key");
        if (!file ) {
            LOGE("open /sdcard/private_key failed!");
            return -1;
        }
        std::stringstream buffer;
        buffer << file.rdbuf();
        file.close();

        std::string key = buffer.str();
        const unsigned char *rsaKeyData = reinterpret_cast<const unsigned char *>(key.c_str());

        onyx::DrmDecrypt decrypt;
        int resultLen = 0;
        unsigned char *result = decrypt.rsaDecryptManifest(rsaKeyData, manifestString.c_str(), &resultLen);
        if (resultLen == -1) {
            LOGE("read metadata failed!");
            return -1;
        }

        jsonxx::Object object;
        if (!object.parse(reinterpret_cast<char *>(result))) {
            LOGE("read metadata failed!");
            return -1;
        }
        std::string aesKey = object.get<std::string>("aesKey");

        onyx::DrmDecryptManager::singleton().setEncrypted(true);
        onyx::DrmDecryptManager::singleton().setAESKey(aesKey.c_str());
    }

    FPDF_FORMFILLINFO formInfo;
    formInfo.version = 1;
    FPDF_FORMHANDLE formHandle = FPDFDOC_InitFormFillEnvironment(document, &formInfo);
    if (!formHandle) {
        LOGE("init form fill environment failed!");
        FPDF_CloseDocument(document);
        return -1;
    }
    FPDF_SetFormFieldHighlightColor(formHandle, 0, 0xFFE4DD);
    FPDF_SetFormFieldHighlightAlpha(formHandle, 100);

    OnyxPdfiumManager::createContext(env, id, document, formHandle);
    return 0;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeCloseDocument
  (JNIEnv *env, jobject thiz, jint id) {
    FPDF_DOCUMENT document = OnyxPdfiumManager::getDocument(env, id);
    if (document == NULL) {
        return false;
    }

    // make sure we close all pages before closing document.
    OnyxPdfiumManager::releaseContext(env, id);

    FPDF_FORMHANDLE formHandle = OnyxPdfiumManager::getFormHandle(env, id);
    FPDFDOC_ExitFormFillEnvironment(formHandle);

    FPDF_CloseDocument(document);
    return true;
}

JNIEXPORT jint JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeMetadata
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

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativePageSize
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

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeClearBitmap
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

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeRenderPage
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
    FPDF_RenderPageBitmap(pdfBitmap, page, x, y, width, height, rotation, FPDF_LCD_TEXT | FPDF_ANNOT | FPDF_REVERSE_BYTE_ORDER);
    FPDF_FORMHANDLE formHandle = OnyxPdfiumManager::getFormHandle(env, id);
    FPDF_FFLDraw(formHandle, pdfBitmap, page, x, y, width, height, rotation, FPDF_LCD_TEXT | FPDF_ANNOT | FPDF_REVERSE_BYTE_ORDER);
    AndroidBitmap_unlockPixels(env, bitmap);
    return true;
}

JNIEXPORT jint JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativePageCount
  (JNIEnv * env, jobject thiz, jint id) {
    FPDF_DOCUMENT document = OnyxPdfiumManager::getDocument(env, id);
    if (document == NULL) {
        return 0;
    }
    int count = FPDF_GetPageCount(document);
    return count;
}

static jobject createEmptySelection(JNIEnv *env, int page) {
    JNIUtils utils(env);
    if (!utils.findMethod(selectionClassName, "<init>", "(I)V")) {
        return NULL;
    }
    return env->NewObject(utils.getClazz(), utils.getMethodId(), page);
}

// convert page's left-bottom origin to screen's left-top origin
// but there are some documents we can't get normalized coordinates simply by subtracting with page width/height,
// so it's safer to use pdfium's built-in FPDF_PageToDevice()
static void pageToDevice(FPDF_PAGE page, double pageWidth, double pageHeight, int rotation,
                         double left, double top, double right, double bottom,
                         double *newLeft, double *newTop, double *newRight, double *newBottom) {
    int pw = static_cast<int>(pageWidth);
    int ph = static_cast<int>(pageHeight);
    FPDF_PageToDeviceEx(page, 0, 0, pw, ph, rotation, left, top, newLeft, newTop);
    FPDF_PageToDeviceEx(page, 0, 0, pw, ph, rotation, right, bottom, newRight, newBottom);
    if (*newRight < *newLeft) {
        std::swap(*newRight, *newLeft);
    }
    if (*newBottom < *newTop) {
        std::swap(*newBottom, *newTop);
    }
}

static int getSelectionRectangles(FPDF_PAGE page, FPDF_TEXTPAGE textPage, int x, int y, int width, int height, int rotation, int start, int end, std::vector<float> &list) {
    double left, right, bottom, top;
    double newLeft, newRight, newBottom, newTop;
    double pageWidth = FPDF_GetPageWidth(page);
    double pageHeight = FPDF_GetPageHeight(page);
    int count = end - start + 1;
    for(int i = 0; i < count; ++i) {
        FPDFText_GetCharBox(textPage, i + start, &left, &right, &bottom, &top);
        pageToDevice(page, pageWidth, pageHeight, rotation,
                     left, top, right, bottom,
                     &newLeft, &newTop, &newRight, &newBottom);
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
        utils.findMethod(selectionClassName, "addRectangle", "(FFFF)V");
        std::vector<float> list;
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

static int getTextSentenceBreakPoint(JNIEnv * env, jobject splitter, jstring text) {
    JNILocalRef<jclass> clz = JNILocalRef<jclass>(env, env->GetObjectClass(splitter));
    jmethodID method = env->GetMethodID(clz.getValue(), "getTextSentenceBreakPoint", "(Ljava/lang/String;)I");
    if (!method) {
        LOGE("find method getTextSentenceBreakPoint failed!");
        return -1;
    }
    return env->CallIntMethod(splitter, method, text);
}

static std::shared_ptr<_jstring> getJStringText(JNIEnv *env, FPDF_TEXTPAGE page, int start, int end) {
    const int count = end - start + 1;
    int textSize = (count + 1) * sizeof(unsigned short);
    JNIByteArray arrayWrapper(env, textSize);
    FPDFText_GetText(page, start, count, (unsigned short *)(arrayWrapper.getBuffer()));

    return StringUtils::newLocalString(env, (jchar *)arrayWrapper.getBuffer(), count);
}

static bool isAlphaOrDigit(JNIEnv *env, FPDF_TEXTPAGE page, jobject splitter, int charIndex) {
    const int count = 1;
    int textSize = (count + 1) * sizeof(unsigned short);
    JNIByteArray arrayWrapper(env, textSize);
    FPDFText_GetText(page, charIndex, count, (unsigned short *)(arrayWrapper.getBuffer()));

    std::shared_ptr<_jstring> jstr = StringUtils::newLocalString(env, (jchar *)arrayWrapper.getBuffer(), count);
    JNILocalRef<jclass> clz = JNILocalRef<jclass>(env, env->GetObjectClass(splitter));
    jmethodID method = env->GetMethodID(clz.getValue(), "isAlphaOrDigit", "(Ljava/lang/String;)Z");
    if (!method) {
        LOGE("find method getTextLeftBoundary failed!");
        return -1;
    }
    return env->CallBooleanMethod(splitter, method, jstr.get());
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
    
    std::shared_ptr<_jstring> word = getJStringText(env, page, start, end);
    std::shared_ptr<_jstring> leftStr = getJStringText(env, page, left, start - 1);
    std::shared_ptr<_jstring> rightStr = getJStringText(env, page, end + 1, right);
    
    int leftBoundary = getTextLeftBoundary(env, splitter, word.get(), leftStr.get(), rightStr.get());
    int rightBoundary = getTextRightBoundary(env, splitter, word.get(), leftStr.get(), rightStr.get());
    if (leftBoundary > 0) {
        *newStart = start - leftBoundary;
    }
    if (rightBoundary > 0) {
        *newEnd = end + rightBoundary;
    }
}

JNIEXPORT jint JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeHitTest
  (JNIEnv *env, jobject thiz, jint id, jint pageIndex,  jint x, jint y, jint width, jint height, jint rotation, jint sx, jint sy, jint ex, jint ey, jobject splitter, jboolean selectingWord, jobject selection) {
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
    if (splitter != NULL) {
        if (selectingWord) {
            selectByWord(env, textPage, splitter, start, end, &newStart, &newEnd);
        } else {
            if (isAlphaOrDigit(env, textPage, splitter, start)) {
                int ignoreEnd = end;
                selectByWord(env, textPage, splitter, start, start, &newStart, &ignoreEnd);
            }
            if (isAlphaOrDigit(env, textPage, splitter, end)) {
                int ignoreStart = start;
                selectByWord(env, textPage, splitter, end, end, &ignoreStart, &newEnd);
            }
        }
    }

    return reportSelection(env, page, textPage, x, y, width, height, rotation, newStart, newEnd, selection);
}

JNIEXPORT jint JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeSelection
  (JNIEnv *env, jobject thiz, jint id, jint pageIndex, jint x, jint y, jint width, jint height, jint rotation, jint startIndex, jint endIndex, jobject selection) {
    FPDF_PAGE page = OnyxPdfiumManager::getPage(env, id, pageIndex);
    FPDF_TEXTPAGE textPage = OnyxPdfiumManager::getTextPage(env, id, pageIndex);
    if (page == NULL || textPage == NULL) {
        return 0;
    }
    return reportSelection(env, page, textPage, x, y, width, height, rotation, startIndex, endIndex, selection);
}

JNIEXPORT jint JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeSearchInPage
  (JNIEnv *env, jobject thiz, jint id, jint pageIndex, jint x, jint y, jint width, jint height, int rotation, jbyteArray array, jboolean caseSensitive, jboolean matchWholeWord, jint contextLength, jobject objectList) {

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
    env->ReleaseByteArrayElements(array,temp,0);
    int flags = 0;
    if (caseSensitive) {
        flags |= FPDF_MATCHCASE;
    }
    if (matchWholeWord) {
        flags |= FPDF_MATCHWHOLEWORD;
    }

    int pageTextCount = FPDFText_CountChars(textPage);
    int contextOffset = (contextLength - length) / 2;

    int count = 0;
    FPDF_SCHHANDLE searchHandle = FPDFText_FindStart(textPage, (unsigned short *)stringData, flags, 0);
    JNIUtils utils(env);
    utils.findStaticMethod(selectionClassName, "addToSelectionList", "(Ljava/util/List;I[F[BIILjava/lang/String;Ljava/lang/String;)V", true);
    while (searchHandle != NULL && FPDFText_FindNext(searchHandle)) {
        ++count;
        int startIndex = FPDFText_GetSchResultIndex(searchHandle);
        int endIndex = startIndex + FPDFText_GetSchCount(searchHandle) - 1;
        std::vector<float> list;
        getSelectionRectangles(page, textPage, x, y, width, height, rotation, startIndex, endIndex, list);

        int contextLeftIndex = std::max(startIndex - contextOffset, 0);
        std::shared_ptr<_jstring> leftText;
        if (startIndex > 0) {
            leftText = getJStringText(env, textPage, contextLeftIndex, startIndex - 1);
        } else {
            leftText = StringUtils::newLocalStringUTF(env, "");
        }

        int contextRightIndex = std::min(endIndex + contextOffset, pageTextCount - 1);
        std::shared_ptr<_jstring> rightText;
        if (endIndex < pageTextCount - 1) {
            rightText = getJStringText(env, textPage, endIndex + 1, contextRightIndex);
        } else {
            rightText = StringUtils::newLocalStringUTF(env, "");
        }

        jfloatArray floatArray = env->NewFloatArray(list.size());
        env->SetFloatArrayRegion(floatArray, 0, list.size(), &list[0]);
        env->CallStaticVoidMethod(utils.getClazz(), utils.getMethodId(), objectList, pageIndex, floatArray, array, startIndex, endIndex, leftText.get(), rightText.get());
        env->DeleteLocalRef(floatArray);
    }
    FPDFText_FindClose(searchHandle);
    delete [] stringData;
    return count;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeIsTextPage
  (JNIEnv *env, jobject thiz, jint id, jint pageIndex) {
    FPDF_PAGE page = OnyxPdfiumManager::getPage(env, id, pageIndex);
    if (page == NULL) {
        LOGE("get page failed: %d", pageIndex);
        return false;
    }
    int objCount = FPDFPage_CountObject(page);
    for (int i = 0; i < objCount; i++) {
        CPDF_PageObject *obj = static_cast<CPDF_PageObject *>(FPDFPage_GetObject(page, i));
        if (obj == NULL) {
            LOGE("get page object failed: %d", i);
            return false;
        }
        if (obj->GetType() != FPDF_PAGEOBJ_FORM && obj->GetType() != FPDF_PAGEOBJ_TEXT) {
            return false;
        }
    }
    return true;
}

JNIEXPORT jbyteArray JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeGetPageText
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

JNIEXPORT jobject JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeGetSentence
  (JNIEnv *env, jobject thiz, jint id, jint pageIndex, jint sentenceStartIndex, jobject splitter) {
    FPDF_TEXTPAGE textPage = OnyxPdfiumManager::getTextPage(env, id, pageIndex);
    if (textPage == NULL) {
        LOGE("get text page failed");
       return NULL;
    }
    int count = FPDFText_CountChars(textPage);
    if (count <= 0) {
        return NULL;
    }

    int limit = std::min(200, count - sentenceStartIndex);
    int lastIndex = sentenceStartIndex + limit - 1;
    LOGE("text range: %d, %d", sentenceStartIndex, lastIndex);
    std::shared_ptr<_jstring> text = getJStringText(env, textPage, sentenceStartIndex, lastIndex);
    int index = getTextSentenceBreakPoint(env, splitter, text.get());
    LOGE("sentence break point: %d", index);
    if (index > 0) {
        lastIndex = sentenceStartIndex + index;
    }
    LOGE("sentence range: %d, %d", sentenceStartIndex, lastIndex);

    FPDF_PAGE page = OnyxPdfiumManager::getPage(env, id, pageIndex);
    if (page == NULL) {
        LOGE("get page failed");
    }

    jobject selection = createEmptySelection(env, pageIndex);
    if (selection == NULL) {
        LOGE("createEmptySelection failed");
        return NULL;
    }
    reportSelection(env, page, textPage, 0, 0, 0, 0, 0, sentenceStartIndex, lastIndex, selection);

    JNIUtils utils(env);
    if (!utils.findStaticMethod(sentenceClassName, "create", "(Lcom/onyx/android/sdk/reader/api/ReaderSelection;IZZ)Lcom/onyx/android/sdk/reader/api/ReaderSentence;", true)) {
        return NULL;
    }

    FPDF_DOCUMENT document = OnyxPdfiumManager::getDocument(env, id);
    if (document == NULL) {
        LOGE("getDocument failed");
        return 0;
    }
    int pageCount = FPDF_GetPageCount(document);

    bool pageEnd = (lastIndex >= (count - 1));
    bool docEnd = pageEnd && (pageIndex >= (pageCount - 1));
    LOGE("pageEnd: %d, docEnd: %d", pageEnd, docEnd);

    return env->CallStaticObjectMethod(utils.getClazz(), utils.getMethodId(), selection, lastIndex + 1, pageEnd, docEnd);
}

static bool buildTableOfContentTree(JNIEnv *env, jclass entryClazz, jmethodID addEntryMethodID, jobject parent, FPDF_DOCUMENT doc, FPDF_BOOKMARK entry) {
    FPDF_DEST dest = FPDFBookmark_GetDest(doc, entry);
    int pageIndex = dest ? FPDFDest_GetPageIndex(doc, dest) : -1;

    long len = FPDFBookmark_GetTitle(entry, NULL, 0) / 2;
    std::vector<unsigned short> buf(len);
    FPDFBookmark_GetTitle(entry, (void *)buf.data(), len * 2);
    jstring title = env->NewString((jchar *)buf.data(), len - 1);
    jobject tocEntry = env->CallStaticObjectMethod(entryClazz, addEntryMethodID, parent, title, pageIndex);
    env->DeleteLocalRef(title);

    FPDF_BOOKMARK child = FPDFBookmark_GetFirstChild(doc, entry);
    while (child != NULL) {
        if (!buildTableOfContentTree(env, entryClazz, addEntryMethodID, tocEntry, doc, child)) {
            env->DeleteLocalRef(tocEntry);
            return false;
        }
        child = FPDFBookmark_GetNextSibling(doc, child);
    }

    env->DeleteLocalRef(tocEntry);
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeGetTableOfContent
  (JNIEnv *env, jobject thiz, jint id, jobject rootEntry) {
    FPDF_DOCUMENT doc = OnyxPdfiumManager::getDocument(env, id);
    if (doc == NULL) {
        return false;
    }

    FPDF_BOOKMARK bookmark = FPDFBookmark_GetFirstChild(doc, NULL);
    if (bookmark == NULL) {
        return false;
    }

    JNILocalRef<jclass> clz = JNILocalRef<jclass>(env, env->GetObjectClass(rootEntry));
    jmethodID addEntryMethodID = env->GetStaticMethodID(clz.getValue(), "addEntry", "(Lcom/onyx/android/sdk/reader/api/ReaderDocumentTableOfContentEntry;Ljava/lang/String;I)Lcom/onyx/android/sdk/reader/api/ReaderDocumentTableOfContentEntry;");

    do {
        if (!buildTableOfContentTree(env, clz.getValue(), addEntryMethodID, rootEntry, doc, bookmark)) {
            return false;
        }
        bookmark = FPDFBookmark_GetNextSibling(doc, bookmark);
    } while (bookmark != NULL);

    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeGetPageLinks
  (JNIEnv *env, jobject, jint id, jint pageIndex, jobject objectList) {
    FPDF_DOCUMENT doc = OnyxPdfiumManager::getDocument(env, id);
    if (doc == NULL) {
        return false;
    }
    FPDF_PAGE page = OnyxPdfiumManager::getPage(env, id, pageIndex);
    if (page == NULL) {
        return false;
    }

    JNIUtils utils(env);
    if (!utils.findStaticMethod(selectionClassName, "addToSelectionList", "(Ljava/util/List;I[F[BIILjava/lang/String;Ljava/lang/String;)V", true)) {
        return false;
    }

    int startPos = 0;
    FPDF_LINK link = nullptr;
    while (FPDFLink_Enumerate(page, &startPos, &link)) {
        FPDF_DEST dest = FPDFLink_GetDest(doc, link);
        if (dest == NULL) {
            FPDF_ACTION action = FPDFLink_GetAction(link);
            if (action == NULL || FPDFAction_GetType(action) != PDFACTION_GOTO) {
                continue;
            }
            FPDF_DEST dest = FPDFAction_GetDest(doc, action);
            if (dest == NULL) {
                continue;
            }
        }
        auto destPage = FPDFDest_GetPageIndex(doc, dest);
        FS_RECTF rect;
        if (!FPDFLink_GetAnnotRect(link, &rect)) {
            continue;
        }
        double newLeft, newRight, newBottom, newTop;
        double pageWidth = FPDF_GetPageWidth(page);
        double pageHeight = FPDF_GetPageHeight(page);
        int rotation = 0;
        pageToDevice(page, pageWidth, pageHeight, rotation,
                     rect.left, rect.top, rect.right, rect.bottom,
                     &newLeft, &newTop, &newRight, &newBottom);
        std::vector<float> list;
        list.push_back(static_cast<float>(newLeft));
        list.push_back(static_cast<float>(newTop));
        list.push_back(static_cast<float>(newRight));
        list.push_back(static_cast<float>(newBottom));

        jfloatArray floatArray = env->NewFloatArray(list.size());
        env->SetFloatArrayRegion(floatArray, 0, list.size(), &list[0]);
        env->CallStaticVoidMethod(utils.getClazz(), utils.getMethodId(), objectList, destPage, floatArray, nullptr, -1, -1, nullptr, nullptr);
    }

    return true;
}

