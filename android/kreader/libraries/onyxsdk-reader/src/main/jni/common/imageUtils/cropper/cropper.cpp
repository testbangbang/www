#include <jni.h>
#include <time.h>
#include <pthread.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <unistd.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <list>

#include <cassert>
#include <cstring>
#include <string>
#include <list>
#include <algorithm>
#include <mutex>
#include <thread>

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

#include "com_onyx_android_sdk_reader_utils_ImageUtils.h"
#include "image_embolden_filter.h"
#include "image_gamma_filter.h"

#include "JNIUtils.h"

#define MAX(a,b) (((a) > (b)) ? (a) : (b))
#define MIN(a,b) (((a) < (b)) ? (a) : (b))

#define LOG_TAG "neo_cropper"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGT(...) __android_log_print(ANDROID_LOG_INFO,"alert",__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)



class JNIBitmap {
private:
    JNIEnv * myEnv;
    AndroidBitmapInfo info;
    int *pixels;


public:
    JNIBitmap(JNIEnv * env);
    ~JNIBitmap();

public:
    bool attach(jobject bitmap);
    const AndroidBitmapInfo & getInfo() const;
    int * getPixels();

};


JNIBitmap::JNIBitmap(JNIEnv * env) : myEnv(env), pixels(0) {

}

JNIBitmap::~JNIBitmap() {
}

const AndroidBitmapInfo & JNIBitmap::getInfo() const {
    return info;
}

int * JNIBitmap::getPixels() {
    return pixels;
}

