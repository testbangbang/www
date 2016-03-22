#include <jni.h>
#include <time.h>
#include <pthread.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <list>

#include <cassert>
#include <cstring>
#include <string>

#ifdef __cplusplus
extern "C" {
#endif

#include "willus.h"
#include "k2pdfopt.h"
#include "context.h"
#include "setting.h"

#ifdef __cplusplus
}
#endif



#ifdef NDK_PROFILER
#include "prof.h"
#endif

#include "com_onyx_kreader_utils_ReaderImageUtils.h"
#include "image_embolden_filter.h"
#include "image_gamma_filter.h"

static const char * readerBitmapClassName = "com/onyx/reader/ReaderScannedPageReflowManager";

#define COLUMN_HALF_HEIGHT 15
#define V_LINE_SIZE 5
#define H_LINE_SIZE 5
#define LINE_MARGIN 20
#define COLUMN_WIDTH 5

static double WHITE_THRESHOLD  = 0.05;

#define MAX(a,b) (((a) > (b)) ? (a) : (b))
#define MIN(a,b) (((a) < (b)) ? (a) : (b))

#define LOG_TAG "libonyx_cropper"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGT(...) __android_log_print(ANDROID_LOG_INFO,"alert",__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

int calculateAvgLum(uint8_t* src, int width, int height, int sub_x, int sub_y, int sub_w, int sub_h);
float getLeftBound(uint8_t* src, int width, int height, int avgLum);
float getLeftColumnBound(uint8_t* src, int width, int height, int avgLum, float x, float y);
float getTopBound(uint8_t* src, int width, int height, int avgLum);
float getRightBound(uint8_t* src, int width, int height, int avgLum);
float getRightColumnBound(uint8_t* src, int width, int height, int avgLum, float x, float y);
float getBottomBound(uint8_t* src, int width, int height, int avgLum);
int isRectWhite(uint8_t* src, int width, int height, int sub_x, int sub_y, int sub_w, int sub_h, int avgLum);

int calculateAvgLum(uint8_t* src, int width, int height, int sub_x, int sub_y, int sub_w, int sub_h) {
    int i, a;
    int midBright = 0;

    int x, y;
    for (y = 0; y < sub_h; y++) {
        for (x = 0; x < sub_w; x++) {
            i = ((y + sub_y) * width + sub_x + x) * 4;
            midBright += (MIN(src[i+2], MIN(src[i + 1],src[i])) + MAX(src[i+2], MAX(src[i + 1], src[i]))) / 2;
        }
    }
    midBright /= (sub_w * sub_h);
    return midBright;
}

float getLeftBound(uint8_t* src, int width, int height, int avgLum) {
    int w = width / 3;
    int whiteCount = 0;
    int x = 0;

    for (x = 0; x < w; x += V_LINE_SIZE) {
        // LOGI("getLeftBound: %d", x);
        int white = isRectWhite(src, width, height, x, LINE_MARGIN, V_LINE_SIZE, height - 2 * LINE_MARGIN, avgLum);
        if (white) {
            whiteCount++;
        } else {
            if (whiteCount >= 1) {
                return (float) (MAX(0, x - V_LINE_SIZE)) / width;
            }
            whiteCount = 0;
        }
    }
    return whiteCount > 0 ? (float) (MAX(0, x - V_LINE_SIZE)) / width : 0;
}

float getLeftColumnBound(uint8_t* src, int width, int height, int avgLum, float x, float y) {
    int blackFound = 0;
    int pointX = (int) (width * x);
    int pointY = (int) (height * y);
    int top = MAX(0, pointY - COLUMN_HALF_HEIGHT);
    int bottom = MIN(height - 1, pointY + COLUMN_HALF_HEIGHT);

    int left;
    for (left = pointX; left >= 0; left -= COLUMN_WIDTH) {
        if (isRectWhite(src, width, height, left, top, COLUMN_WIDTH, bottom - top, avgLum)) {
            if (blackFound) {
                return ((float) left / width);
            }
        } else {
            blackFound = 1;
        }
    }
    return 0;
}

