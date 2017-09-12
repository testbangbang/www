#ifndef ONYXPDFWRITER_H
#define ONYXPDFWRITER_H

#include <vector>
#include <string>
#include <memory>

class PointF;
class RectF;
class PageScribble;
class PageAnnotation;

class OnyxPdfWriter
{
public:
    OnyxPdfWriter();
    ~OnyxPdfWriter();

    bool openPDF(const std::string &path);
    bool saveAs(const std::string &path, bool savePagesWithAnnotation);
    void close();

    bool isOpened() const;

    bool writeLine(const int page, const RectF &rect, const uint32_t color, const float strokeThickness,
                   const PointF &start, const PointF &end);
    bool writePolyLine(const int page, const RectF &rect, const uint32_t color, const float strokeThickness,
                      const std::vector<PointF> &points);
    bool writePolygon(const int page, const RectF &rect, const uint32_t color, const float strokeThickness,
                      const std::vector<PointF> &points);
    bool writeSquare(const int page, const RectF &rect, const uint32_t color, const float strokeThickness);
    bool writeCircle(const int page, const RectF &rect, const uint32_t color, const float strokeThickness);

    bool writeAnnotation(const PageAnnotation &annotation);

    bool setDocumentTitle(const std::string &path, const std::string &title);

private:
    class Impl;
    std::unique_ptr<Impl> impl;
};

#endif // ONYXPDFWRITER_H
