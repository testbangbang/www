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

bool translateFromScreenToPage(const RectF &cropBox, PointF *point) {
    if (point->y > cropBox.height()) {
        return false;
    }
    point->x += cropBox.left;
    point->y = cropBox.top + (cropBox.height() - point->y);
    return true;
}

bool translateFromScreenToPage(const RectF &cropBox, RectF *rect) {
    PointF leftTop(rect->left, rect->top);
    PointF rightBottom(rect->right, rect->bottom);
    if (!translateFromScreenToPage(cropBox, &leftTop)) {
        return false;
    }
    if (!translateFromScreenToPage(cropBox, &rightBottom)) {
        return false;
    }
    rect->set(leftTop, rightBottom);
    return true;
}

bool translateFromScreenToPage(const RectF &cropBox, PageScribble *scribble) {
    for (auto &stroke : scribble->strokes) {
        if (!translateFromScreenToPage(cropBox, &stroke.rect)) {
            return false;
        }
        for (auto &point : stroke.points) {
            if (!translateFromScreenToPage(cropBox, &point)) {
                return false;
            }
        }
    }
    return true;
}

bool translateFromScreenToPage(const RectF &cropBox, PageAnnotation *annotation) {
    for (auto &rect : annotation->rects) {
        if (!translateFromScreenToPage(cropBox, &rect)) {
            return false;
        }
    }
    return true;
}

bool createAnnotationPolyLine(PoDoFo::PdfDocument *document,
                              PoDoFo::PdfPage *page,
                              const PageScribble::Stroke &stroke) {
    assert(document && page);

    using namespace PoDoFo;

    const RectF &rect = stroke.rect;
    const std::vector<PointF> &points = stroke.points;
    const double thickness = stroke.thickness;
    const PdfColor color(colorFromRgb(stroke.color));

    PdfRect pdfRect(rect.left, rect.bottom, rect.width(), rect.height());

    PdfAnnotation *polyline = page->CreateAnnotation(ePdfAnnotation_PolyLine, pdfRect);
    polyline->SetColor(color.GetRed(), color.GetGreen(), color.GetBlue());

    PdfDictionary &dict = polyline->GetObject()->GetDictionary();
    PdfArray *vertices = new PdfArray();
    for (std::vector<PointF>::const_iterator it = points.begin(); it != points.end(); ++it) {
        vertices->push_back(it->x);
        vertices->push_back(it->y);
    }
    dict.AddKey(PdfName("Vertices"), *vertices);

    PdfXObject *xobj = new PdfXObject(pdfRect, document);
    PdfPainter pnt;
    pnt.SetPage(xobj);
    pnt.SetStrokeWidth(thickness);
    pnt.SetStrokingColor(color);
    for (size_t i = 0; i < points.size() - 1; i++) {
        pnt.DrawLine(points[i].x, points[i].y, points[i + 1].x, points[i + 1].y);
    }
    pnt.FinishPage();

    polyline->SetAppearanceStream(xobj);

    return true;
}

bool createAnnotationHightlight(PoDoFo::PdfDocument *document,
                                PoDoFo::PdfPage *page,
                                const PageAnnotation &annotation) {
    using namespace PoDoFo;

    std::vector<RectF> rectlist = annotation.rects;
    if (rectlist.empty()) {
        return false;
    }

    RectF boundRect(PointF(rectlist.at(0).left, rectlist.at(0).bottom),
                     PointF(rectlist.at(0).right, rectlist.at(0).top));
    for (auto it = rectlist.begin();
         it != rectlist.end();
         it++) {
        const RectF r = *it;
        boundRect.unite(RectF(PointF(r.left, r.top), PointF(r.right, r.bottom)));
    }

    PdfRect pdfBoundingRect(boundRect.left, boundRect.bottom,
                     boundRect.width(), boundRect.height());
    PdfAnnotation *highlight = page->CreateAnnotation(ePdfAnnotation_Highlight, pdfBoundingRect);
    if (!highlight) {
        return false;
    }

    highlight->SetContents(PdfString(reinterpret_cast<const pdf_utf8*>(annotation.note.c_str())));

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
    for (auto it = rectlist.begin();
         it != rectlist.end();
         it++) {
        const RectF r = *it;

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
        if (doc_) {
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
        if (!doc_) {
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
        if (!doc_) {
            return;
        }
        delete doc_;
        doc_ = nullptr;
    }

    bool writeScribbles(std::vector<PageScribble> &pageScribbles) {
        if (!doc_) {
            return false;
        }

        std::vector<PageScribble>::size_type num_pages = pageScribbles.size();
        for (std::vector<PageScribble>::size_type i = 0; i < num_pages; i++) {
            PoDoFo::PdfPage *page = doc_->GetPage(pageScribbles[i].page);
            if (!page) {
                continue;
            }

            const PoDoFo::PdfRect crop_box = page->GetCropBox();
            const RectF pdf_rect(PointF(crop_box.GetLeft(), crop_box.GetBottom()),
                                 PointF(crop_box.GetLeft() + crop_box.GetWidth(),
                                        crop_box.GetBottom() + crop_box.GetHeight()));
            if (!translateFromScreenToPage(pdf_rect, &(pageScribbles[i]))) {
                continue;
            }

            for (std::vector<PageScribble::Stroke>::const_iterator it = pageScribbles[i].strokes.begin();
                 it != pageScribbles[i].strokes.end();
                 it++) {
                if (!createAnnotationPolyLine(doc_, page, *it)) {
                    continue;
                }
            }
        }

        return true;
    }

    bool writeAnnotations(std::vector<PageAnnotation> &pageAnnotations) {
        if (!doc_) {
            return false;
        }

        for (std::vector<PageAnnotation>::iterator it = pageAnnotations.begin();
             it != pageAnnotations.end();
             it++) {
            PageAnnotation annot = *it;

            PoDoFo::PdfPage *page = doc_->GetPage(annot.page);
            if (!page) {
                continue;
            }

            const PoDoFo::PdfRect cropBox = page->GetCropBox();
            const RectF pdfRect(PointF(cropBox.GetLeft(), cropBox.GetBottom()),
                                 PointF(cropBox.GetLeft() + cropBox.GetWidth(),
                                        cropBox.GetBottom() + cropBox.GetHeight()));
            if (!translateFromScreenToPage(pdfRect, &annot)) {
                continue;
            }

            if (!createAnnotationHightlight(doc_, page, annot)) {
                continue;
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

bool OnyxPdfWriter::writeScribbles(std::vector<PageScribble> &scribbles)
{
    return impl->writeScribbles(scribbles);
}

bool OnyxPdfWriter::writeAnnotations(std::vector<PageAnnotation> &annotations)
{
    return impl->writeAnnotations(annotations);
}
