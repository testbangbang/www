package com.onyx.kreader.ui.actions;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.RectF;
import android.util.Log;
import android.widget.Toast;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDictionaryInfo;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.data.ReaderMenuState;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.ui.data.ReaderLayerColorMenu;
import com.onyx.android.sdk.ui.data.ReaderLayerMenu;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuItem;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuRepository;
import com.onyx.android.sdk.ui.dialog.DialogBrightness;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.dataprovider.LegacySdkDataUtils;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.kreader.host.request.ChangeLayoutRequest;
import com.onyx.kreader.host.request.ScaleRequest;
import com.onyx.kreader.host.request.ScaleToPageCropRequest;
import com.onyx.kreader.host.request.ScaleToPageRequest;
import com.onyx.kreader.host.request.ScaleToWidthContentRequest;
import com.onyx.kreader.host.request.ScaleToWidthRequest;
import com.onyx.kreader.note.actions.ChangeNoteShapeAction;
import com.onyx.kreader.note.actions.ChangeStrokeWidthAction;
import com.onyx.kreader.note.actions.ClearPageAction;
import com.onyx.kreader.note.actions.FlushNoteAction;
import com.onyx.kreader.note.actions.RedoAction;
import com.onyx.kreader.note.actions.RestoreShapeAction;
import com.onyx.kreader.note.actions.ResumeDrawingAction;
import com.onyx.kreader.note.actions.StopNoteActionChain;
import com.onyx.kreader.note.actions.UndoAction;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.dialog.DialogExport;
import com.onyx.kreader.ui.dialog.DialogNavigationSettings;
import com.onyx.kreader.ui.dialog.DialogScreenRefresh;
import com.onyx.kreader.ui.dialog.DialogSearch;
import com.onyx.kreader.ui.dialog.DialogTableOfContent;
import com.onyx.kreader.ui.dialog.DialogTextStyle;
import com.onyx.kreader.ui.events.QuitEvent;
import com.onyx.kreader.utils.DeviceConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Joy on 2016/6/7.
 */
public class ShowReaderMenuAction extends BaseAction {

    public static final String TAG = ShowReaderMenuAction.class.getSimpleName();
    ReaderActivity readerActivity;

