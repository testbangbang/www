#ifndef PDFSCRIBBLE_H
#define PDFSCRIBBLE_H

#include <stdint.h>
#include <vector>

#include "pointf.h"
#include "rectf.h"

struct PageScribble
{
public:
    struct Stroke {
        RectF rect;
        std::vector<PointF> points;
        double thickness;
        uint32_t color;
    };

    int page;
    std::vector<Stroke> strokes;
};

#endif // PDFSCRIBBLE_H
