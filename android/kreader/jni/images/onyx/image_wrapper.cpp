
#include "image_wrapper.h"

ImageWrapper::ImageWrapper() : width(0), height(0), bpp(0), channels(0), colorType(0) {
}

ImageWrapper::~ImageWrapper() {
}

bool ImageWrapper::loadImage(const std::string &path) {
    return false;
}

PNGWrapper::PNGWrapper() : fp(NULL), pngPtr(NULL), infoPtr(NULL)  {

}

PNGWrapper::~PNGWrapper() {
    if (pngPtr != NULL && infoPtr != NULL) {
        png_destroy_read_struct(&pngPtr, &infoPtr, NULL);
        pngPtr = NULL;
        infoPtr = NULL;
    } else if (pngPtr != NULL) {
        png_destroy_read_struct(&pngPtr, NULL, NULL);
        pngPtr = NULL;
    }
    if (fp != NULL) {
        fclose(fp);
        fp = NULL;
    }
}

bool PNGWrapper::loadImage(const std::string & path) {

    if ((fp = fopen(path.c_str(), "rb")) == NULL) {
        return false;
    }
    pngPtr = png_create_read_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
    if (pngPtr == NULL) {
        return false;
    }

    infoPtr = png_create_info_struct(pngPtr);
    if (infoPtr == NULL) {
        return false;
    }

    if (setjmp(png_jmpbuf(pngPtr))) {
        return false;
    }

    png_init_io(pngPtr, fp);
    png_read_png(pngPtr, infoPtr, PNG_TRANSFORM_STRIP_16 | PNG_TRANSFORM_SWAP_ALPHA | PNG_TRANSFORM_EXPAND, NULL);

    width     = png_get_image_width(pngPtr, infoPtr);
    height    = png_get_image_height(pngPtr, infoPtr);
    bpp       = png_get_bit_depth(pngPtr, infoPtr);
    channels  = png_get_channels(pngPtr, infoPtr);
    colorType = png_get_color_type(pngPtr, infoPtr);
    return true;
}

ImageManager::ImageManager() {
}

ImageManager::~ImageManager() {
}

ImageWrapper * ImageManager::getImage(const std::string & path) {
    table_iterator iterator = imageTable.find(path);
    ImageWrapper * imageWrapper = 0;
    if (iterator == imageTable.end()) {
        imageWrapper = new PNGWrapper();
        imageWrapper->loadImage(path);
        imageTable[path] = imageWrapper;
    } else {
        imageWrapper = iterator->second;
    }
    return imageWrapper;
}

bool ImageManager::releaseImage(const std::string & path) {
    table_iterator iterator = imageTable.find(path);
    if (iterator != imageTable.end()) {
        delete iterator->second;
        imageTable.erase(iterator);
        return true;
    }
    return false;
}
