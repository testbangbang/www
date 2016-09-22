package com.onyx.kreader.ui.handler;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.KeyAction;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.ui.actions.PanAction;
import com.onyx.kreader.ui.actions.ShowReaderMenuAction;
import com.onyx.android.sdk.data.CustomBindKeyBean;
import com.onyx.kreader.ui.actions.ToggleBookmarkAction;
import com.onyx.kreader.ui.data.ReaderConfig;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;

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

    public static final String TAG = HandlerManager.class.getSimpleName();
    public static final String READING_PROVIDER = "rp";
    public static final String WORD_SELECTION_PROVIDER = "wp";
    public static final String SCRIBBLE_PROVIDER = "scribble";
    public static final String ERASER_PROVIDER = "eraser";
    public static final String TTS_PROVIDER = "tts";

    private String activeProviderName;
    private Map<String, BaseHandler> providerMap = new HashMap<String, BaseHandler>();
    private PointF touchStartPosition;
    private boolean enable;
    private boolean enableTouch;
    static private boolean enableScrollAfterLongPress = false;
    private boolean  penErasing = false;
    private boolean penStart = false;
    private ReaderConfig readerConfig;
    private int lastToolType = MotionEvent.TOOL_TYPE_FINGER;
    private ReaderDataHolder readerDataHolder;

    public HandlerManager(final ReaderDataHolder holder) {
        super();
        readerDataHolder = holder;
        initProviderMap(readerDataHolder.getContext());
    }

    private void initProviderMap(final Context context) {
        providerMap.put(READING_PROVIDER, new ReadingHandler(this));
        providerMap.put(WORD_SELECTION_PROVIDER, new WordSelectionHandler(this, readerDataHolder.getContext()));
        providerMap.put(SCRIBBLE_PROVIDER, new ScribbleHandler(this));
        providerMap.put(ERASER_PROVIDER, new ScribbleHandler(this));
        providerMap.put(TTS_PROVIDER, new TtsHandler(this));
        activeProviderName = READING_PROVIDER;
        enable = true;
        enableTouch = true;
        readerConfig = ReaderConfig.sharedInstance(context);
    }

    public com.onyx.kreader.ui.data.ReaderDataHolder getReaderDataHolder() {
        return readerDataHolder;
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
        if (SingletonSharedPreference.getPrefs() != null) {
            CustomBindKeyBean bean = JSONObject.parseObject(SingletonSharedPreference.getPrefs().
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
            return object.getString(KeyAction.KEY_ACTION_TAG);
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
            return object.getString(KeyAction.KEY_ARGS_TAG);
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
        setActiveProvider(READING_PROVIDER);
    }

    public void setActiveProvider(final String providerName) {
        getActiveProvider().onDeactivate(readerDataHolder);
        activeProviderName = providerName;
        getActiveProvider().onActivate(readerDataHolder);
    }

    public BaseHandler getActiveProvider() {
        return providerMap.get(activeProviderName);
    }

    public String getActiveProviderName() {
        return activeProviderName;
    }

    public boolean onKeyDown(ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        if (!isEnable()) {
            return false;
        }
        return processKeyDownEvent(readerDataHolder, keyCode, event);
    }

    public boolean onKeyUp(ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        if (!isEnable()) {
            return false;
        }
        return getActiveProvider().onKeyUp(readerDataHolder, keyCode, event);
    }

    public boolean onTouchEvent(ReaderDataHolder readerDataHolder, MotionEvent e) {
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

        return getActiveProvider().onTouchEvent(readerDataHolder, e);
    }

    public boolean onActionUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onActionUp(readerDataHolder, getTouchStartPosition().x, getTouchStartPosition().y, e.getX(), e.getY());
    }

    public boolean onDown(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }

        return getActiveProvider().onDown(readerDataHolder, e);
    }

    public boolean onDoubleTap(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }

        return getActiveProvider().onDoubleTap(readerDataHolder, e);
    }

    public void onShowPress(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnable()) {
            return;
        }
        getActiveProvider().onShowPress(readerDataHolder, e);
    }

    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onSingleTapUp(readerDataHolder, e);
    }

    public boolean onSingleTapConfirmed(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onSingleTapConfirmed(readerDataHolder, e);
    }

    public boolean onScrollAfterLongPress(ReaderDataHolder readerDataHolder, float x1, float y1, float x2, float y2) {
        if (enableScrollAfterLongPress) {
            if (!isEnable()) {
                return false;
            }
            if (!isEnableTouch()) {
                return false;
            }
            return getActiveProvider().onScrollAfterLongPress(readerDataHolder, x1, y1, x2, y2);
        }
        return false;
    }

    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onScroll(readerDataHolder, e1, e2, distanceX, distanceY);
    }

    public void onLongPress(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnable()) {
            return;
        }
        if (!isEnableTouch()) {
            return;
        }
        if (getActiveProviderName().equals(READING_PROVIDER)){
            setActiveProvider(HandlerManager.WORD_SELECTION_PROVIDER);
        }
        getActiveProvider().onLongPress(readerDataHolder, getTouchStartPosition().x, getTouchStartPosition().y, e.getX(), e.getY());
    }

    public boolean onFling(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onFling(readerDataHolder, e1, e2, velocityX, velocityY);
    }

    public boolean onScaleEnd(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onScaleEnd(readerDataHolder, detector);
    }

    public boolean onScaleBegin(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onScaleBegin(readerDataHolder, detector);
    }

    public boolean onScale(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector)  {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onScale(readerDataHolder, detector);
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

    public boolean processKeyDownEvent(final ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        final String key = KeyEvent.keyCodeToString(keyCode);
        final String action = getKeyAction(TAG, key);
        final String args = getKeyArgs(TAG, key);
        if (StringUtils.isNullOrEmpty(action)) {
            Log.w(TAG, "No action found for key: " + key);
        }
        return processKeyDown(readerDataHolder, action, args);
    }

    public boolean processKeyDown(ReaderDataHolder readerDataHolder, final String action, final String args) {
        getActiveProvider().beforeProcessKeyDown(readerDataHolder);
        if (StringUtils.isNullOrEmpty(action)) {
            return false;
        }

        if (action.equals(KeyAction.NEXT_SCREEN)) {
            nextScreen(readerDataHolder);
        } else if (action.equals(KeyAction.NEXT_PAGE)) {
            nextPage(readerDataHolder);
        } else if (action.equals(KeyAction.PREV_SCREEN)) {
            prevScreen(readerDataHolder);
        } else if (action.equals(KeyAction.PREV_PAGE)) {
            prevPage(readerDataHolder);
        } else if (action.equals(KeyAction.MOVE_LEFT)) {
            panLeft(readerDataHolder);
        } else if (action.equals(KeyAction.MOVE_RIGHT)) {
            panRight(readerDataHolder);
        } else if (action.equals(KeyAction.MOVE_UP)) {
            panUp(readerDataHolder);
        } else if (action.equals(KeyAction.MOVE_DOWN)) {
            panDown(readerDataHolder);
        } else if (action.equals(KeyAction.TOGGLE_BOOKMARK)) {
            toggleBookmark(readerDataHolder);
        } else if (action.equals(KeyAction.SHOW_MENU)) {
            new ShowReaderMenuAction().execute(readerDataHolder);
        } else if (action.equals(KeyAction.CHANGE_TO_ERASE_MODE)) {
        } else if (action.equals(KeyAction.CHANGE_TO_SCRIBBLE_MODE)) {
        } else {
            return false;
        }
        return true;
    }

    private void nextScreen(final ReaderDataHolder readerDataHolder) {
        getActiveProvider().beforeChangePosition(readerDataHolder);
        getActiveProvider().nextScreen(readerDataHolder);
        getActiveProvider().afterChangePosition(readerDataHolder);
    }

    private void nextPage(final ReaderDataHolder readerDataHolder) {
        getActiveProvider().beforeChangePosition(readerDataHolder);
        getActiveProvider().nextPage(readerDataHolder);
        getActiveProvider().afterChangePosition(readerDataHolder);
    }

    private void prevScreen(final ReaderDataHolder readerDataHolder) {
        getActiveProvider().beforeChangePosition(readerDataHolder);
        getActiveProvider().prevScreen(readerDataHolder);
        getActiveProvider().afterChangePosition(readerDataHolder);
    }

    private void prevPage(final ReaderDataHolder readerDataHolder) {
        getActiveProvider().beforeChangePosition(readerDataHolder);
        getActiveProvider().prevPage(readerDataHolder);
        getActiveProvider().afterChangePosition(readerDataHolder);
    }

    private void panLeft(final ReaderDataHolder readerDataHolder) {
        getActiveProvider().beforeChangePosition(readerDataHolder);
        getActiveProvider().panLeft(readerDataHolder);
        getActiveProvider().afterChangePosition(readerDataHolder);
    }

    private void panRight(final ReaderDataHolder readerDataHolder) {
        getActiveProvider().beforeChangePosition(readerDataHolder);
        getActiveProvider().panRight(readerDataHolder);
        getActiveProvider().afterChangePosition(readerDataHolder);
    }

    private void panUp(final ReaderDataHolder readerDataHolder) {
        getActiveProvider().beforeChangePosition(readerDataHolder);
        getActiveProvider().panUp(readerDataHolder);
        getActiveProvider().afterChangePosition(readerDataHolder);
    }

    private void panDown(final ReaderDataHolder readerDataHolder) {
        getActiveProvider().beforeChangePosition(readerDataHolder);
        getActiveProvider().panDown(readerDataHolder);
        getActiveProvider().afterChangePosition(readerDataHolder);
    }

    public void toggleBookmark(ReaderDataHolder readerDataHolder) {
        if (readerDataHolder.hasBookmark()) {
            removeBookmark(readerDataHolder);
        } else {
            addBookmark(readerDataHolder);
        }
    }

    private void removeBookmark(ReaderDataHolder readerDataHolder) {
        new ToggleBookmarkAction(getFirstPageInfo(readerDataHolder), ToggleBookmarkAction.ToggleSwitch.Off).execute(readerDataHolder);
    }

    private void addBookmark(ReaderDataHolder readerDataHolder) {
        new ToggleBookmarkAction(getFirstPageInfo(readerDataHolder), ToggleBookmarkAction.ToggleSwitch.On).execute(readerDataHolder);
    }

    private PageInfo getFirstPageInfo(ReaderDataHolder readerDataHolder) {
        return readerDataHolder.getReaderViewInfo().getFirstVisiblePage();
    }

}
