#include "com_onyx_android_sdk_scribble_utils_InkUtils.h"

#include <cstddef>
#include <cmath>
#include <list>
#include <memory>
#include <vector>

#include <jni.h>

#include "JNIUtils.h"
#include "../touchreader/log.h"

namespace {

bool PRECISE_STYLUS_INPUT = true;

const int TOOL_TYPE_STYLUS = 2;

struct Rect {
    int left, top, right, bottom;

    Rect(int left, int top, int right, int bottom)
        : left(left), top(top), right(right), bottom(bottom) {
    }
};

struct BrushPoint {
    float x;
    float y;
    float size;
    float pressure;
    int toolType;
    long time;

    BrushPoint() {

    }

    BrushPoint(float x, float y, float size, float pressure, int toolType, long time)
        : x(x), y(y), size(size), pressure(pressure), toolType(toolType), time(time) {

    }
};

struct BrushStroke {
    std::vector<BrushPoint> points;

    void addPoint(const BrushPoint &point) {
        points.push_back(point);
    }
};

class BrushPointFilter {
public:
    BrushPointFilter(size_t size, float posDecay, float pressureDecay)
        : bufferSize(size), posDecay(posDecay), pressureDecay(pressureDecay) {

    }

    BrushPoint doFilter(const BrushPoint &point) {
        addToBuffer(point);
        return filterPoint();
    }

private:
    void addToBuffer(const BrushPoint &point) {
        // add new point to the front of the buffer
        if (bufferedPoints.size() > bufferSize) {
            bufferedPoints.pop_back();
        }
        bufferedPoints.push_front(point);
    }

    BrushPoint filterPoint() {
        float wi = 1, w = 0;
        float wi_press = 1, w_press = 0;
        float x = 0, y = 0, pressure = 0, size = 0;
        long time = 0;

        for (auto &p : bufferedPoints) {
            x += p.x * wi;
            y += p.y * wi;
            time += p.time * wi;

            pressure += p.pressure * wi_press;
            size += p.size * wi_press;

            w += wi;
            wi *= posDecay; // exponential backoff

            w_press += wi_press;
            wi_press *= pressureDecay;

            if (PRECISE_STYLUS_INPUT && p.toolType == TOOL_TYPE_STYLUS) {
                // just take the top one, no need to average
                break;
            }
        }

        BrushPoint point;
        point.x = x / w;
        point.y = y / w;
        point.pressure = pressure / w_press;
        point.size = size / w_press;
        point.time = time;
        point.toolType = bufferedPoints.front().toolType;
        return point;
    }

private:
    size_t bufferSize;
    float posDecay;
    float pressureDecay;

    std::list<BrushPoint> bufferedPoints;
};

class BrushStrokeRenderer {
private:
    static constexpr int SMOOTHING_FILTER_WLEN = 6;
    static constexpr float SMOOTHING_FILTER_POS_DECAY = 0.65f;
    static constexpr float SMOOTHING_FILTER_PRESSURE_DECAY = 0.9f;

    static constexpr bool ASSUME_STYLUS_CALIBRATED = true;

public:
    BrushStrokeRenderer(const BrushStroke &stroke)
        : pointFilter(SMOOTHING_FILTER_WLEN, SMOOTHING_FILTER_POS_DECAY, SMOOTHING_FILTER_PRESSURE_DECAY),
          stroke(stroke),
          mLastX(0.0f), mLastY(0.0f), mLastLen(0.0f), mLastR(-1.0f),
          mPressureExponent(2.0f), mRadiusMin(1.0f), mRadiusMax(10.0f) {

    }


public:
    void drawStroke(JNIEnv *env, jobject canvas, jobject paint) {
        for (auto &p : stroke.points) {
            strokeTo(env, canvas, paint, p);
        }
    }

