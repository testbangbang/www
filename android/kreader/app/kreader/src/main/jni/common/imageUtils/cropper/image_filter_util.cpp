#include <cstdlib>
#include <ctime>
#include <cassert>

#include <string>
#include <sstream>

#include "image_filter_util.h"

using namespace imgfilter;

void *ImageFilterUtil::getRgb32ImageFromGray(const unsigned char *grayData, const int width, const int height, int *resultData) {
    assert(resultData && grayData);
    int *rgb_line = resultData;
    const unsigned char *gray_line = grayData;
    for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
            rgb_line[col] = grayToRgb32(gray_line[col]);
        }
        gray_line += width;
        rgb_line += width;
    }
}

void *ImageFilterUtil::getRgb32ImageFromRgb24(const unsigned char *rgb24Data, const int width, const int height, int *resultData) {
    assert(rgb24Data && resultData);
    int *rgb32_line = resultData;
    const unsigned char *rgb24_line = rgb24Data;
    const int rgb24_width = 3 * width;
    for (int row = 0; row < height; row++) {
        for (int col = 0, col_rgb24 = 0; col < width; col++, col_rgb24 += 3) {
            rgb32_line[col] = 0xFF000000 | (rgb24_line[col_rgb24] << 16) | (rgb24_line[col_rgb24 + 1] << 8) | rgb24_line[col_rgb24 + 2];
        }
        rgb24_line += rgb24_width;
        rgb32_line += width;
    }
}

std::string ImageFilterUtil::getTimeStamp() {
    time_t t = time(0);
    tm *lt = localtime(&t);

    std::ostringstream ostr;
    ostr<<(lt->tm_year + 1900)<<"-"<<(lt->tm_mon+1)<<"-"<<lt->tm_mday<<"_"<<lt->tm_hour<<"-"<<lt->tm_min<<"-"<<lt->tm_sec;
    return ostr.str();
}

std::string ImageFilterUtil::getSaveAsPath(const std::string &filePath) {
    std::string::size_type idx = filePath.find_last_of('.');
    if (idx == std::string::npos) {
        return filePath + "_" + ImageFilterUtil::getTimeStamp();
    } else {
        return filePath.substr(0, idx) + "_" + ImageFilterUtil::getTimeStamp() +
                "." + filePath.substr(idx + 1, filePath.length() - idx - 1);
    }
}

