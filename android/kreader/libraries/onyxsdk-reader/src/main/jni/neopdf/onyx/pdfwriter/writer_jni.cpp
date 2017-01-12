#include "writer_jni.h"

#include "JNIUtils.h"

#include "onyx_pdf_writer.h"
#include "page_annotation.h"
#include "page_scribble.h"

#include <android/log.h>

#define LOG_TAG "neo_pdfwriter"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGT(...) __android_log_print(ANDROID_LOG_INFO,"alert",__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

namespace {

bool readRectFromArray(JNIEnv *env, jfloatArray array, RectF *rect) {
    jsize len = env->GetArrayLength(array);
    if (len != 4) {
        LOGE("invalid rect array");
        return false;
    }
    jfloat *buf = env->GetFloatArrayElements(array, NULL);
    if (!buf) {
        return false;
    }
    rect->set(buf[0], buf[1], buf[2], buf[3]);
    env->ReleaseFloatArrayElements(array, buf, 0);
    return true;
}

bool readPointsFromArray(JNIEnv *env, jfloatArray array, std::vector<PointF> *points) {
    jsize len = env->GetArrayLength(array);
    if (len % 2) {
        LOGE("invalid point array");
        return false;
    }
    jfloat *buf = env->GetFloatArrayElements(array, NULL);
    if (!buf) {
        return false;
    }
    for (jsize i = 0; i < len - 1; i+= 2) {
        points->push_back(PointF(buf[i], buf[i + 1]));
    }
    env->ReleaseFloatArrayElements(array, buf, 0);
    return true;
}

OnyxPdfWriter g_writer;

}

jboolean Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_openExistingDocument(JNIEnv *env, jclass, jstring path)
{
    if (g_writer.isOpened()) {
        g_writer.close();
    }

    JNIString string(env, path);
    return g_writer.openPDF(string.getLocalString());
}

jboolean Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_createNewDocument(JNIEnv *, jclass)
{
    if (g_writer.isOpened()) {
        g_writer.close();
    }
    return false;
}

jboolean Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_writeHighlight(JNIEnv *env, jclass, jint page, jstring noteString, jfloatArray quadPointArray)
{
    if (!g_writer.isOpened()) {
        return false;
    }

    JNIString string(env, noteString);
    std::string note(string.getLocalString());

    std::vector<PointF> points;
    if (!readPointsFromArray(env, quadPointArray, &points)) {
        return false;
    }

    PageAnnotation annotation;
    annotation.page = page;
    annotation.note = note;

    for (size_t i = 0; i < points.size() - 1; i += 2) {
        annotation.rects.push_back(RectF(points[i], points[i + 1]));
    }

    return g_writer.writeAnnotation(annotation);
}

jboolean Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_writeLine(JNIEnv *env, jclass, jint page, jfloatArray rectArray, jint color, jfloat strokeThickness, jfloat startX, jfloat startY, jfloat endX, jfloat endY)
{
    if (!g_writer.isOpened()) {
        return false;
    }

    RectF rect;
    if (!readRectFromArray(env, rectArray, &rect)) {
        return false;
    }
    return g_writer.writeLine(page, rect, static_cast<uint_t>(color), strokeThickness,
                              PointF(startX, startY), PointF(endX, endY));
}

jboolean Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_writePolyLine(JNIEnv *env, jclass, jint page, jfloatArray rectArray, jint color, jfloat strokeThickness, jfloatArray verticeArray)
{
    if (!g_writer.isOpened()) {
        return false;
    }

    PageScribble::Stroke stroke;
    stroke.color = static_cast<uint32_t>(color);
    stroke.thickness = strokeThickness;

    RectF rect;
    if (!readRectFromArray(env, rectArray, &rect)) {
        return false;
    }
    std::vector<PointF> points;
    if (!readPointsFromArray(env, verticeArray, &points)) {
        return false;
    }
    return g_writer.writePolyLine(page, rect, static_cast<uint_t>(color), strokeThickness, points);
}

jboolean Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_writePolygon(JNIEnv *env, jclass, jint page, jfloatArray rectArray, jint color, jfloat strokeThickness, jfloatArray verticeArray)
{
    if (!g_writer.isOpened()) {
        return false;
    }

    PageScribble::Stroke stroke;
    stroke.color = static_cast<uint32_t>(color);
    stroke.thickness = strokeThickness;

    RectF rect;
    if (!readRectFromArray(env, rectArray, &rect)) {
        return false;
    }
    std::vector<PointF> points;
    if (!readPointsFromArray(env, verticeArray, &points)) {
        return false;
    }
    return g_writer.writePolygon(page, rect, static_cast<uint_t>(color), strokeThickness, points);
}

jboolean Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_writeSquare(JNIEnv *env, jclass, jint page, jfloatArray rectArray, jint color, jfloat strokeThickness)
{
    if (!g_writer.isOpened()) {
        return false;
    }

    RectF rect;
    if (!readRectFromArray(env, rectArray, &rect)) {
        return false;
    }
    return g_writer.writeSquare(page, rect, static_cast<uint_t>(color), strokeThickness);
}

jboolean Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_writeCircle(JNIEnv *env, jclass, jint page, jfloatArray rectArray, jint color, jfloat strokeThickness)
{
    if (!g_writer.isOpened()) {
        return false;
    }

    RectF rect;
    if (!readRectFromArray(env, rectArray, &rect)) {
        return false;
    }
    return g_writer.writeCircle(page, rect, static_cast<uint_t>(color), strokeThickness);
}

jboolean Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_saveAs(JNIEnv *env, jclass, jstring pathString, jboolean savePagesWithAnnotation)
{
    if (!g_writer.isOpened()) {
        return false;
    }

    JNIString string(env, pathString);
    return g_writer.saveAs(string.getLocalString(), savePagesWithAnnotation);
}

void Java_com_onyx_android_sdk_reader_utils_PdfWriterUtils_close(JNIEnv *, jclass)
{
    if (!g_writer.isOpened()) {
        return;
    }
    g_writer.close();
}