    Rect addPoint(JNIEnv *env, jobject canvas, jobject paint, const BrushPoint &point) {
        Rect dirty = strokeTo(env, canvas, paint, pointFilter.doFilter(point));
        stroke.addPoint(point);
        return dirty;
    }

private:
    Rect strokeTo(JNIEnv *env, jobject canvas, jobject paint, BrushPoint point) {
        float pressureNorm;

        if (ASSUME_STYLUS_CALIBRATED && point.toolType == TOOL_TYPE_STYLUS) {
            pressureNorm = point.pressure;
        } else {
//            pressureNorm = pressureCooker.getAdjustedPressure(point.pressure);
            pressureNorm = point.pressure;
        }

        float radius = lerp(mRadiusMin, mRadiusMax,
                            static_cast<float>(std::pow(pressureNorm, mPressureExponent)));

        return strokeTo(env, canvas, paint, point.x, point.y, radius);
    }

    Rect strokeTo(JNIEnv *env, jobject canvas, jobject paint, float x, float y, float r) {
        Rect dirty(static_cast<int>(x), static_cast<int>(y),
                   static_cast<int>(x), static_cast<int>(y));

        if (mLastR < 0) {
            // always draw the first point
            drawBrushPoint(env, canvas, paint, x, y, r, &dirty);
        } else {
            // connect the dots, la-la-la
            mLastLen = dist(mLastX, mLastY, x, y);
            float xi, yi, ri, frac;
            float d = 0;
            while (true) {
                if (d > mLastLen) {
                    break;
                }
                frac = d == 0.0f ? 0.0f : (d / mLastLen);
                ri = lerp(mLastR, r, frac);
                xi = lerp(mLastX, x, frac);
                yi = lerp(mLastY, y, frac);
                drawBrushPoint(env, canvas, paint, xi, yi, ri, &dirty);

                // for very narrow lines we must step (not much more than) one radius at a time
                constexpr float MIN = 1.0f;
                constexpr float THRESH = 16.0f;
                constexpr float SLOPE = 0.1f; // asymptote: the spacing will increase as SLOPE*x
                if (ri <= THRESH) {
                    d += MIN;
                } else {
                    d += static_cast<float>(std::sqrt(SLOPE * static_cast<float>(std::pow(ri - THRESH, 2)) + MIN));
                }
            }

        }

        mLastX = x;
        mLastY = y;
        mLastR = r;

        return dirty;
    }

    void drawBrushPoint(JNIEnv *env, jobject canvas, jobject paint, float x, float y, float r, Rect *dirty) {
        JNIUtils utils(env);
        if (!utils.findClass("android/graphics/Canvas")) {
            return;
        }
        if (!utils.findMethod("drawCircle", "(FFFLandroid/graphics/Paint;)V")) {
            return;
        }
        env->CallVoidMethod(canvas, utils.getMethodId(), x, y, r, paint);

        dirty->left = std::min(dirty->left, static_cast<int>(x - r));
        dirty->top = std::min(dirty->top, static_cast<int>(y - r));
        dirty->right = std::max(dirty->right, static_cast<int>(x + r));
        dirty->bottom = std::max(dirty->bottom, static_cast<int>(y + r));
    }

    static float dist(float x1, float y1, float x2, float y2) {
        x2 -= x1;
        y2 -= y1;
        return static_cast<float>(std::sqrt(x2 * x2 + y2 * y2));
    }

    static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

private:
    BrushPointFilter pointFilter;
    BrushStroke stroke;

    float mLastX, mLastY, mLastLen;
    float mLastR;

    float mPressureExponent;
    float mRadiusMin;
    float mRadiusMax;

};

}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_scribble_utils_InkUtils_nativeDrawStroke
  (JNIEnv *env, jclass, jobject canvas, jobject paint, jfloatArray points) {
    jsize len = env->GetArrayLength(points);
    std::vector<float> pts(static_cast<size_t>(len));
    env->GetFloatArrayRegion(points, 0, len, pts.data());

    BrushStroke stroke;
    for (size_t i = 0; i < static_cast<size_t>(len); i += 5) {
        float x = pts[i];
        float y = pts[i + 1];
        float pressure = pts[i + 2];
        float size = pts[i + 3];
        long time = static_cast<long>(pts[i + 4]);
        stroke.addPoint(BrushPoint(x, y, size, pressure, TOOL_TYPE_STYLUS, time));
    }

    BrushStrokeRenderer renderer(stroke);
    renderer.drawStroke(env, canvas, paint);
}
