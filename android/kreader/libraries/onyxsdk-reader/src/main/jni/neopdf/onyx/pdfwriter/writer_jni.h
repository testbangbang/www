/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_onyx_android_sdk_reader_utils_PdfWriterUtils */

#ifndef _Included_com_onyx_android_sdk_reader_utils_PdfWriterUtils
#define _Included_com_onyx_android_sdk_reader_utils_PdfWriterUtils
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_onyx_android_sdk_reader_utils_PdfWriterUtils
 * Method:    openExistingDocument
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_openExistingDocument
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_onyx_android_sdk_reader_utils_PdfWriterUtils
 * Method:    createNewDocument
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_createNewDocument
  (JNIEnv *, jclass);

/*
 * Class:     com_onyx_android_sdk_reader_utils_PdfWriterUtils
 * Method:    writeHighlight
 * Signature: (ILjava/lang/String;[F)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_writeHighlight
  (JNIEnv *, jclass, jint, jstring, jfloatArray);

/*
 * Class:     com_onyx_android_sdk_reader_utils_PdfWriterUtils
 * Method:    writeLine
 * Signature: (I[FIFFFFF)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_writeLine
  (JNIEnv *, jclass, jint, jfloatArray, jint, jfloat, jfloat, jfloat, jfloat, jfloat);

/*
 * Class:     com_onyx_android_sdk_reader_utils_PdfWriterUtils
 * Method:    writePolyLine
 * Signature: (I[FIF[F)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_writePolyLine
  (JNIEnv *, jclass, jint, jfloatArray, jint, jfloat, jfloatArray);

/*
 * Class:     com_onyx_android_sdk_reader_utils_PdfWriterUtils
 * Method:    writePolygon
 * Signature: (I[FIF[F)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_writePolygon
  (JNIEnv *, jclass, jint, jfloatArray, jint, jfloat, jfloatArray);

/*
 * Class:     com_onyx_android_sdk_reader_utils_PdfWriterUtils
 * Method:    writeSquare
 * Signature: (I[FIF)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_writeSquare
  (JNIEnv *, jclass, jint, jfloatArray, jint, jfloat);

/*
 * Class:     com_onyx_android_sdk_reader_utils_PdfWriterUtils
 * Method:    writeCircle
 * Signature: (I[FIF)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_writeCircle
  (JNIEnv *, jclass, jint, jfloatArray, jint, jfloat);

/*
 * Class:     com_onyx_android_sdk_reader_utils_PdfWriterUtils
 * Method:    saveAs
 * Signature: (Ljava/lang/String;Z)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_saveAs
  (JNIEnv *, jclass, jstring, jboolean);

/*
 * Class:     com_onyx_android_sdk_reader_utils_PdfWriterUtils
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_close
  (JNIEnv *, jclass);

/*
 * Class:     com_onyx_android_sdk_reader_utils_PdfWriterUtils
 * Method:    setDocumentTitle
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_setDocumentTitle
  (JNIEnv *, jclass, jstring, jstring);

#ifdef __cplusplus
}
#endif
#endif
