package com.onyx.kreader.ui.actions;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.host.request.LoadDocumentOptionsRequest;
import com.onyx.android.sdk.reader.host.request.RestoreRequest;
import com.onyx.android.sdk.utils.LocaleUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.CreateViewRequest;
import com.onyx.kreader.device.DeviceConfig;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.events.OpenDocumentFailedEvent;
import com.onyx.android.sdk.reader.host.request.OpenRequest;
import com.onyx.android.sdk.reader.host.request.SaveDocumentOptionsRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogLoading;
import com.onyx.kreader.ui.dialog.DialogMessage;
import com.onyx.kreader.ui.dialog.DialogPassword;
import com.onyx.kreader.ui.events.BeforeDocumentOpenEvent;
import com.onyx.kreader.ui.events.ChangeOrientationEvent;
import com.onyx.android.sdk.utils.DeviceUtils;

/**
 * Created by zhuzeng on 5/17/16.
 * steps:
 * 1. loadDocument document options.
 * 2. open with options.
 * 3. create view
 * 4. restoreWithOptions.
 */
public class OpenDocumentAction extends BaseAction {
    private Activity activity;
    private String documentPath;
    private DataManager dataProvider;
    private boolean canceled = false;
    private boolean processOrientation = false;

    public OpenDocumentAction(final Activity activity, final String path) {
        this.activity = activity;
        documentPath = path;
        dataProvider = new DataManager();
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        if (!readerDataHolder.isDocumentOpened()) {
            openDocumentImpl(readerDataHolder, callback);
            return;
        }

        readerDataHolder.destroy(new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    showErrorDialog(readerDataHolder);
                    return;
                }
                openDocumentImpl(readerDataHolder, callback);
            }
        });
    }

    private void openDocumentImpl(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.initReaderFromPath(documentPath);
        readerDataHolder.getEventBus().post(new BeforeDocumentOpenEvent(documentPath));

        //LoadingDialog shows only after the decorview is drawn,preventing the dialog from swinging.
        activity.getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                showLoadingDialog(readerDataHolder, R.string.loading_document, new DialogLoading.Callback() {
                    @Override
                    public void onCanceled() {
                        canceled = true;
                        cleanup(readerDataHolder);
                    }
                });
            }
        });
        final LoadDocumentOptionsRequest loadDocumentOptionsRequest = new LoadDocumentOptionsRequest(documentPath,
                readerDataHolder.getReader().getDocumentMd5());
        dataProvider.submit(readerDataHolder.getContext(), loadDocumentOptionsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || canceled) {
                    cleanup(readerDataHolder);
                    return;
                }
                // ignore document's orientation temporary for multi-document
