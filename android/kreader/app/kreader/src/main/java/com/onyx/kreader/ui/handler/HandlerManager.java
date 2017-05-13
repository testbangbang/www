package com.onyx.kreader.ui.handler;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.CustomBindKeyBean;
import com.onyx.android.sdk.data.KeyAction;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.ui.actions.DecreaseFontSizeAction;
import com.onyx.kreader.ui.actions.IncreaseFontSizeAction;
import com.onyx.kreader.ui.actions.ShowReaderMenuAction;
import com.onyx.kreader.ui.actions.ToggleBookmarkAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.device.DeviceConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public static final String SLIDESHOW_PROVIDER = "slideshow";

    private String activeProviderName;
    private Map<String, BaseHandler> providerMap = new HashMap<String, BaseHandler>();
    private PointF touchStartPosition;
    private AtomicBoolean enable = new AtomicBoolean();
    private AtomicBoolean enableTouch = new AtomicBoolean();
    static private boolean enableScrollAfterLongPress = false;
    private DeviceConfig deviceConfig;
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
        providerMap.put(SLIDESHOW_PROVIDER, new SlideshowHandler(this));
        activeProviderName = READING_PROVIDER;
        enable.set(true);
        enableTouch.set(true);
        deviceConfig = DeviceConfig.sharedInstance(context);
    }

    public com.onyx.kreader.ui.data.ReaderDataHolder getReaderDataHolder() {
        return readerDataHolder;
    }

    public Map<String, Map<String, JSONObject>> getKeyBinding() {
        return deviceConfig.getKeyBinding();
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
        enable.set(e);
    }

    public boolean isEnable() {
        return enable.get();
    }

    public void setEnableTouch(boolean e) {
        enableTouch.set(e);
    }

    public boolean isEnableTouch() {
        if (!hasReaderViewInfo()) {
            return false;
        }
        return enableTouch.get();
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
        if (getActiveProvider().onKeyDown(readerDataHolder, keyCode, event)) {
            return true;
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

    private boolean hasReaderViewInfo() {
        return readerDataHolder.getReaderViewInfo() != null;
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
        getActiveProvider().beforeProcessKeyDown(readerDataHolder, action, args);
        if (StringUtils.isNullOrEmpty(action)) {
            return false;
        }

        if (action.equals(KeyAction.CLOSE)) {
            close(readerDataHolder);
        } else if (action.equals(KeyAction.NEXT_SCREEN)) {
            nextScreen(readerDataHolder);
        } else if (action.equals(KeyAction.NEXT_PAGE)) {
            nextPage(readerDataHolder);
        } else if (action.equals(KeyAction.PREV_SCREEN)) {
            prevScreen(readerDataHolder);
        } else if (action.equals(KeyAction.PREV_PAGE)) {
            prevPage(readerDataHolder);
        } else if (action.equals(KeyAction.MOVE_LEFT)) {
            onMoveLeft(readerDataHolder);
        } else if (action.equals(KeyAction.MOVE_RIGHT)) {
            onMoveRight(readerDataHolder);
        } else if (action.equals(KeyAction.MOVE_UP)) {
            onMoveUp(readerDataHolder);
        } else if (action.equals(KeyAction.MOVE_DOWN)) {
            onMoveDown(readerDataHolder);
        } else if (action.equals(KeyAction.INCREASE_FONT_SIZE)) {
            increaseFontSize(readerDataHolder);
        } else if (action.equals(KeyAction.DECREASE_FONT_SIZE)) {
            decreaseFontSize(readerDataHolder);
        } else if (action.equals(KeyAction.MOVE_DOWN)) {
        } else if (action.equals(KeyAction.TOGGLE_BOOKMARK)) {
            toggleBookmark(readerDataHolder);
        } else if (action.equals(KeyAction.SHOW_MENU)) {
            new ShowReaderMenuAction().execute(readerDataHolder, null);
        } else if (action.equals(KeyAction.CHANGE_TO_ERASE_MODE)) {
        } else if (action.equals(KeyAction.CHANGE_TO_SCRIBBLE_MODE)) {
        } else {
            return false;
        }
        return true;
    }

    private void onMoveLeft(ReaderDataHolder readerDataHolder) {
        if (readerDataHolder.supportScalable() && readerDataHolder.canPan()) {
            panLeft(readerDataHolder);
        } else {
            prevScreen(readerDataHolder);
        }
    }

    private void onMoveRight(ReaderDataHolder readerDataHolder) {
        if (readerDataHolder.supportScalable() && readerDataHolder.canPan()) {
            panRight(readerDataHolder);
        } else {
            nextScreen(readerDataHolder);
        }
    }

    private void onMoveUp(ReaderDataHolder readerDataHolder) {
        if (readerDataHolder.supportScalable()) {
            if (readerDataHolder.canPan()) {
                panUp(readerDataHolder);
            } else {
                prevScreen(readerDataHolder);
            }
        } else if (readerDataHolder.supportFontSizeAdjustment()) {
            increaseFontSize(readerDataHolder);
        } else {
            prevScreen(readerDataHolder);
        }
    }

    private void onMoveDown(ReaderDataHolder readerDataHolder) {
        if (readerDataHolder.supportScalable()) {
            if (readerDataHolder.canPan()) {
                panDown(readerDataHolder);
            } else {
                nextScreen(readerDataHolder);
            }
        } else if (readerDataHolder.supportFontSizeAdjustment()) {
            decreaseFontSize(readerDataHolder);
        } else {
            nextScreen(readerDataHolder);
        }
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

    private void increaseFontSize(final ReaderDataHolder readerDataHolder) {
        new IncreaseFontSizeAction().execute(readerDataHolder, null);
    }

    private void decreaseFontSize(final ReaderDataHolder readerDataHolder) {
        new DecreaseFontSizeAction().execute(readerDataHolder, null);
    }

    public void toggleBookmark(ReaderDataHolder readerDataHolder) {
        if (readerDataHolder.hasBookmark()) {
            removeBookmark(readerDataHolder);
        } else {
            addBookmark(readerDataHolder);
        }
    }

    private void removeBookmark(ReaderDataHolder readerDataHolder) {
        new ToggleBookmarkAction(readerDataHolder.getFirstVisiblePageWithBookmark(),
                ToggleBookmarkAction.ToggleSwitch.Off).execute(readerDataHolder, null);
    }

    private void addBookmark(ReaderDataHolder readerDataHolder) {
        new ToggleBookmarkAction(readerDataHolder.getFirstPageInfo(), ToggleBookmarkAction.ToggleSwitch.On).execute(readerDataHolder, null);
    }

    private void close(final ReaderDataHolder readerDataHolder) {
        getActiveProvider().close(readerDataHolder);
    }
}
