package com.onyx.kreader.ui.handler;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.api.device.FrontLightController;
import com.onyx.android.sdk.data.ControlType;
import com.onyx.android.sdk.data.CustomBindKeyBean;
import com.onyx.android.sdk.data.KeyAction;
import com.onyx.android.sdk.data.KeyBinding;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.TouchAction;
import com.onyx.android.sdk.data.TouchBinding;
import com.onyx.android.sdk.reader.api.ReaderImage;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.ui.ReaderTabHostBroadcastReceiver;
import com.onyx.kreader.ui.actions.DecreaseFontSizeAction;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.ui.actions.IncreaseFontSizeAction;
import com.onyx.kreader.ui.actions.ShowReaderMenuAction;
import com.onyx.kreader.ui.actions.StartTtsAction;
import com.onyx.kreader.ui.actions.ToggleAnimationUpdateAction;
import com.onyx.kreader.ui.actions.ToggleBookmarkAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.device.DeviceConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private static final int TOUCH_HORIZONTAL_PART = 3;
    private static final int TOUCH_VERTICAL_PART = 2;

    private String activeProviderName;
    private Map<String, BaseHandler> providerMap = new HashMap<String, BaseHandler>();
    private PointF touchStartPosition;
    private AtomicBoolean enable = new AtomicBoolean();
    private AtomicBoolean enableTouch = new AtomicBoolean();
    static private boolean enableScrollAfterLongPress = false;
    private DeviceConfig deviceConfig;
    private ReaderDataHolder readerDataHolder;
    private static final List<String> AUDIO_TYPE_LIST = new ArrayList<>();
    static {
        AUDIO_TYPE_LIST.add(".mp3");
    }

    public static boolean isAudio(String name){
        for(String type : AUDIO_TYPE_LIST){
            if(name.endsWith(type)){
                return true;
            }
        }
        return false;
    }

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

    public KeyBinding getControlBinding() {
        return deviceConfig.getKeyBinding();
    }

    public TouchBinding getControlTouchBinding() {
        return deviceConfig.getTouchBinding();
    }

    public CustomBindKeyBean getControlBinding(final ControlType controlType, final String controlCode) {
        Map<String, CustomBindKeyBean> map = controlType == ControlType.KEY ? getControlBinding().getHandlerManager() : getControlTouchBinding().getTouchBindingMap();
        if (map == null) {
            return null;
        }
        return  map.get(controlCode);
    }

    private final CustomBindKeyBean getControlBean(final String controlCode) {
        if (SingletonSharedPreference.getPrefs() != null) {
            CustomBindKeyBean bean = JSONObject.parseObject(SingletonSharedPreference.getPrefs().
                    getString(controlCode, null), CustomBindKeyBean.class);
            return bean;
        }
        return null;
    }

    public final String getControlAction(final ControlType controlType, final String controlCode) {
        final CustomBindKeyBean bean = getControlBean(controlCode);
        if (bean != null) {
            return bean.getAction();
        }

        CustomBindKeyBean object = getControlBinding(controlType, controlCode);
        if (object != null) {
            return object.getAction();
        }
        return null;
    }

    public final String getControlArgs(final ControlType controlType, final String controlCode) {
        final CustomBindKeyBean bean = getControlBean(controlCode);
        if (bean != null) {
            return bean.getArgs();
        }

        CustomBindKeyBean object = getControlBinding(controlType, controlCode);
        if (object != null) {
            return object.getArgs();
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
        setActiveProvider(providerName, null);
    }

    public void setActiveProvider(final String providerName, final BaseHandler.HandlerInitialState initialState) {
        getActiveProvider().onDeactivate(readerDataHolder);
        activeProviderName = providerName;
        getActiveProvider().onActivate(readerDataHolder, initialState);
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

    public boolean onActionCancel(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnable()) {
            return false;
        }
        if (!isEnableTouch()) {
            return false;
        }
        return getActiveProvider().onActionCancel(readerDataHolder, getTouchStartPosition().x, getTouchStartPosition().y, e.getX(), e.getY());
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
        String audioName = tryPageImage(readerDataHolder,e.getX(),e.getY());
        if(StringUtils.isNotBlank(audioName)){
            StartTtsAction action = new StartTtsAction(null);
            action.setAudioPath(audioName);
            action.execute(readerDataHolder, null);
            return true;
        }
        if (getActiveProvider().onSingleTapUp(readerDataHolder, e)) {
            return true;
        }
        return processSingleTapUpEvent(readerDataHolder, e);
    }

    private String tryPageImage(ReaderDataHolder readerDataHolder, final float x, final float y) {
        for (PageInfo pageInfo : readerDataHolder.getReaderViewInfo().getVisiblePages()) {
            if (!readerDataHolder.getReaderUserDataInfo().hasPageImages(pageInfo)) {
                continue;
            }
            List<ReaderImage> images = readerDataHolder.getReaderUserDataInfo().getPageImages(pageInfo);
            for (ReaderImage image : images) {
                if (image.getRectangle().contains(x, y)) {
                    if(isAudio(image.getName())) {
                        return image.getName();
                    }
                }
            }
        }
        return null;
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
        final String action = getControlAction(ControlType.KEY, key);
        final String args = getControlArgs(ControlType.KEY, key);
        if (StringUtils.isNullOrEmpty(action)) {
            Log.w(TAG, "No action found for key: " + key);
        }
        return processKeyDown(readerDataHolder, action, args);
    }

    private boolean processSingleTapUpEvent(final ReaderDataHolder readerDataHolder, final MotionEvent motionEvent) {
        final String touchArea = getTouchAreaCode(readerDataHolder, motionEvent);
        String action;
        String args;
        action = getControlAction(ControlType.TOUCH, touchArea);
        args = getControlArgs(ControlType.TOUCH, touchArea);
        return processSingleTapUp(readerDataHolder, action, args);
    }

    private String getTouchAreaCode(final ReaderDataHolder readerDataHolder, final MotionEvent motionEvent) {
        int displayWidth = readerDataHolder.getDisplayWidth();
        int displayHeight = readerDataHolder.getDisplayHeight();
        if (motionEvent.getX() > displayWidth * TOUCH_VERTICAL_PART / TOUCH_HORIZONTAL_PART &&
                motionEvent.getY() > displayHeight / TOUCH_VERTICAL_PART) {
            return TouchBinding.TOUCH_RIGHT_BOTTOM;
        } else if (motionEvent.getX() < displayWidth / TOUCH_HORIZONTAL_PART &&
                motionEvent.getY() > displayHeight / TOUCH_VERTICAL_PART) {
            return TouchBinding.TOUCH_LEFT_BOTTOM;
        }else if (motionEvent.getX() < displayWidth / TOUCH_HORIZONTAL_PART &&
                motionEvent.getY() < displayHeight / TOUCH_VERTICAL_PART) {
            return TouchBinding.TOUCH_LEFT_TOP;
        }else if (motionEvent.getX() > displayWidth * TOUCH_VERTICAL_PART / TOUCH_HORIZONTAL_PART &&
                motionEvent.getY() < displayHeight / TOUCH_VERTICAL_PART) {
            return TouchBinding.TOUCH_RIGHT_TOP;
        }else {
            return TouchBinding.TOUCH_CENTER;
        }
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
        } else if (action.equals(KeyAction.TOGGLE_BOOKMARK)) {
            toggleBookmark(readerDataHolder);
        } else if (action.equals(KeyAction.SHOW_MENU)) {
            onShowMenu(readerDataHolder);
        } else if (action.equals(KeyAction.CHANGE_TO_ERASE_MODE)) {
        } else if (action.equals(KeyAction.CHANGE_TO_SCRIBBLE_MODE)) {
        } else {
            return false;
        }
        return true;
    }

    private boolean isEnableProcessSingleTapUp(final ReaderDataHolder readerDataHolder) {
        if (readerDataHolder.inNoteWritingProvider()) {
            return false;
        }
        return true;
    }

    private boolean processSingleTapUp(final ReaderDataHolder readerDataHolder, final String action, final String args) {
        if (!isEnableProcessSingleTapUp(readerDataHolder)) {
            return false;
        }
        if (StringUtils.isNullOrEmpty(action)) {
            return false;
        }
        if (action.equals(TouchAction.NEXT_PAGE)) {
            nextScreen(readerDataHolder);
        }else if (action.equals(TouchAction.PREV_PAGE)) {
            prevScreen(readerDataHolder);
        }else if (action.equals(TouchAction.SHOW_MENU)) {
            onShowMenu(readerDataHolder);
        }else if (action.equals(TouchAction.INCREASE_BRIGHTNESS)) {
            increaseBrightness(readerDataHolder);
        }else if (action.equals(TouchAction.DECREASE_BRIGHTNESS)) {
            decreaseBrightness(readerDataHolder);
        }else if (action.equals(TouchAction.TOGGLE_FULLSCREEN)) {
            toggleFullscreen(readerDataHolder);
        }else if (action.equals(TouchAction.OPEN_TTS)) {
            ShowReaderMenuAction.showTtsDialog(readerDataHolder);
        }else if (action.equals(TouchAction.AUTO_PAGE)) {
            readerDataHolder.enterSlideshow();
        }else if (action.equals(TouchAction.NEXT_TEN_PAGE)) {
            nextTenPage(readerDataHolder);
        }else if (action.equals(TouchAction.PREV_TEN_PAGE)) {
            prevTenPage(readerDataHolder);
        }else if (action.equals(TouchAction.TOGGLE_A2)) {
            toggleAnimationUpdate(readerDataHolder);
        }else {
            return false;
        }
        return true;
    }

    private void toggleAnimationUpdate(final ReaderDataHolder readerDataHolder) {
        new ToggleAnimationUpdateAction(true).execute(readerDataHolder, null);
    }

    private void toggleFullscreen(final ReaderDataHolder readerDataHolder) {
        if (DeviceConfig.sharedInstance(readerDataHolder.getContext()).isSupportColor()) {
            return;
        }
        if (SingletonSharedPreference.isSystemStatusBarEnabled(readerDataHolder.getContext())) {
            ReaderTabHostBroadcastReceiver.sendEnterFullScreenIntent(readerDataHolder.getContext());
        }else {
            ReaderTabHostBroadcastReceiver.sendQuitFullScreenIntent(readerDataHolder.getContext());
        }
    }

    private void increaseBrightness(final ReaderDataHolder readerDataHolder) {
        int value = FrontLightController.getBrightness(readerDataHolder.getContext());
        int max = FrontLightController.getMaxFrontLightValue(readerDataHolder.getContext());
        FrontLightController.setBrightness(readerDataHolder.getContext(), Math.min(value + 10, max));
    }

    private void decreaseBrightness(final ReaderDataHolder readerDataHolder) {
        int value = FrontLightController.getBrightness(readerDataHolder.getContext());
        int min = FrontLightController.getMinFrontLightValue(readerDataHolder.getContext());
        FrontLightController.setBrightness(readerDataHolder.getContext(), Math.max(value - 10, min));
    }

    private void nextTenPage(final ReaderDataHolder readerDataHolder) {
        int currentPage = readerDataHolder.getCurrentPage();
        int gotoPage = Math.min(currentPage + 10, readerDataHolder.getPageCount() - 1);
        new GotoPageAction(gotoPage).execute(readerDataHolder);
    }

    private void prevTenPage(final ReaderDataHolder readerDataHolder) {
        int currentPage = readerDataHolder.getCurrentPage();
        int gotoPage = Math.max(currentPage - 10, 0);
        new GotoPageAction(gotoPage).execute(readerDataHolder);
    }

    private void onShowMenu(final ReaderDataHolder readerDataHolder) {
        new ShowReaderMenuAction().execute(readerDataHolder, null);
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
