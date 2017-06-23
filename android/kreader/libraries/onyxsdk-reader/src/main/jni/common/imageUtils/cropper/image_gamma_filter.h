#ifndef IMAGE_GAMMA_FILTER_H
#define IMAGE_GAMMA_FILTER_H

#include "abstract_image_filter.h"

namespace imgfilter
{

class ImageGammaFilter : public AbstractImageFilter {
public:
    static constexpr float DEFAULT_GAMMA = -1.0f;

    ImageGammaFilter();

    virtual bool doFilterInPlace(AndroidBitmapFormat format, unsigned char *data, const int width, const int height);

    /**
     * @brief setContrast
     * @param contrast ranges from [1, 10], 5 as default value, contrast enhances while value increasing
     */
    void setGamma(float gamma);

    bool doRegionFilterInPlace(AndroidBitmapFormat format,
                                 unsigned char *data,
                                 const int x,
                                 const int y,
                                 const int width,
                                 const int height,
                                 const int strideInBytes);


private:
    int gamma;
};

}

#endif // IMAGE_GAMMA_FILTER_H
