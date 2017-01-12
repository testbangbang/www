/*
 * ImageThinningFilter.cpp
 *
 *  Created on: 30 Sep, 2011
 *      Author: joy
 */

#include <cassert>
#include <cstdlib>
#include <cstdio>
#include <cmath>
#include <cstring>

#include <string>
#include <iostream>
#include <vector>
#include <map>
#include <utility>

#include "image_embolden_filter.h"
#include "image_filter_util.h"

using namespace imgfilter;

namespace
{

const unsigned int MAX_STRONG_LEVEL = 8;

/**
  * embolden algorithm from FT_Bitmap_Embolden@freetype
  */
static void emboldenBitmapFromFreeType(unsigned char *grayData, int width, int height, unsigned int strongX, unsigned int strongY)
{
    assert(grayData);

    int strong_x = (strongX <= MAX_STRONG_LEVEL) ? strongX : MAX_STRONG_LEVEL;
    int strong_y = (strongY <= MAX_STRONG_LEVEL) ? strongY : MAX_STRONG_LEVEL;

    unsigned char *line = grayData;
    for (int row = 0; row < height; row++) {
        for (int col = width - 1; col >= 0; col--) {
            for (int i = 1; i <= strong_x; i++) {
                if ((col - i) >= 0) {
                    if ((line[col] + line[col - i]) > ImageFilterUtil::GRAY_MAX) {
                        line[col] = ImageFilterUtil::GRAY_MAX;
                        break;
                    }
                    else {
                        line[col] += line[col - i];
                        if (line[col] == ImageFilterUtil::GRAY_MAX) {
                            break;
                        }
                    }
                }
            }
        }

        for (int i = 1; i <= strong_y; i++) {
            if ((row - i) >= 0) {
                unsigned char *dst_line = line - (width * i);

                for (int j = 0; j < width; j++) {
                    dst_line[j] |= line[j];
                }
            }
        }

        line += width;
    }
}

static void invertGrayImage(unsigned char *grayData, const int width, const int height)
{
    assert(grayData);

    unsigned char *gray_ptr = grayData;

    for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
            *gray_ptr = ImageFilterUtil::GRAY_MAX - *gray_ptr;
            gray_ptr++;
        }
    }
}

static unsigned char *createGrayImageFromGRB32(const int *data, const int width, const int height) {
    // every line bytes in bitmap should always be multiple of 4
//    const int BITMAP_ALIGNMENT = 4;
//    const int mod = width % BITMAP_ALIGNMENT;
//    const int gray_aligned_width = mod == 0 ? width : width + (4 - mod);

    unsigned char *gray_data = (unsigned char *)malloc(width * height);
    if (gray_data == 0) {
        return 0;
    }

    const int *rgb_line = data;
    unsigned char *gray_line = gray_data;

    for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
            gray_line[col] = ImageFilterUtil::rgb32ToGray(rgb_line[col]);
        }
        gray_line += width;
        rgb_line += width;
    }
    return gray_data;
}

static bool emboldenGrayImage(unsigned char *grayData, const int width, const int height, const unsigned int strongLevel) {
    // due to the nature of emboldenBitmapFromFreeType()
    invertGrayImage(grayData, width, height);
    switch (strongLevel) {
    case 1:
        emboldenBitmapFromFreeType(grayData, width, height, 0, 1);
        break;
    case 2:
        emboldenBitmapFromFreeType(grayData, width, height, 1, 0);
        break;
    case 3:
        emboldenBitmapFromFreeType(grayData, width, height, 1, 1);
        break;
    case 4:
        emboldenBitmapFromFreeType(grayData, width, height, 2, 1);
        break;
    case 5:
        emboldenBitmapFromFreeType(grayData, width, height, 3, 1);
        break;
    }

    invertGrayImage(grayData, width, height);
    return grayData;
}

static bool filterInPlaceCore(int *data, const int width, const int height, const unsigned int strongLevel) {
    unsigned char *gray_data = createGrayImageFromGRB32(data, width, height);
    if (!gray_data) {
        return false;
    }

    if (!emboldenGrayImage(gray_data, width, height, strongLevel)) {
        return false;
    }

//    *data = gray_image->convertToFormat(QImage::Format_RGB32);
    ImageFilterUtil::getRgb32ImageFromGray(gray_data, width, height, data);
    free(gray_data);
    return true;
}

const size_t Default_Embolden_Level = 1;
}

ImageEmboldenFilter::ImageEmboldenFilter()
    : embolden_level_(Default_Embolden_Level)
{
}

ImageEmboldenFilter::ImageEmboldenFilter(size_t level)
    : embolden_level_(level)
{
}

ImageEmboldenFilter::~ImageEmboldenFilter()
{
}

bool ImageEmboldenFilter::doFilterInPlace(AndroidBitmapFormat format, unsigned char *data, const int width, const int height) {
    if (format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return false;
    }

    return filterInPlaceCore((int *)data, width, height, embolden_level_);
}

size_t ImageEmboldenFilter::getLevel() {
    return embolden_level_;
}

void ImageEmboldenFilter::setLevel(size_t level) {
    if (level > MAX_STRONG_LEVEL) {
        embolden_level_ = MAX_STRONG_LEVEL;
        return;
    }

    embolden_level_ = level;
}