float getTopBound(uint8_t* src, int width, int height, int avgLum) {
    int h = height / 3;
    int whiteCount = 0;
    int y = 0;

    for (y = 0; y < h; y += H_LINE_SIZE) {
        // LOGI("getTopBound: %d", y);
        int white = isRectWhite(src, width, height, LINE_MARGIN, y, width - 2 * LINE_MARGIN, H_LINE_SIZE, avgLum);
        if (white) {
            whiteCount++;
        } else {
            if (whiteCount >= 1) {
                return (float) (MAX(0, y - H_LINE_SIZE)) / height;
            }
            whiteCount = 0;
        }
    }
    return whiteCount > 0 ? (float) (MAX(0, y - H_LINE_SIZE)) / height : 0;
}

float getRightBound(uint8_t* src, int width, int height, int avgLum) {
    int w = width / 3;
    int whiteCount = 0;
    int x = 0;

    for (x = width - V_LINE_SIZE; x > width - w; x -= V_LINE_SIZE) {
        // LOGI("getRightBound: %d", x);
        int white = isRectWhite(src, width, height, x, LINE_MARGIN, V_LINE_SIZE, height - 2 * LINE_MARGIN, avgLum);
        if (white) {
            whiteCount++;
        } else {
            if (whiteCount >= 1) {
                return (float) (MIN(width, x + 2 * V_LINE_SIZE)) / width;
            }
            whiteCount = 0;
        }
    }
    return whiteCount > 0 ? (float) (MIN(width, x + 2 * V_LINE_SIZE)) / width : 1;
}

float getRightColumnBound(uint8_t* src, int width, int height, int avgLum, float x, float y) {
    int blackFound = 0;
    int pointX = (int) (width * x);
    int pointY = (int) (height * y);
    int top = MAX(0, pointY - COLUMN_HALF_HEIGHT);
    int bottom = MIN(height - 1, pointY + COLUMN_HALF_HEIGHT);

    int left;
    for (left = pointX; left < width - COLUMN_WIDTH; left += COLUMN_WIDTH) {
        if (isRectWhite(src, width, height, left, top, COLUMN_WIDTH, bottom - top, avgLum)) {
            if (blackFound) {
                return ((float) (left + COLUMN_WIDTH) / width);
            }
        } else {
            blackFound = 1;
        }
    }
    return 1;
}

float getBottomBound(uint8_t* src, int width, int height, int avgLum) {
    int h = height / 3;
    int whiteCount = 0;
    int y = 0;
    for (y = height - H_LINE_SIZE; y > height - h; y -= H_LINE_SIZE) {
        int white = isRectWhite(src, width, height, LINE_MARGIN, y, width - 2 * LINE_MARGIN, H_LINE_SIZE, avgLum);
        // LOGI("getBottomBound: %d white %d", y, white);
        if (white) {
            whiteCount++;
        } else {
            if (whiteCount >= 1) {
                return (float) (MIN(height, y + 2 * H_LINE_SIZE)) / height;
            }
            whiteCount = 0;
        }
    }
    return whiteCount > 0 ? (float) (MIN(height, y + 2 * H_LINE_SIZE)) / height : 1;
}

int isRectWhite(uint8_t* src, int width, int height, int sub_x, int sub_y, int sub_w, int sub_h, int avgLum) {
    int count = 0;

    int x, y;
    for (y = 0; y < sub_h; y++) {
        for (x = 0; x < sub_w; x++) {
            int i = ((y + sub_y) * width + sub_x + x) * 4;
            int minLum = MIN(src[i+2],MIN(src[i + 1],src[i]));
            int maxLum = MAX(src[i+2],MAX(src[i + 1],src[i]));
            int lum = (minLum + maxLum) / 2;
            if ((lum < avgLum) && ((avgLum - lum) * 10 > avgLum)) {
                count++;
            }
        }
    }
    float white = (float) count / (sub_w * sub_h);
    //LOGI("White: %f %d %f", white, count, WHITE_THRESHOLD);
    return white < WHITE_THRESHOLD ? 1 : 0;
}

