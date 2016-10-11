#ifndef PDFANNOTATION_H
#define PDFANNOTATION_H

#include <string>
#include <vector>

#include "rectf.h"

struct PageAnnotation
{
    int page;
    std::string note;
    std::vector<RectF> rects;
};

#endif // PDFANNOTATION_H
