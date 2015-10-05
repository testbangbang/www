#ifndef ONYX_MATRIX_H_
#define ONYX_MATRIX_H_

#include "dp_all.h"
#include "types.h"


namespace onyx
{

class OnyxMatrix : public dpdoc::Matrix
{
public:
    OnyxMatrix();
    ~OnyxMatrix();

public:
    double scale() const;
    double x() const;
    double y() const;

    void setX(double x);
    void setY(double y);
    void setXY(double x, double y);
    void mirror();

    OnyxMatrix & setScale(double s, bool scale_x = false, bool scale_y = false);
    OnyxMatrix & move(double dx, double dy);

    bool operator == (const OnyxMatrix & right) const;

};

class OnyxRectangle : public dpdoc::Rectangle
{
public:
    OnyxRectangle();
    OnyxRectangle(const OnyxRectangle & ref);
    OnyxRectangle(double x, double y, double width, double height);
    OnyxRectangle(double width, double height);
    OnyxRectangle &operator=(const OnyxRectangle &ref);
    ~OnyxRectangle();

public:
    bool isEmpty() const;
    void setCoords(double left, double top, double right, double bottom);
    void reset();

    double left() const;
    double right() const;
    double top() const;
    double bottom() const;

    double width() const;
    double height() const;

    void move(double dx, double dy);

    void setTopLeft(double x, double y);
    void setBottomRight(double x, double y);

    void scale(double s);
    void adjust(double dx1, double dy1, double dx2, double dy2);


    OnyxRectangle unite(const OnyxRectangle &r);

};

class OnyxMargins
{
public:
    OnyxMargins();
    OnyxMargins(double l, double t, double r, double b);
    OnyxMargins & operator = (const OnyxMargins & right);

    double left() const { return left_; }
    double top() const { return top_; }
    double right() const { return right_; }
    double bottom() const { return bottom_; }

    OnyxMargins & applyScale(double scale);


    void reset();
    bool isEmpty();
    bool isValid();

private:
    double left_, top_, right_, bottom_;
};

class PositionHolder {
public:
    PositionHolder(dpdoc::Renderer *renderer, bool save = true);
    ~PositionHolder();

public:
    void save();
    void restore();
    OnyxMatrix & matrix();

private:
    bool savePosition;
    dpdoc::Renderer *renderer;
    int mode;
    OnyxMatrix pm;
    dp::ref<dpdoc::Location> location;
};


};

#endif