jdoubleArray doubleArrayFromRect(JNIEnv * env, double left, double top, double right, double bottom) {
    const int COUNT = 4;
	jdouble * buffer = new double[COUNT];

    buffer[0] = left;
    buffer[1] = top;
    buffer[2] = right;
    buffer[3] = bottom;

	// copy from buffer to double array.
	jdoubleArray array = env->NewDoubleArray(COUNT);
    env->SetDoubleArrayRegion(array, 0, COUNT, buffer);
    return array;
}

JNIEXPORT jdoubleArray JNICALL Java_com_onyx_reader_ReaderImageUtils_crop(JNIEnv *env, jclass,
    jobject bitmap, jint left, jint top, jint right, jint bottom, jdouble whiteThreshold) {
    AndroidBitmapInfo info;
	void *pixels;
	int ret;

	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return NULL;
	}

	if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
		LOGE("Bitmap format is not RGBA_8888 !");
		return NULL;
	}

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
		return NULL;
	}

    WHITE_THRESHOLD = whiteThreshold;
    uint8_t * src = (uint8_t*)pixels;
    int width = right - left;
    int height = bottom - top;
    int avgLum = calculateAvgLum(src, info.width, info.height, 0, 0, info.width, info.height);

    double coords[4];
    coords[0] = getLeftBound(src, width, height, avgLum) * (right - left) + left;
    coords[1] = getTopBound(src, width, height, avgLum) * (bottom - top) + top;
    coords[2] = getRightBound(src, width, height, avgLum) * (right - left) + left;
    coords[3] = getBottomBound(src, width, height, avgLum) * (bottom - top) + top;
    return doubleArrayFromRect(env, coords[0], coords[1], coords[2], coords[3]);
}

static bool convertToWillusBmp(JNIEnv *env, void * data, jint width, jint height, jint stride, WILLUSBITMAP *bmp) {
    bmp_init(bmp);
    bmp->width = width;
    bmp->height = height;
    bmp->bpp = 24;
    bmp_alloc(bmp);

    unsigned char *src = (unsigned char *)data;
    for (int row = 0; row < height; row++) {
        unsigned char *dst = bmp_rowptr_from_top(bmp, row);
        for (int col = 0; col < width; col++) {
            memcpy(dst, src, 3);
            dst += 3;
            src += 4;
        }
    }
    return true;
}

/*
static jobject createReaderBitmapList(JNIEnv * env) {
    jclass cls_bmp_list = env->FindClass(readerBitmapClassName);
    if (cls_bmp_list == 0) {
        LOGE("Could not find class: %s", readerBitmapClassName);
        return 0;
    }

    jmethodID mid_cons =env->GetMethodID(cls_bmp_list, "<init>", "()V");
    if (mid_cons == 0) {
        LOGE("Find method constructure failed");
        return 0;
    }

    jobject result = env->NewObject(cls_bmp_list, mid_cons);
    if (result == 0) {
        LOGE("OOM: env->NewObject failed");
    }
    return result;
}

*/
/**
 * create context from settings
 */
