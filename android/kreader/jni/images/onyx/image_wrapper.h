

#ifndef IMAGE_WRAPPER_H_
#define IMAGE_WRAPPER_H_


#include <unordered_map>
#include <png.h>

class ImageWrapper {

protected:
    int width;
    int height;
    int bpp;
    int channels;
    unsigned int colorType;
    unsigned char * buffer;

public:
    ImageWrapper();
    virtual ~ImageWrapper();

    virtual bool loadImage(const std::string & path);

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

};

class PNGWrapper : public ImageWrapper {

private:
    FILE * fp;
    png_structp pngPtr;
    png_infop infoPtr;


public:
    PNGWrapper();
    virtual ~PNGWrapper();

public:
    bool loadImage(const std::string & path);

};

class ImageManager {

private:
    std::unordered_map<std::string, ImageWrapper *> imageTable;
    typedef std::unordered_map<std::string, ImageWrapper *>::iterator table_iterator;

public:
    ImageManager();
    ~ImageManager();

public:
    ImageWrapper * getImage(const std::string & path);
    bool releaseImage(const std::string & path);

};


#endif