package com.onyx.kreader.ui.handler;

import android.content.Context;
import android.graphics.PointF;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.alibaba.fastjson.JSONObject;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderConfig;
import com.onyx.kreader.ui.data.CustomBindKeyBean;
import com.onyx.kreader.dataprovider.SharedPreferenceProvider;
import com.onyx.kreader.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 3/19/14
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class HandlerManager {

    public static final String BASE_PROVIDER = "bp";
    public static final String WORD_SELECTION_PROVIDER = "wp";
    public static final String SCRIBBLE_PROVIDER = "scribble";
    public static final String ERASER_PROVIDER = "eraser";
    public static final String SELECTION_ZOOM = "sz";

    private String activeProvider;
    private Map<String, BaseHandler> providerMap = new HashMap<String, BaseHandler>();
    private PointF touchStartPosition;
    private boolean enable;
    private boolean enableTouch;
    static private boolean enableScrollAfterLongPress = false;
    private boolean  penErasing = false;
    private boolean penStart = false;
    private ReaderConfig readerConfig;
    private int lastToolType = MotionEvent.TOOL_TYPE_FINGER;

    public HandlerManager(final Context context) {
        super();
        initProviderMap(context);
    }

    private void initProviderMap(final Context context) {
        providerMap.put(BASE_PROVIDER, new BaseHandler(this));
        providerMap.put(WORD_SELECTION_PROVIDER, new WordSelectionHandler(this, context));
        providerMap.put(SCRIBBLE_PROVIDER, new ScribbleHandler(this));
        activeProvider = BASE_PROVIDER;
        enable = true;
        enableTouch = true;
        readerConfig = ReaderConfig.sharedInstance(context);
    }

    public Map<String, Map<String, JSONObject>> getKeyBinding() {
        return readerConfig.getKeyBinding();
    }

    public JSONObject getKeyBinding(final String state, final String keycode) {
        Map<String, JSONObject> map = getKeyBinding().get(state);
        if (map == null) {
            return null;
        }
        return  map.get(keycode);
    }

    private final CustomBindKeyBean getKeyBean(final String keycode) {
        if (SharedPreferenceProvider.getPrefs() != null) {
            CustomBindKeyBean bean = JSONObject.parseObject(SharedPreferenceProvider.getPrefs().
                    getString(keycode, null), CustomBindKeyBean.class);
            return bean;
        }
        return null;
    }

    public final String getKeyAction(final String state, final String keycode) {
        final CustomBindKeyBean bean = getKeyBean(keycode);
        if (bean != null) {
            return bean.getAction();
        }

        JSONObject object = getKeyBinding(state, keycode);
        if (object != null) {
            return object.getString(ReaderConfig.KEY_ACTION_TAG);
        }
        return null;
    }

    public final String getKeyArgs(final String state, final String keycode) {
        final CustomBindKeyBean bean = getKeyBean(keycode);
        if (bean != null) {
            return bean.getArgs();
        }

        JSONObject object = getKeyBinding(state, keycode);
        if (object != null) {
            return object.getString(ReaderConfig.KEY_ARGS_TAG);
        }
        return null;
    }

    public void setTouchStartEvent(MotionEvent event) {
        if (touchStartPosition == null) {
            touchStartPosition = new PointF(event.getX(), event.getY());
        }
    }

    public void setEnable(boolean e) {
        enable = e;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnableTouch(boolean e) {
        enableTouch = e;
    }

    public boolean isEnableTouch() {
        return enableTouch;
    }

    public PointF getTouchStartPosition() {
        return touchStartPosition;
    }

    public void resetTouchStartPosition() {
        touchStartPosition = null;
    }

    public void resetToDefaultProvider() {
        activeProvider = BASE_PROVIDER;
    }

    public void setActiveProvider(final String providerName) {
        activeProvider = providerName;
        if (providerName.equals(ERASER_PROVIDER)) {
            setPenStart(false);
            setPenErasing(true);
        } else if (providerName.equals(SCRIBBLE_PROVIDER)) {
            setPenStart(true);
        }
    }

    public BaseHandler getActiveProvider() {
        return providerMap.get(activeProvider);
    }

    public boolean isLongPress() {
        return getActiveProvider().isLongPress();
    }

    public boolean onKeyDown(ReaderActivity activity, int keyCode, KeyEvent event) {
        if (!isEnable()) {
            return false;
        }
        return getActiveProvider().onKeyDown(activity, keyCode, event);
    }

    public boolean onKeyUp(ReaderActivity activity, int keyCode, KeyEvent event) {
        if (!isEnable()) {
            return false;
        }
        return getActiveProvider().onKeyUp(activity, keyCode, event);
    }

    public boolean onTouchEvent(ReaderActivity activity, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        int toolType = e.getToolType(0);
        if (lastToolType != toolType) {
            if ((toolType == MotionEvent.TOOL_TYPE_STYLUS) || (lastToolType == MotionEvent.TOOL_TYPE_ERASER && toolType == MotionEvent.TOOL_TYPE_FINGER)) {
                //activity.changeToScribbleMode();
            } else if (toolType == MotionEvent.TOOL_TYPE_ERASER) {
                //activity.changeToEraseMode();
            }
            lastToolType = toolType;
        }

        return getActiveProvider().onTouchEvent(activity, e);
    }

    public boolean onActionUp(ReaderActivity activity, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onActionUp(activity, getTouchStartPosition().x, getTouchStartPosition().y, e.getX(), e.getY());
    }

    public boolean onDown(ReaderActivity activity, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }

        return getActiveProvider().onDown(activity, e);
    }

    public boolean onDoubleTap(ReaderActivity activity, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }

        return getActiveProvider().onDoubleTap(activity, e);
    }

    public void onShowPress(ReaderActivity activity, MotionEvent e) {
        if (!isEnable()) {
            return;
        }
        getActiveProvider().onShowPress(activity, e);
    }

    public boolean onSingleTapUp(ReaderActivity activity,  MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onSingleTapUp(activity, e);
    }

    public boolean onSingleTapConfirmed(ReaderActivity activity, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onSingleTapConfirmed(activity, e);
    }

    public boolean onScrollAfterLongPress(ReaderActivity activity,  float x1, float y1, float x2, float y2) {
        if (enableScrollAfterLongPress) {
            if (!isEnable()) {
                return false;
            }
            if (!isEnableTouch()) {
                return false;
            }
            return getActiveProvider().onScrollAfterLongPress(activity, x1, y1, x2, y2);
        }
        return false;
    }

    public boolean onScroll(ReaderActivity activity,  MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onScroll(activity, e1, e2, distanceX, distanceY);
    }

    public void onLongPress(ReaderActivity activity, MotionEvent e) {
        if (!isEnable()) {
            return;
        }
        if (!isEnableTouch()) {
            return;
        }
        getActiveProvider().onLongPress(activity, getTouchStartPosition().x, getTouchStartPosition().y, e.getX(), e.getY());
    }

    public boolean onFling(ReaderActivity activity, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onFling(activity, e1, e2, velocityX, velocityY);
    }

    public boolean onScaleEnd(ReaderActivity activity, ScaleGestureDetector detector) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onScaleEnd(activity, detector);
    }

    public boolean onScaleBegin(ReaderActivity activity, ScaleGestureDetector detector) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onScaleBegin(activity, detector);
    }

    public boolean onScale(ReaderActivity activity, ScaleGestureDetector detector)  {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onScale(activity, detector);
    }

    public void setPenErasing(boolean c) {
        penErasing = c;
    }

    public boolean isPenErasing() {
        return penErasing;
    }

    public void setPenStart(boolean s) {
        penStart = s;
    }

    public boolean isPenStart() {
        return penStart;
    }

    public void resetPenState() {
        penErasing = false;
        penStart = false;
        lastToolType = MotionEvent.TOOL_TYPE_FINGER;
    }

    public boolean processKeyDown(ReaderActivity activity,  final String action, final String args) {
        if (StringUtils.isNullOrEmpty(action)) {
            return false;
        }
        if (action.equals(ReaderConfig.NEXT_SCREEN)) {
            activity.beforePageChangeByUser();
            activity.nextScreen();
        } else if (action.equals(ReaderConfig.NEXT_PAGE)) {
            activity.beforePageChangeByUser();
            activity.nextPage();
        } else if (action.equals(ReaderConfig.PREV_SCREEN)) {
            activity.beforePageChangeByUser();
            activity.prevScreen();
        } else if (action.equals(ReaderConfig.PREV_PAGE)) {
            activity.beforePageChangeByUser();
            activity.prevPage();
        } else if (action.equals(ReaderConfig.MOVE_LEFT)) {
            //activity.moveLeft();
        } else if (action.equals(ReaderConfig.MOVE_RIGHT)) {
            //activity.moveRight();
        } else if (action.equals(ReaderConfig.MOVE_UP)) {
            //activity.moveUp();
        } else if (action.equals(ReaderConfig.MOVE_DOWN)) {
            //activity.moveDown();
        } else if (action.equals(ReaderConfig.TOGGLE_BOOKMARK)) {
            //activity.toggleBookmark();
        } else if (action.equals(ReaderConfig.SHOW_MENU)) {
            activity.showReaderMenu();
        } else if (action.equals(ReaderConfig.CHANGE_TO_ERASE_MODE)) {


        } else if (action.equals(ReaderConfig.CHANGE_TO_SCRIBBLE_MODE)) {

        } else {
            return false;
        }
        return true;
    }

}
