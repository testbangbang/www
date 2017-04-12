package com.onyx.kreader.ui.actions;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
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
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.reader.utils.TocUtils;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.ui.data.ReaderLayerColorMenu;
import com.onyx.android.sdk.ui.data.ReaderLayerMenu;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuItem;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuRepository;
import com.onyx.android.sdk.ui.dialog.DialogBrightness;
import com.onyx.android.sdk.ui.dialog.DialogNaturalLightBrightness;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.reader.dataprovider.LegacySdkDataUtils;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.host.request.ChangeLayoutRequest;
import com.onyx.android.sdk.reader.host.request.ScaleRequest;
import com.onyx.android.sdk.reader.host.request.ScaleToPageCropRequest;
import com.onyx.android.sdk.reader.host.request.ScaleToPageRequest;
import com.onyx.android.sdk.reader.host.request.ScaleToWidthContentRequest;
import com.onyx.android.sdk.reader.host.request.ScaleToWidthRequest;
import com.onyx.kreader.note.actions.ChangeNoteShapeAction;
import com.onyx.kreader.note.actions.ChangeStrokeWidthAction;
import com.onyx.kreader.note.actions.ClearPageAction;
import com.onyx.kreader.note.actions.FlushNoteAction;
import com.onyx.kreader.note.actions.RedoAction;
import com.onyx.kreader.note.actions.RestoreShapeAction;
import com.onyx.kreader.note.actions.ResumeDrawingAction;
import com.onyx.kreader.note.actions.UndoAction;
import com.onyx.kreader.note.data.ReaderNoteDataInfo;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderCropArgs;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.dialog.DialogContrast;
import com.onyx.kreader.ui.dialog.DialogExport;
import com.onyx.kreader.ui.dialog.DialogNavigationSettings;
import com.onyx.kreader.ui.dialog.DialogScreenRefresh;
import com.onyx.kreader.ui.dialog.DialogSearch;
import com.onyx.kreader.ui.dialog.DialogTableOfContent;
import com.onyx.kreader.ui.dialog.DialogTextStyle;
import com.onyx.kreader.ui.events.QuitEvent;
import com.onyx.kreader.device.DeviceConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Joy on 2016/6/7.
 */
public class ShowReaderMenuAction extends BaseAction {

    public static final String TAG = ShowReaderMenuAction.class.getSimpleName();
    ReaderActivity readerActivity;

