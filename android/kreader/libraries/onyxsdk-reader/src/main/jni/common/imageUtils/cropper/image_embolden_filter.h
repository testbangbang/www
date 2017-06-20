#ifndef IMAGETHINNINGFILTER_H
#define IMAGETHINNINGFILTER_H

#include <string>

#include "abstract_image_filter.h"

namespace imgfilter
{

/// level range from [0, 8]
class ImageEmboldenFilter : public AbstractImageFilter {
public:
    ImageEmboldenFilter();
    ImageEmboldenFilter(size_t level);
    virtual ~ImageEmboldenFilter();

    virtual bool doFilterInPlace(AndroidBitmapFormat format, unsigned char *data, const int width, const int height);

    size_t getLevel();
    void setLevel(size_t level);

private:
    size_t embolden_level_;
};

}

#endif // IMAGETHINNINGFILTER_H
