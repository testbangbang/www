

#ifndef PNG_WRAPPER_H_
#define PNG_WRAPPER_H_


#include <png.h>

class PNGWrapper {

private:
    FILE * fp;
    png_structp pngPtr;
    png_infop infoPtr;
    int width;
    int height;
    int bpp;
    int channels;
    unsigned int colorType;


public:
    PNGWrapper(const char *path);
    ~PNGWrapper();

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    int getBitPerPixel() {
        return bpp;
    }

    int getChannels() {
        return channels;
    }

    unsigned int getColorType() {
        return colorType;
    }

private:
    bool loadImage(const char *path);

};


#endif