static bool convertToKoptContext(JNIEnv *env, jobject jSettings, KOPTContext *context) {
    jclass clz_settings = env->GetObjectClass(jSettings);
    if (clz_settings == 0) {
        LOGE("Find class com/onyx/reader/settings failed");
        return false;
    }

    jfieldID fid_dev_dpi = env->GetFieldID(clz_settings, "dev_dpi", "I");
    if (fid_dev_dpi == 0) {
        LOGE("GetFieldID dev_dpi failed");
        return false;
    }
    jfieldID fid_dev_width = env->GetFieldID(clz_settings, "dev_width", "I");
    if (fid_dev_width == 0) {
        LOGE("GetFieldID dev_width failed");
        return false;
    }
    jfieldID fid_dev_height = env->GetFieldID(clz_settings, "dev_height", "I");
    if (fid_dev_height == 0) {
        LOGE("GetFieldID dev_height failed");
        return false;
    }
    jfieldID fid_page_width = env->GetFieldID(clz_settings, "page_width", "I");
    if (fid_page_width == 0) {
        LOGE("GetFieldID page_width failed");
        return false;
    }
    jfieldID fid_page_height = env->GetFieldID(clz_settings, "page_height", "I");
    if (fid_page_height == 0) {
        LOGE("GetFieldID page_height failed");
        return false;
    }
    jfieldID fid_trim = env->GetFieldID(clz_settings, "trim", "I");
    if (fid_trim == 0) {
        LOGE("GetFieldID trim failed");
        return false;
    }
    jfieldID fid_wrap = env->GetFieldID(clz_settings, "wrap", "I");
    if (fid_wrap == 0) {
        LOGE("GetFieldID wrap failed");
        return false;
    }
    jfieldID fid_columns = env->GetFieldID(clz_settings, "columns", "I");
    if (fid_columns == 0) {
        LOGE("GetFieldID columns failed");
        return false;
    }
    jfieldID fid_indent = env->GetFieldID(clz_settings, "indent", "I");
    if (fid_indent == 0) {
        LOGE("GetFieldID indent failed");
        return false;
    }
    jfieldID fid_straighten = env->GetFieldID(clz_settings, "straighten", "I");
    if (fid_straighten == 0) {
        LOGE("GetFieldID straighten failed");
        return false;
    }
    jfieldID fid_rotate = env->GetFieldID(clz_settings, "rotate", "I");
    if (fid_rotate == 0) {
        LOGE("GetFieldID rotate failed");
        return false;
    }
    jfieldID fid_justification = env->GetFieldID(clz_settings, "justification", "I");
    if (fid_justification == 0) {
        LOGE("GetFieldID justification failed");
        return false;
    }
    jfieldID fid_word_spacing = env->GetFieldID(clz_settings, "word_spacing", "D");
    if (fid_word_spacing == 0) {
        LOGE("GetFieldID word_spacing failed");
        return false;
    }
    jfieldID fid_defect_size = env->GetFieldID(clz_settings, "defect_size", "D");
    if (fid_defect_size == 0) {
        LOGE("GetFieldID defect_size failed");
        return false;
    }
    jfieldID fid_result_line_spacing = env->GetFieldID(clz_settings, "line_spacing", "D");
    if (fid_result_line_spacing == 0) {
        LOGE("GetFieldID line_spacing failed");
        return false;
    }
    jfieldID fid_margin = env->GetFieldID(clz_settings, "margin", "D");
    if (fid_margin == 0) {
        LOGE("GetFieldID margin failed");
        return false;
    }
    jfieldID fid_quality = env->GetFieldID(clz_settings, "quality", "D");
    if (fid_quality == 0) {
        LOGE("GetFieldID quality failed");
        return false;
    }
    jfieldID fid_contrast = env->GetFieldID(clz_settings, "contrast", "D");
    if (fid_contrast == 0) {
        LOGE("GetFieldID contrast failed");
        return false;
    }

    int dev_dpi = env->GetIntField(jSettings, fid_dev_dpi);
    int dev_width = env->GetIntField(jSettings, fid_dev_width);
    int dev_height = env->GetIntField(jSettings, fid_dev_height);
    int page_width = env->GetIntField(jSettings, fid_page_width);
    int page_height = env->GetIntField(jSettings, fid_page_height);
    int trim = env->GetIntField(jSettings, fid_trim);
    int wrap = env->GetIntField(jSettings, fid_wrap);
    int columns = env->GetIntField(jSettings, fid_columns);
    int indent = env->GetIntField(jSettings, fid_indent);
    int straighten = env->GetIntField(jSettings, fid_straighten);
    int rotate = env->GetIntField(jSettings, fid_rotate);
    int justification = env->GetIntField(jSettings, fid_justification);
    double word_spacing = env->GetDoubleField(jSettings, fid_word_spacing);
    double defect_size = env->GetDoubleField(jSettings, fid_defect_size);
    double line_spacing = env->GetDoubleField(jSettings, fid_result_line_spacing);
    double margin = env->GetDoubleField(jSettings, fid_margin);
    double quality = env->GetDoubleField(jSettings, fid_quality);
    double contrast = env->GetDoubleField(jSettings, fid_contrast);

/*
    LOGI("dev_dpi: %d, dev_width: %d, dev_height: %d, page_width: %d, page_height: %d, trim: %d, wrap: %d, columns: %d, indent: %d, "
         "straighten: %d, rotate: %d, justification: %d, word_spacing: %f, defect_size: %f, line_spacing: %f, margin: %f, quality: %f, contrast: %f",
         dev_dpi, dev_width, dev_height, page_width, page_height, trim, wrap, columns, indent, straighten, rotate, justification,
         word_spacing, defect_size, line_spacing, margin, quality, contrast);
*/

    context->dev_dpi = dev_dpi;
    context->dev_width = dev_width;
    context->dev_height = dev_height;
    context->page_width = page_width;
    context->page_height = page_height;

    context->trim = trim;
    context->wrap = wrap;
    context->columns = columns;
    context->indent = indent;
    context->straighten = straighten;
    context->rotate = rotate;
    context->justification = justification;

    context->word_spacing = word_spacing;
    context->defect_size = defect_size;
    context->line_spacing = line_spacing;
    context->margin = margin;
    context->quality = quality;
    context->contrast = contrast;

    return true;
}

