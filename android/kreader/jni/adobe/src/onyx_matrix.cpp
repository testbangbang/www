#include "onyx_matrix.h"

namespace onyx
{

OnyxMatrix::OnyxMatrix()
: Matrix()
{
}

OnyxMatrix::~OnyxMatrix()
{
}

double OnyxMatrix::scale() const
{
    return a;
}

double OnyxMatrix::x() const
{
    return e;
}

double OnyxMatrix::y() const
{
    return f;
}

void OnyxMatrix::setX(double x)
{
    e = x;
}

void OnyxMatrix::setY(double y)
{
    f = y;
}

void OnyxMatrix::setXY(double x, double y)
{
    e = x;
    f = y;
}

void OnyxMatrix::mirror() {
    e = -e;
    f = -f;
}

OnyxMatrix & OnyxMatrix::setScale(double s, bool scale_x, bool scale_y)
{
    if (scale_x)
    {
        e *= s/a;
    }
    if (scale_y)
    {
        f *= s/d;
    }

    a = s;
    d = s;
    return *this;
}

OnyxMatrix & OnyxMatrix::move(double dx, double dy)
{
    e += dx;
    f += dy;
    return *this;
}

bool OnyxMatrix::operator == (const OnyxMatrix & right) const
{
    return (a == right.a &&
            d == right.d &&
            e == right.e &&
            f == right.f);
}


OnyxRectangle::OnyxRectangle()
: Rectangle()
{
}

OnyxRectangle::OnyxRectangle(const OnyxRectangle & ref)
{
    xMin = ref.xMin;
    yMin = ref.yMin;
    xMax = ref.xMax;
    yMax = ref.yMax;
}

OnyxRectangle::OnyxRectangle(double x, double y, double width, double height)
{
    xMin = x;
    yMin = y;
    xMax = x + width - 1;
    yMax = y + height - 1;
}

OnyxRectangle::OnyxRectangle(double width, double height)
: Rectangle()
{
    xMin = 0;
    yMin = 0;
    xMax = width - 1;
    yMax = height - 1;
}

OnyxRectangle &OnyxRectangle::operator=(const OnyxRectangle &ref)
{
    if (this != &ref) {
        xMin = ref.xMin;
        yMin = ref.yMin;
        xMax = ref.xMax;
        yMax = ref.yMax;
    }
    return *this;
}

OnyxRectangle::~OnyxRectangle()
{
}

bool OnyxRectangle::isEmpty() const
{
    return (xMin >= xMax || yMin >= yMax);
}

void OnyxRectangle::setCoords(double left, double top, double right, double bottom)
{
    xMin = left;
    yMin = top;
    xMax = right;
    yMax = bottom;
}

void OnyxRectangle::reset()
{
    xMin = yMin = xMax = yMax = 0;
}

double OnyxRectangle::left() const
{
    return xMin;
}

double OnyxRectangle::right() const
{
    return xMax;
}

double OnyxRectangle::top() const
{
    return yMin;
}

double OnyxRectangle::bottom() const
{
    return yMax;
}

double OnyxRectangle::width() const
{
    return xMax - xMin + 1;
}

double OnyxRectangle::height() const
{
    return yMax - yMin + 1;
}

void OnyxRectangle::move(double dx, double dy)
{
    xMin += dx; xMax += dx;
    yMin += dy; yMax += dy;
}

void OnyxRectangle::setTopLeft(double x, double y)
{
    xMin = x;
    yMin = y;
}

void OnyxRectangle::setBottomRight(double x, double y)
{
    xMax = x;
    yMax = y;
}

void OnyxRectangle::scale(double s)
{
    xMin *= s;
    yMin *= s;
    xMax *= s;
    yMax *= s;
}

void OnyxRectangle::adjust(double dx1, double dy1, double dx2, double dy2)
{
    xMin += dx1;
    yMin += dy1;
    xMax += dx2;
    yMax += dy2;
}


OnyxRectangle OnyxRectangle::unite(const OnyxRectangle &r)
{
    OnyxRectangle l;
    return l;
}

OnyxMargins::OnyxMargins()
: left_(0)
, top_(0)
, right_(0)
, bottom_(0)
{
}

OnyxMargins::OnyxMargins(double l, double t, double r, double b)
: left_(l)
, top_(t)
, right_(r)
, bottom_(b)
{
}

OnyxMargins & OnyxMargins::operator = (const OnyxMargins & right)
{
    if (this != &right)
    {
        left_ = right.left_;
        right_ = right.right_;
        top_ = right.top_;
        bottom_ = right.bottom_;
    }
    return *this;
}

OnyxMargins & OnyxMargins::applyScale(double scale)
{
    left_ *= scale;
    top_ *= scale;
    right_ *= scale;
    bottom_ *= scale;
    return *this;
}


void OnyxMargins::reset()
{
    left_ = top_ = right_ = bottom_ = 0;
}

bool OnyxMargins::isEmpty()
{
    return (left_ >= right_ || top_ >= bottom_);
}

bool OnyxMargins::isValid()
{
    return (left_ != 0 || right_ != 0 || top_ != 0 || bottom_ != 0);
}


PositionHolder::PositionHolder(dpdoc::Renderer *r, bool s)
: renderer(r)
, savePosition(s) {
    if (savePosition) {
        save();
    }
}

PositionHolder::~PositionHolder() {
    if (savePosition) {
        restore();
    }
}

void PositionHolder::save() {
    mode = renderer->getPagingMode();
    location = renderer->getCurrentLocation();
    renderer->getNavigationMatrix(&pm);
}

void PositionHolder::restore() {
    if (mode != renderer->getPagingMode()) {
        renderer->setPagingMode(mode);
    }
    renderer->navigateToLocation(location);
    renderer->setNavigationMatrix(pm);

}

OnyxMatrix & PositionHolder::matrix() {
    return pm;
}

}

