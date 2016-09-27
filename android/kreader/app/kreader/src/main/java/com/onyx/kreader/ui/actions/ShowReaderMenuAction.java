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
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.data.ScribbleMenuAction;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.ui.data.ReaderLayerMenu;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuItem;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuRepository;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuState;
import com.onyx.android.sdk.ui.dialog.DialogBrightness;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.dataprovider.LegacySdkDataUtils;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.request.ChangeLayoutRequest;
import com.onyx.kreader.host.request.ScaleRequest;
import com.onyx.kreader.host.request.ScaleToPageCropRequest;
import com.onyx.kreader.host.request.ScaleToPageRequest;
import com.onyx.kreader.host.request.ScaleToWidthContentRequest;
import com.onyx.kreader.host.request.ScaleToWidthRequest;
import com.onyx.kreader.note.actions.ChangeNoteShapeAction;
import com.onyx.kreader.note.actions.ChangeStrokeWidthAction;
import com.onyx.kreader.note.actions.FlushNoteAction;
import com.onyx.kreader.note.actions.RedoAction;
import com.onyx.kreader.note.actions.StopNoteActionChain;
import com.onyx.kreader.note.actions.UndoAction;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogExport;
import com.onyx.kreader.ui.dialog.DialogNavigationSettings;
import com.onyx.kreader.ui.dialog.DialogScreenRefresh;
import com.onyx.kreader.ui.dialog.DialogSearch;
import com.onyx.kreader.ui.dialog.DialogTableOfContent;

import java.util.List;

/**
 * Created by Joy on 2016/6/7.
 */
public class ShowReaderMenuAction extends BaseAction {

    public static final String TAG = ShowReaderMenuAction.class.getSimpleName();
    ReaderActivity readerActivity;

