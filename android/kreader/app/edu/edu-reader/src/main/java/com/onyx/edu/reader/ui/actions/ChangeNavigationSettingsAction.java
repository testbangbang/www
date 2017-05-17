package com.onyx.edu.reader.ui.actions;

import android.graphics.RectF;

import com.onyx.android.cropimage.data.CropArgs;
import com.onyx.android.sdk.data.ReaderPointMatrix;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.host.request.ChangeLayoutRequest;
import com.onyx.android.sdk.reader.host.request.GotoPositionRequest;
import com.onyx.android.sdk.reader.host.request.ScaleToPageCropRequest;
import com.onyx.android.sdk.reader.host.request.ScaleToPageRequest;
import com.onyx.edu.reader.ui.data.ReaderCropArgs;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

/**
 * Created by joy on 7/12/16.
 */
public class ChangeNavigationSettingsAction extends BaseAction {

    public enum CropByOddAndEventStep { First, Second }

    private ReaderCropArgs cropArgs;
    private CropByOddAndEventStep step = CropByOddAndEventStep.First;
    private boolean autoCropForEachBlock;

    public ChangeNavigationSettingsAction(ReaderCropArgs args, boolean autoCropForEachBlock) {
        this.cropArgs = args;
        this.autoCropForEachBlock = autoCropForEachBlock;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        setupNavigationSettings(readerDataHolder, readerDataHolder.getCurrentPage());
        BaseCallback.invoke(callback, null, null);
    }

    private void setupNavigationSettings(final ReaderDataHolder readerDataHolder, final int page) {
        int scale = PageConstants.SCALE_TO_PAGE;
        if (cropArgs.getCropPageMode() == ReaderCropArgs.CropPageMode.AUTO_CROP_PAGE) {
            scale = PageConstants.SCALE_TO_PAGE_CONTENT;
        }
        scalePage(readerDataHolder, page, scale);
    }

