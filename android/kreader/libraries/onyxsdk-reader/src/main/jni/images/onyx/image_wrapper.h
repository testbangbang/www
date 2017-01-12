

#ifndef IMAGE_WRAPPER_H_
#define IMAGE_WRAPPER_H_


#include <unordered_map>
#include <string>


extern "C" {
#include <png.h>
#include <jpeglib.h>
}

class ImageWrapper {

protected:
    int width;
    int height;
    int bpp;
    int channels;
    unsigned int colorType;
    std::string myPath;

public:
    ImageWrapper();
    virtual ~ImageWrapper();

public:
    virtual bool loadImage(const std::string & path);
    virtual bool draw(void *pixel, int x, int y, int width, int height, int bmpWidth, int bmpHeight, int stride);

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
    virtual bool loadImage(const std::string & path);
    virtual bool draw(void *pixel, int x, int y, int width, int height, int bmpWidth, int bmpHeight, int stride);

private:
    void cleanup();
};

class JPEGWrapper : public ImageWrapper {

private:
    FILE * fp;

    struct ErrorManager {
        struct jpeg_error_mgr pub;    /* "public" fields */
        jmp_buf setjmp_buffer;        /* for return to caller */
    };
    static void errorExit(j_common_ptr cinfo);

public:
    JPEGWrapper();
    virtual ~JPEGWrapper();

public:
    virtual bool loadImage(const std::string & path);
    virtual bool draw(void *pixel, int x, int y, int width, int height, int bmpWidth, int bmpHeight, int stride);

private:
    void cleanup();


};

class ImageManager {

private:
    std::unordered_map<std::string, ImageWrapper *> imageTable;
    typedef std::unordered_map<std::string, ImageWrapper *>::iterator table_iterator;

    ImageWrapper * createInstance(const std::string &path);

public:
    ImageManager();
    ~ImageManager();

public:
    ImageWrapper * getImage(const std::string & path);
    bool releaseImage(const std::string & path);
    void clear();

};


#endif