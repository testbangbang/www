#ifndef ABSTRACTIMAGEFILTER_H
#define ABSTRACTIMAGEFILTER_H

#include <string>

#include <android/bitmap.h>

namespace imgfilter {

class AbstractImageFilter {
public:
    /// accept rgb32 bitmap buffer
    virtual bool doFilterInPlace(AndroidBitmapFormat format, unsigned char *data, const int width, const int height) = 0;
}; // ImageFilter
} // namespace

#endif // ABSTRACTIMAGEFILTER_H
