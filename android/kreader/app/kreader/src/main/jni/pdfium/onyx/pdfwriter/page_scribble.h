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
        uint32_t color;
        double thickness;

        Stroke()
            : color(0), thickness(1.0) {
        }
    };

    int page;
    std::vector<Stroke> strokes;

    PageScribble()
        : page(0) {
    }
};

#endif // PDFSCRIBBLE_H
