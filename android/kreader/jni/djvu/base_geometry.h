#ifdef __cplusplus
extern "C" {
#endif

struct fz_bbox_s
{
    int x0, y0;
    int x1, y1;
};

typedef struct fz_bbox_s fz_bbox;

/* Rectangles and bounding boxes */
#define SAFE_INT(f) ((f > INT_MAX) ? INT_MAX : ((f < INT_MIN) ? INT_MIN : (int)f))

/*
    fz_is_empty_rect: Check if rectangle is empty.

    An empty rectangle is defined as one whose area is zero.
*/
#define fz_is_empty_rect(r) ((r).x0 == (r).x1 || (r).y0 == (r).y1)

/*
    fz_is_empty_bbox: Check if bounding box is empty.

    Same definition of empty bounding boxes as for empty
    rectangles. See fz_is_empty_rect.
*/
#define fz_is_empty_bbox(b) ((b).x0 == (b).x1 || (b).y0 == (b).y1)

/*
    fz_is_infinite: Check if rectangle is infinite.

    An infinite rectangle is defined as one where either of the
    two relationships between corner coordinates are not true.
*/
#define fz_is_infinite_rect(r) ((r).x0 > (r).x1 || (r).y0 > (r).y1)

/*
    fz_is_infinite_bbox: Check if bounding box is infinite.

    Same definition of infinite bounding boxes as for infinite
    rectangles. See fz_is_infinite_rect.
*/
#define fz_is_infinite_bbox(b) ((b).x0 > (b).x1 || (b).y0 > (b).y1)

fz_bbox fz_intersect_bbox(fz_bbox a, fz_bbox b);

#ifdef __cplusplus
}
#endif
