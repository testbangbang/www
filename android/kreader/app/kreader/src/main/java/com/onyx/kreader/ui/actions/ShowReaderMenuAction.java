package com.onyx.kreader.ui.actions;

import android.graphics.RectF;
import android.util.Log;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.ui.data.ReaderLayerMenu;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuItem;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuState;
import com.onyx.kreader.R;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.kreader.host.request.ChangeLayoutRequest;
import com.onyx.kreader.host.request.ScaleRequest;
import com.onyx.kreader.host.request.ScaleToPageRequest;
import com.onyx.kreader.host.request.ScaleToWidthRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.dialog.DialogNavigationSettings;
import com.onyx.kreader.ui.dialog.DialogTableOfContent;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.kreader.utils.RawResourceUtil;

import java.util.List;

/**
 * Created by Joy on 2016/6/7.
 */
public class ShowReaderMenuAction extends BaseAction {

    public static final String TAG = ShowReaderMenuAction.class.getSimpleName();

    // use reader menu as static field to avoid heavy init of showing reader menu each time
    private static ReaderLayerMenu readerMenu;

    @Override
    public void execute(ReaderActivity readerActivity) {
        showReaderMenu(readerActivity);
    }

    public static void resetReaderMenu() {
        readerMenu = null;
    }

    public static boolean isReaderMenuShown() {
        return readerMenu != null && readerMenu.isShown();
    }

    public static void hideReaderMenu(final ReaderActivity readerActivity) {
//        readerActivity.hideToolbar();
        if (isReaderMenuShown()) {
            readerMenu.hide();
        }
    }

    private void showReaderMenu(final ReaderActivity readerActivity) {
//        readerActivity.showToolbar();
        getReaderMenu(readerActivity).show(new ReaderLayerMenuState());
    }

    private ReaderMenu getReaderMenu(final ReaderActivity readerActivity) {
        if (readerMenu == null) {
            initReaderMenu(readerActivity);
        }
        return readerMenu;
    }

    private void initReaderMenu(final ReaderActivity readerActivity) {
        LinearLayout layout = (LinearLayout)readerActivity.findViewById(R.id.left_drawer);
        createReaderSideMenu(readerActivity, layout);
    }

    private void createReaderSideMenu(final ReaderActivity readerActivity, LinearLayout drawerLayout) {
        readerMenu = new ReaderLayerMenu(readerActivity);
        updateReaderMenuCallback(readerMenu, readerActivity);
        List<ReaderLayerMenuItem> items = createReaderSideMenuItems(readerActivity);
        readerMenu.fillItems(items);
    }

