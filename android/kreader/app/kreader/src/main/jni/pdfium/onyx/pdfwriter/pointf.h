#ifndef POINTF_H
#define POINTF_H


struct PointF
{
    float x;
    float y;

    PointF(float x, float y)
        : x(x), y(y) {
    }

    PointF(double x, double y)
        : x(static_cast<float>(x)), y(static_cast<float>(y)) {
    }
};

#endif // POINTF_H