//                if (!processOrientation(readerDataHolder, loadDocumentOptionsRequest.getDocumentOptions())) {
//                    return;
//                }

                if (processOrientation) {
                    if (!processOrientation(readerDataHolder, loadDocumentOptionsRequest.getDocumentOptions())) {
                        return;
                    }
                }
                BaseOptions baseOptions = loadDocumentOptionsRequest.getDocumentOptions();
                adjustOptionsWithDeviceConfig(baseOptions, readerDataHolder);
                openWithOptions(readerDataHolder, loadDocumentOptionsRequest.getDocumentOptions());
            }
        });
    }

    private void adjustOptionsWithDeviceConfig(final BaseOptions baseOptions, final ReaderDataHolder readerDataHolder) {
        BaseOptions.setGlobalDefaultGamma(DeviceConfig.sharedInstance(readerDataHolder.getContext()).getDefaultGamma());
        BaseOptions.setGlobalDefaultTextGamma(DeviceConfig.sharedInstance(readerDataHolder.getContext()).getDefaultTextGamma());
        if (DeviceConfig.sharedInstance(readerDataHolder.getContext()).getFixedGamma() > 0) {
            baseOptions.setGamma(DeviceConfig.sharedInstance(readerDataHolder.getContext()).getFixedGamma());
        }
        ReaderTextStyle.setDefaultFontSizes(DeviceConfig.sharedInstance(readerDataHolder.getContext()).getDefaultFontSizes());

        adjustFontFace(readerDataHolder, baseOptions);
        adjustFontSize(readerDataHolder, baseOptions);
        adjustLineSpacing(readerDataHolder, baseOptions);
        adjustLeftMargin(readerDataHolder, baseOptions);
        adjustTopMargin(readerDataHolder, baseOptions);
        adjustRightMargin(readerDataHolder, baseOptions);
        adjustBottomMargin(readerDataHolder, baseOptions);
    }

    private void adjustFontFace(final ReaderDataHolder readerDataHolder, final BaseOptions baseOptions) {
        String fontFace = baseOptions.getFontFace();
        if (StringUtils.isNullOrEmpty(fontFace) && LocaleUtils.isChinese()) {
            fontFace = DeviceConfig.sharedInstance(readerDataHolder.getContext()).getDefaultFontFileForChinese();
        }
        baseOptions.setFontFace(fontFace);
    }

    private void adjustFontSize(final ReaderDataHolder readerDataHolder, final BaseOptions baseOptions) {
        float fontSize = baseOptions.getFontSize();
        if (fontSize == BaseOptions.INVALID_FLOAT_VALUE) {
            int index = DeviceConfig.sharedInstance(readerDataHolder.getContext()).getDefaultFontSizeIndex();
            fontSize = ReaderTextStyle.getFontSizeByIndex(index).getValue();
            fontSize = SingletonSharedPreference.getLastFontSize(fontSize);
        }
        baseOptions.setFontSize(fontSize);
    }

    private void adjustLineSpacing(final ReaderDataHolder readerDataHolder, final BaseOptions baseOptions) {
        int lineSpacing = baseOptions.getLineSpacing();
        if (lineSpacing == BaseOptions.INVALID_INT_VALUE) {
            int index = DeviceConfig.sharedInstance(readerDataHolder.getContext()).getDefaultLineSpacingIndex();
            lineSpacing = ReaderTextStyle.getLineSpacingByIndex(index).getPercent();
            lineSpacing = SingletonSharedPreference.getLastLineSpacing(lineSpacing);
        }
        baseOptions.setLineSpacing(lineSpacing);
    }

    private void adjustLeftMargin(final ReaderDataHolder readerDataHolder, final BaseOptions baseOptions) {
        int leftMargin = baseOptions.getLeftMargin();
        if (leftMargin == BaseOptions.INVALID_INT_VALUE) {
            leftMargin = getDefaultPageMargin(readerDataHolder.getContext()).getLeftMargin().getPercent();
            leftMargin = SingletonSharedPreference.getLastLeftMargin(leftMargin);
        }
        baseOptions.setLeftMargin(leftMargin);
    }

    private void adjustTopMargin(final ReaderDataHolder readerDataHolder, final BaseOptions baseOptions) {
        int topMargin = baseOptions.getTopMargin();
        if (topMargin == BaseOptions.INVALID_INT_VALUE) {
            topMargin = getDefaultPageMargin(readerDataHolder.getContext()).getTopMargin().getPercent();
            topMargin = SingletonSharedPreference.getLastTopMargin(topMargin);
        }
        baseOptions.setTopMargin(topMargin);
    }

    private void adjustRightMargin(final ReaderDataHolder readerDataHolder, final BaseOptions baseOptions) {
        int rightMargin = baseOptions.getRightMargin();
        if (rightMargin == BaseOptions.INVALID_INT_VALUE) {
            rightMargin = getDefaultPageMargin(readerDataHolder.getContext()).getRightMargin().getPercent();
            rightMargin = SingletonSharedPreference.getLastRightMargin(rightMargin);
        }
        baseOptions.setRightMargin(rightMargin);
    }

    private void adjustBottomMargin(final ReaderDataHolder readerDataHolder, final BaseOptions baseOptions) {
        int bottomMargin = baseOptions.getBottomMargin();
        if (bottomMargin == BaseOptions.INVALID_INT_VALUE) {
            bottomMargin = getDefaultPageMargin(readerDataHolder.getContext()).getBottomMargin().getPercent();
            bottomMargin = SingletonSharedPreference.getLastBottomMargin(bottomMargin);
        }
        baseOptions.setBottomMargin(bottomMargin);
    }

    private ReaderTextStyle.PageMargin getDefaultPageMargin(Context context) {
        int index = DeviceConfig.sharedInstance(context).getDefaultPageMarginIndex();
        return ReaderTextStyle.getPageMarginByIndex(index);
    }

    private boolean processOrientation(final ReaderDataHolder readerDataHolder, final BaseOptions options) {
        int target = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if (options != null && options.getOrientation() >= 0) {
            target = options.getOrientation();
        }
        int current = DeviceUtils.getScreenOrientation(activity);
        Debug.d("current orientation: " + current + ", target orientation: " + target);
        if (current != target) {
            readerDataHolder.getEventBus().post(new ChangeOrientationEvent(target));
            if (target == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                // reverse portrait will not trigger onConfigurationChanged() in activity,
                // so we process as orientation not changed
                return true;
            }
            hideLoadingDialog();
            return false;
        }
        return true;
    }

    private void openWithOptions(final ReaderDataHolder readerDataHolder, final BaseOptions options) {
        final BaseReaderRequest openRequest = new OpenRequest(documentPath, options, true);
        readerDataHolder.submitNonRenderRequest(openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (canceled) {
                    cleanup(readerDataHolder);
                    return;
                }
                if (e != null) {
                    processOpenException(readerDataHolder, options, e);
                    return;
                }
                onFileOpenSucceed(readerDataHolder, options);
            }
        });
    }

    private void onFileOpenSucceed(final ReaderDataHolder readerDataHolder, final BaseOptions options) {
        readerDataHolder.onDocumentOpened();
        readerDataHolder.getHandlerManager().setEnable(true);
        final BaseReaderRequest config = new CreateViewRequest(readerDataHolder.getDisplayWidth(), readerDataHolder.getDisplayHeight());
        readerDataHolder.submitNonRenderRequest(config, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || canceled) {
                    cleanup(readerDataHolder);
                    return;
                }
                restoreWithOptions(readerDataHolder, options);
            }
        });
    }

    private void showPasswordDialog(final ReaderDataHolder readerDataHolder,final BaseOptions options) {
        hideLoadingDialog();
        final DialogPassword dlg = new DialogPassword(readerDataHolder.getContext());
        dlg.setOnPasswordEnteredListener(new DialogPassword.OnPasswordEnteredListener() {
            @Override
            public void onPasswordEntered(boolean success, String password) {
                dlg.dismiss();
                if (!success) {
                    cleanup(readerDataHolder);
                } else {
                    options.setPassword(password);
                    openWithOptions(readerDataHolder, options);
                }
            }
        });
        dlg.show();
    }

    private void showErrorDialog(final ReaderDataHolder holder) {
        hideLoadingDialog();
        final DialogMessage dlg = new DialogMessage(holder.getContext(), R.string.open_document_failed);
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                cleanup(holder);
            }
        });
        dlg.show();
    }

    private void cleanup(final ReaderDataHolder holder) {
        hideLoadingDialog();
        holder.getEventBus().post(new OpenDocumentFailedEvent());
    }

    private void restoreWithOptions(final ReaderDataHolder readerDataHolder, final BaseOptions options) {
        final RestoreRequest restoreRequest = new RestoreRequest(options);
        readerDataHolder.submitRenderRequest(restoreRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || canceled) {
                    cleanup(readerDataHolder);
                    return;
                }
                hideLoadingDialog();
                readerDataHolder.submitNonRenderRequest(new SaveDocumentOptionsRequest());
                readerDataHolder.onDocumentInitRendered();
            }
        });
    }

    private void processOpenException(final ReaderDataHolder holder, final BaseOptions options, final Throwable e) {
        if (!(e instanceof ReaderException)) {
            cleanup(holder);
            return;
        }
        final ReaderException readerException = (ReaderException)e;
        if (readerException.getCode() == ReaderException.PASSWORD_REQUIRED) {
            showPasswordDialog(holder, options);
            return;
        }
        showErrorDialog(holder);
    }

}