bool JNIBitmap::attach(jobject bitmap) {
    int ret;
    if ((ret = AndroidBitmap_getInfo(myEnv, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return false;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return false;
    }

    if ((ret = AndroidBitmap_lockPixels(myEnv, bitmap, (void **)&pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return false;
    }

    return true;
}


namespace {

// use std::list to simulate FIFO queue
std::list<std::pair<std::string, std::shared_ptr<WILLUSBITMAP>>> reflowedPages;
std::mutex pagesMutex;

void insertReflowedPage(const std::string &pageName, WILLUSBITMAP *bmp) {
    std::lock_guard<std::mutex> lock(pagesMutex);
    reflowedPages.push_back(std::make_pair(pageName,
                                           std::shared_ptr<WILLUSBITMAP>(bmp, [](WILLUSBITMAP *bmp) {
                                               bmp_free(bmp);
                                               free(bmp);
                                           })));
    const int MAX_QUEUE_SIZE = 2;
    if (reflowedPages.size() > MAX_QUEUE_SIZE) {
        reflowedPages.pop_front();
    }
}

void releaseReflowedPages() {
    std::lock_guard<std::mutex> lock(pagesMutex);
    while (!reflowedPages.empty()) {
        reflowedPages.pop_front();
    }
}

std::shared_ptr<WILLUSBITMAP> getReflowedPage(const std::string &pageName) {
    std::lock_guard<std::mutex> lock(pagesMutex);
    auto found = std::find_if(reflowedPages.cbegin(), reflowedPages.cend(),
                             [&pageName](const std::pair<std::string, std::shared_ptr<WILLUSBITMAP>> &pair) {
        return pair.first == pageName;
    });
    if (found == reflowedPages.cend()) {
        return nullptr;
    }
    return found->second;
}

const int COLUMN_HALF_HEIGHT = 15;
const int V_LINE_SIZE = 2;
const int H_LINE_SIZE = 2;
const int LINE_MARGIN = 20;
const int COLUMN_WIDTH = 5;
double WHITE_THRESHOLD  = 0.01;

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
    double white = (double) count / (sub_w * sub_h);
    //LOGI("White: %f %d %f", white, count, WHITE_THRESHOLD);
    return white < WHITE_THRESHOLD ? 1 : 0;
}

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

int getLeftBound(uint8_t* src, int width, int height, int sub_x, int sub_y, int sub_w, int sub_h, int avgLum) {
    int w = sub_w / 3;
    int whiteCount = 0;
    int x = 0;

    for (x = sub_x; x < w; x += V_LINE_SIZE) {
        // LOGI("getLeftBound: %d", x);
        int white = isRectWhite(src, width, height, x, sub_y + LINE_MARGIN, V_LINE_SIZE, sub_h - 2 * LINE_MARGIN, avgLum);
        if (white) {
            whiteCount++;
        } else {
            if (whiteCount >= 1) {
                return MAX(sub_x, x - V_LINE_SIZE);
            }
            whiteCount = 0;
        }
    }
    return whiteCount > 0 ? MAX(sub_x, x - V_LINE_SIZE) : sub_x;
}

int getTopBound(uint8_t* src, int width, int height, int sub_x, int sub_y, int sub_w, int sub_h, int avgLum) {
    int h = sub_h / 3;
    int whiteCount = 0;
    int y = 0;

    for (y = sub_y; y < h; y += H_LINE_SIZE) {
        // LOGI("getTopBound: %d", y);
        int white = isRectWhite(src, width, height, sub_x + LINE_MARGIN, y, sub_w - 2 * LINE_MARGIN, H_LINE_SIZE, avgLum);
        if (white) {
            whiteCount++;
        } else {
            if (whiteCount >= 1) {
                return MAX(sub_y, y - H_LINE_SIZE);
            }
            whiteCount = 0;
        }
    }
    return whiteCount > 0 ? MAX(sub_y, y - H_LINE_SIZE) : sub_y;
}

int getRightBound(uint8_t* src, int width, int height, int sub_x, int sub_y, int sub_w, int sub_h, int avgLum) {
    int w = sub_w / 3;
    int whiteCount = 0;
    int x = 0;
    int subRight = sub_x + sub_w;

    for (x = subRight - V_LINE_SIZE; x > subRight - w; x -= V_LINE_SIZE) {
        // LOGI("getRightBound: %d", x);
        int white = isRectWhite(src, width, height, x, sub_y + LINE_MARGIN, V_LINE_SIZE, sub_h - 2 * LINE_MARGIN, avgLum);
        if (white) {
            whiteCount++;
        } else {
            if (whiteCount >= 1) {
                return MIN(subRight, x + 2 * V_LINE_SIZE);
            }
            whiteCount = 0;
        }
    }
    return whiteCount > 0 ? MIN(subRight, x + 2 * V_LINE_SIZE) : subRight;
}

int getBottomBound(uint8_t* src, int width, int height, int sub_x, int sub_y, int sub_w, int sub_h, int avgLum) {
    int h = sub_h / 3;
    int whiteCount = 0;
    int y = 0;
    int subBottom = sub_y + sub_h;

    for (y = subBottom - H_LINE_SIZE; y > subBottom - h; y -= H_LINE_SIZE) {
        int white = isRectWhite(src, width, height, sub_x + LINE_MARGIN, y, sub_w - 2 * LINE_MARGIN, H_LINE_SIZE, avgLum);
        // LOGI("getBottomBound: %d white %d", y, white);
        if (white) {
            whiteCount++;
        } else {
            if (whiteCount >= 1) {
                return MIN(subBottom, y + 2 * H_LINE_SIZE);
            }
            whiteCount = 0;
        }
    }
    return whiteCount > 0 ? MIN(subBottom, y + 2 * H_LINE_SIZE) : subBottom;
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

bool convertToWillusBmp(JNIEnv *env, void * data, jint width, jint height, jint stride, WILLUSBITMAP *bmp) {
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
jobject createReaderBitmapList(JNIEnv * env) {
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

void bmp_move(WILLUSBITMAP *dest, WILLUSBITMAP *src) {
    dest->width  = src->width;
    dest->height = src->height;
    dest->bpp    = src->bpp;
    dest->type   = src->type;
    memcpy(dest->red,src->red,sizeof(int)*256);
    memcpy(dest->green,src->green,sizeof(int)*256);
    memcpy(dest->blue,src->blue,sizeof(int)*256);
    dest->data = src->data;
    dest->size_allocated = src->size_allocated;

    src->data = NULL;
    src->size_allocated = 0;
}

/**
 * create context from settings
 */
bool convertToKoptContext(JNIEnv *env, jobject jSettings, KOPTContext *context) {
    jclass clz_settings = env->GetObjectClass(jSettings);
    if (clz_settings == 0) {
        LOGE("Find class com/onyx/android/sdk/reader/reflow/ImageReflowSettings failed");
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
    jfieldID fid_src_left_to_right = env->GetFieldID(clz_settings, "src_left_to_right", "I");
    if (fid_src_left_to_right == 0) {
        LOGE("GetFieldID src_left_to_right failed");
        return false;
    }
    jfieldID fid_src_rot = env->GetFieldID(clz_settings, "src_rot", "I");
    if (fid_src_rot == 0) {
        LOGE("GetFieldID src_rot failed");
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
    int src_left_to_right = env->GetIntField(jSettings, fid_src_left_to_right);
    int src_rot = env->GetIntField(jSettings, fid_src_rot);

    LOGI("dev_dpi: %d, dev_width: %d, dev_height: %d, page_width: %d, page_height: %d, trim: %d, wrap: %d, columns: %d, indent: %d, "
         "straighten: %d, rotate: %d, justification: %d, word_spacing: %f, defect_size: %f, line_spacing: %f, margin: %f, quality: %f, contrast: %f, src_left_to_right: %d, src_rot: %d",
         dev_dpi, dev_width, dev_height, page_width, page_height, trim, wrap, columns, indent, straighten, rotate, justification,
         word_spacing, defect_size, line_spacing, margin, quality, contrast, src_left_to_right, src_rot);

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

    context->src_left_to_right = src_left_to_right;
    context->src_rot = src_rot;

    return true;
}

jobject createBitmap(JNIEnv * env, WILLUSBITMAP *bmp) {
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

jboolean k2pdfopt_reflow_bmp(const std::string &pageName, KOPTContext *kctx) {
    K2PDFOPT_SETTINGS _k2settings, *k2settings;
    MASTERINFO _masterinfo, *masterinfo;
    WILLUSBITMAP _srcgrey, *srcgrey;
    WILLUSBITMAP *src, *dst;
    BMPREGION region;
    int i, bw, martop, marbot, marleft;

    if (0) {
        bmp_write(&kctx->src, "/sdcard/reflowin.bmp", stdout, 100);
    }

    src = &kctx->src;
    srcgrey = &_srcgrey;
    bmp_init(srcgrey);

    k2settings = &_k2settings;
    masterinfo = &_masterinfo;
    /* Initialize settings */
    k2pdfopt_settings_init_from_koptcontext(k2settings, kctx);
    k2pdfopt_settings_quick_sanity_check(k2settings);
    /* Init for new source doc */
    k2pdfopt_settings_new_source_document_init(k2settings);
    /* Init master output structure */
    masterinfo_init(masterinfo, k2settings);
    wrapbmp_init(&masterinfo->wrapbmp, k2settings->dst_color);
    /* Init new source bitmap */
    bmpregion_init(&region);
    masterinfo_new_source_page_init(masterinfo, k2settings, src, srcgrey, NULL,
                                    &region, k2settings->src_rot, NULL, NULL, 1, -1, NULL );
    /* Set output size */
    k2pdfopt_settings_set_margins_and_devsize(k2settings,&region,masterinfo,-1.,0);
    /* Process single source page */
    bmpregion_source_page_add(&region, k2settings, masterinfo, 1, 0);
    wrapbmp_flush(masterinfo, k2settings, 0);

    if (fabs(k2settings->dst_gamma - 1.0) > .001) {
        bmp_gamma_correct(&masterinfo->bmp, &masterinfo->bmp, k2settings->dst_gamma);
    }

    /* copy master bitmap to context dst bitmap */
    dst = (WILLUSBITMAP *)malloc(sizeof(WILLUSBITMAP));
    bmp_init(dst);
    martop = (int) (k2settings->dst_dpi * k2settings->dstmargins.box[1] * 2 + .5);
    marbot = (int) (k2settings->dst_dpi * k2settings->dstmargins.box[1] * 2 + .5);
    marleft = (int) (k2settings->dst_dpi * k2settings->dstmargins.box[0] + .5);
    dst->bpp = masterinfo->bmp.bpp;
    dst->width = (masterinfo->bmp.width / 4) * 4;
    // avoid too small page height that will cause perfermance issue in scroll mode
    dst->height = masterinfo->rows + martop + marbot > kctx->page_height
            ? masterinfo->rows + martop + marbot : kctx->page_height;
    bmp_alloc(dst);
    bmp_fill(dst, 255, 255, 255);
    bw = bmp_bytewidth(&masterinfo->bmp);
    for (i = 0; i < masterinfo->rows; i++)
        memcpy(bmp_rowptr_from_top(dst, i + martop),
               bmp_rowptr_from_top(&masterinfo->bmp, i), bw);

    kctx->page_width = dst->width;
    kctx->page_height = dst->height;
    LOGI("reflowed page size: [%d, %d]", dst->width, dst->height);

    if (0) {
        bmp_write(dst, "/sdcard/reflowout.bmp", stdout, 100);
    }

    insertReflowedPage(pageName, dst);

    bmp_free(src);
    bmp_free(srcgrey);
    masterinfo_free(masterinfo, k2settings);
    return true;
}

}

JNIEXPORT jdoubleArray JNICALL Java_com_onyx_android_sdk_reader_utils_ImageUtils_crop(JNIEnv *env, jclass,
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
    int avgLum = calculateAvgLum(src, info.width, info.height, left, top, width, height);

    double coords[4];
    coords[0] = getLeftBound(src, info.width, info.height, left, top, width, height, avgLum);
    coords[1] = getTopBound(src, info.width, info.height, left, top, width, height, avgLum);
    coords[2] = getRightBound(src, info.width, info.height, left, top, width, height, avgLum);
    coords[3] = getBottomBound(src, info.width, info.height, left, top, width, height, avgLum);
    return doubleArrayFromRect(env, coords[0], coords[1], coords[2], coords[3]);
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_ImageUtils_emboldenInPlace
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

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_ImageUtils_gammaCorrection
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

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_ImageUtils_reflowPage
  (JNIEnv * env, jclass thiz, jstring pageNameString, jobject  bitmap, jobject settings) {
    JNIString pageName(env, pageNameString);
    auto page = getReflowedPage(pageName.getLocalString());
    if (page) {
        return true;
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
    return k2pdfopt_reflow_bmp(pageName.getLocalString(), &kctx);
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_ImageUtils_isPageReflowed
  (JNIEnv *env, jclass, jstring pageNameString) {
    JNIString pageName(env, pageNameString);
    return getReflowedPage(pageName.getLocalString()) != nullptr;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_ImageUtils_getReflowedPageSize
  (JNIEnv *env, jclass, jstring pageNameString, jintArray sizeArray) {
    JNIString pageName(env, pageNameString);
    auto page = getReflowedPage(pageName.getLocalString());
    if (!page) {
        return false;
    }

    jint size[] = { page->width, page->height };
    env->SetIntArrayRegion(sizeArray, 0, 2, size);
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_ImageUtils_renderReflowedPage
  (JNIEnv *env, jclass, jstring pageNameString, jint left, jint top, jint right, jint bottom, jobject bitmap) {
    JNIString pageName(env, pageNameString);
    auto page = getReflowedPage(pageName.getLocalString());
    if (!page) {
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


    WILLUSBITMAP dst;
    bmp_init(&dst);
    bmp_crop_ex(&dst, page.get(), left, top, right - left, bottom - top);

    unsigned int * target = (unsigned int *)pixels;
    for(int row = 0; row < dst.height; ++row) {
        unsigned char *src;
        src = bmp_rowptr_from_top(&dst, row);
        for (int col = 0; col < dst.width ; col++) {
            const unsigned char gray = *src++;
            *target++ = 0xFF000000 | (gray << 16) | (gray << 8) | gray;
        }
    }

    LOGE("renderReflowedPage, render reflow page finished");

    bmp_free(&dst);
    AndroidBitmap_unlockPixels(env, bitmap);

    return true;
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_reader_utils_ImageUtils_releaseReflowedPages
  (JNIEnv *, jclass) {
    releaseReflowedPages();
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_reader_utils_ImageUtils_toRgbwBitmap
  (JNIEnv *env, jclass thiz, jobject dstBitmap, jobject srcBitmap, jint orientation) {
    JNIBitmap dst(env);
    JNIBitmap src(env);
    if (!dst.attach(dstBitmap) || !src.attach(srcBitmap)) {
        return;
    }

    int sw = src.getInfo().width;
    int sh = src.getInfo().height;
    int ss = src.getInfo().stride;
    int * srcData = src.getPixels();

    int * dstData = dst.getPixels();
    int ds = dst.getInfo().stride;

    for(int y = 0; y < sh; ++y) {
        int * srcLine = srcData + ss * y / 4;
        int * dstLine1 = dstData + ds * y * 2 / 4;
        int * dstLine2 = dstLine1 + ds / 4;
        for(int x = 0; x < sw; ++x) {
            int argb = *srcLine++;
            unsigned char a = ColorUtils::alpha(argb);
            unsigned char r = ColorUtils::red(argb);
            unsigned char g = ColorUtils::green(argb);
            unsigned char b = ColorUtils::blue(argb);
            unsigned char w = ColorUtils::white(r, g, b);

            *dstLine1++ = ColorUtils::argb(a, r, r, r);
            *dstLine1++ = ColorUtils::argb(a, g, g, g);

            *dstLine2++ = ColorUtils::argb(a, w, w, w);
            *dstLine2++ = ColorUtils::argb(a, b, b, b);
        }
    }
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_reader_utils_ImageUtils_toColorBitmap
  (JNIEnv *env, jclass thiz, jobject dstBitmap, jobject srcBitmap, jint orientation) {
    JNIBitmap dst(env);
    JNIBitmap src(env);
    if (!dst.attach(dstBitmap) || !src.attach(srcBitmap)) {
        return;
    }

    int sw = src.getInfo().width;
    int sh = src.getInfo().height;
    int ss = src.getInfo().stride;
    int * srcData = src.getPixels();

    int * dstData = dst.getPixels();
    int ds = dst.getInfo().stride;

    for(int y = 0; y < sh / 2; ++y) {
        int * srcLine1 = srcData + ss * y * 2 / 4;
        int * srcLine2 = srcLine1 + ss / 4;
        int * dstLine = dstData + ds * y / 4;
        for(int x = 0; x < sw / 2; ++x) {
            int r = ColorUtils::red(*srcLine1++);
            int g = ColorUtils::green(*srcLine1++);
            int w = ColorUtils::alpha(*srcLine2++);
            int b = ColorUtils::blue(*srcLine2++);

            *dstLine++ = ColorUtils::argb(0xff, r, g, b);
        }
    }
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_utils_ImageUtils_isValidPage
  (JNIEnv *env, jclass thiz) {
    return DeviceUtils::isValid(env);
}
