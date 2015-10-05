
#ifndef ONYX_SURFACE_H__
#define ONYX_SURFACE_H__

#include "dp_all.h"
#include "onyx_matrix.h"

namespace onyx
{

class OnyxImage
{
public:
  enum Format {
        Format_Invalid,
        Format_Mono,
        Format_MonoLSB,
        Format_Indexed8,
        Format_RGB32,
        Format_ARGB32,
        Format_ARGB32_Premultiplied,
        Format_RGB16
    };

public:
    OnyxImage();
    OnyxImage(int w, int h, Format f);

    virtual int width() const;
    virtual int height() const;
    virtual int depth() const;
    int bytesPerLine();

    void attach(const AndroidBitmapInfo & info, void * pixels, bool clear);

    virtual bool contentArea(OnyxRectangle & content);

    unsigned char * getData();
    unsigned char * bits();

private:
    int imageWidth;
    int imageHeight;
    int stride;
    Format format;
    unsigned char * data;

};

/// Use backend GUI DIB to implement required surface interface.
class OnyxSurface : public dpdoc::Surface
{
public:
    OnyxSurface(unsigned int width, unsigned int height);
    ~OnyxSurface();

     virtual int getSurfaceKind();
     virtual int getPixelLayout();
     virtual unsigned char * getTransferMap( int channel );
     virtual unsigned char * getDitheringClipMap( int channel );
     virtual int getDitheringDepth( int channel );
     virtual unsigned char * checkOut( int xMin, int yMin, int xMax, int yMax, size_t * stride );
     virtual void checkIn( unsigned char * basePtr );

     void attach(const AndroidBitmapInfo & info, void * pixels, bool clear);

     static OnyxImage::Format imageFormat();
     static void setImageFormat(OnyxImage::Format format);

     int width() const;
     int height() const;

     void setDirty(bool dirty = true);
     bool isDirty();

     bool acquire();
     bool isLock();
     bool release();

     OnyxImage &image();
     OnyxImage &cachedImage();
     void cacheImage();

private:
    int width_;
    int height_;
    bool dirty_;
    OnyxImage backendImage;
    OnyxImage myCachedImage;
    bool lock;
};

};

#endif // ONYX_SURFACE_H__
