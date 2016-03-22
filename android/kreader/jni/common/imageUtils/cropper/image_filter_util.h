#ifndef IMAGE_FILTER_UTIL_H
#define IMAGE_FILTER_UTIL_H

#include <cstdlib>

#include <string>

namespace imgfilter {

class ImageFilterUtil {

public:
    static const unsigned char GRAY_MAX = 255;

    static inline unsigned int redRgb32(const int c)
    {
        return (c >> 16) & 0xFF;
    }
    static inline unsigned int greenRgb32(const int c)
    {
        return (c >> 8) & 0xFF;
    }
    static inline unsigned int blueRgb32(const int c)
    {
        return c & 0xFF;
    }

    static inline unsigned char rgb32ToGray(const int c)
    {
        return (redRgb32(c)*11 + greenRgb32(c)*16 + blueRgb32(c)*5) >> 5;
    }
    static inline int grayToRgb32(const unsigned char gray)
    {
        return 0xFF000000 | (gray << 16) | (gray << 8) | gray;
    }

    static void *getRgb32ImageFromGray(const unsigned char *grayData, const int width, const int height, int *resultData);
    static void *getRgb32ImageFromRgb24(const unsigned char *rgb24Data, const int width, const int height, int *resultData);

    static std::string getTimeStamp();
    static std::string getSaveAsPath(const std::string &filePath);

    template<class T> static T **create2DArray(int row, int column) {
        T **array = 0;
        array = (T **)calloc(row, sizeof(T *));
        if (!array) {
            return 0;
        }

        for (int i = 0; i < row; i++) {
            array[i] = (T *)calloc(column, sizeof(T));
            if (!array[i]) {
                return 0;
            }
        }
        return array;
    }

};

} // imgfilter
#endif // INNERUTIL_H
