package com.onyx.kreader.ui.actions;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.RectF;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDictionaryInfo;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.ui.data.ReaderLayerMenu;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuItem;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuState;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.dataprovider.compatability.LegacySdkDataUtils;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.request.ChangeLayoutRequest;
import com.onyx.kreader.host.request.ScaleRequest;
import com.onyx.kreader.host.request.ScaleToPageRequest;
import com.onyx.kreader.host.request.ScaleToWidthRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogNavigationSettings;
import com.onyx.kreader.ui.dialog.DialogScreenRefresh;
import com.onyx.kreader.ui.dialog.DialogSearch;
import com.onyx.kreader.ui.dialog.DialogTableOfContent;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.utils.RawResourceUtil;

import java.util.List;

/**
 * Created by Joy on 2016/6/7.
 */
public class ShowReaderMenuAction extends BaseAction {

    public static final String TAG = ShowReaderMenuAction.class.getSimpleName();
    ReaderActivity readerActivity;

    // use reader menu as static field to avoid heavy init of showing reader menu each time
    private static ReaderLayerMenu readerMenu;

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        readerActivity = (ReaderActivity)readerDataHolder.getContext();
        showReaderMenu(readerDataHolder);
    }

    public static void resetReaderMenu(final ReaderActivity readerActivity) {
        readerMenu = null;
    }

    public static boolean isReaderMenuShown() {
        return readerMenu != null && readerMenu.isShown();
    }

    public static void hideReaderMenu() {
//        readerActivity.hideToolbar();
        if (isReaderMenuShown()) {
            readerMenu.hide();
        }
    }

    private void showReaderMenu(final ReaderDataHolder readerDataHolder) {
//        readerActivity.showToolbar();
        ReaderLayerMenuState state = new ReaderLayerMenuState();
        updateReaderMenuState(readerDataHolder, state);
        state.setTitle(FileUtils.getFileName(readerDataHolder.getReader().getDocumentPath()));
        state.setPageCount(readerDataHolder.getPageCount());
        state.setPageIndex(readerDataHolder.getCurrentPage());
        getReaderMenu(readerDataHolder).show(state);
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
        readerMenu = new ReaderLayerMenu(readerActivity);
        updateReaderMenuCallback(readerMenu, readerDataHolder);
        List<ReaderLayerMenuItem> items = createReaderSideMenuItems(readerDataHolder);
        readerMenu.fillItems(items);
    }

    private static void updateReaderMenuState(final ReaderDataHolder readerDataHolder, final ReaderLayerMenuState state) {
        state.setTtsState(getTtsState(readerDataHolder));
    }

    private static ReaderLayerMenuState.TtsState getTtsState(final ReaderDataHolder readerreaderDataHolderctivity) {
        if (readerreaderDataHolderctivity.getTtsManager().isSpeaking()) {
            return ReaderLayerMenuState.TtsState.Speaking;
        } else if (readerreaderDataHolderctivity.getTtsManager().isPaused()) {
            return ReaderLayerMenuState.TtsState.Paused;
        } else {
            return ReaderLayerMenuState.TtsState.Stopped;
        }
    }

    private void updateReaderMenuCallback(final ReaderMenu menu, final ReaderDataHolder readerDataHolder) {
        menu.setReaderMenuCallback(new ReaderMenu.ReaderMenuCallback() {
            @Override
            public void onHideMenu() {
                hideReaderMenu();
            }

            @Override
            public void onMenuItemClicked(ReaderMenuItem menuItem) {
                Log.d(TAG, "onMenuItemClicked: " + menuItem.getURI().getRawPath());
                switch (menuItem.getURI().getRawPath()) {
                    case "/Rotation/Rotation0":
                        rotateScreen(readerDataHolder, 0);
                        break;
                    case "/Rotation/Rotation90":
                        rotateScreen(readerDataHolder, 90);
                        break;
                    case "/Rotation/Rotation180":
                        rotateScreen(readerDataHolder, 180);
                        break;
                    case "/Rotation/Rotation270":
                        rotateScreen(readerDataHolder, 270);
                        break;
                    case "/Zoom/ZoomIn":
                        scaleUp(readerDataHolder);
                        break;
                    case "/Zoom/ZoomOut":
                        scaleDown(readerDataHolder);
                        break;
                    case "/Zoom/ToPage":
                        scaleToPage(readerDataHolder);
                        break;
                    case "/Zoom/ToWidth":
                        scaleToWidth(readerDataHolder);
                        break;
                    case "/Zoom/ByRect":
                        scaleByRect(readerDataHolder);
                        break;
                    case "/Zoom/Crop":
                        scaleByAutoCrop(readerDataHolder);
                        break;
                    case "/Navigation/ArticleMode":
                        switchNavigationToArticleMode(readerDataHolder);
                        break;
                    case "/Navigation/ComicMode":
                        switchNavigationToComicMode(readerDataHolder);
                        break;
                    case "/Navigation/Reset":
                        resetNavigationMode(readerDataHolder);
                        break;
                    case "/Navigation/MoreSetting":
                        showNavigationSettingsDialog(readerDataHolder);
                        break;
                    case "/Spacing/DecreaseSpacing":
                        break;
                    case "/Spacing/EnlargeSpacing":
                        break;
                    case "/Spacing/NormalSpacing":
                        break;
                    case "/Spacing/SmallSpacing":
                        break;
                    case "/Spacing/LargeSpacing":
                        break;
                    case "/Spacing/Indent":
                        break;
                    case "/Font/DecreaseSpacing":
                        break;
                    case "/Font/IncreaseSpacing":
                        break;
                    case "/Font/Gamma":
                        adjustContrast(readerDataHolder);
                        break;
                    case "/Font/Embolden":
                        adjustEmbolden(readerDataHolder);
                        break;
                    case "/Font/FontReflow":
                        imageReflow(readerDataHolder);
                        break;
                    case "/Directory/TOC":
                        showTocDialog(readerDataHolder, DialogTableOfContent.DirectoryTab.TOC);
                        break;
                    case "/Directory/Bookmark":
                        showTocDialog(readerDataHolder, DialogTableOfContent.DirectoryTab.Bookmark);
                        break;
                    case "/Directory/Note":
                        showTocDialog(readerDataHolder, DialogTableOfContent.DirectoryTab.Annotation);
                        break;
                    case "/Directory/ShapeModel":
                        break;
                    case "/Directory/Export":
                        break;
                    case "/TTS/Play":
                        ttsPlay(readerDataHolder);
                        break;
                    case "/TTS/Pause":
                        ttsPause(readerDataHolder);
                        break;
                    case "/TTS/Stop":
                        ttsStop(readerDataHolder);
                        break;
                    case "/More/shape":
                        startShapeDrawing(readerDataHolder);
                        break;
                    case "/GotoPage":
                        gotoPage(readerDataHolder);
                        break;
                    case "/SetScreenRefreshRate":
                        showScreenRefreshDialog(readerActivity);
                        break;
                    case "/StartDictApp":
                        startDictionaryApp(readerActivity);
                        break;
                    case "/Search":
                        showSearchDialog(readerDataHolder);
                        break;
                    case "/Exit":
                        readerActivity.onBackPressed();
                        break;
                }
            }

            @Override
            public void onMenuItemValueChanged(ReaderMenuItem menuItem, Object oldValue, Object newValue) {
                Debug.d("onMenuItemValueChanged: " + menuItem.getURI().getRawPath() + ", " + oldValue + ", " + newValue);
            }
        });
    }

    private List<ReaderLayerMenuItem> createReaderSideMenuItems(final ReaderDataHolder readerDataHolder) {
        JSONObject json = JSON.parseObject(RawResourceUtil.contentOfRawResource(readerDataHolder.getContext(), R.raw.reader_menu_fixed_page));
        JSONArray array = json.getJSONArray("menu_list");
        return ReaderLayerMenuItem.createFromJSON(readerDataHolder.getContext(), array);
    }

    private void rotateScreen(final ReaderDataHolder readerDataHolder, int rotationOperation) {
        final ChangeOrientationAction action = new ChangeOrientationAction(rotationOperation);
        action.execute(readerDataHolder);
    }

    private void scaleUp(final ReaderDataHolder readerDataHolder) {
        final ChangeScaleWithDeltaAction action = new ChangeScaleWithDeltaAction(0.1f);
        action.execute(readerDataHolder);
    }

    private void scaleDown(final ReaderDataHolder readerDataHolder) {
        final ChangeScaleWithDeltaAction action = new ChangeScaleWithDeltaAction(-0.1f);
        action.execute(readerDataHolder);
    }

    private void scaleByValue(final ReaderDataHolder readerDataHolder, float scale) {
        final ScaleRequest request = new ScaleRequest(readerDataHolder.getCurrentPageName(), scale, readerDataHolder.getDisplayWidth() / 2, readerDataHolder.getDisplayHeight() / 2);
        readerDataHolder.submitRequest(request);
    }

    private void scaleToPage(final ReaderDataHolder readerDataHolder) {
        final ScaleToPageRequest request = new ScaleToPageRequest(readerDataHolder.getCurrentPageName());
        readerDataHolder.submitRequest(request);
    }

    private void scaleToWidth(final ReaderDataHolder readerDataHolder) {
        final ScaleToWidthRequest request = new ScaleToWidthRequest(readerDataHolder.getCurrentPageName());
        readerDataHolder.submitRequest(request);
    }

    private void scaleByRect(final ReaderDataHolder readerDataHolder) {
        final SelectionScaleAction action = new SelectionScaleAction();
        action.execute(readerDataHolder);
    }

    private void scaleByAutoCrop(final ReaderDataHolder readerDataHolder) {
        final PageCropAction action = new PageCropAction(readerDataHolder.getCurrentPageName());
        action.execute(readerDataHolder);
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
        readerDataHolder.submitRequest(request);
    }

    private void resetNavigationMode(final ReaderDataHolder readerDataHolder) {
        BaseReaderRequest request = new ChangeLayoutRequest(PageConstants.SINGLE_PAGE, new NavigationArgs());
        readerDataHolder.submitRequest(request);
    }

    private void showNavigationSettingsDialog(ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        DialogNavigationSettings dlg = new DialogNavigationSettings(readerDataHolder);
        dlg.show();
    }

    private void adjustContrast(final ReaderDataHolder readerDataHolder) {
        final AdjustContrastAction action = new AdjustContrastAction();
        action.execute(readerDataHolder);
    }

    private void adjustEmbolden(final ReaderDataHolder readerDataHolder) {
        final EmboldenAction action = new EmboldenAction();
        action.execute(readerDataHolder);
    }

    private void imageReflow(final ReaderDataHolder readerDataHolder) {
        final ImageReflowAction action = new ImageReflowAction();
        action.execute(readerDataHolder);
    }

    private void showTocDialog(final ReaderDataHolder readerDataHolder, DialogTableOfContent.DirectoryTab tab) {
        final GetTableOfContentAction action = new GetTableOfContentAction(tab);
        action.execute(readerDataHolder);
    }

    private void ttsPlay(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.TTS_PROVIDER);
        readerDataHolder.submitRequest(new ScaleToPageRequest(readerDataHolder.getCurrentPageName()), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerDataHolder.getTtsManager().play();
            }
        });
    }

    private void ttsPause(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.getTtsManager().pause();
    }

    private void ttsStop(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.getTtsManager().stop();
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.BASE_PROVIDER);
    }

    private void startShapeDrawing(final ReaderDataHolder readerDataHolder) {
        // get current page and start rendering.
        readerActivity.getHandlerManager().setActiveProvider(HandlerManager.SCRIBBLE_PROVIDER);
        ReaderDeviceManager.startScreenHandWriting(readerActivity.getSurfaceView());
    }

    private void gotoPage(final ReaderDataHolder readerDataHolder) {
        hideReaderMenu();
        new ShowQuickPreviewAction().execute(readerDataHolder);
    }

    private void showScreenRefreshDialog(final ReaderActivity readerActivity) {
        DialogScreenRefresh dlg = new DialogScreenRefresh();
        dlg.setListener(new DialogScreenRefresh.onScreenRefreshChangedListener() {
            @Override
            public void onRefreshIntervalChanged(int oldValue, int newValue) {
                LegacySdkDataUtils.setScreenUpdateGCInterval(readerActivity, newValue);
                ReaderDeviceManager.setGcInterval(newValue);
            }
        });
        dlg.show(readerActivity.getFragmentManager());
    }

    private boolean startDictionaryApp(final ReaderActivity readerActivity) {
        OnyxDictionaryInfo info = LegacySdkDataUtils.getDictionary(readerActivity);
        if (info == null) {
            Toast.makeText(readerActivity, R.string.did_not_find_the_dictionary, Toast.LENGTH_LONG).show();
            return false;
        }
        Intent intent = new Intent(info.action).setComponent(new ComponentName(info.packageName, info.className));
        try {
            readerActivity.startActivity(intent);
        } catch ( ActivityNotFoundException e ) {
            Toast.makeText(readerActivity, R.string.did_not_find_the_dictionary, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void showSearchDialog(final ReaderDataHolder readerDataHolder){
        DialogSearch dialogSearch = new DialogSearch(readerDataHolder);
        dialogSearch.show();
    }
}
