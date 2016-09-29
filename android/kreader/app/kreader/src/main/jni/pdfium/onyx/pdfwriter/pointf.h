#ifndef POINTF_H
#define POINTF_H


struct PointF
{
    float x;
    float y;

    PointF()
        : x(0), y(0) {
    }

    PointF(float x, float y)
        : x(x), y(y) {
    }

    PointF(double x, double y)
        : x(static_cast<float>(x)), y(static_cast<float>(y)) {
    }

    void set(float x, float y) {
        this->x = x;
        this->y = y;
    }
};

#endif // POINTF_H
