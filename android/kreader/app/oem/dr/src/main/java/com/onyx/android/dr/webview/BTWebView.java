/*
 * Copyright (C) 2012 Brandon Tate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onyx.android.dr.webview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.GoodSentenceNotebookEvent;
import com.onyx.android.dr.event.NewWordQueryEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Webview subclass that hijacks web content selection.
 *
 * @author Brandon Tate
 */
public class BTWebView extends WebView implements TextSelectionJavascriptInterfaceListener,
        OnTouchListener, OnLongClickListener, QuickAction.OnDismissListener, DragListener{
    /** The logging tag. */
    private static final String TAG = "BTWebView";
    /** Context. */
    protected Context context;
    /** The context menu. */
    protected QuickAction contextMenu;
    /** The drag layer for selection. */
    protected DragLayer selectionDragLayer;
    /** The drag controller for selection. */
    protected DragController dragController;
    /** The selection bounds. */
    protected Rect selectionBounds = null;
    /** The previously selected region. */
    protected Region lastSelectedRegion = null;
    /** The selected range. */
    protected String selectedRange = "";
    /** The selected text. */
    protected String selectedText = "";
    /** Javascript interface for catching text selection. */
    protected TextSelectionJavascriptInterface textSelectionJSInterface = null;
    /** Selection mode flag. */
    protected boolean inSelectionMode = false;
    /** Flag for dragging. */
    protected boolean dragging = false;
    /** Flag to stop from showing context menu twice. */
    protected boolean contextMenuVisible = false;
    /** The current content width. */
    protected int contentWidth = 0;
    /** The current scale of the web view. */
    protected float currentScale = 1.0f;
    /** The start selection handle. */
    protected ImageView startSelectionHandle;
    /** the end selection handle. */
    protected ImageView endSelectionHandle;
    /** Identifier for the selection start handle. */
    protected final int SELECTION_START_HANDLE = 0;
    /** Identifier for the selection end handle. */
    protected final int SELECTION_END_HANDLE = 1;
    /** Last touched selection handle. */
    protected int lastTouchedSelectionHandle = -1;
    private boolean scrolling = false;
    private float scrollDiffY = 0;
    private float lastTouchY = 0;
    private float scrollDiffX = 0;
    private float lastTouchX = 0;
    private int menuBoundRight = 30;
    private int menuBoundLeft = 0;
    private int menuBoundTop = 50;
    private int menuBoundBottom = 25;
    private int selectionBoundRight = 0;
    private int selectionBoundLeft = 35;
    private int selectionBoundTop = 32;
    private int selectionBoundBottom = 30;

    public BTWebView(Context context) {
        super(context);
        this.context = context;
        setup(context);
    }

    public BTWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setup(context);
    }

    public BTWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setup(context);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float xPoint = getDensityIndependentValue(event.getX(), context) / getDensityIndependentValue(getScale(), context);
        float yPoint = getDensityIndependentValue(event.getY(), context) / getDensityIndependentValue(getScale(), context);
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            String startTouchUrl = String.format(Locale.US, "javascript:android.selection.startTouch(%f, %f);",
                    xPoint, yPoint);
            lastTouchX = xPoint;
            lastTouchY = yPoint;
            loadUrl(startTouchUrl);
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            // Check for scrolling flag
            if(!scrolling){
                scrolling = false;
                endSelectionMode();
                return false;
            }
            scrollDiffX = 0;
            scrollDiffY = 0;
            scrolling = false;
            // Fixes 4.4 double selection
            return true;
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            scrollDiffX += (xPoint - lastTouchX);
            scrollDiffY += (yPoint - lastTouchY);
            lastTouchX = xPoint;
            lastTouchY = yPoint;
            // Only account for legitimate movement.
            scrolling = (Math.abs(scrollDiffX) > 10 || Math.abs(scrollDiffY) > 10);
        }
        // If this is in selection mode, then nothing else should handle this touch
        return false;
    }

    @Override
    public boolean onLongClick(View v){
        // Tell the javascript to handle this if not in selection mode
        if(!isInSelectionMode()){
            loadUrl("javascript:android.selection.longTouch();");
            scrolling = true;
        }
        // Don't let the webview handle it
        return true;
    }

    /**
     * Setups up the web view.
     * @param context
     */
    protected void setup(Context context){
        // On Touch Listener
        setOnLongClickListener(this);
        setOnTouchListener(this);
        // Webview setup
        getSettings().setJavaScriptEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setPluginState(WebSettings.PluginState.ON);
        // Webview client.
        setWebViewClient(new WebViewClient(){
            // This is how it is supposed to work, so I'll leave it in, but this doesn't get called on pinch
            // So for now I have to use deprecated getScale method.
            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
                currentScale = newScale;
            }
        });
        // Javascript interfaces
        textSelectionJSInterface = new TextSelectionJavascriptInterface(context, this);
        addJavascriptInterface(textSelectionJSInterface, textSelectionJSInterface.getInterfaceName());
        // Create the selection handles
        createSelectionLayer(context);
        // Set to the empty region
        Region region = new Region();
        region.setEmpty();
        lastSelectedRegion = region;
        // Load up the android asset file
        String filePath = "file:///android_asset/content.html";
        // Load the url
        this.loadUrl(filePath);
    }

    /**
     * Creates the selection layer.
     *
     * @param context
     */
    protected void createSelectionLayer(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectionDragLayer = (DragLayer) inflater.inflate(R.layout.selection_drag_layer, null);
        // Make sure it's filling parent
        dragController = new DragController(context);
        dragController.setDragListener(this);
        dragController.addDropTarget(selectionDragLayer);
        selectionDragLayer.setDragController(dragController);
        startSelectionHandle = (ImageView) selectionDragLayer.findViewById(R.id.startHandle);
        startSelectionHandle.setTag(new Integer(SELECTION_START_HANDLE));
        endSelectionHandle = (ImageView) selectionDragLayer.findViewById(R.id.endHandle);
        endSelectionHandle.setTag(new Integer(SELECTION_END_HANDLE));
        OnTouchListener handleTouchListener = new OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean handledHere = false;
                final int action = event.getAction();
                // Down event starts drag for handle.
                if (action == MotionEvent.ACTION_DOWN) {
                    handledHere = startDrag (v);
                    lastTouchedSelectionHandle = (Integer) v.getTag();
                }
                return handledHere;
            }


        };
        startSelectionHandle.setOnTouchListener(handleTouchListener);
        endSelectionHandle.setOnTouchListener(handleTouchListener);
    }

    /**
     * Starts selection mode on the UI thread
     */
    private Handler startSelectionModeHandler = new Handler(){

        public void handleMessage(Message m){
            if(selectionBounds == null)
                return;
            addView(selectionDragLayer);
            drawSelectionHandles();
            int contentHeight = (int) Math.ceil(getDensityDependentValue(getContentHeight(), context));
            // Update Layout Params
            ViewGroup.LayoutParams layerParams = selectionDragLayer.getLayoutParams();
            layerParams.height = contentHeight;
            layerParams.width = computeHorizontalScrollRange() + contentWidth;
            selectionDragLayer.setLayoutParams(layerParams);
        }

    };

    /**
     * Starts selection mode.
     *
     */
    public void startSelectionMode(){
        startSelectionModeHandler.sendEmptyMessage(0);
    }

    // Ends selection mode on the UI thread
    private Handler endSelectionModeHandler = new Handler(){
        public void handleMessage(Message m){
            if(getParent() != null && contextMenu != null && contextMenuVisible){
                // This will throw an error if the webview is being redrawn.
                // No error handling needed, just need to stop the crash.
                try{
                    contextMenu.dismiss();
                }
                catch(Exception e){
                }
            }
            selectionBounds = null;
            lastTouchedSelectionHandle = -1;
            loadUrl("javascript: android.selection.clearSelection();");
            removeView(selectionDragLayer);
        }
    };

    /**
     * Ends selection mode.
     */
    public void endSelectionMode(){
        endSelectionModeHandler.sendEmptyMessage(0);
    }

    /**
     * Calls the handler for drawing the selection handles.
     */
    private void drawSelectionHandles(){
        drawSelectionHandlesHandler.sendEmptyMessage(0);
    }

    /**
     * Handler for drawing the selection handles on the UI thread.
     */
    private Handler drawSelectionHandlesHandler = new Handler(){
        public void handleMessage(Message m){
            MyAbsoluteLayout.LayoutParams startParams = (MyAbsoluteLayout.LayoutParams) startSelectionHandle.getLayoutParams();

            int intrinsicWidth = startSelectionHandle.getDrawable().getIntrinsicWidth();
            startParams.x = (int) (selectionBounds.left - (intrinsicWidth * 3));
            startParams.y = (int) (selectionBounds.bottom);// - startSelectionHandle.getDrawable().getIntrinsicHeight());
            // Stay on screen.
            startParams.x = (startParams.x < 0) ? 0 : startParams.x;
            startParams.y = (startParams.y < 0) ? 0 : startParams.y;
            startSelectionHandle.setLayoutParams(startParams);
            MyAbsoluteLayout.LayoutParams endParams = (MyAbsoluteLayout.LayoutParams) endSelectionHandle.getLayoutParams();
            endParams.x = (int) selectionBounds.right - (intrinsicWidth / 2);
            endParams.y = (int) selectionBounds.bottom;
            // Stay on screen
            endParams.x = (endParams.x < 0) ? 0 : endParams.x;
            endParams.y = (endParams.y < 0) ? 0 : endParams.y;
            endSelectionHandle.setLayoutParams(endParams);
        }
    };

    /**
     * Checks to see if this view is in selection mode.
     * @return
     */
    public boolean isInSelectionMode(){
        return selectionDragLayer.getParent() != null;
    }

    /**
     * Checks to see if the view is currently dragging.
     * @return
     */
    public boolean isDragging(){
        return dragging;
    }

    /**
     * Start dragging a view.
     *
     */
    protected boolean startDrag (View v) {
        dragging = true;
        Object dragInfo = v;
        dragController.startDrag (v, selectionDragLayer, dragInfo, DragController.DRAG_ACTION_MOVE);
        return true;
    }

    @Override
    public void onDragStart(DragSource source, Object info, int dragAction) {
    }

	@Override
	public void onDrag() {
        MyAbsoluteLayout.LayoutParams startHandleParams = (MyAbsoluteLayout.LayoutParams) startSelectionHandle.getLayoutParams();
        MyAbsoluteLayout.LayoutParams endHandleParams = (MyAbsoluteLayout.LayoutParams) endSelectionHandle.getLayoutParams();
        float scale = getDensityIndependentValue(getScale(), context);
        float startX = startHandleParams.x - getScrollX();
        float startY = startHandleParams.y - getScrollY();
        float endX = endHandleParams.x - getScrollX();
        float endY = endHandleParams.y - getScrollY();
        startX = getDensityIndependentValue(startX, context) / scale;
        startY = getDensityIndependentValue(startY, context) / scale;
        endX = getDensityIndependentValue(endX, context) / scale;
        endY = getDensityIndependentValue(endY, context) / scale;
        if(lastTouchedSelectionHandle == SELECTION_START_HANDLE && startX > 0 && startY > 0){
            String saveStartString = String.format(Locale.US, "javascript: android.selection.setStartPos(%f, %f);", startX, startY);
            loadUrl(saveStartString);
        }
        if(lastTouchedSelectionHandle == SELECTION_END_HANDLE && endX > 0 && endY > 0){
            String saveEndString = String.format(Locale.US, "javascript: android.selection.setEndPos(%f, %f);", endX, endY);
            loadUrl(saveEndString);
        }
	}
	
    @Override
    public void onDragEnd() {
        MyAbsoluteLayout.LayoutParams startHandleParams = (MyAbsoluteLayout.LayoutParams) startSelectionHandle.getLayoutParams();
        MyAbsoluteLayout.LayoutParams endHandleParams = (MyAbsoluteLayout.LayoutParams) endSelectionHandle.getLayoutParams();
        float scale = getDensityIndependentValue(getScale(), context);
        float startX = startHandleParams.x - getScrollX();
        float startY = startHandleParams.y - getScrollY();
        float endX = endHandleParams.x - getScrollX();
        float endY = endHandleParams.y - getScrollY();
        startX = getDensityIndependentValue(startX, context) / scale;
        startY = getDensityIndependentValue(startY, context) / scale;
        endX = getDensityIndependentValue(endX, context) / scale;
        endY = getDensityIndependentValue(endY, context) / scale;
        if(lastTouchedSelectionHandle == SELECTION_START_HANDLE && startX > 0 && startY > 0){
            String saveStartString = String.format(Locale.US, "javascript: android.selection.setStartPos(%f, %f);", startX, startY);
            loadUrl(saveStartString);
        }
        if(lastTouchedSelectionHandle == SELECTION_END_HANDLE && endX > 0 && endY > 0){
            String saveEndString = String.format(Locale.US, "javascript: android.selection.setEndPos(%f, %f);", endX, endY);
            loadUrl(saveEndString);
        }
        dragging = false;
    }

    /**
     * Shows the context menu using the given region as an anchor point.
     * @param displayRect
     */
    protected void showContextMenu(Rect displayRect){
        // Don't show this twice
        if(contextMenuVisible){
            return;
        }
        // Don't use empty rect
        //if(displayRect.isEmpty()){
        if(displayRect.right <= displayRect.left){
            return;
        }
        //Copy action item
        ActionItem buttonOne = new ActionItem();
        buttonOne.setTitle(context.getString(R.string.new_word_query));
        buttonOne.setActionId(Constants.ACTION_ONE);
        //Highlight action item
        ActionItem buttonTwo = new ActionItem();
        buttonTwo.setTitle(context.getString(R.string.good_sentence_excerpt));
        buttonTwo.setActionId(Constants.ACTION_TWO);
        // The action menu
        contextMenu  = new QuickAction(getContext());
        contextMenu.setOnDismissListener(this);
        // Add buttons
        contextMenu.addActionItem(buttonOne);
        contextMenu.addActionItem(buttonTwo);
        //setup the action item click listener
        contextMenu.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                if (actionId == Constants.ACTION_ONE) {
                    EventBus.getDefault().post(new NewWordQueryEvent(selectedText));
                } else if (actionId == Constants.ACTION_TWO) {
                    EventBus.getDefault().post(new GoodSentenceNotebookEvent(selectedText));
                }
                contextMenuVisible = false;
            }
        });
        contextMenuVisible = true;
        contextMenu.show(this, displayRect);
    }

    /**
     * Clears the selection when the context menu is dismissed.
     */
    public void onDismiss(){
        contextMenuVisible = false;
    }

    /**
     * Shows/updates the context menu based on the range
     *
     * @param error
     */
    public void tsjiJSError(String error){
        Log.e(TAG, "JSError: " + error);
    }


    /**
     * The user has started dragging the selection handles.
     */
    public void tsjiStartSelectionMode(){
        startSelectionMode();
    }

    /**
     * The user has stopped dragging the selection handles.
     */
    public void tsjiEndSelectionMode(){
        endSelectionMode();
    }

    /**
     * The selection has changed
     * @param range
     * @param text
     * @param handleBounds
     * @param menuBounds
     */
    public void tsjiSelectionChanged(String range, String text, String handleBounds, String menuBounds){
        handleSelection(range, text, handleBounds);
        Rect displayRect = getContextMenuBounds(menuBounds);
        if(displayRect != null)
            // This will send the menu rect
            showContextMenu(displayRect);
    }


    /**
     * Receives the content width for the page.
     */
    public void tsjiSetContentWidth(float contentWidth){
        this.contentWidth = (int) getDensityDependentValue(contentWidth, context);
    }

    /**
     * Puts up the selection view.
     * @param range
     * @param text
     * @param handleBounds
     * @return
     */
    protected void handleSelection(String range, String text, String handleBounds){
        try{
            JSONObject selectionBoundsObject = new JSONObject(handleBounds);
            float scale = getDensityIndependentValue(getScale(), context);
            Rect handleRect = new Rect();
            handleRect.left = (int) (getDensityDependentValue(selectionBoundsObject.getInt("left") + selectionBoundLeft , getContext()) * scale);
            handleRect.top = (int) (getDensityDependentValue(selectionBoundsObject.getInt("top") + selectionBoundTop, getContext()) * scale);
            handleRect.right = (int) (getDensityDependentValue(selectionBoundsObject.getInt("right") + selectionBoundRight, getContext()) * scale);
            handleRect.bottom = (int) (getDensityDependentValue(selectionBoundsObject.getInt("bottom") + selectionBoundBottom, getContext()) * scale);
            selectionBounds = handleRect;
            selectedRange = range;
            selectedText = text;
            if(!isInSelectionMode()){
                startSelectionMode();
            }
            drawSelectionHandles();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the context menu display rect
     * @param menuBounds
     * @return The display Rect
     */
    protected Rect getContextMenuBounds(String menuBounds){
        try{
            JSONObject menuBoundsObject = new JSONObject(menuBounds);
            float scale = getDensityIndependentValue(getScale(), context);
            Rect displayRect = new Rect();
            displayRect.left = (int) (getDensityDependentValue(menuBoundsObject.getInt("left"), getContext()) * scale);
            displayRect.top = (int) (getDensityDependentValue(menuBoundsObject.getInt("top") - menuBoundTop, getContext()) * scale);
            displayRect.right = (int) (getDensityDependentValue(menuBoundsObject.getInt("right") + menuBoundRight, getContext()) * scale);
            displayRect.bottom = (int) (getDensityDependentValue(menuBoundsObject.getInt("bottom") - menuBoundBottom, getContext()) * scale);
            return displayRect;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the density dependent value of the given float
     * @param val
     * @param ctx
     * @return
     */
    public float getDensityDependentValue(float val, Context ctx){
        // Get display from context
        Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        // Calculate min bound based on metrics
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return val * (metrics.densityDpi / 160f);
    }

    /**
     * Returns the density independent value of the given float
     * @param val
     * @param ctx
     * @return
     */
    public float getDensityIndependentValue(float val, Context ctx){
        // Get display from context
        Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        // Calculate min bound based on metrics
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return val / (metrics.densityDpi / 160f);
    }
}
