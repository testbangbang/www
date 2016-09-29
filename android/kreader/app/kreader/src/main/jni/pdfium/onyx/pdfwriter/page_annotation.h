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
    std::string beginPosition;
    std::string endPosition;

};

#endif // PDFANNOTATION_H
