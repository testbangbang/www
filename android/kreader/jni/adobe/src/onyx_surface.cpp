
#include "onyx_surface.h"
#include "types.h"

namespace onyx {

OnyxImage::OnyxImage()
{
}

OnyxImage::OnyxImage(int w, int h, Format f) {
    imageWidth = w;
    imageHeight = h;
    format = f;
    data = NULL;
    stride = imageWidth * 4;
}

int OnyxImage::width() const
{
    return imageWidth;
}

int OnyxImage::height() const
{
    return imageHeight;
}

int OnyxImage::depth() const {
    return 32;
}

unsigned char * OnyxImage::getData() {
    return data;
}

unsigned char * OnyxImage::bits() {
    return data;
}

int OnyxImage::bytesPerLine() {
    if (stride <= 0) {
        return imageWidth * 4;
    }
    return stride;
}

void OnyxImage::attach(const AndroidBitmapInfo & info, void * pixels, bool clear) {
    stride = info.stride;
    imageWidth = info.width;
    imageHeight = info.height;
    data = (unsigned char *)pixels;
    if (clear) {
        memset(data, 0xffffffff, stride * imageHeight);
    }
}

bool OnyxImage::contentArea(OnyxRectangle & content)
{
    return false;
}

static OnyxImage::Format g_image_format_ = OnyxImage::Format_ARGB32;
static unsigned char g_dithering_clip_map[256];
static bool g_dithering_clip_map_initialized = false;
static int g_dithering_depth = 3;
static std::vector<unsigned int> g_color_table;

OnyxSurface::OnyxSurface(unsigned int width, unsigned int height)
: width_(width)
, height_(height)
, dirty_(true)
, backendImage(width, height, g_image_format_)
, lock(false)
{
    //LOGI("Create surface %d %d", width, height);
    //image_.getImage().fill(0xffffffff);
    if (g_color_table.size() <= 0)
    {
        for(int i = 0; i < 256; ++i)
        {
            g_color_table.push_back(i);
        }
    }
    //image_.getImage().setColorTable(g_color_table);

    if (!g_dithering_clip_map_initialized)
    {
        dpdoc::Surface::initDitheringClipMap(g_dithering_clip_map, g_dithering_depth);
        g_dithering_clip_map_initialized = true;
    }
}

void OnyxSurface::attach(const AndroidBitmapInfo & info, void * pixels, bool clear) {
    backendImage.attach(info, pixels, clear);
}

OnyxSurface::~OnyxSurface()
{
}

OnyxImage::Format OnyxSurface::imageFormat()
{
    return g_image_format_;
}

void OnyxSurface::setImageFormat(OnyxImage::Format format)
{
    g_image_format_ = format;
}

int OnyxSurface::getSurfaceKind()
{
    return dpdoc::SK_RASTER;
}

int OnyxSurface::getPixelLayout()
{
    switch (backendImage.depth())
    {
    case 24:
        return dpdoc::PL_RGB;
    case 32:
        return dpdoc::PL_BGRX;
    case 16:
        return  dpdoc::PL_BGRA;
    case 8:
        return dpdoc::PL_L;
    }
    return dpdoc::PL_BGR;
}

unsigned char * OnyxSurface::getTransferMap(int channel)
{
    return NULL;
}

unsigned char * OnyxSurface::getDitheringClipMap(int channel)
{
    int depth = getDitheringDepth(0);
    if (depth == 0 || depth > 3)
    {
        return 0;
    }
    return g_dithering_clip_map;
}

int OnyxSurface::getDitheringDepth(int channel)
{
    int format = getPixelLayout();
    return format == dpdoc::PL_L ? g_dithering_depth : 0;
}

unsigned char * OnyxSurface::checkOut(int xMin,
                                      int yMin,
                                      int xMax,
                                      int yMax,
                                      size_t * stride)
{
    *stride = backendImage.bytesPerLine();
    int bpp = backendImage.depth() / 8;
    if (bpp < 1)
    {
        bpp = 1;
    }

    const uchar * buf = backendImage.bits();
    return (uchar*)buf + (*stride) * yMin + bpp * xMin;
}

void OnyxSurface::checkIn(unsigned char * basePtr)
{
}

int OnyxSurface::width() const
{
    return width_;
}

int OnyxSurface::height() const
{
    return height_;
}

void OnyxSurface::setDirty(bool dirty)
{
    dirty_ = dirty;
}

bool OnyxSurface::isDirty()
{
    return dirty_;
}

/// It's necessary to provide a dummy image as the backend image
/// may be in renderering. When it's locked, we return the dummy
/// image, viewer can draw an empty image on screen.
OnyxImage & OnyxSurface::image()
{
    if (isLock())
    {
        static OnyxImage dummy;
        LOGE("fetch dummy image");
        return dummy;
    }
    return backendImage;
}

bool OnyxSurface::acquire()
{
    if (lock)
    {
        return false;
    }
    lock = true;
    return true;
}

bool OnyxSurface::isLock()
{
    return lock;
}

bool OnyxSurface::release()
{
    if (!lock)
    {
        return false;
    }
    lock = false;
    return true;
}

}

