#include "onyx_pdf_writer.h"

#include "podofo/podofo.h"

#include "page_annotation.h"
#include "page_scribble.h"

namespace {

PoDoFo::PdfColor colorFromRgb(uint32_t rgb) {
    double r = (rgb >> 16 & 0xFF) / static_cast<double>(255);
    double g = (rgb >> 8 & 0xFF) / static_cast<double>(255);
    double b = (rgb & 0xFF) / static_cast<double>(255);
    return PoDoFo::PdfColor(r, g, b);
}

/**
 * @brief getPageCropBox
 * @param page
 * @return rect in PDF's user space coordinate system
 */
RectF getPageCropBox(PoDoFo::PdfPage *page) {
    const PoDoFo::PdfRect cropBox = page->GetCropBox();
    return RectF(static_cast<float>(cropBox.GetLeft()),
                 static_cast<float>(cropBox.GetBottom() + cropBox.GetHeight()),
                 static_cast<float>(cropBox.GetLeft() + cropBox.GetWidth()),
                 static_cast<float>(cropBox.GetBottom()));
}

bool translateFromDeviceToPage(const RectF &cropBox, PointF *point) {
    if (point->y > cropBox.height()) {
        return false;
    }
    point->x += cropBox.left;
    point->y = cropBox.bottom + (cropBox.height() - point->y);
    return true;
}

bool translateFromDeviceToPage(const RectF &cropBox, RectF *rect) {
    PointF leftTop(rect->left, rect->top);
    PointF rightBottom(rect->right, rect->bottom);
    if (!translateFromDeviceToPage(cropBox, &leftTop)) {
        return false;
    }
    if (!translateFromDeviceToPage(cropBox, &rightBottom)) {
        return false;
    }
    rect->set(leftTop, rightBottom);
    return true;
}

bool createAnnotationPolyLine(PoDoFo::PdfDocument *document,
                              PoDoFo::PdfPage *page,
                              const PageScribble::Stroke &stroke) {
    assert(document && page);

    using namespace PoDoFo;

    const RectF cropBox = getPageCropBox(page);

    RectF rect = stroke.rect;
    if (!translateFromDeviceToPage(cropBox, &rect)) {
        return false;
    }
    PdfRect pdfRect(static_cast<int>(rect.left),
                    static_cast<int>(rect.bottom),
                    static_cast<int>(rect.width()),
                    static_cast<int>(rect.height()));

    PdfAnnotation *polyline = page->CreateAnnotation(ePdfAnnotation_PolyLine, pdfRect);

    const PdfColor color(colorFromRgb(stroke.color));
    polyline->SetColor(color.GetRed(), color.GetGreen(), color.GetBlue());

    PdfDictionary &dict = polyline->GetObject()->GetDictionary();
    PdfArray *vertices = new PdfArray();
    for (auto point : stroke.points) {
        if (!translateFromDeviceToPage(cropBox, &point)) {
            return false;
        }
        vertices->push_back(point.x);
        vertices->push_back(point.y);
    }
    dict.AddKey(PdfName("Vertices"), *vertices);

    PdfXObject *xobj = new PdfXObject(pdfRect, document);
    PdfPainter pnt;
    pnt.SetPage(xobj);
    pnt.SetStrokeWidth(stroke.thickness);
    pnt.SetStrokingColor(color);
    for (size_t i = 0; i < stroke.points.size() - 1; i++) {
        PointF p1 = stroke.points[i];
        PointF p2 = stroke.points[i + 1];
        if (!translateFromDeviceToPage(cropBox, &p1) ||
                !translateFromDeviceToPage(cropBox, &p2)) {
            return false;
        }
        pnt.DrawLine(p1.x, p1.y, p2.x, p2.y);
    }
    pnt.FinishPage();

    polyline->SetAppearanceStream(xobj);

    return true;
}

bool createAnnotationHightlight(PoDoFo::PdfDocument *document,
                                PoDoFo::PdfPage *page,
                                const PageAnnotation &annotation) {
    using namespace PoDoFo;

    std::vector<RectF> rectList = annotation.rects;
    if (rectList.empty()) {
        return false;
    }

    RectF cropBox = getPageCropBox(page);

    RectF boundingRect(PointF(rectList.at(0).left, rectList.at(0).top),
                     PointF(rectList.at(0).right, rectList.at(0).bottom));
    for (auto &rect : rectList) {
        boundingRect.unite(RectF(rect.left, rect.top, rect.right, rect.bottom));
    }
    if (!translateFromDeviceToPage(cropBox, &boundingRect)) {
        return false;
    }

    PdfRect pdfBoundingRect(static_cast<int>(boundingRect.left),
                            static_cast<int>(boundingRect.bottom),
                            static_cast<int>(boundingRect.width()),
                            static_cast<int>(boundingRect.height()));
    PdfAnnotation *highlight = page->CreateAnnotation(ePdfAnnotation_Highlight, pdfBoundingRect);
    if (!highlight) {
        return false;
    }

    if (annotation.note.size() > 0) {
        highlight->SetContents(PdfString(reinterpret_cast<const pdf_utf8*>(annotation.note.c_str())));
    }

    // default (1.0, 1.0, 0.0) is very obscure on device,
    // so we choose darker color instead
    const PdfColor color(0.9, 0.9, 0.0);
    highlight->SetColor(color.GetRed(), color.GetGreen(), color.GetBlue());

    PdfXObject *xobj = new PdfXObject(pdfBoundingRect, document);
    PdfPainter pnt;
    pnt.SetPage(xobj);
    PdfExtGState *gstate = new PdfExtGState((document));
    gstate->SetFillOpacity(1.0);
    gstate->SetBlendMode("Multiply");
    pnt.SetExtGState(gstate);
    pnt.SetColor(color);

    PdfArray quads;
    for (auto r : rectList) {
        if (!translateFromDeviceToPage(cropBox, &r)) {
            return false;
        }
        quads.push_back(static_cast<double>(r.left));
        quads.push_back(static_cast<double>(r.top));
        quads.push_back(static_cast<double>(r.right));
        quads.push_back(static_cast<double>(r.top));
        quads.push_back(static_cast<double>(r.left));
        quads.push_back(static_cast<double>(r.bottom));
        quads.push_back(static_cast<double>(r.right));
        quads.push_back(static_cast<double>(r.bottom));

        pnt.Rectangle(r.left, r.bottom, r.width(), r.height());
        pnt.Fill();
    }

    highlight->SetQuadPoints(quads);

    pnt.FinishPage();
    highlight->SetAppearanceStream(xobj);

    return true;
}

}