    private void scalePage(final ReaderDataHolder readerDataHolder, final int page, final int scale) {
        final BaseReaderRequest scaleRequest;
        if (scale == PageConstants.SCALE_TO_PAGE_CONTENT) {
            scaleRequest = new ScaleToPageCropRequest(PagePositionUtils.fromPageNumber(page));
        } else {
            scaleRequest = new ScaleToPageRequest(PagePositionUtils.fromPageNumber(page));
        }

        readerDataHolder.submitRenderRequest(scaleRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (cropArgs.getCropPageMode() == ReaderCropArgs.CropPageMode.AUTO_CROP_PAGE &&
                        cropArgs.getNavigationMode() == ReaderCropArgs.NavigationMode.SINGLE_PAGE_MODE) {
                    return;
                }
                int page = PagePositionUtils.getPageNumber(scaleRequest.getReaderViewInfo().getFirstVisiblePage().getName());
                cropAndSplitPage(readerDataHolder, scaleRequest, page);
            }
        });
    }

    private void cropAndSplitPage(final ReaderDataHolder readerDataHolder, final BaseReaderRequest request, final int currentPage) {
        if (isManualCropPageByOddAndEven() && step == CropByOddAndEventStep.First) {
            initNavigationArgsForOddAndEvenCrop();
        }
        new SelectionScaleAction(cropArgs.getCropArgs(), new SelectionScaleAction.Callback() {
            @Override
            public void onSelectionFinished(ReaderDataHolder dataHolder, CropArgs args) {
                Debug.d("cropping page: " + currentPage);
                if (isManualCropPageByOddAndEven()) {
                    int index = currentPage % 2;
                    cropArgs.getManualCropDocRegions().set(index, new RectF(args.selectionRect));
                    cropArgs.getManualPointMatrixList().set(index, args.pointMatrixList);

                    if (step == CropByOddAndEventStep.First) {
                        step = CropByOddAndEventStep.Second;
                        cropOddAndEventPageNextStep(dataHolder, dataHolder.getCurrentPage());
                        return;
                    }
                } else {
                    cropArgs.getManualCropDocRegions().add(new RectF(args.selectionRect));
                    cropArgs.getManualPointMatrixList().add(args.pointMatrixList);
                }

                NavigationArgs navigationArgs = new NavigationArgs();
                navigationArgs.setAutoCropForEachBlock(autoCropForEachBlock);
                buildNavigationArgs(dataHolder, request, navigationArgs);
                dataHolder.submitRenderRequest(new ChangeLayoutRequest(PageConstants.SINGLE_PAGE_NAVIGATION_LIST, navigationArgs));
            }
        }).execute(readerDataHolder, null);
    }

    private void buildNavigationArgs(final ReaderDataHolder readerDataHolder, final BaseReaderRequest request, final NavigationArgs navigationArgs) {
        RectF defaultLimit = getNavigationArgsLimitRect(readerDataHolder, request, cropArgs.getManualCropDocRegions().get(0));
        ReaderPointMatrix defaultMatrix = cropArgs.getManualPointMatrixList().get(0);
        RectF oddLimit = null;
        ReaderPointMatrix oddMatrix = null;
        if (isManualCropPageByOddAndEven()) {
            oddLimit = getNavigationArgsLimitRect(readerDataHolder, request, cropArgs.getManualCropDocRegions().get(1));
            oddMatrix = cropArgs.getManualPointMatrixList().get(1);
        }
        switch (cropArgs.getNavigationMode()) {
            case SINGLE_PAGE_MODE:
                if (isManualCropPageByOddAndEven()) {
                    navigationArgs.columnsLeftToRight(NavigationArgs.Type.EVEN, defaultMatrix, defaultLimit);
                    navigationArgs.columnsLeftToRight(NavigationArgs.Type.ODD, oddMatrix, oddLimit);
                } else {
                    navigationArgs.columnsLeftToRight(NavigationArgs.Type.ALL, defaultMatrix, defaultLimit);
                }
                break;
            case ROWS_LEFT_TO_RIGHT_MODE:
                if (isManualCropPageByOddAndEven()) {
                    navigationArgs.rowsLeftToRight(NavigationArgs.Type.EVEN, defaultMatrix, defaultLimit);
                    navigationArgs.rowsLeftToRight(NavigationArgs.Type.ODD, oddMatrix, oddLimit);
                } else {
                    navigationArgs.rowsLeftToRight(NavigationArgs.Type.ALL, defaultMatrix, defaultLimit);
                }
                break;
            case ROWS_RIGHT_TO_LEFT_MODE:
                if (isManualCropPageByOddAndEven()) {
                    navigationArgs.rowsRightToLeft(NavigationArgs.Type.EVEN, defaultMatrix, defaultLimit);
                    navigationArgs.rowsRightToLeft(NavigationArgs.Type.ODD, oddMatrix, oddLimit);
                } else {
                    navigationArgs.rowsRightToLeft(NavigationArgs.Type.ALL, defaultMatrix, defaultLimit);
                }
                break;
            case COLUMNS_LEFT_TO_RIGHT_MODE:
                if (isManualCropPageByOddAndEven()) {
                    navigationArgs.columnsLeftToRight(NavigationArgs.Type.EVEN, defaultMatrix, defaultLimit);
                    navigationArgs.columnsLeftToRight(NavigationArgs.Type.ODD, oddMatrix, oddLimit);
                } else {
                    navigationArgs.columnsLeftToRight(NavigationArgs.Type.ALL, defaultMatrix, defaultLimit);
                }
                break;
            case COLUMNS_RIGHT_TO_LEFT_MODE:
                if (isManualCropPageByOddAndEven()) {
                    navigationArgs.columnsRightToLeft(NavigationArgs.Type.EVEN, defaultMatrix, defaultLimit);
                    navigationArgs.columnsRightToLeft(NavigationArgs.Type.ODD, oddMatrix, oddLimit);
                } else {
                    navigationArgs.columnsRightToLeft(NavigationArgs.Type.ALL, defaultMatrix, defaultLimit);
                }
                break;
        }
    }

    private RectF getNavigationArgsLimitRect(final ReaderDataHolder readerDataHolder, final BaseReaderRequest request, final RectF selectionRect) {
        RectF docRect = PageUtils.translateToDocument(request.getReaderViewInfo().getFirstVisiblePage(), selectionRect);
        return new RectF(docRect.left / request.getReaderViewInfo().getFirstVisiblePage().getOriginWidth(),
                docRect.top / request.getReaderViewInfo().getFirstVisiblePage().getOriginHeight(),
                docRect.right / request.getReaderViewInfo().getFirstVisiblePage().getOriginWidth(),
                docRect.bottom / request.getReaderViewInfo().getFirstVisiblePage().getOriginHeight());
    }

    private void cropOddAndEventPageNextStep(final ReaderDataHolder readerDataHolder, final int currentPage) {
        final int nextPage;
        if (isLastPage(readerDataHolder, currentPage)) {
            nextPage = Math.max(currentPage - 1, 0);
        } else {
            nextPage = currentPage + 1;
        }
        BaseReaderRequest request = new GotoPositionRequest(PagePositionUtils.fromPageNumber(nextPage));
        readerDataHolder.submitNonRenderRequest(request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                setupNavigationSettings(readerDataHolder, nextPage);
            }
        });
    }

    private boolean isManualCropPageByOddAndEven() {
        return cropArgs.getCropPageMode() == ReaderCropArgs.CropPageMode.MANUAL_CROP_PAGE_BY_ODD_AND_EVEN;
    }

    private void initNavigationArgsForOddAndEvenCrop() {
        cropArgs.getManualCropDocRegions().add(new RectF());
        cropArgs.getManualCropDocRegions().add(new RectF());
        cropArgs.getManualPointMatrixList().add(new ReaderPointMatrix());
        cropArgs.getManualPointMatrixList().add(new ReaderPointMatrix());
    }

    private boolean isLastPage(final ReaderDataHolder readerDataHolder, final int page) {
        return page >= readerDataHolder.getPageCount() - 1;
    }

}
