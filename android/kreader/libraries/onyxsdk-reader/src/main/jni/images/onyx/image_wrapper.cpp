
#include "image_wrapper.h"
#include "log.h"

#include "JNIUtils.h"

ImageWrapper::ImageWrapper() : width(0), height(0), bpp(0), channels(0), colorType(0) {
}

ImageWrapper::~ImageWrapper() {
}

bool ImageWrapper::loadImage(const std::string &path) {
    return false;
}

bool ImageWrapper::draw(void *pixel, int x, int y, int width, int height, int bmpWidth, int bmpHeight, int stride) {
    return false;
}

PNGWrapper::PNGWrapper() : fp(NULL), pngPtr(NULL), infoPtr(NULL)  {

}

PNGWrapper::~PNGWrapper() {
    cleanup();
}

void PNGWrapper::cleanup() {
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
    myPath = path;

    if ((fp = fopen(path.c_str(), "rb")) == NULL) {
        cleanup();
        return false;
    }

    pngPtr = png_create_read_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
    if (pngPtr == NULL) {
        cleanup();
        return false;
    }

    infoPtr = png_create_info_struct(pngPtr);
    if (infoPtr == NULL) {
        cleanup();
        return false;
    }

    if (setjmp(png_jmpbuf(pngPtr))) {
        cleanup();
        return false;
    }

    png_init_io(pngPtr, fp);
    png_read_png(pngPtr, infoPtr, PNG_TRANSFORM_STRIP_16 | PNG_TRANSFORM_SWAP_ALPHA | PNG_TRANSFORM_EXPAND, NULL);

    width     = png_get_image_width(pngPtr, infoPtr);
    height    = png_get_image_height(pngPtr, infoPtr);
    bpp       = png_get_bit_depth(pngPtr, infoPtr);
    channels  = png_get_channels(pngPtr, infoPtr);
    colorType = png_get_color_type(pngPtr, infoPtr);

    cleanup();
    return true;
}

bool PNGWrapper::draw(void *pixel, int x, int y, int width, int height, int bmpWidth, int bmpHeight, int stride) {
    png_image image;
    memset(&image, 0, (sizeof image));
    image.version = PNG_IMAGE_VERSION;
    if (!png_image_begin_read_from_file(&image, myPath.c_str())) {
        LOGE("Read image %s failed", myPath.c_str());
        return false;
    }
    image.format = PNG_FORMAT_RGBA;
    return png_image_finish_read(&image, NULL, pixel, stride/*row_stride*/, NULL/*colormap*/);
}

JPEGWrapper::JPEGWrapper() : fp(0) {

}

JPEGWrapper::~JPEGWrapper() {
}

void JPEGWrapper::errorExit (j_common_ptr cinfo) {

}

bool JPEGWrapper::loadImage(const std::string & path) {
    myPath = path;
    if ((fp = fopen(path.c_str(), "rb")) == NULL) {
        LOGE("Cloud not open file %s", path.c_str());
        return false;
    }
    jpeg_decompress_struct cinfo;
    ErrorManager jerr;

    cinfo.err = jpeg_std_error(&jerr.pub);
    jerr.pub.error_exit = errorExit;

    if (setjmp(jerr.setjmp_buffer)) {
        LOGE("Decode %s failed.", path.c_str());
        jpeg_destroy_decompress(&cinfo);
        cleanup();
        return false;
    }
    jpeg_create_decompress(&cinfo);
    jpeg_stdio_src(&cinfo, fp);
    jpeg_read_header(&cinfo, TRUE);
    width = cinfo.image_width;
    height = cinfo.image_height;
    bpp = cinfo.num_components;
    return true;
}

bool JPEGWrapper::draw(void *pixel, int x, int y, int width, int height, int bmpWidth, int bmpHeight, int stride) {
    return false;
}

void JPEGWrapper::cleanup() {
    if (fp != 0) {
        fclose(fp);
        fp = 0;
    }
}




ImageManager::ImageManager() {
}

ImageManager::~ImageManager() {
}

ImageWrapper * ImageManager::createInstance(const std::string &path) {
    if (StringUtils::endsWith(path, ".png")) {
        return new PNGWrapper();
    } else if (StringUtils::endsWith(path, ".jpg") || StringUtils::endsWith(path, ".jpeg")) {
        return new JPEGWrapper();
    }
    return 0;
}

ImageWrapper * ImageManager::getImage(const std::string & path) {
    table_iterator iterator = imageTable.find(path);
    ImageWrapper * imageWrapper = 0;
    if (iterator == imageTable.end()) {
        imageWrapper = createInstance(path);
        if (imageWrapper == 0) {
            return 0;
        }
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

void ImageManager::clear() {
    for(table_iterator iterator = imageTable.begin(); iterator != imageTable.end(); ++iterator) {
        delete iterator->second;
    }
    imageTable.clear();
}