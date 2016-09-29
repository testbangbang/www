#include "com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper.h"

#include <vector>

#include "onyx_context.h"
#include "JNIUtils.h"

#include "pdfwriter/onyx_pdf_writer.h"
#include "pdfwriter/page_annotation.h"
#include "pdfwriter/page_scribble.h"

#include "fpdf_doc.h"

#include "fpdfapi/fpdf_pageobj.h"
#include "fpdf_edit.h"

#include <memory>

static const char * selectionClassName = "com/onyx/kreader/plugins/neopdf/NeoPdfSelection";
static const char * splitterClassName = "com/onyx/kreader/api/ReaderTextSplitter";
static const char * sentenceClassName = "com/onyx/kreader/api/ReaderSentence";
static const char * annotationClassName = "com/onyx/android/sdk/data/model/Annotation";

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
 * Class:     com_onyx_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeInitLibrary
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeInitLibrary
  (JNIEnv *, jobject thiz) {
    if (libraryReference++ <= 0) {
        FPDF_InitLibrary();
    }
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeDestroyLibrary
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
JNIEXPORT jlong JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeOpenDocument
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

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeCloseDocument
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

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeMetadata
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

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativePageSize
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

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeClearBitmap
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

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeRenderPage
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

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativePageCount
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

static int getSelectionRectangles(FPDF_PAGE page, FPDF_TEXTPAGE textPage, int x, int y, int width, int height, int rotation, int start, int end, std::vector<int> & list) {
    double left, right, bottom, top;
    int newLeft, newRight, newBottom, newTop;
    int pageWidth = FPDF_GetPageWidth(page);
    int pageHeight = FPDF_GetPageHeight(page);
    int count = end - start + 1;
    for(int i = 0; i < count; ++i) {
        FPDFText_GetCharBox(textPage, i + start, &left, &right, &bottom, &top);
        // convert page's left-bottom origin to screen's left-top origin
        // but there are some documents we can't get normalized coordinates simply by subtracting with page width/height,
        // so it's safer to use pdfium's built-in FPDF_PageToDevice()
        FPDF_PageToDevice(page, 0, 0, pageWidth, pageHeight, rotation, left, top, &newLeft, &newTop);
        FPDF_PageToDevice(page, 0, 0, pageWidth, pageHeight, rotation, right, bottom, &newRight, &newBottom);
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

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeHitTest
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
            LOGE("selectByWord finished");
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

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeSelection
  (JNIEnv *env, jobject thiz, jint id, jint pageIndex, jint x, jint y, jint width, jint height, jint rotation, jint startIndex, jint endIndex, jobject selection) {
    FPDF_PAGE page = OnyxPdfiumManager::getPage(env, id, pageIndex);
    FPDF_TEXTPAGE textPage = OnyxPdfiumManager::getTextPage(env, id, pageIndex);
    if (page == NULL || textPage == NULL) {
        return 0;
    }
    return reportSelection(env, page, textPage, x, y, width, height, rotation, startIndex, endIndex, selection);
}

JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeSearchInPage
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
    utils.findStaticMethod(selectionClassName, "addToSelectionList", "(Ljava/util/List;I[I[BIILjava/lang/String;Ljava/lang/String;)V");
    while (searchHandle != NULL && FPDFText_FindNext(searchHandle)) {
        ++count;
        int startIndex = FPDFText_GetSchResultIndex(searchHandle);
        int endIndex = startIndex + FPDFText_GetSchCount(searchHandle) - 1;
        std::vector<int> list;
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

        JNIIntArray intArray(env, list.size(), &list[0]);
        env->CallStaticVoidMethod(utils.getClazz(), utils.getMethodId(), objectList, pageIndex, intArray.getIntArray(true), array, startIndex, endIndex, leftText.get(), rightText.get());
    }
    FPDFText_FindClose(searchHandle);
    delete [] stringData;
    return count;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeIsTextPage
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
        if (obj->m_Type != FPDF_PAGEOBJ_FORM && obj->m_Type != FPDF_PAGEOBJ_TEXT) {
            return false;
        }
    }
    return true;
}

JNIEXPORT jbyteArray JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeGetPageText
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

JNIEXPORT jobject JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeGetSentence
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
    if (!utils.findStaticMethod(sentenceClassName, "create", "(Lcom/onyx/kreader/api/ReaderSelection;IZZ)Lcom/onyx/kreader/api/ReaderSentence;")) {
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

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeGetTableOfContent
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
    jmethodID addEntryMethodID = env->GetStaticMethodID(clz.getValue(), "addEntry", "(Lcom/onyx/kreader/api/ReaderDocumentTableOfContentEntry;Ljava/lang/String;I)Lcom/onyx/kreader/api/ReaderDocumentTableOfContentEntry;");

    do {
        if (!buildTableOfContentTree(env, clz.getValue(), addEntryMethodID, rootEntry, doc, bookmark)) {
            return false;
        }
        bookmark = FPDFBookmark_GetNextSibling(doc, bookmark);
    } while (bookmark != NULL);

    return true;
}

bool convertToNativeRectF(JNIEnv *env, jobject rect, RectF *result) {
    JNIUtils utils(env);
    if (!utils.getObjectClass(rect)) {
        return false;
    }
    jfieldID fid = env->GetFieldID(utils.getClazz(), "left", "F");
    if (fid == NULL) {
        return false;
    }
    float left = env->GetFloatField(rect, fid);
    fid = env->GetFieldID(utils.getClazz(), "top", "F");
    if (fid == NULL) {
        return false;
    }
    float top = env->GetFloatField(rect, fid);
    fid = env->GetFieldID(utils.getClazz(), "right", "F");
    if (fid == NULL) {
        return false;
    }
    float right = env->GetFloatField(rect, fid);
    fid = env->GetFieldID(utils.getClazz(), "bottom", "F");
    if (fid == NULL) {
        return false;
    }
    float bottom = env->GetFloatField(rect, fid);
    result->set(left, top, right, bottom);
    return true;
}

bool convertToPageAnnotation(JNIEnv *env, jobject annotation, PageAnnotation *result) {
    JNIUtils utils(env);
    if (!utils.findMethod(annotationClassName, "getPageNumber", "()I")) {
        return false;
    }
    result->page = env->CallIntMethod(annotation, utils.getMethodId());

    if (!utils.findMethod(annotationClassName, "getNote", "()Ljava/lang/String;")) {
        return false;
    }
    jstring jstr = static_cast<jstring>(env->CallObjectMethod(annotation, utils.getMethodId()));
    if (!jstr) {
        LOGE("get annotation note failed");
        return false;
    }
    result->note = JNIString(env, jstr).getLocalString();

    if (!utils.findMethod(annotationClassName, "getRectangles", "()Ljava/util/List;")) {
        return false;
    }
    jobject rectList = env->CallObjectMethod(annotation, utils.getMethodId());
    if (!rectList) {
        LOGE("get rect list of annotation failed");
        return false;
    }
    if (!utils.findMethod("java/util/List", "size", "()I")) {
        return false;
    }
    int size = env->CallIntMethod(rectList, utils.getMethodId());
    if (size <= 0) {
        return true;
    }
    if (!utils.findMethod("java/util/List", "get", "(I)Ljava/lang/Object;")) {
        return false;
    }
    for (int i = 0; i < size; i++) {
        jobject rectObj = env->CallObjectMethod(rectList, utils.getMethodId(), i);
        RectF rect;
        if (!convertToNativeRectF(env, rectObj, &rect)) {
            LOGE("convertToNativeRectF failed");
            return false;
        }
        result->rects.push_back(rect);
    }

    return true;
}

bool convertToNativeAnnotations(JNIEnv *env, jobject annotationList, std::vector<PageAnnotation> *result) {
    JNIUtils utils(env);
    if (!utils.findMethod("java/util/List", "size", "()I")) {
        return false;
    }
    int size = env->CallIntMethod(annotationList, utils.getMethodId());
    if (size <= 0) {
        return true;
    }
    if (!utils.findMethod("java/util/List", "get", "(I)Ljava/lang/Object;")) {
        return false;
    }

    for (int i = 0; i < size; i++) {
        jobject annot = env->CallObjectMethod(annotationList, utils.getMethodId(), i);
        PageAnnotation pageAnnot;
        if (!convertToPageAnnotation(env, annot, &pageAnnot)) {
            LOGE("convertToPageAnnotation failed");
            return false;
        }
        result->push_back(pageAnnot);
    }

    return true;
}

bool convertToNativePointF(JNIEnv *env, jobject touchPoint, PointF *result) {
    JNIUtils utils(env);
    if (!utils.getObjectClass(touchPoint)) {
        return false;
    }
    jfieldID fid = env->GetFieldID(utils.getClazz(), "x", "F");
    if (fid == NULL) {
        return false;
    }
    float x = env->GetFloatField(touchPoint, fid);
    fid = env->GetFieldID(utils.getClazz(), "y", "F");
    if (fid == NULL) {
        return false;
    }
    float y = env->GetFloatField(touchPoint, fid);
    result->set(x, y);
    return true;
}

bool convertToPageScribble(JNIEnv *env, jobject scribble, PageScribble *result) {
    JNIUtils utils(env);
    if (!utils.getObjectClass(scribble)) {
        return false;
    }

    if (!utils.findMethod("getStrokeWidth", "()F")) {
        return false;
    }
    float strokeWidth = env->CallFloatMethod(scribble, utils.getMethodId());

    if (!utils.findMethod("getBoundingRect", "()Landroid/graphics/RectF;")) {
        return false;
    }
    jobject rectObj = env->CallObjectMethod(scribble, utils.getMethodId());
    if (rectObj == NULL) {
        LOGE("get bounding rect of scribble failed");
        return false;
    }
    RectF rect;
    if (!convertToNativeRectF(env, rectObj, &rect)) {
        LOGE("convertToNativeRectF failed");
        return false;
    }

    if (!utils.findMethod("getPoints", "()Lcom/onyx/android/sdk/scribble/data/TouchPointList;")) {
        return false;
    }

    jobject touchPointList = env->CallObjectMethod(scribble, utils.getMethodId());
    if (touchPointList == NULL) {
        return false;
    }

    if (!utils.getObjectClass(touchPointList)) {
        return false;
    }
    if (!utils.findMethod("getPoints", "()Ljava/util/List;")) {
        return false;
    }
    jobject pointList = env->CallObjectMethod(touchPointList, utils.getMethodId());
    if (pointList == NULL) {
        // no points in the scribble, just skip this scribble
        return true;
    }

    if (!utils.getObjectClass(pointList)) {
        return false;
    }
    if (!utils.findMethod("size", "()I")) {
        return false;
    }
    int size = env->CallIntMethod(pointList, utils.getMethodId());
    if (size <= 0) {
        return true;
    }
    if (!utils.findMethod("get", "(I)Ljava/lang/Object;")) {
        return false;
    }

    PageScribble::Stroke stroke;
    for (int i = 0; i < size; i++) {
        jobject pointObj = env->CallObjectMethod(pointList, utils.getMethodId(), i);
        PointF point;
        if (!convertToNativePointF(env, pointObj, &point)) {
            LOGE("convertToNativePointF failed");
            return false;
        }
        stroke.points.push_back(point);
    }

    stroke.thickness = strokeWidth;
    stroke.rect = rect;
    result->strokes.push_back(stroke);
    return true;
}

int getScribblePageNumber(JNIEnv *env, jobject scribble) {
    JNIUtils utils(env);
    if (!utils.getObjectClass(scribble)) {
        return -1;
    }

    if (!utils.findMethod("getPageUniqueId", "()Ljava/lang/String;")) {
        return -1;
    }
    jstring pageName = static_cast<jstring>(env->CallObjectMethod(scribble, utils.getMethodId()));
    if (pageName == NULL) {
        return -1;
    }
    JNIString str(env, pageName);
    int page = std::atoi(str.getLocalString());
    return page;
}

bool convertToNativeScribbles(JNIEnv *env, jobject scribbleList, std::vector<PageScribble> *result) {
    JNIUtils utils(env);
    if (!utils.findMethod("java/util/List", "size", "()I")) {
        return false;
    }
    int size = env->CallIntMethod(scribbleList, utils.getMethodId());
    if (size <= 0) {
        return true;
    }
    if (!utils.findMethod("java/util/List", "get", "(I)Ljava/lang/Object;")) {
        return false;
    }

    std::map<int, std::shared_ptr<PageScribble>> pageScribbles;
    for (int i = 0; i < size; i++) {
        jobject scribble = env->CallObjectMethod(scribbleList, utils.getMethodId(), i);
        int page = getScribblePageNumber(env, scribble);
        if (page < 0) {
            LOGE("get scribble page failed");
            return false;
        }
        if (pageScribbles.find(page) == pageScribbles.end()) {
            PageScribble *scribble = new PageScribble();
            scribble->page = page;
            pageScribbles[page] = std::shared_ptr<PageScribble>(scribble);
        }
        std::shared_ptr<PageScribble> pageScribble = pageScribbles[page];
        if (!convertToPageScribble(env, scribble, pageScribble.get())) {
            LOGE("convertToPageAnnotation failed");
            return false;
        }
    }
    for (auto pageScribble : pageScribbles) {
        result->push_back(*pageScribble.second);
    }

    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_neopdf_NeoPdfJniWrapper_nativeExportNotes
  (JNIEnv *env, jobject thiz, jint id, jstring sourceDocPath, jstring targetDocPath, jobject annotationList, jobject scribbleList) {
    std::vector<PageAnnotation> pageAnnotations;
    if (!convertToNativeAnnotations(env, annotationList, &pageAnnotations)) {
        LOGE("convertToNativeAnnotations failed");
        return false;
    }
    std::vector<PageScribble> pageScribbles;
    if (!convertToNativeScribbles(env, scribbleList, &pageScribbles)) {
        LOGE("convertToNativeScribbles failed");
        return false;
    }

    JNIString src(env, sourceDocPath);
    JNIString dst(env, targetDocPath);
    OnyxPdfWriter writer;
    if (!writer.openPDF(src.getLocalString())) {
        LOGE("open pdf for writing failed");
        return false;
    }
    if (!writer.writeAnnotations(pageAnnotations)) {
        LOGE("write annotations failed");
        return false;
    }
    if (!writer.writeScribbles(pageScribbles)) {
        LOGE("write scribbles failed");
        return false;
    }
    if (!writer.saveAs(dst.getLocalString())) {
        LOGE("save exported document failed");
        return false;
    }
    writer.close();
    return true;
}
