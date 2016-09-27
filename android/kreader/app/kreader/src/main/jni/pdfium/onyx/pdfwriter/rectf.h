#ifndef RECTF_H
#define RECTF_H

#include <cmath>
#include <algorithm>

#include "pointf.h"

struct RectF {
    float left;
    float top;
    float right;
    float bottom;

    RectF() {
    }

    RectF(const PointF &leftTop, const PointF rightBottom)
        : left(leftTop.x), top(leftTop.y),
          right(rightBottom.x), bottom(rightBottom.y) {
    }

    float width() const {
        return std::abs(right - left);
    }

    float height() const {
        return std::abs(bottom - top);
    }

    void set(const PointF &leftTop, const PointF rightBottom) {
        this->left = leftTop.x;
        this->top = leftTop.y;
        this->right = rightBottom.x;
        this->bottom = rightBottom.y;
    }

    void set(const float left, const float top, const float right, const float bottom) {
        this->left = left;
        this->top = top;
        this->right = right;
        this->bottom = bottom;
    }

    void unite(RectF rect) {
        left = std::min(left, rect.left);
        top = std::min(top, rect.top);
        right = std::max(right, rect.right);
        bottom = std::max(right, rect.right);
    }
};

#endif // RECTF_H
