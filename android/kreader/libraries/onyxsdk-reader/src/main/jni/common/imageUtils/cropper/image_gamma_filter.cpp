#include "image_gamma_filter.h"

#include <math.h>

namespace imgfilter {

static unsigned const int ALPHA = 0xFF;
static unsigned char gamma_lut[256];

ImageGammaFilter::ImageGammaFilter()
    : gamma(DEFAULT_GAMMA)
{
}

bool ImageGammaFilter::doFilterInPlace(AndroidBitmapFormat format, unsigned char *data, const int width, const int height) {
    if (gamma < 0) {
        return false;
    }

    if (format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        int len = 4 * width * height;
        for (int i = 0; i < len; i++) {
            if (i % 4 == 3) {
                data[i] = ALPHA;
            } else {
                data[i] = gamma_lut[data[i]];
            }
        }
        return true;
    }

    return false;
}

bool ImageGammaFilter::doRegionFilterInPlace(AndroidBitmapFormat format,
                                             unsigned char *data,
                                             const int left,
                                             const int top,
                                             const int right,
                                             const int bottom,
                                             const int strideInBytes) {
    if (gamma < 0) {
        return false;
    }

    if (format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return false;
    }

    for(int j = top; j < bottom; ++j) {
        unsigned char * line = data + j * strideInBytes;
        int begin = 4 * left;
        int end = 4 * right;
        for(int i = begin; i < end; ++i) {
            if (i % 4 == 3) {
                line[i] = ALPHA;
            } else  {
                line[i] = gamma_lut[line[i]];
            }
        }
    }
    return true;
}

void ImageGammaFilter::setGamma(float g) {
    if (gamma == g) {
        return;
    }
    gamma = g;
    for(int i = 0; i < 256; i++) {
        gamma_lut[i] = pow(i / 255.0f, gamma) * 255;
    }
}

}