    // use reader menu as static field to avoid heavy init of showing reader menu each time
    private static ReaderMenu readerMenu;
    private boolean disableScribbleBrush = true;
    private static boolean isScribbleMenuVisible = false;
    private static Set<ReaderMenuAction> disableMenus = new HashSet<>();
    private static List<String> fontFaces = new ArrayList<>();


    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerActivity = (ReaderActivity)readerDataHolder.getContext();
        showReaderMenu(readerDataHolder);
        BaseCallback.invoke(callback, null, null);
    }

    public static void resetReaderMenu(final ReaderDataHolder readerDataHolder) {
        if (readerMenu != null) {
            readerMenu.hide();
            readerMenu = null;
        }
    }

    public static boolean isReaderMenuShown() {
        return readerMenu != null && readerMenu.isShown();
    }

    public static void hideReaderMenu() {
        if (isReaderMenuShown()) {
            readerMenu.hide();
        }
    }

    private void showReaderMenu(final ReaderDataHolder readerDataHolder) {
        getReaderMenu(readerDataHolder).show(getReaderMenuState(readerDataHolder));
    }

    private ReaderMenu getReaderMenu(final ReaderDataHolder readerDataHolder) {
        if (readerMenu == null) {
            initReaderMenu(readerDataHolder);
        }
        return readerMenu;
    }

    private void initReaderMenu(final ReaderDataHolder readerDataHolder) {
        getDisableMenus(readerDataHolder);
        createReaderSideMenu(readerDataHolder);
    }

    private void getDisableMenus(ReaderDataHolder readerDataHolder) {
        disableMenus.clear();

        if (DeviceConfig.sharedInstance(readerDataHolder.getContext()).isDisableNoteFunc()) {
            disableMenus.add(ReaderMenuAction.NOTE);
        }
        if (!DeviceConfig.sharedInstance(readerDataHolder.getContext()).isTtsEnabled()) {
            disableMenus.add(ReaderMenuAction.TTS);
        }
        if (!DeviceConfig.sharedInstance(readerDataHolder.getContext()).hasFrontLight()) {
            disableMenus.add(ReaderMenuAction.FRONT_LIGHT);
        }

        if (!readerDataHolder.supportTextPage()) {
            disableMenus.add(ReaderMenuAction.TTS);
        }
        if (!readerDataHolder.supportNoteExport()) {
            disableMenus.add(ReaderMenuAction.NOTE_EXPORT);
        }
        if (!readerDataHolder.supportScalable()) {
            disableMenus.add(ReaderMenuAction.ZOOM);
            disableMenus.add(ReaderMenuAction.IMAGE_REFLOW);
            disableMenus.add(ReaderMenuAction.NAVIGATION_COMIC_MODE);
            disableMenus.add(ReaderMenuAction.NAVIGATION_ARTICLE_MODE);
            disableMenus.add(ReaderMenuAction.NAVIGATION_RESET);
            disableMenus.add(ReaderMenuAction.NAVIGATION_MORE_SETTINGS);
        }else {
            disableMenus.add(ReaderMenuAction.FONT);
        }

        if (disableScribbleBrush) {
            disableMenus.add(ReaderMenuAction.SCRIBBLE_BRUSH);
        }

        if (DeviceConfig.sharedInstance(readerDataHolder.getContext()).isSupportColor()) {
            disableMenus.add(ReaderMenuAction.SCRIBBLE_DRAG);
        }else {
            disableMenus.add(ReaderMenuAction.SCRIBBLE_COLOR);
        }
    }

    private void createReaderSideMenu(final ReaderDataHolder readerDataHolder) {
        ReaderLayerMenuItem[] menuItems;
        if (DeviceConfig.sharedInstance(readerDataHolder.getContext()).isSupportColor()) {
            menuItems = ReaderLayerMenuRepository.colorMenuItems;
            readerMenu = new ReaderLayerColorMenu(readerDataHolder.getContext());
        }else {
            menuItems = ReaderLayerMenuRepository.fixedPageMenuItems;
            readerMenu = new ReaderLayerMenu(readerDataHolder.getContext());
        }
        updateReaderMenuCallback(readerMenu, readerDataHolder);
        List<ReaderLayerMenuItem> items = createReaderSideMenuItems(readerDataHolder, menuItems);
        readerMenu.fillItems(items);
    }

    private void updateReaderMenuCallback(final ReaderMenu menu, final ReaderDataHolder readerDataHolder) {
        menu.setReaderMenuCallback(new ReaderMenu.ReaderMenuCallback() {
            @Override
            public void onHideMenu() {
                hideReaderMenu();
            }

            @Override
            public void onMenuItemClicked(ReaderMenuItem menuItem) {
                Log.d(TAG, "onMenuItemClicked: " + menuItem.getAction());
                switch (menuItem.getAction()) {
                    case FONT:
                        showTextStyleDialog(readerDataHolder);
                        break;
                    case ROTATION_ROTATE_0:
                        rotateScreen(readerDataHolder, 0);
                        break;
                    case ROTATION_ROTATE_90:
                        rotateScreen(readerDataHolder, 90);
                        break;
                    case ROTATION_ROTATE_180:
                        rotateScreen(readerDataHolder, 180);
                        break;
                    case ROTATION_ROTATE_270:
                        rotateScreen(readerDataHolder, 270);
                        break;
                    case IMAGE_REFLOW:
                        imageReflow(readerDataHolder);
                        break;
                    case ZOOM_IN:
                        scaleUp(readerDataHolder);
                        break;
                    case ZOOM_OUT:
                        scaleDown(readerDataHolder);
                        break;
                    case ZOOM_TO_PAGE:
                        scaleToPage(readerDataHolder);
                        break;
                    case ZOOM_TO_WIDTH:
                        scaleToWidth(readerDataHolder);
                        break;
                    case ZOOM_BY_CROP_PAGE:
                        cropPage(readerDataHolder);
                        break;
                    case ZOOM_BY_CROP_WIDTH:
                        cropWidth(readerDataHolder);
                        break;
                    case ZOOM_BY_RECT:
                        scaleByRect(readerDataHolder);
                        break;
                    case NAVIGATION_ARTICLE_MODE:
                        switchNavigationToArticleMode(readerDataHolder);
                        break;
                    case NAVIGATION_COMIC_MODE:
                        switchNavigationToComicMode(readerDataHolder);
                        break;
                    case NAVIGATION_RESET:
                        resetNavigationMode(readerDataHolder);
                        break;
                    case NAVIGATION_MORE_SETTINGS:
                        showNavigationSettingsDialog(readerDataHolder);
                        break;
                    case GAMMA_CORRECTION:
                        adjustContrast(readerDataHolder);
                        break;
                    case GLYPH_EMBOLDEN:
                        adjustEmbolden(readerDataHolder);
                        break;
                    case DIRECTORY_TOC:
                        showTocDialog(readerDataHolder, DialogTableOfContent.DirectoryTab.TOC);
                        break;
                    case DIRECTORY_BOOKMARK:
                        showTocDialog(readerDataHolder, DialogTableOfContent.DirectoryTab.Bookmark);
                        break;
                    case DIRECTORY_NOTE:
                        showTocDialog(readerDataHolder, DialogTableOfContent.DirectoryTab.Annotation);
                        break;
                    case DIRECTORY_SCRIBBLE:
                        startNoteDrawing(readerDataHolder, readerActivity);
                        break;
                    case NOTE_EXPORT:
                        showExportDialog(readerDataHolder);
                        break;
                    case SHOW_NOTE:
                        showScribble(readerDataHolder);
                        break;
                    case TTS:
                        showTtsDialog(readerDataHolder);
                        break;
                    case REFRESH:
                        showScreenRefreshDialog(readerDataHolder);
                        break;
                    case FRONT_LIGHT:
                        showBrightnessDialog(readerDataHolder);
                        break;
                    case GOTO_PAGE:
                        gotoPage(readerDataHolder);
                        break;
                    case NAVIGATE_BACKWARD:
                        backward(readerDataHolder);
                        break;
                    case NAVIGATE_FORWARD:
                        forward(readerDataHolder);
                        break;
                    case DICT:
                        startDictionaryApp(readerDataHolder);
                        break;
                    case SEARCH:
                        showSearchDialog(readerDataHolder);
                        break;
                    case SETTINGS:
                        showReaderSettings(readerDataHolder);
                        break;
                    case NOTE_WRITING:
                        startNoteDrawing(readerDataHolder, readerActivity);
                        break;
                    case EXIT:
                        readerDataHolder.getEventBus().post(new QuitEvent());
                        break;
                }
            }

            @Override
            public void onMenuItemValueChanged(ReaderMenuItem menuItem, Object oldValue, Object newValue) {
                Debug.d("onMenuItemValueChanged: " + menuItem.getAction() + ", " + oldValue + ", " + newValue);
                switch (menuItem.getAction()) {
                    case JUMP_PAGE:
                        gotoPage(readerDataHolder, newValue);
                        break;
                }
            }
        });
    }

    private List<ReaderLayerMenuItem> createReaderSideMenuItems(final ReaderDataHolder readerDataHolder, ReaderLayerMenuItem[] menuItems) {
        return ReaderLayerMenuRepository.createFromArray(menuItems, disableMenus);
    }

    private void rotateScreen(final ReaderDataHolder readerDataHolder, int rotationOperation) {
        final ChangeOrientationAction action = new ChangeOrientationAction(readerActivity.getRequestedOrientation(), rotationOperation);
        action.execute(readerDataHolder, null);
    }

    private void scaleUp(final ReaderDataHolder readerDataHolder) {
        final ChangeScaleWithDeltaAction action = new ChangeScaleWithDeltaAction(0.1f);
        action.execute(readerDataHolder, null);
    }

    private void scaleDown(final ReaderDataHolder readerDataHolder) {
        final ChangeScaleWithDeltaAction action = new ChangeScaleWithDeltaAction(-0.1f);
        action.execute(readerDataHolder, null);
    }

    private void scaleByValue(final ReaderDataHolder readerDataHolder, float scale) {
        final ScaleRequest request = new ScaleRequest(readerDataHolder.getCurrentPageName(), scale, readerDataHolder.getDisplayWidth() / 2, readerDataHolder.getDisplayHeight() / 2);
        readerDataHolder.submitRenderRequest(request);
    }

    private void scaleToPage(final ReaderDataHolder readerDataHolder) {
        final ScaleToPageRequest request = new ScaleToPageRequest(readerDataHolder.getCurrentPageName());
        readerDataHolder.submitRenderRequest(request);
    }

    private void scaleToWidth(final ReaderDataHolder readerDataHolder) {
        final ScaleToWidthRequest request = new ScaleToWidthRequest(readerDataHolder.getCurrentPageName());
        readerDataHolder.submitRenderRequest(request);
    }

    private void cropPage(final ReaderDataHolder readerDataHolder) {
        final ScaleToPageCropRequest request = new ScaleToPageCropRequest(readerDataHolder.getCurrentPageName());
        readerDataHolder.submitRenderRequest(request);
    }

    private void cropWidth(final ReaderDataHolder readerDataHolder) {
        final ScaleToWidthContentRequest request = new ScaleToWidthContentRequest(readerDataHolder.getCurrentPageName());
        readerDataHolder.submitRenderRequest(request);
    }

    private void scaleByRect(final ReaderDataHolder readerDataHolder) {
        final SelectionScaleAction action = new SelectionScaleAction();
        action.execute(readerDataHolder, null);
    }

    private void scaleByAutoCrop(final ReaderDataHolder readerDataHolder) {
        final PageCropAction action = new PageCropAction(readerDataHolder.getCurrentPageName());
        action.execute(readerDataHolder, null);
    }

    private void switchNavigationToArticleMode(final ReaderDataHolder readerDataHolder) {
        NavigationArgs args = new NavigationArgs();
        RectF limit = new RectF(0, 0, 0, 0);
        args.columnsLeftToRight(NavigationArgs.Type.ALL, 2, 2, limit);
        switchPageNavigationMode(readerDataHolder, args);
    }

    private void switchNavigationToComicMode(final ReaderDataHolder readerDataHolder) {
        NavigationArgs args = new NavigationArgs();
        RectF limit = new RectF(0, 0, 0, 0);
        args.rowsRightToLeft(NavigationArgs.Type.ALL, 2, 2, limit);
        switchPageNavigationMode(readerDataHolder, args);
    }

    private void switchPageNavigationMode(final ReaderDataHolder readerDataHolder, NavigationArgs args) {
        BaseReaderRequest request = new ChangeLayoutRequest(PageConstants.SINGLE_PAGE_NAVIGATION_LIST, args);
        readerDataHolder.submitRenderRequest(request);
    }

    private void resetNavigationMode(final ReaderDataHolder readerDataHolder) {
        BaseReaderRequest request = new ChangeLayoutRequest(PageConstants.SINGLE_PAGE, new NavigationArgs());
        readerDataHolder.submitRenderRequest(request);
    }

    private void showNavigationSettingsDialog(ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        Dialog dlg = new DialogNavigationSettings(readerDataHolder);
        dlg.show();
        readerDataHolder.addActiveDialog(dlg);
    }

    private void adjustContrast(final ReaderDataHolder readerDataHolder) {
        final AdjustContrastAction action = new AdjustContrastAction();
        action.execute(readerDataHolder, null);
    }

    private void adjustEmbolden(final ReaderDataHolder readerDataHolder) {
        final EmboldenAction action = new EmboldenAction();
        action.execute(readerDataHolder, null);
    }

    private void imageReflow(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        final ImageReflowAction action = new ImageReflowAction();
        action.execute(readerDataHolder, null);
    }

    private void showTocDialog(final ReaderDataHolder readerDataHolder, DialogTableOfContent.DirectoryTab tab) {
        Dialog dialog = new DialogTableOfContent(readerDataHolder, tab);
        dialog.show();
        readerDataHolder.addActiveDialog(dialog);
    }

    private void gotoPage(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        new ShowQuickPreviewAction(readerDataHolder).execute(readerDataHolder, null);
    }

    private void backward(final ReaderDataHolder readerDataHolder) {
        new BackwardAction().execute(readerDataHolder, null);
    }

    private void forward(final ReaderDataHolder readerDataHolder) {
        new ForwardAction().execute(readerDataHolder, null);
    }

    private void gotoPage(final ReaderDataHolder readerDataHolder, Object o) {
        if (o == null) {
            return;
        }
        int page = (int) o;
        new GotoPageAction(page).execute(readerDataHolder);
    }

    private void showScreenRefreshDialog(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        DialogScreenRefresh dlg = new DialogScreenRefresh();
        dlg.setListener(new DialogScreenRefresh.onScreenRefreshChangedListener() {
            @Override
            public void onRefreshIntervalChanged(int oldValue, int newValue) {
                LegacySdkDataUtils.setScreenUpdateGCInterval(readerDataHolder.getContext(), newValue);
                ReaderDeviceManager.setGcInterval(newValue);
            }
        });
        dlg.show(readerActivity.getFragmentManager());
        readerDataHolder.addActiveDialog(dlg);
    }

    private void showBrightnessDialog(ReaderDataHolder readerDataHolder){
        Dialog dlg = new DialogBrightness(readerDataHolder.getContext());
        dlg.show();
        readerDataHolder.addActiveDialog(dlg);
    }

    private void showExportDialog(ReaderDataHolder readerDataHolder){
        hideReaderMenu();
        Dialog exportDialog = new DialogExport(readerDataHolder);
        exportDialog.show();
        readerDataHolder.addActiveDialog(exportDialog);
    }

    private boolean startDictionaryApp(final ReaderDataHolder readerDataHolder) {
        OnyxDictionaryInfo info = LegacySdkDataUtils.getDictionary(readerDataHolder.getContext());
        if (info == null) {
            Toast.makeText(readerDataHolder.getContext(), R.string.did_not_find_the_dictionary, Toast.LENGTH_LONG).show();
            return false;
        }
        Intent intent = new Intent(info.action).setComponent(new ComponentName(info.packageName, info.className));
        try {
            readerDataHolder.getContext().startActivity(intent);
        } catch ( ActivityNotFoundException e ) {
            Toast.makeText(readerDataHolder.getContext(), R.string.did_not_find_the_dictionary, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void showTextStyleDialog(ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        final Dialog dialog = new DialogTextStyle(readerDataHolder, new DialogTextStyle.TextStyleCallback() {
            @Override
            public void onSaveReaderStyle(ReaderTextStyle readerStyle) {

            }
        });
        dialog.show();
        readerDataHolder.addActiveDialog(dialog);
    }

    private void showSearchDialog(final ReaderDataHolder readerDataHolder){
        Dialog dlg = new DialogSearch(readerDataHolder);
        dlg.show();
        readerDataHolder.addActiveDialog(dlg);
    }

    private void showTtsDialog(final ReaderDataHolder readerDataHolder){
        hideReaderMenu();
        StartTtsAction action = new StartTtsAction();
        action.execute(readerDataHolder, null);
    }

    private void showReaderSettings(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        new ShowReaderSettingsAction().execute(readerDataHolder, null);
    }

    private void showScribble(ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        boolean isShowScribble = !SingletonSharedPreference.isShowNote(readerDataHolder.getContext());
        SingletonSharedPreference.setIsShowNote(readerDataHolder.getContext(), isShowScribble);
        new GotoPositionAction(readerDataHolder.getCurrentPageName()).execute(readerDataHolder);
    }

    public static void updateReaderMenuState(final ReaderDataHolder readerDataHolder) {
        readerMenu.updateReaderMenuState(getReaderMenuState(readerDataHolder));
    }

    public static void startNoteDrawing(final ReaderDataHolder readerDataHolder, final ReaderActivity readerActivity) {
        hideReaderMenu();
        setIsScribbleMenuVisible(true);
        final ShowScribbleMenuAction menuAction = new ShowScribbleMenuAction(readerActivity.getMainView(),
                getScribbleActionCallback(readerDataHolder),
                disableMenus);
        menuAction.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                setIsScribbleMenuVisible(false);
                StopNoteActionChain stopNoteActionChain = new StopNoteActionChain(true, true, false, false, false, true);
                stopNoteActionChain.execute(readerDataHolder, null);
            }
        });
    }

    public static boolean isScribbleMenuVisible() {
        return isScribbleMenuVisible;
    }

    public static void setIsScribbleMenuVisible(boolean isScribbleMenuVisible) {
        ShowReaderMenuAction.isScribbleMenuVisible = isScribbleMenuVisible;
    }

    public static ShowScribbleMenuAction.ActionCallback getScribbleActionCallback(final ReaderDataHolder readerDataHolder) {
        final ShowScribbleMenuAction.ActionCallback callback = new ShowScribbleMenuAction.ActionCallback() {
            @Override
            public void onClicked(final ReaderMenuAction action) {
                if (processScribbleActionGroup(readerDataHolder, action)) {
                    return;
                }
                processScribbleAction(readerDataHolder, action);
            }
        };
        return callback;
    }

    public static boolean isGroupAction(final ReaderMenuAction action) {
        return (action == ReaderMenuAction.SCRIBBLE_ERASER ||
                action == ReaderMenuAction.SCRIBBLE_WIDTH ||
                action == ReaderMenuAction.SCRIBBLE_SHAPE);
    }

    public static boolean processScribbleActionGroup(final ReaderDataHolder readerDataHolder, final ReaderMenuAction action) {
        if (!isGroupAction(action)) {
            return false;
        }
        final FlushNoteAction flushNoteAction = new FlushNoteAction(readerDataHolder.getVisiblePages(), true, true, false, false);
        flushNoteAction.execute(readerDataHolder, null);
        return true;
    }

    public static void processScribbleAction(final ReaderDataHolder readerDataHolder, final ReaderMenuAction action) {
        switch (action) {
            case SCRIBBLE_WIDTH1:
                useStrokeWidth(readerDataHolder, 2.0f);
                break;
            case SCRIBBLE_WIDTH2:
                useStrokeWidth(readerDataHolder, 4.0f);
                break;
            case SCRIBBLE_WIDTH3:
                useStrokeWidth(readerDataHolder, 6.0f);
                break;
            case SCRIBBLE_WIDTH4:
                useStrokeWidth(readerDataHolder, 9.0f);
                break;
            case SCRIBBLE_WIDTH5:
                useStrokeWidth(readerDataHolder, 12.0f);
                break;
            case SCRIBBLE_PENCIL:
                useShape(readerDataHolder, ShapeFactory.SHAPE_PENCIL_SCRIBBLE);
                break;
            case SCRIBBLE_BRUSH:
                useShape(readerDataHolder, ShapeFactory.SHAPE_BRUSH_SCRIBBLE);
                break;
            case SCRIBBLE_LINE:
                useShape(readerDataHolder, ShapeFactory.SHAPE_LINE);
                break;
            case SCRIBBLE_TRIANGLE:
                useShape(readerDataHolder, ShapeFactory.SHAPE_TRIANGLE);
                break;
            case SCRIBBLE_CIRCLE:
                useShape(readerDataHolder, ShapeFactory.SHAPE_CIRCLE);
                break;
            case SCRIBBLE_SQUARE:
                useShape(readerDataHolder, ShapeFactory.SHAPE_RECTANGLE);
                break;
            case SCRIBBLE_TEXT:
                useShape(readerDataHolder, ShapeFactory.SHAPE_ANNOTATION);
                break;
            case SCRIBBLE_ERASER_PART:
                startErasing(readerDataHolder);
                break;
            case SCRIBBLE_ERASER_ALL:
                eraseWholePage(readerDataHolder);
                break;
            case SCRIBBLE_DRAG:
                toggleSelection(readerDataHolder);
                break;
            case SCRIBBLE_MINIMIZE:
                toggleMenu(readerDataHolder);
                break;
            case SCRIBBLE_MAXIMIZE:
                toggleMenu(readerDataHolder);
                break;
            case SCRIBBLE_PREV_PAGE:
                prevScreen(readerDataHolder);
                break;
            case SCRIBBLE_NEXT_PAGE:
                nextScreen(readerDataHolder);
                break;
            case SCRIBBLE_UNDO:
                undo(readerDataHolder);
                break;
            case SCRIBBLE_SAVE:
                save(readerDataHolder);
                break;
            case SCRIBBLE_REDO:
                redo(readerDataHolder);
                break;
        }
    }

    private static void useStrokeWidth(final ReaderDataHolder readerDataHolder, float width) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, true, false, false));
        actionChain.addAction(new ChangeStrokeWidthAction(width, true));
        actionChain.execute(readerDataHolder, null);
    }

    private static void useShape(final ReaderDataHolder readerDataHolder, int type) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, true, false, false));
        actionChain.addAction(new ChangeNoteShapeAction(type));
        actionChain.execute(readerDataHolder, null);
    }

    private static void undo(final ReaderDataHolder readerDataHolder) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, true, false, false));
        actionChain.addAction(new UndoAction());
        actionChain.execute(readerDataHolder, null);
    }

    private static void redo(final ReaderDataHolder readerDataHolder) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, true, false, false));
        actionChain.addAction(new RedoAction());
        actionChain.execute(readerDataHolder, null);
    }

    private static void save(final ReaderDataHolder readerDataHolder) {
        FlushNoteAction flushNoteAction = new FlushNoteAction(readerDataHolder.getVisiblePages(), true, true, true, true);
        flushNoteAction.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                resumeDrawing(readerDataHolder);
            }
        });
    }

    private static void nextScreen(final ReaderDataHolder readerDataHolder) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, true, false, false));
        actionChain.addAction(new NextScreenAction());
        actionChain.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                resumeDrawing(readerDataHolder);
            }
        });
    }

    private static void prevScreen(final ReaderDataHolder readerDataHolder) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getReaderViewInfo().getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, true, false, false));
        actionChain.addAction(new PreviousScreenAction());
        actionChain.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                resumeDrawing(readerDataHolder);
            }
        });
    }

    private static void resumeDrawing(final ReaderDataHolder readerDataHolder) {
        final ResumeDrawingAction action = new ResumeDrawingAction(readerDataHolder.getVisiblePages());
        action.execute(readerDataHolder, null);
    }

    private static void eraseWholePage(final ReaderDataHolder readerDataHolder) {
        final ClearPageAction clearPageAction = new ClearPageAction(readerDataHolder.getFirstPageInfo());
        clearPageAction.execute(readerDataHolder, null);
    }

    private static void startErasing(final ReaderDataHolder readerDataHolder) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getReaderViewInfo().getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, true, false, false));
        actionChain.addAction(new ChangeNoteShapeAction(ShapeFactory.SHAPE_ERASER));
        actionChain.execute(readerDataHolder, null);
    }

    private static void toggleMenu(final ReaderDataHolder readerDataHolder) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getReaderViewInfo().getVisiblePages();
        final FlushNoteAction flushNoteAction = new FlushNoteAction(pages, true, true, false, false);
        final ResumeDrawingAction action = new ResumeDrawingAction(pages);
        actionChain.addAction(flushNoteAction);
        actionChain.addAction(action);
        actionChain.execute(readerDataHolder, null);
    }

    private static void toggleSelection(final ReaderDataHolder readerDataHolder) {
        if (readerDataHolder.getNoteManager().isInSelection()) {
            final ActionChain actionChain = new ActionChain();
            actionChain.addAction(new RestoreShapeAction());
            actionChain.execute(readerDataHolder, null);
        } else {
            final ActionChain actionChain = new ActionChain();
            final List<PageInfo> pages = readerDataHolder.getReaderViewInfo().getVisiblePages();
            actionChain.addAction(new FlushNoteAction(pages, true, true, false, false));
            actionChain.addAction(new ChangeNoteShapeAction(ShapeFactory.SHAPE_SELECTOR));
            actionChain.execute(readerDataHolder, null);
        }
    }

    private static ReaderMenuState getReaderMenuState(final ReaderDataHolder readerDataHolder) {
        return new ReaderMenuState() {
            @Override
            public String getTitle() {
                return FileUtils.getFileName(readerDataHolder.getReader().getDocumentPath());
            }

            @Override
            public int getPageIndex() {
                return readerDataHolder.getCurrentPage();
            }

            @Override
            public int getPageCount() {
                return readerDataHolder.getPageCount();
            }

            @Override
            public boolean canGoBack() {
                return readerDataHolder.getReaderViewInfo().canGoBack;
            }

            @Override
            public boolean canGoForward() {
                return readerDataHolder.getReaderViewInfo().canGoForward;
            }

            @Override
            public boolean isFixedPagingMode() {
                return readerDataHolder.supportScalable();
            }

            @Override
            public boolean isShowingNotes() {
                return SingletonSharedPreference.isShowNote(readerDataHolder.getContext());
            }

            @Override
            public List<String> getFontFaces() {
                return fontFaces;
            }

            @Override
            public ReaderTextStyle getReaderStyle() {
                return readerDataHolder.getReaderViewInfo().getReaderTextStyle();
            }
        };
    }

}