static jobject createBitmap(JNIEnv * env, WILLUSBITMAP *bmp) {
    using namespace std;
    jclass java_bitmap_class = (jclass)env->FindClass("android/graphics/Bitmap");
    jmethodID mid = env->GetStaticMethodID(java_bitmap_class, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jclass bitmapConfig = env->FindClass("android/graphics/Bitmap$Config");
    jfieldID fid = env->GetStaticFieldID(bitmapConfig, "ARGB_8888", "Landroid/graphics/Bitmap$Config;");
    jobject argb8888 = env->GetStaticObjectField(bitmapConfig, fid);
    jobject bitmap = env->CallStaticObjectMethod(java_bitmap_class, mid, bmp->width, bmp->height, argb8888);
    AndroidBitmapInfo info;
	void *pixels;
	int ret;
	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return NULL;
	}

	if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
		LOGE("Bitmap format is not RGBA_8888 !");
		return NULL;
	}

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
		return NULL;
	}
	LOGI("willusbitmap bpp %d", bmp->bpp);
	unsigned int * target = (unsigned int *)pixels;
    for(int i = 0; i < bmp->height; ++i) {
        unsigned char *src;
        src = bmp_rowptr_from_top(bmp, i);
        for (int col = 0; col < bmp->width; col++) {
            const unsigned char gray = *src++;
            *target++ = 0xFF000000 | (gray << 16) | (gray << 8) | gray;
        }
    }
    return bitmap;
}

static bool addBitmap(JNIEnv * env, jstring pageName, int subPage, jobject parent, jobject bitmap) {
    jclass cls_bmp_list = env->FindClass(readerBitmapClassName);
    if (cls_bmp_list == 0) {
        LOGE("Could not find class: %s", readerBitmapClassName);
        return false;
    }

    jmethodID mid = env->GetMethodID(cls_bmp_list, "addBitmap", "(Ljava/lang/String;ILandroid/graphics/Bitmap;)V");
    if (mid == 0) {
        LOGE("Find method addBitmap failed");
        return false;
    }

    env->CallVoidMethod(parent, mid, pageName, subPage, bitmap);
    return true;
}