class OnyxPdfWriter::Impl {
public:
    Impl()
        : doc_(nullptr) {
    }

    ~Impl() {
        close();
    }

    bool openPDF(const std::string &docPath) {
        if (isOpened()) {
            close();
        }

        doc_ = new PoDoFo::PdfMemDocument(docPath.c_str());
        if (!doc_->GetInfo()) {
            delete doc_;
            doc_ = nullptr;
            return false;
        }

        this->docPath_ = docPath;
        return true;
    }

    bool saveAs(const std::string &dstPath) {
        if (!isOpened()) {
            return false;
        }

        if (doc_->GetInfo() && doc_->GetInfo()->GetTitle().IsValid()) {
            std::string utf8Title = doc_->GetInfo()->GetTitle().GetStringUtf8();
            std::string newTitle = std::string(utf8Title) + " - Merged";
            PoDoFo::PdfString dstTitle(reinterpret_cast<const PoDoFo::pdf_utf8*>(newTitle.c_str()));
            doc_->GetInfo()->SetTitle(dstTitle);
        }

        doc_->Write(dstPath.c_str());
        return true;
    }

    void close() {
        if (!isOpened()) {
            return;
        }

        delete doc_;
        doc_ = nullptr;
    }

    bool isOpened() const {
        return doc_ && doc_->IsLoaded();
    }

    bool writeScribble(const PageScribble &scribble) {
        if (!isOpened()) {
            return false;
        }

        PoDoFo::PdfPage *page = doc_->GetPage(scribble.page);
        if (!page) {
            return false;
        }
        for (const auto &stroke : scribble.strokes) {
            if (!createAnnotationPolyLine(doc_, page, stroke)) {
                return false;
            }
        }
        return true;
    }

    bool writeScribbles(const std::vector<PageScribble> &pageScribbles) {
        if (!isOpened()) {
            return false;
        }

        for (const auto &scribble : pageScribbles) {
            PoDoFo::PdfPage *page = doc_->GetPage(scribble.page);
            if (!page) {
                return false;
            }
            if (!writeScribble(scribble)) {
                return false;
            }
        }

        return true;
    }

    bool writeAnnotation(const PageAnnotation &annotation) {
        if (!isOpened()) {
            return false;
        }

        PoDoFo::PdfPage *page = doc_->GetPage(annotation.page);
        if (!page) {
            return false;
        }
        return createAnnotationHightlight(doc_, page, annotation);
    }

    bool writeAnnotations(const std::vector<PageAnnotation> &pageAnnotations) {
        if (!isOpened()) {
            return false;
        }

        for (const auto &annot : pageAnnotations) {
            if (!writeAnnotation(annot)) {
                return false;
            }
        }

        return true;
    }

private:
    std::string docPath_;
    PoDoFo::PdfMemDocument *doc_;
};

OnyxPdfWriter::OnyxPdfWriter()
{
    impl.reset(new Impl());
}

OnyxPdfWriter::~OnyxPdfWriter()
{

}

bool OnyxPdfWriter::openPDF(const std::string &path)
{
    return impl->openPDF(path);
}

bool OnyxPdfWriter::saveAs(const std::string &path)
{
    return impl->saveAs(path);
}

void OnyxPdfWriter::close()
{
    impl->close();
}

bool OnyxPdfWriter::isOpened() const
{
    return impl->isOpened();
}

bool OnyxPdfWriter::writeScribble(const PageScribble &scribble)
{
    return impl->writeScribble(scribble);
}

bool OnyxPdfWriter::writeScribbles(const std::vector<PageScribble> &scribbles)
{
    return impl->writeScribbles(scribbles);
}

bool OnyxPdfWriter::writeAnnotation(const PageAnnotation &annotation)
{
    return impl->writeAnnotation(annotation);
}

bool OnyxPdfWriter::writeAnnotations(const std::vector<PageAnnotation> &annotations)
{
    return impl->writeAnnotations(annotations);
}