    // use reader menu as static field to avoid heavy init of showing reader menu each time
    private static ReaderMenu readerMenu;
    private static Set<ReaderMenuAction> disableMenus = new HashSet<>();
    private static List<String> fontFaces = new ArrayList<>();
    private static Map<Float, ReaderMenuAction> strokeMapping;
    private static List<Integer> tocChapterNodeList;

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerActivity = (ReaderActivity)readerDataHolder.getContext();
        showReaderMenu(readerDataHolder, DeviceUtils.isFullScreen(readerActivity));
        BaseCallback.invoke(callback, null, null);
    }

    public static void resetReaderMenu(final ReaderDataHolder readerDataHolder) {
        if (readerMenu != null) {
            readerMenu.hide();
            readerMenu = null;
        }
        if (tocChapterNodeList != null) {
            tocChapterNodeList.clear();
            tocChapterNodeList = null;
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

    public static List<Integer> getTocChapterNodeList() {
        return tocChapterNodeList;
    }

    public static void setTocChapterNodeList(List<Integer> tocChapterNodeList) {
        ShowReaderMenuAction.tocChapterNodeList = tocChapterNodeList;
    }

    private void showReaderMenu(final ReaderDataHolder readerDataHolder, boolean fullscreen) {
        ReaderMenu readerMenu = getReaderMenu(readerDataHolder);
        readerMenu.setFullScreen(fullscreen);
        readerMenu.show(getReaderMenuState(readerDataHolder));
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

    public static void initDisableMenus(ReaderDataHolder readerDataHolder) {
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

        if (!DeviceConfig.sharedInstance(readerDataHolder.getContext()).hasNaturalLight()) {
            disableMenus.add(ReaderMenuAction.NATURAL_LIGHT);
        }

        if (!readerDataHolder.supportTextPage()) {
            disableMenus.add(ReaderMenuAction.TTS);
        }
        if (!readerDataHolder.supportNoteExport()) {
            disableMenus.add(ReaderMenuAction.NOTE_EXPORT);
        }
        if (!readerDataHolder.isFixedPageDocument()) {
            disableMenus.add(ReaderMenuAction.ZOOM);
            disableMenus.add(ReaderMenuAction.IMAGE_REFLOW);
            disableMenus.add(ReaderMenuAction.NAVIGATION_COMIC_MODE);
            disableMenus.add(ReaderMenuAction.NAVIGATION_ARTICLE_MODE);
            disableMenus.add(ReaderMenuAction.NAVIGATION_RESET);
            disableMenus.add(ReaderMenuAction.NAVIGATION_MORE_SETTINGS);
            disableMenus.add(ReaderMenuAction.NOTE);
            disableMenus.add(ReaderMenuAction.NAVIGATION);
        } else if (!readerDataHolder.isFlowDocument()) {
            disableMenus.add(ReaderMenuAction.FONT);
        }

        if (!DeviceConfig.sharedInstance(readerDataHolder.getContext()).isSupportBrushPen()) {
            disableMenus.add(ReaderMenuAction.SCRIBBLE_BRUSH);
        }

        if (!Device.detectDevice().isTouchable(readerDataHolder.getContext())){
            disableMenus.add(ReaderMenuAction.ZOOM_BY_RECT);
            disableMenus.add(ReaderMenuAction.NAVIGATION_MORE_SETTINGS);
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
                    case MANUAL_CROP:
                        manualCrop(readerDataHolder);
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
                        startNoteDrawing(readerDataHolder, readerActivity, false);
                        break;
                    case NOTE_EXPORT:
                        showExportDialog(readerDataHolder);
                        break;
                    case NOTE_IMPORT:
                        importScribbleData(readerDataHolder);
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
                    case SLIDESHOW:
                        enterSlideshow(readerDataHolder, readerActivity);
                        break;
                    case FRONT_LIGHT:
                        showBrightnessDialog(readerDataHolder);
                        break;
                    case NATURAL_LIGHT:
                        showNaturalBrightnessDialog(readerDataHolder);
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
                        startNoteDrawing(readerDataHolder, readerActivity, false);
                        break;
                    case EXIT:
                        hideReaderMenu();
                        readerDataHolder.getEventBus().post(new QuitEvent());
                        break;
                    case PREV_CHAPTER:
                        prepareGotoChapter(readerDataHolder, true);
                        break;
                    case NEXT_CHAPTER:
                        prepareGotoChapter(readerDataHolder, false);
                        break;
                }
            }

            @Override
            public void onMenuItemValueChanged(ReaderMenuItem menuItem, Object oldValue, Object newValue) {
                Debug.d("onMenuItemValueChanged: " + menuItem.getAction() + ", " + oldValue + ", " + newValue);
                switch (menuItem.getAction()) {
                    case JUMP_PAGE:
                        gotoPage(readerDataHolder, newValue, true);
                        break;
                }
            }
        });
    }

    private List<ReaderLayerMenuItem> createReaderSideMenuItems(final ReaderDataHolder readerDataHolder, ReaderLayerMenuItem[] menuItems) {
        return ReaderLayerMenuRepository.createFromArray(menuItems, disableMenus);
    }

    private void rotateScreen(final ReaderDataHolder readerDataHolder, int rotationOperation) {
        final ChangeOrientationAction action = new ChangeOrientationAction(DeviceUtils.getScreenOrientation(readerActivity), rotationOperation);
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
        hideReaderMenu();
        final ScaleToPageRequest request = new ScaleToPageRequest(readerDataHolder.getCurrentPageName());
        readerDataHolder.submitRenderRequest(request);
    }

    private void scaleToWidth(final ReaderDataHolder readerDataHolder) {
        final ScaleToWidthRequest request = new ScaleToWidthRequest(readerDataHolder.getCurrentPageName());
        readerDataHolder.submitRenderRequest(request);
    }

    private void cropPage(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        final ScaleToPageCropRequest request = new ScaleToPageCropRequest(readerDataHolder.getCurrentPageName());
        readerDataHolder.submitRenderRequest(request);
    }

    private void cropWidth(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
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
        hideReaderMenu();
        NavigationArgs args = new NavigationArgs();
        RectF limit = new RectF(0, 0, 0, 0);
        args.columnsLeftToRight(NavigationArgs.Type.ALL, 2, 2, limit);
        switchPageNavigationMode(readerDataHolder, args);
    }

    private void switchNavigationToComicMode(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        NavigationArgs args = new NavigationArgs();
        RectF limit = new RectF(0, 0, 0, 0);
        args.rowsRightToLeft(NavigationArgs.Type.ALL, 2, 2, limit);
        switchPageNavigationMode(readerDataHolder, args);
    }

    private void manualCrop(final ReaderDataHolder readerDataHolder) {
        ReaderCropArgs navigationArgs = new ReaderCropArgs();
        navigationArgs.setNavigationMode(ReaderCropArgs.NavigationMode.SINGLE_PAGE_MODE);
        navigationArgs.setCropPageMode(ReaderCropArgs.CropPageMode.MANUAL_CROP_PAGE);
        navigationArgs.setRows(1);
        navigationArgs.setColumns(1);
        new ChangeNavigationSettingsAction(navigationArgs, true).execute(readerDataHolder, null);
    }

    private void switchPageNavigationMode(final ReaderDataHolder readerDataHolder, NavigationArgs args) {
        BaseReaderRequest request = new ChangeLayoutRequest(PageConstants.SINGLE_PAGE_NAVIGATION_LIST, args);
        readerDataHolder.submitRenderRequest(request);
    }

    private void resetNavigationMode(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        BaseReaderRequest request = new ChangeLayoutRequest(PageConstants.SINGLE_PAGE, new NavigationArgs());
        readerDataHolder.submitRenderRequest(request);
    }

    private void showNavigationSettingsDialog(ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        Dialog dlg = new DialogNavigationSettings(readerDataHolder);
        readerDataHolder.trackDialog(dlg);
        dlg.show();
    }

    private void adjustContrast(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        Dialog dialogContrast = new DialogContrast(readerDataHolder);
        readerDataHolder.trackDialog(dialogContrast);
        dialogContrast.show();
    }

    private void adjustEmbolden(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
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
        readerDataHolder.trackDialog(dialog);
        dialog.show();
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

    private void gotoPosition(final ReaderDataHolder readerDataHolder, Object o, final boolean abortPendingTasks) {
        if (o == null) {
            return;
        }
        int page = (int) o;
        new GotoPositionAction(page, abortPendingTasks).execute(readerDataHolder, null);
    }

    private void gotoPage(final ReaderDataHolder readerDataHolder, Object o, final boolean abortPendingTasks) {
        if (o == null) {
            return;
        }
        int page = (int) o;
        new GotoPageAction(page, abortPendingTasks).execute(readerDataHolder);
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
    }

    public static void enterSlideshow(final ReaderDataHolder readerDataHolder, final ReaderActivity readerActivity) {
        hideReaderMenu();
        new SlideshowAction(readerActivity.getMainView()).execute(readerDataHolder, null);
    }

    private void showBrightnessDialog(ReaderDataHolder readerDataHolder){
        hideReaderMenu();
        Dialog dlg = new DialogBrightness(readerDataHolder.getContext());
        dlg.show();
    }

    private void showNaturalBrightnessDialog(ReaderDataHolder readerDataHolder){
        hideReaderMenu();
        Dialog dlg = new DialogNaturalLightBrightness(readerDataHolder.getContext());
        dlg.show();
    }

    private void showExportDialog(ReaderDataHolder readerDataHolder){
        hideReaderMenu();
        Dialog exportDialog = new DialogExport(readerDataHolder);
        readerDataHolder.trackDialog(exportDialog);
        exportDialog.show();
    }

    private void importScribbleData(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        new ImportReaderScribbleAction(readerDataHolder).execute(readerDataHolder, null);
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

    private void showTextStyleDialog(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        final Dialog dialog = new DialogTextStyle(readerDataHolder, new DialogTextStyle.TextStyleCallback() {
            @Override
            public void onSaveReaderStyle(final DialogTextStyle dialogTextStyle, ReaderTextStyle readerStyle) {
                readerDataHolder.removeActiveDialog(dialogTextStyle);
            }

            public void onCancel(final DialogTextStyle dialogTextStyle) {
                readerDataHolder.removeActiveDialog(dialogTextStyle);
            }
        });
        readerDataHolder.trackDialog(dialog);
        dialog.show();
    }

    private void showSearchDialog(final ReaderDataHolder readerDataHolder){
        final Dialog dlg = new DialogSearch(readerDataHolder);
        readerDataHolder.trackDialog(dlg);
        dlg.show();
    }

    private void prepareGotoChapter(final ReaderDataHolder readerDataHolder, final boolean back) {
        List<Integer> tocChapterNodeList = getTocChapterNodeList();
        if (tocChapterNodeList == null) {
            new GetTableOfContentAction().execute(readerDataHolder, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    BaseReaderRequest readerRequest = (BaseReaderRequest) request;
                    ReaderDocumentTableOfContent toc = readerRequest.getReaderUserDataInfo().getTableOfContent();
                    boolean hasToc = toc != null && !toc.isEmpty();
                    if (!hasToc) {
                        Toast.makeText(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.no_chapters), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Integer> readTocChapterNodeList = TocUtils.buildChapterNodeList(toc);
                    setTocChapterNodeList(readTocChapterNodeList);
                    gotoChapter(readerDataHolder, back, readTocChapterNodeList);
                }
            });
        }else {
            gotoChapter(readerDataHolder, back, tocChapterNodeList);
        }
    }

    private void gotoChapter(final ReaderDataHolder readerDataHolder, final boolean back, final List<Integer> tocChapterNodeList) {
        if (tocChapterNodeList.size() <= 0) {
            return;
        }
        int currentPagePosition = PagePositionUtils.getPosition(readerDataHolder.getCurrentPagePosition());
        if (back && currentPagePosition <= tocChapterNodeList.get(0)) {
            Toast.makeText(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.first_chapter), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!back && currentPagePosition >= tocChapterNodeList.get(tocChapterNodeList.size() - 1)) {
            Toast.makeText(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.last_chapter), Toast.LENGTH_SHORT).show();
            return;
        }

        int chapterPosition;
        if (back) {
            chapterPosition = getChapterPositionByPage(currentPagePosition, back, tocChapterNodeList);
        } else {
            chapterPosition = getChapterPositionByPage(currentPagePosition, back, tocChapterNodeList);
        }
        gotoPosition(readerDataHolder, chapterPosition, true);
    }

    private int getChapterPositionByPage(int pagePosition, boolean back, List<Integer> tocChapterNodeList) {
        int size = tocChapterNodeList.size();
        for (int i = 0; i < size; i++) {
            if (pagePosition < tocChapterNodeList.get(i)) {
                if (back) {
                    int index = i - 1;
                    if (index < 0) {
                        return 0;
                    }
                    int position = tocChapterNodeList.get(Math.max(0, index));
                    if (position < pagePosition) {
                        return position;
                    }else {
                        return getChapterPositionByPage(pagePosition - 1, back, tocChapterNodeList);
                    }
                } else {
                    int position = tocChapterNodeList.get(i);
                    if (position > pagePosition) {
                        return position;
                    }else {
                        return getChapterPositionByPage(pagePosition + 1, back, tocChapterNodeList);
                    }
                }

            }
        }

        if (back) {
            return pagePosition - 1;
        } else {
            return pagePosition + 1;
        }

    }

    public static void showTtsDialog(final ReaderDataHolder readerDataHolder){
        hideReaderMenu();
        StartTtsAction action = new StartTtsAction(null);
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

    public static void startNoteDrawing(final ReaderDataHolder readerDataHolder, final ReaderActivity readerActivity, boolean showFullToolbar) {
        hideReaderMenu();
        final ShowScribbleMenuAction menuAction = new ShowScribbleMenuAction(readerActivity.getMainView(),
                getScribbleActionCallback(readerDataHolder),
                disableMenus,
                showFullToolbar);
        ReaderNoteDataInfo noteDataInfo = readerDataHolder.getNoteManager().getNoteDataInfo();
        if (noteDataInfo != null) {
            int currentShapeType = noteDataInfo.getCurrentShapeType();
            menuAction.setSelectShapeAction(createShapeAction(currentShapeType));
        }
        menuAction.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerDataHolder.resetHandlerManager();
            }
        });
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

            @Override
            public void onToggle(final ReaderMenuAction action, boolean expand){
            }
        };
        return callback;
    }

    public static boolean isGroupAction(final ReaderMenuAction action) {
        return (action == ReaderMenuAction.SCRIBBLE_ERASER ||
                action == ReaderMenuAction.SCRIBBLE_WIDTH ||
                action == ReaderMenuAction.SCRIBBLE_SHAPE ||
                action == ReaderMenuAction.SCRIBBLE_COLOR);
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
            case SCRIBBLE_WIDTH2:
            case SCRIBBLE_WIDTH3:
            case SCRIBBLE_WIDTH4:
            case SCRIBBLE_WIDTH5:
                float value = strokeWidthFromMenuId(action);
                useStrokeWidth(readerDataHolder, value);
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
            case SCRIBBLE_BLACK:
                useColor(readerDataHolder, Color.BLACK);
                break;
            case SCRIBBLE_BLUE:
                useColor(readerDataHolder, Color.BLUE);
                break;
            case SCRIBBLE_GREEN:
                useColor(readerDataHolder, Color.GREEN);
                break;
            case SCRIBBLE_MAGENTA:
                useColor(readerDataHolder, Color.MAGENTA);
                break;
            case SCRIBBLE_RED:
                useColor(readerDataHolder, Color.RED);
                break;
            case SCRIBBLE_YELLOW:
                useColor(readerDataHolder, Color.YELLOW);
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

    private static void useColor(final ReaderDataHolder readerDataHolder, int color) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, true, false, false));
        actionChain.addAction(new ChangeNoteColorAction(color));
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

    public static void startErasing(final ReaderDataHolder readerDataHolder) {
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
                String bookName = readerDataHolder.getReader().getBookName();
                if(StringUtils.isNullOrEmpty(bookName)){
                    bookName = readerDataHolder.getReader().getDocumentPath();
                }
                return FileUtils.getFileName(bookName);
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
                return readerDataHolder.isFixedPageDocument() && readerDataHolder.supportScalable();
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


    public static Map<Float, ReaderMenuAction> getStrokeMapping() {
        if (strokeMapping == null) {
            strokeMapping = new HashMap<>();
            strokeMapping.put(NoteModel.getDefaultStrokeWidth(), ReaderMenuAction.SCRIBBLE_WIDTH1);
            strokeMapping.put(4.0f, ReaderMenuAction.SCRIBBLE_WIDTH2);
            strokeMapping.put(6.0f, ReaderMenuAction.SCRIBBLE_WIDTH3);
            strokeMapping.put(8.0f, ReaderMenuAction.SCRIBBLE_WIDTH4);
            strokeMapping.put(10.0f, ReaderMenuAction.SCRIBBLE_WIDTH5);
        }
        return strokeMapping;
    }

    public static float strokeWidthFromMenuId(final ReaderMenuAction menuId) {
        final Map<Float, ReaderMenuAction> map = getStrokeMapping();
        for(Map.Entry<Float, ReaderMenuAction> entry : map.entrySet()) {
            if (entry.getValue() == menuId) {
                return entry.getKey();
            }
        }
        return NoteModel.getDefaultStrokeWidth();
    }

    public static ReaderMenuAction menuIdFromStrokeWidth(final float width) {
        final Map<Float, ReaderMenuAction> map = getStrokeMapping();
        if (map.containsKey(width)) {
            return map.get(width);
        }
        return ReaderMenuAction.SCRIBBLE_WIDTH1;
    }

    public static final ReaderMenuAction createShapeAction(int type) {
        ReaderMenuAction action;
        switch (type) {
            case ShapeFactory.SHAPE_PENCIL_SCRIBBLE:
                action = ReaderMenuAction.SCRIBBLE_PENCIL;
                break;
            case ShapeFactory.SHAPE_BRUSH_SCRIBBLE:
                action = ReaderMenuAction.SCRIBBLE_BRUSH;
                break;
            case ShapeFactory.SHAPE_LINE:
                action = ReaderMenuAction.SCRIBBLE_LINE;
                break;
            case ShapeFactory.SHAPE_TRIANGLE:
                action = ReaderMenuAction.SCRIBBLE_TRIANGLE;
                break;
            case ShapeFactory.SHAPE_CIRCLE:
                action = ReaderMenuAction.SCRIBBLE_CIRCLE;
                break;
            case ShapeFactory.SHAPE_RECTANGLE:
                action = ReaderMenuAction.SCRIBBLE_SQUARE;
                break;
            default:
                action = ReaderMenuAction.SCRIBBLE_PENCIL;
                break;
        }
        return action;
    }

}
