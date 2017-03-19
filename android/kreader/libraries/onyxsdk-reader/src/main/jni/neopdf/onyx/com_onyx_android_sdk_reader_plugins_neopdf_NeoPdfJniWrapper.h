/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper */

#ifndef _Included_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
#define _Included_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeInitLibrary
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeInitLibrary
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeDestroyLibrary
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeDestroyLibrary
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeOpenDocument
 * Signature: (ILjava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeOpenDocument
  (JNIEnv *, jobject, jint, jstring, jstring);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeCloseDocument
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeCloseDocument
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeMetadata
 * Signature: (ILjava/lang/String;[B)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeMetadata
  (JNIEnv *, jobject, jint, jstring, jbyteArray);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativePageCount
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativePageCount
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativePageSize
 * Signature: (II[F)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativePageSize
  (JNIEnv *, jobject, jint, jint, jfloatArray);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeRenderPage
 * Signature: (IIIIIIILandroid/graphics/Bitmap;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeRenderPage
  (JNIEnv *, jobject, jint, jint, jint, jint, jint, jint, jint, jobject);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeHitTest
 * Signature: (IIIIIIIIIIILcom/onyx/android/sdk/reader/api/ReaderTextSplitter;ZLcom/onyx/android/sdk/reader/plugins/neopdf/NeoPdfSelection;)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeHitTest
  (JNIEnv *, jobject, jint, jint, jint, jint, jint, jint, jint, jint, jint, jint, jint, jobject, jboolean, jobject);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeSelection
 * Signature: (IIIIIIIIILcom/onyx/android/sdk/reader/plugins/neopdf/NeoPdfSelection;)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeSelection
  (JNIEnv *, jobject, jint, jint, jint, jint, jint, jint, jint, jint, jint, jobject);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeSearchInPage
 * Signature: (IIIIIII[BZZILjava/util/List;)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeSearchInPage
  (JNIEnv *, jobject, jint, jint, jint, jint, jint, jint, jint, jbyteArray, jboolean, jboolean, jint, jobject);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeIsTextPage
 * Signature: (II)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeIsTextPage
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeGetPageText
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeGetPageText
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeGetSentence
 * Signature: (IIILcom/onyx/android/sdk/reader/api/ReaderTextSplitter;)Lcom/onyx/android/sdk/reader/api/ReaderSentence;
 */
JNIEXPORT jobject JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeGetSentence
  (JNIEnv *, jobject, jint, jint, jint, jobject);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeGetTableOfContent
 * Signature: (ILcom/onyx/android/sdk/reader/api/ReaderDocumentTableOfContentEntry;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeGetTableOfContent
  (JNIEnv *, jobject, jint, jobject);

/*
 * Class:     com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper
 * Method:    nativeGetPageLinks
 * Signature: (IILjava/util/List;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeGetPageLinks
  (JNIEnv *, jobject, jint, jint, jobject);

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_neopdf_NeoPdfJniWrapper_nativeGetPageTextRegions
  (JNIEnv *, jobject, jint, jint, jobject);



#ifdef __cplusplus
}
#endif
#endif