jboolean k2pdfopt_reflow_bmp(JNIEnv * env, jstring pageName, jobject parent, KOPTContext *kctx) {
    K2PDFOPT_SETTINGS k2settings;
    MASTERINFO masterinfo;
    WILLUSBITMAP srcgrey;
    WILLUSBITMAP *src;
    BMPREGION region;
    int initgap;

    src = &kctx->src;
    bmp_init(&srcgrey);

    k2pdfopt_settings_init_from_koptcontext(&k2settings, kctx);
    k2pdfopt_settings_sanity_check(&k2settings);

    masterinfo_init(&masterinfo, &k2settings);
    bmp_free(&masterinfo.bmp);
    bmp_init(&masterinfo.bmp);
    masterinfo.bmp.width = 0;
    masterinfo.bmp.height = 0;
    wrapbmp_free(&masterinfo.wrapbmp);
    wrapbmp_init(&masterinfo.wrapbmp, k2settings.dst_color);

    bmpregion_init(&region);
    masterinfo_new_source_page_init(&masterinfo, &k2settings, src, &srcgrey, NULL, &region, k2settings.src_rot, NULL, NULL, 1, NULL);
    bmpregion_source_page_add(&region, &k2settings, &masterinfo, 1, 0);
    wrapbmp_flush(&masterinfo, &k2settings, 0);

    bmp_free(src);
    bmp_free(&srcgrey);

    if (fabs(k2settings.dst_gamma - 1.0) > .001) {
        bmp_gamma_correct(&masterinfo.bmp, &masterinfo.bmp, k2settings.dst_gamma);
    }

    LOGI("Generating sub pages...");
    WILLUSBITMAP subPage;
    int pn = 0, rows, size_reduction;
    double bmpdpi;
    bmp_init(&subPage);
    while ((rows=masterinfo_get_next_output_page(&masterinfo, &k2settings,1, &subPage, &bmpdpi, &size_reduction, NULL)) > 0) {
        jobject androidBitmap = createBitmap(env, &subPage);
        addBitmap(env, pageName, pn, parent, androidBitmap);
        pn++;
        LOGI("sub page number : %d bmp %d %d", pn, subPage.width, subPage.height);
        if (0) {
            char filename[256] = {0};
            sprintf(filename, "/mnt/sdcard/out%03d.png", pn);
            bmp_write(&subPage, filename, stdout, 100);
        }
    }
    bmp_free(&subPage);
    masterinfo_free(&masterinfo, &k2settings);
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_ReaderImageUtils_reflowPage
  (JNIEnv * env, jclass thiz, jobject  bitmap, jstring pageName, jobject parent, jobject settings) {
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

    KOPTContext kctx = {0};
    if (!convertToKoptContext(env, settings, &kctx)) {
        LOGE("convertToKoptContext failed");
        return false;
    }

    LOGI("Java_com_onyx_reader_ReaderImageUtils_reflowPage: %d %d %d", info.width, info.height, info.stride);
    if (!convertToWillusBmp(env, pixels, info.width, info.height, info.stride, &kctx.src)) {
        LOGE("convertToWillusBmp failed");
        return false;
    }
    return k2pdfopt_reflow_bmp(env, pageName, parent, &kctx);
}


JNIEXPORT jboolean JNICALL Java_com_onyx_reader_ReaderImageUtils_emboldenInPlace
  (JNIEnv *env, jclass thiz, jobject bitmap, jint level) {
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

    imgfilter::ImageEmboldenFilter filter;
    filter.setLevel(level);

    if (!filter.doFilterInPlace((AndroidBitmapFormat)info.format, (unsigned char *)pixels, info.width, info.height)) {
        LOGE("embolden glyph failed");
        return false;
    }
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_ReaderImageUtils_gammaCorrection
  (JNIEnv *env, jclass thiz, jobject bitmap, jfloat gamma) {
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

    imgfilter::ImageGammaFilter filter;
    filter.setGamma(gamma);

    if (!filter.doFilterInPlace((AndroidBitmapFormat)info.format, (unsigned char *)pixels, info.width, info.height)) {
        LOGE("set contrast failed");
        return false;
    }
    return true;
}
