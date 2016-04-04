package com.example.engbooktest2;

import com.neverland.engbook.bookobj.AlBookEng;
import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.AlTapInfo;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_GOTOCOMMAND;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_SELECTION_MODE;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_PAGE_INDEX;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class MainView extends View {

	private boolean TAP_ON_WORD_AS_DICTIONARY = false;
    private static int POST_INVALIDATE_TIME = 20;
    private static long TIME_SCROLL_ALL_PAGE = 3000;
	
    private AlBookEng bookEng = null;
    private Context mcontext;
    private MainApp appl = null;
    private Paint paint = new Paint();
    private Paint paintShadow = new Paint();
    private final Rect rSrc = new Rect();
    private final Rect rDst = new Rect();

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;

        appl = MainApp.getOurInstance();

        paint.setColor(0xffff0000);
        paint.setTextSize(11 * appl.dpiMultiplex);
        paint.setAntiAlias(false);
        paint.setFilterBitmap(false);
        paint.setStrokeWidth(0);
    }

    public MainView(Context context) {
        this(context, null);
    }

    public void assignPaintViewWithBookEng(AlBookEng eng) {
        bookEng = eng;
    }

    private void stopAnyScroll() {
        mousePoint.doit = true;
        postInvalidateDelayed(POST_INVALIDATE_TIME);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = this.getWidth();
        int height = this.getHeight();

        if (bookEng != null) {
            bookEng.setNewScreenSize(width, height);
            AlBitmap bmp = bookEng.getPageBitmap(TAL_PAGE_INDEX.CURR, width, height), bmp1 = null;

            if (bmp != null) {
                if (scrollCloser.work) {

                    scrollCloser.recalc();

                    if (!scrollCloser.workWithNext) {
                        bmp1 = bookEng.getPageBitmap(TAL_PAGE_INDEX.PREV, width, height);

                        if (bmp1 != null) {
                            int reservedHeight = bmp1.freeSpaceAfterPage;
                            if (scrollCloser.currentPoint - height + reservedHeight > 0) {
                                canvas.drawBitmap(bmp1.bmp, 0, 0, paint);
                                canvas.drawBitmap(bmp.bmp, 0, height - reservedHeight, paint);
                            } else {
                                canvas.drawBitmap(bmp1.bmp, 0, scrollCloser.currentPoint - height + reservedHeight, paint);
                                canvas.drawBitmap(bmp.bmp, 0, scrollCloser.currentPoint, paint);
                            }
                        } else {
                            canvas.drawBitmap(bmp.bmp, 0, 0, paint);
                            stopAnyScroll();
                        }
                    } else {
                        int reservedHeight = bmp.freeSpaceAfterPage;
                        bmp1 = bookEng.getPageBitmap(TAL_PAGE_INDEX.NEXT, width, height);
                        if (bmp1 != null) {
                            if (height - scrollCloser.currentPoint - reservedHeight < 0) {
                                canvas.drawBitmap(bmp1.bmp, 0, 0, paint);
                            } else {
                                canvas.drawBitmap(bmp1.bmp, 0, height - scrollCloser.currentPoint - reservedHeight, paint);

                                rSrc.left = rSrc.top = 0;
                                rSrc.right = width;
                                rSrc.bottom = height;

                                rDst.left = 0;
                                rDst.right = width;
                                rDst.top = - scrollCloser.currentPoint;
                                rDst.bottom = rDst.top + height;

                                canvas.drawBitmap(bmp.bmp, rSrc, rDst, paint);
                            }
                        } else {
                            canvas.drawBitmap(bmp.bmp, 0, 0, paint);
                            stopAnyScroll();
                        }
                    }

                    if (!scrollCloser.isStop())
                        postInvalidateDelayed(POST_INVALIDATE_TIME);
                } else
                if (mousePoint.isSCROLLX) {
                    canvas.drawBitmap(bmp.bmp, 0, 0, paint);
                    if (mousePoint.last_x >= mousePoint.start_x) {
                        bmp = bookEng.getPageBitmap(TAL_PAGE_INDEX.PREV, width, height);

                        if (bmp != null) {
                            int percent = 196 * (mousePoint.last_x - mousePoint.start_x) / width;
                            percent <<= 24;
                            paintShadow.setColor(percent);
                            canvas.drawRect(mousePoint.last_x - mousePoint.start_x, 0, width, height, paintShadow);
                            canvas.drawBitmap(bmp.bmp, mousePoint.last_x - mousePoint.start_x - width, 0, paint);
                        } else {
                            stopAnyScroll();
                        }
                    } else {
                        bmp = bookEng.getPageBitmap(TAL_PAGE_INDEX.NEXT, width, height);

                        if (bmp != null) {
                            int percent = 196 * (mousePoint.start_x - mousePoint.last_x) / width;
                            percent <<= 24;
                            paintShadow.setColor(percent);
                            canvas.drawRect(0, 0, width - mousePoint.start_x + mousePoint.last_x, height, paintShadow);

                            if (bmp != null)
                                canvas.drawBitmap(bmp.bmp, width - mousePoint.start_x + mousePoint.last_x, 0, paint);
                        } else {
                            stopAnyScroll();
                        }
                    }
                } else
                if (mousePoint.isSCROLLY) {

                    if (mousePoint.last_y >= mousePoint.start_y) {
                        bmp1 = bookEng.getPageBitmap(TAL_PAGE_INDEX.PREV, width, height);

                        if (bmp1 != null) {
                            int reservedHeight = bmp1.freeSpaceAfterPage;
                            if (mousePoint.last_y - mousePoint.start_y - height + reservedHeight > 0) {
                                canvas.drawBitmap(bmp1.bmp, 0, 0, paint);
                                canvas.drawBitmap(bmp.bmp, 0, height - reservedHeight, paint);
                            } else {
                                canvas.drawBitmap(bmp1.bmp, 0, mousePoint.last_y - mousePoint.start_y - height + reservedHeight, paint);
                                canvas.drawBitmap(bmp.bmp, 0, mousePoint.last_y - mousePoint.start_y, paint);
                            }
                        } else {
                            canvas.drawBitmap(bmp.bmp, 0, 0, paint);
                            stopAnyScroll();
                        }
                    } else {
                        int reservedHeight = bmp.freeSpaceAfterPage;
                        bmp1 = bookEng.getPageBitmap(TAL_PAGE_INDEX.NEXT, width, height);
                        if (bmp1 != null) {
                            if (height - mousePoint.start_y + mousePoint.last_y - reservedHeight < 0) {
                                canvas.drawBitmap(bmp1.bmp, 0, 0, paint);
                            } else {
                                canvas.drawBitmap(bmp1.bmp, 0, height - mousePoint.start_y + mousePoint.last_y - reservedHeight, paint);

                                rSrc.left = rSrc.top = 0;
                                rSrc.right = width;
                                rSrc.bottom = height;

                                rDst.left = 0;
                                rDst.right = width;
                                rDst.top = - mousePoint.start_y + mousePoint.last_y;
                                rDst.bottom = rDst.top + height;

                                canvas.drawBitmap(bmp.bmp, rSrc, rDst, paint);
                            }
                        } else {
                            canvas.drawBitmap(bmp.bmp, 0, 0, paint);
                            stopAnyScroll();
                        }
                    }

                } else {
                    canvas.drawBitmap(bmp.bmp, 0, 0, paint);

                    AlIntHolder currPage = new AlIntHolder(0), allPage = new AlIntHolder(0), readPosition = new AlIntHolder(0);
                    if (bookEng.getPageCount(currPage, allPage, readPosition) == TAL_RESULT.OK) {
                        canvas.drawText("pages " + currPage.toString() + '/' + allPage.toString() + " position " + readPosition.toString(), 5, height - 5, paint);
                    }
                }
            }
        }
    }

    private static final int DETECT_SHIFT_MOVE_VALUE = 12;

    private class MousePoint {
        int start_x = 0;
        int start_y = 0;
       
        boolean istap = true;
        boolean doit = false;

        int last_x = 0;
        int last_y = 0;

        boolean isSCROLLX = false;
        boolean isSCROLLY = false;
    }

    private class ScrollCloser {
        static final int TO_PREV = -1;
        static final int TO_NEXT = -2;
        static final int TO_NOTHING = -3;

        private long        startTime;
        private long        drawTime;

        private int         newPos = 0;

        boolean     work = false;
        private boolean     needChangePage = false;
        boolean     workWithNext = true;

        private int         sPoint, ePoint;
        int         currentPoint;

        void start(boolean nextPage, int startPoint, int endPoint, int height, int newBookPosition) {
            needChangePage = newBookPosition != 0;
            newPos = newBookPosition;
            workWithNext = nextPage;

            sPoint = startPoint;
            ePoint = endPoint;

            drawTime = TIME_SCROLL_ALL_PAGE * Math.abs(endPoint - startPoint) / height;
            if (drawTime < 1)
                drawTime = 1;
            startTime = System.currentTimeMillis();

            work = true;
            postInvalidate();
        }

        void stop() {
            if (work) {
                work = false;
                postInvalidate();
            }
        }

        void recalc() {
            long percent = 100 * (System.currentTimeMillis() - startTime) / drawTime;
            if (percent > 100)
                percent = 100;

            if (ePoint < sPoint) {
                currentPoint = (int) (sPoint - (percent * (sPoint - ePoint) / 100));
            } else {
                currentPoint = (int) (sPoint + (percent * (ePoint - sPoint) / 100));
            }
        }

        boolean isStop() {
            work &= System.currentTimeMillis() - startTime <= drawTime;

            if (!work && needChangePage) {
                if (newPos == TO_NOTHING) {
                    postInvalidateDelayed(POST_INVALIDATE_TIME);
                } else
                if (newPos == TO_PREV) {
                    bookEng.gotoPosition(TAL_GOTOCOMMAND.PREVPAGE, 0);
                } else
                if (newPos == TO_NEXT) {
                    bookEng.gotoPosition(TAL_GOTOCOMMAND.NEXTPAGE, 0);
                } else {
                    bookEng.gotoPosition(TAL_GOTOCOMMAND.POSITION, newPos);
                }
            }

            return !work;
        }
    }

    private final ScrollCloser scrollCloser = new ScrollCloser();
    private final MousePoint mousePoint = new MousePoint();

    private void scanSelect(boolean isUp) {
        if (!mousePoint.doit) {
        	
        	AlTapInfo tapInfo = bookEng.getInfoByTap(mousePoint.last_x, mousePoint.last_y, TAP_ON_WORD_AS_DICTIONARY);
            
            TAL_SCREEN_SELECTION_MODE isSelection = bookEng.getSelectionMode();
            
            if (isUp) {
	            switch (isSelection) {
	            case NONE:
	            	if (tapInfo != null && tapInfo.isLocalLink) {
	            		bookEng.gotoPosition(TAL_GOTOCOMMAND.POSITION, tapInfo.linkLocalPosition);
	            	} else 
	            	if (tapInfo != null && tapInfo.isExtLink) {
	            		
	            	} else {
                        bookEng.gotoPosition((mousePoint.last_y > (this.getHeight() >> 1)) ?
                                TAL_GOTOCOMMAND.NEXTPAGE : TAL_GOTOCOMMAND.PREVPAGE, 0);
                    }
	            	break;
	            case DICTIONARY:
                    String res = bookEng.getSelectedText();
	            	bookEng.setSelectionMode(TAL_SCREEN_SELECTION_MODE.NONE);
	            	break;
	            }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        scrollCloser.stop();

        int savex = mousePoint.last_x;
        int savey = mousePoint.last_y;

        int cnt_point = event.getPointerCount();
        if (cnt_point > 1) {            
            mousePoint.doit = true;
            return true;
        } else {
            mousePoint.last_x = x;
            mousePoint.last_y = y;
        }
        
        TAL_SCREEN_SELECTION_MODE isSelection = bookEng.getSelectionMode();

        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
                mousePoint.doit = true;
                break;
            case MotionEvent.ACTION_DOWN:
                mousePoint.doit = false;
                mousePoint.istap = true;                                
                mousePoint.start_x = x;
                mousePoint.start_y = y;

                if (TAP_ON_WORD_AS_DICTIONARY) {
                    scanSelect(false);
                } else {
                    isSelection = bookEng.getSelectionMode();
                    if (isSelection != EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE)
                        scanSelect(false);
                }
                mousePoint.isSCROLLX = mousePoint.isSCROLLY = false;
                break;
            case MotionEvent.ACTION_MOVE:

                if (!mousePoint.doit) {
                    isSelection = bookEng.getSelectionMode();
                    if (isSelection == EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE) {
                        if (mousePoint.istap) {
                            if (Math.abs(mousePoint.last_x - mousePoint.start_x) > DETECT_SHIFT_MOVE_VALUE * appl.dpiMultiplex) {
                                mousePoint.istap = false;
                                mousePoint.doit = true;
                                mousePoint.isSCROLLX = true;
                            }

                            if (Math.abs(mousePoint.last_y - mousePoint.start_y) > DETECT_SHIFT_MOVE_VALUE * appl.dpiMultiplex) {
                                mousePoint.istap = false;
                                mousePoint.doit = true;
                                mousePoint.isSCROLLY = true;
                            }
                        }
                    } else {
                        scanSelect(false);
                    }
                }
                if (mousePoint.isSCROLLX || mousePoint.isSCROLLY) {
                    if (savex != mousePoint.last_x || savey != mousePoint.last_y)
                        postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                scanSelect(true);
                if (mousePoint.isSCROLLX || mousePoint.isSCROLLY) {
                    if (mousePoint.doit && (mousePoint.isSCROLLY && mousePoint.last_y != mousePoint.start_y)) {

                        int height = getHeight();
                        int newPos = ScrollCloser.TO_NOTHING;
                        int ePoint = 0;

                        if (mousePoint.last_y > mousePoint.start_y) {
                            if (true) {
                                AlIntHolder ps = new AlIntHolder(0);
                                AlIntHolder sh = new AlIntHolder(height);
                                bookEng.getScrollShift(false, mousePoint.last_y - mousePoint.start_y, sh, ps);
                                newPos = ps.value;
                                ePoint = sh.value;
                                if (ePoint == height) {
                                    newPos = ScrollCloser.TO_PREV;
                                    //ePoint = height;
                                }
                            } else {
                                if (mousePoint.last_y - mousePoint.start_y > (height / 3)) {
                                    newPos = ScrollCloser.TO_PREV;
                                    ePoint = height;
                                } else {
                                    ePoint = 0;
                                }
                            }

                            scrollCloser.start(
                                    false,
                                    mousePoint.last_y - mousePoint.start_y,
                                    ePoint,
                                    getHeight(),
                                    newPos);
                        } else {
                            if (true) {
                                AlIntHolder ps = new AlIntHolder(0);
                                AlIntHolder sh = new AlIntHolder(height);
                                bookEng.getScrollShift(true, mousePoint.start_y - mousePoint.last_y, sh, ps);
                                newPos = ps.value;
                                ePoint = sh.value;
                                if (ePoint == height)
                                    newPos = ScrollCloser.TO_NEXT;
                            } else {
                                if (mousePoint.start_y - mousePoint.last_y > (height / 3)) {
                                    newPos = ScrollCloser.TO_NEXT;
                                    ePoint = height;
                                } else {
                                    ePoint = 0;
                                }
                            }

                            scrollCloser.start(
                                    true,
                                    mousePoint.start_y - mousePoint.last_y,
                                    ePoint,
                                    getHeight(),
                                    newPos);
                        }
                    }
                    mousePoint.isSCROLLX = mousePoint.isSCROLLY = false;
                    postInvalidate();
                }
                break;
        }

        return true;

    }
}