    // use reader menu as static field to avoid heavy init of showing reader menu each time
    private static ReaderLayerMenu readerMenu;
    private static ReaderLayerMenuState state;

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
        if (state == null){
            state = new ReaderLayerMenuState();
        }
        state.setTitle(FileUtils.getFileName(readerDataHolder.getReader().getDocumentPath()));
        state.setPageCount(readerDataHolder.getPageCount());
        state.setPageIndex(readerDataHolder.getCurrentPage());
        getReaderMenu(readerDataHolder).show(state);
        updateBackwardForwardState(readerDataHolder);
    }

    private ReaderMenu getReaderMenu(final ReaderDataHolder readerDataHolder) {
        if (readerMenu == null) {
            initReaderMenu(readerDataHolder);
        }
        return readerMenu;
    }

    private void initReaderMenu(final ReaderDataHolder readerDataHolder) {
        createReaderSideMenu(readerDataHolder);
    }

    private void createReaderSideMenu(final ReaderDataHolder readerDataHolder) {
        readerMenu = new ReaderLayerMenu(readerDataHolder.getContext());
        updateReaderMenuCallback(readerMenu, readerDataHolder);
        List<ReaderLayerMenuItem> items = createReaderSideMenuItems(readerDataHolder);
        readerMenu.fillItems(items);
    }

    private static void updateReaderMenuState(final ReaderDataHolder readerDataHolder, final ReaderLayerMenuState state) {
        readerMenu.updateReaderMenuState(state);
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
                        startNoteDrawing(readerDataHolder);
                        break;
                    case DIRECTORY_EXPORT:
                        showExportDialog(readerDataHolder);
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
                        startNoteDrawing(readerDataHolder);
                        break;
                    case EXIT:
                        readerActivity.onBackPressed();
                        break;
                }
            }

            @Override
            public void onMenuItemValueChanged(ReaderMenuItem menuItem, Object oldValue, Object newValue) {
                Debug.d("onMenuItemValueChanged: " + menuItem.getAction() + ", " + oldValue + ", " + newValue);
            }
        });
    }

    private List<ReaderLayerMenuItem> createReaderSideMenuItems(final ReaderDataHolder readerDataHolder) {
        return ReaderLayerMenuRepository.createFromArray(ReaderLayerMenuRepository.fixedPageMenuItems);
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
        final GetDocumentInfoChain action = new GetDocumentInfoChain(tab);
        action.execute(readerDataHolder, null);
    }

    private void gotoPage(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        new ShowQuickPreviewAction().execute(readerDataHolder, null);
    }

    private void backward(final ReaderDataHolder readerDataHolder) {
        new BackwardAction().execute(readerDataHolder, null);
    }

    private void forward(final ReaderDataHolder readerDataHolder) {
        new ForwardAction().execute(readerDataHolder, null);
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
    
    public static void updateBackwardForwardState(ReaderDataHolder readerDataHolder){
        state.setCanGoBack(readerDataHolder.getReaderViewInfo().canGoBack);
        state.setCanGoForward(readerDataHolder.getReaderViewInfo().canGoForward);
        updateReaderMenuState(readerDataHolder, state);
    }

    private void startNoteDrawing(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        final ShowScribbleMenuAction menuAction = new ShowScribbleMenuAction(readerActivity.getMainView(),
                getScribbleActionCallback(readerDataHolder));
        menuAction.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                StopNoteActionChain stopNoteActionChain = new StopNoteActionChain();
                stopNoteActionChain.execute(readerDataHolder, null);
            }
        });
    }

    private ShowScribbleMenuAction.ActionCallback getScribbleActionCallback(final ReaderDataHolder readerDataHolder) {
        final ShowScribbleMenuAction.ActionCallback callback = new ShowScribbleMenuAction.ActionCallback() {
            @Override
            public void onClicked(final ScribbleMenuAction action) {
                if (processScribbleActionGroup(readerDataHolder, action)) {
                    return;
                }
                processScribbleAction(readerDataHolder, action);
            }
        };
        return callback;
    }

    private boolean isGroupAction(final ScribbleMenuAction action) {
        return (action == ScribbleMenuAction.ERASER ||
                action == ScribbleMenuAction.WIDTH ||
                action == ScribbleMenuAction.TEXT ||
                action == ScribbleMenuAction.SHAPE ||
                action == ScribbleMenuAction.DRAG ||
                action == ScribbleMenuAction.MINIMIZE);
    }

    private boolean processScribbleActionGroup(final ReaderDataHolder readerDataHolder, final ScribbleMenuAction action) {
        if (!isGroupAction(action)) {
            return false;
        }
        final FlushNoteAction flushNoteAction = new FlushNoteAction(readerDataHolder.getVisiblePages(), true, false, false);
        flushNoteAction.execute(readerDataHolder, null);
        return true;
    }

    private void processScribbleAction(final ReaderDataHolder readerDataHolder, final ScribbleMenuAction action) {
        switch (action) {
            case WIDTH1:
                useStrokeWidth(readerDataHolder, 2.0f);
                break;
            case WIDTH2:
                useStrokeWidth(readerDataHolder, 4.0f);
                break;
            case WIDTH3:
                useStrokeWidth(readerDataHolder, 6.0f);
                break;
            case WIDTH4:
                useStrokeWidth(readerDataHolder, 9.0f);
                break;
            case WIDTH5:
                useStrokeWidth(readerDataHolder, 12.0f);
                break;
            case PENCIL:
                useShape(readerDataHolder, ShapeFactory.SHAPE_PENCIL_SCRIBBLE);
                break;
            case BRUSH:
                useShape(readerDataHolder, ShapeFactory.SHAPE_BRUSH_SCRIBBLE);
                break;
            case LINE:
                useShape(readerDataHolder, ShapeFactory.SHAPE_LINE);
                break;
            case TRIANGLE:
                useShape(readerDataHolder, ShapeFactory.SHAPE_TRIANGLE);
                break;
            case CIRCLE:
                useShape(readerDataHolder, ShapeFactory.SHAPE_CIRCLE);
                break;
            case SQUARE:
                useShape(readerDataHolder, ShapeFactory.SHAPE_RECTANGLE);
                break;
            case TEXT:
                break;
            case ERASER_PART:
                break;
            case ERASER_ALL:
                break;
            case DRAG:
                break;
            case MINIMIZE:
                break;
            case MAXIMIZE:
                break;
            case PREV_PAGE:
                prevScreen(readerDataHolder);
                break;
            case NEXT_PAGE:
                nextScreen(readerDataHolder);
                break;
            case UNDO:
                undo(readerDataHolder);
                break;
            case SAVE:
                save(readerDataHolder);
                break;
            case REDO:
                redo(readerDataHolder);
                break;
        }
    }

    private void useStrokeWidth(final ReaderDataHolder readerDataHolder, float width) {
        ChangeStrokeWidthAction action = new ChangeStrokeWidthAction(width);
        action.execute(readerDataHolder, null);
    }

    private void useShape(final ReaderDataHolder readerDataHolder, int type) {
        ChangeNoteShapeAction changeNoteShapeAction = new ChangeNoteShapeAction(type);
        changeNoteShapeAction.execute(readerDataHolder, null);
    }

    private void undo(final ReaderDataHolder readerDataHolder) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getReaderViewInfo().getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, false, false));
        actionChain.addAction(new UndoAction());
        actionChain.execute(readerDataHolder, null);
    }

    private void redo(final ReaderDataHolder readerDataHolder) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getReaderViewInfo().getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, false, false));
        actionChain.addAction(new RedoAction());
        actionChain.execute(readerDataHolder, null);
    }

    private void save(final ReaderDataHolder readerDataHolder) {
        FlushNoteAction flushNoteAction = new FlushNoteAction(readerDataHolder.getReaderViewInfo().getVisiblePages(), true, true, true);
        flushNoteAction.execute(readerDataHolder, null);
    }

    private void nextScreen(final ReaderDataHolder readerDataHolder) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getReaderViewInfo().getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, false, false));
        actionChain.addAction(new NextScreenAction());
        actionChain.execute(readerDataHolder, null);
    }

    private void prevScreen(final ReaderDataHolder readerDataHolder) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getReaderViewInfo().getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, false, false));
        actionChain.addAction(new PreviousScreenAction());
        actionChain.execute(readerDataHolder, null);
    }
}
