#include "writer_jni.h"

#include "JNIUtils.h"

#include "onyx_pdf_writer.h"
#include "page_annotation.h"
#include "page_scribble.h"

#include <android/log.h>

#define LOG_TAG "onyx_pdfwriter"

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

jboolean Java_com_onyx_kreader_utils_PdfWriterUtils_openExistingDocument(JNIEnv *env, jclass, jstring path)
{
    if (g_writer.isOpened()) {
        g_writer.close();
    }

    JNIString string(env, path);
    return g_writer.openPDF(string.getLocalString());
}

jboolean Java_com_onyx_kreader_utils_PdfWriterUtils_createNewDocument(JNIEnv *, jclass)
{
    if (g_writer.isOpened()) {
        g_writer.close();
    }
    return false;
}

jboolean Java_com_onyx_kreader_utils_PdfWriterUtils_writeHighlight(JNIEnv *env, jclass, jint page, jstring noteString, jfloatArray quadPointArray)
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

jboolean Java_com_onyx_kreader_utils_PdfWriterUtils_writePolyLine(JNIEnv *env, jclass, jint page, jfloatArray rectArray, jint color, jfloat strokeWidth, jfloatArray verticeArray)
{
    if (!g_writer.isOpened()) {
        return false;
    }

    PageScribble::Stroke stroke;
    stroke.color = static_cast<uint32_t>(color);
    stroke.thickness = strokeWidth;
    if (!readRectFromArray(env, rectArray, &stroke.rect)) {
        return false;
    }
    if (!readPointsFromArray(env, verticeArray, &stroke.points)) {
        return false;
    }

    PageScribble scribble;
    scribble.page = page;
    scribble.strokes.push_back(stroke);

    return g_writer.writeScribble(scribble);
}

jboolean Java_com_onyx_kreader_utils_PdfWriterUtils_saveAs(JNIEnv *env, jclass, jstring pathString)
{
    if (!g_writer.isOpened()) {
        return false;
    }

    JNIString string(env, pathString);
    return g_writer.saveAs(string.getLocalString());
}

void Java_com_onyx_kreader_utils_PdfWriterUtils_close(JNIEnv *, jclass)
{
    if (!g_writer.isOpened()) {
        return;
    }
    g_writer.close();
}