    private void updateReaderMenuCallback(final ReaderMenu menu, final ReaderActivity readerActivity) {
        menu.setReaderMenuCallback(new ReaderMenu.ReaderMenuCallback() {
            @Override
            public void onHideMenu() {
                hideReaderMenu(readerActivity);
            }

            @Override
            public void onMenuItemClicked(ReaderMenuItem menuItem) {
                Log.d(TAG, "onMenuItemClicked: " + menuItem.getURI().getRawPath());
                switch (menuItem.getURI().getRawPath()) {
                    case "/Rotation/Rotation0":
                        rotateScreen(readerActivity, 0);
                        break;
                    case "/Rotation/Rotation90":
                        rotateScreen(readerActivity, 90);
                        break;
                    case "/Rotation/Rotation180":
                        rotateScreen(readerActivity, 180);
                        break;
                    case "/Rotation/Rotation270":
                        rotateScreen(readerActivity, 270);
                        break;
                    case "/Zoom/ZoomIn":
                        scaleUp(readerActivity);
                        break;
                    case "/Zoom/ZoomOut":
                        scaleDown(readerActivity);
                        break;
                    case "/Zoom/ToPage":
                        scaleToPage(readerActivity);
                        break;
                    case "/Zoom/ToWidth":
                        scaleToWidth(readerActivity);
                        break;
                    case "/Zoom/ByRect":
                        scaleByRect(readerActivity);
                        break;
                    case "/Zoom/Crop":
                        scaleByAutoCrop(readerActivity);
                        break;
                    case "/Navigation/ArticleMode":
                        switchNavigationToArticleMode(readerActivity);
                        break;
                    case "/Navigation/ComicMode":
                        switchNavigationToComicMode(readerActivity);
                        break;
                    case "/Navigation/Reset":
                        resetNavigationMode(readerActivity);
                        break;
                    case "/Navigation/MoreSetting":
                        showNavigationSettingsDialog(readerActivity);
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
                        adjustContrast(readerActivity);
                        break;
                    case "/Font/Embolden":
                        adjustEmbolden(readerActivity);
                        break;
                    case "/Font/FontReflow":
                        imageReflow(readerActivity);
                        break;
                    case "/Directory/TOC":
                        showTocDialog(readerActivity, DialogTableOfContent.DirectoryTab.TOC);
                        break;
                    case "/Directory/Bookmark":
                        showTocDialog(readerActivity, DialogTableOfContent.DirectoryTab.Bookmark);
                        break;
                    case "/Directory/Note":
                        showTocDialog(readerActivity, DialogTableOfContent.DirectoryTab.Annotation);
                        break;
                    case "/Directory/ShapeModel":
                        break;
                    case "/Directory/Export":
                        break;
                    case "/More/shape":
                        startShapeDrawing(readerActivity);
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

    private List<ReaderLayerMenuItem> createReaderSideMenuItems(final ReaderActivity readerActivity) {
        JSONObject json = JSON.parseObject(RawResourceUtil.contentOfRawResource(readerActivity, R.raw.reader_menu));
        JSONArray array = json.getJSONArray("menu_list");
        return ReaderLayerMenuItem.createFromJSON(readerActivity, array);
    }

    private void rotateScreen(final ReaderActivity readerActivity, int rotationOperation) {
        final ChangeOrientationAction action = new ChangeOrientationAction(rotationOperation);
        action.execute(readerActivity);
    }

    private void scaleUp(final ReaderActivity readerActivity) {
        final ChangeScaleWithDeltaAction action = new ChangeScaleWithDeltaAction(0.1f);
        action.execute(readerActivity);
    }

    private void scaleDown(final ReaderActivity readerActivity) {
        final ChangeScaleWithDeltaAction action = new ChangeScaleWithDeltaAction(-0.1f);
        action.execute(readerActivity);
    }

    private void scaleByValue(final ReaderActivity readerActivity, float scale) {
        final ScaleRequest request = new ScaleRequest(readerActivity.getCurrentPageName(), scale, readerActivity.getDisplayWidth() / 2, readerActivity.getDisplayHeight() / 2);
        readerActivity.submitRequest(request);
    }

    private void scaleToPage(final ReaderActivity readerActivity) {
        final ScaleToPageRequest request = new ScaleToPageRequest(readerActivity.getCurrentPageName());
        readerActivity.submitRequest(request);
    }

    private void scaleToWidth(final ReaderActivity readerActivity) {
        final ScaleToWidthRequest request = new ScaleToWidthRequest(readerActivity.getCurrentPageName());
        readerActivity.submitRequest(request);
    }

    private void scaleByRect(final ReaderActivity readerActivity) {
        final SelectionScaleAction action = new SelectionScaleAction();
        action.execute(readerActivity);
    }

    private void scaleByAutoCrop(final ReaderActivity readerActivity) {
        final PageCropAction action = new PageCropAction(readerActivity.getCurrentPageName());
        action.execute(readerActivity);
    }

    private void switchNavigationToArticleMode(final ReaderActivity readerActivity) {
        NavigationArgs args = new NavigationArgs();
        RectF limit = new RectF(0, 0, 0, 0);
        args.columnsLeftToRight(NavigationArgs.Type.ALL, 2, 2, limit);
        switchPageNavigationMode(readerActivity, args);
    }

    private void switchNavigationToComicMode(final ReaderActivity readerActivity) {
        NavigationArgs args = new NavigationArgs();
        RectF limit = new RectF(0, 0, 0, 0);
        args.rowsRightToLeft(NavigationArgs.Type.ALL, 2, 2, limit);
        switchPageNavigationMode(readerActivity, args);
    }

    private void switchPageNavigationMode(final ReaderActivity readerActivity, NavigationArgs args) {
        BaseReaderRequest request = new ChangeLayoutRequest(PageConstants.SINGLE_PAGE_NAVIGATION_LIST, args);
        readerActivity.submitRequest(request);
    }

    private void resetNavigationMode(final ReaderActivity readerActivity) {
        BaseReaderRequest request = new ChangeLayoutRequest(PageConstants.SINGLE_PAGE, new NavigationArgs());
        readerActivity.submitRequest(request);
    }

    private void showNavigationSettingsDialog(ReaderActivity readerActivity) {
        hideReaderMenu(readerActivity);
        DialogNavigationSettings dlg = new DialogNavigationSettings(readerActivity);
        dlg.show();
    }

    private void adjustContrast(final ReaderActivity readerActivity) {
        final AdjustContrastAction action = new AdjustContrastAction();
        action.execute(readerActivity);
    }

    private void adjustEmbolden(final ReaderActivity readerActivity) {
        final EmboldenAction action = new EmboldenAction();
        action.execute(readerActivity);
    }

    private void imageReflow(final ReaderActivity readerActivity) {
        final ImageReflowAction action = new ImageReflowAction();
        action.execute(readerActivity);
    }

    private void showTocDialog(final ReaderActivity readerActivity, DialogTableOfContent.DirectoryTab tab) {
        final GetTableOfContentAction action = new GetTableOfContentAction(tab);
        action.execute(readerActivity);
    }

    private void startShapeDrawing(final ReaderActivity readerActivity) {
        // get current page and start rendering.
        readerActivity.getHandlerManager().setActiveProvider(HandlerManager.SCRIBBLE_PROVIDER);
        ReaderDeviceManager.startScreenHandWriting(readerActivity.getSurfaceView());
    }
}
