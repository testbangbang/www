package com.onyx.kreader.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.manager.OssManager;
import com.onyx.android.sdk.data.manager.WeChatManager;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.model.PushOssProduct;
import com.onyx.android.sdk.data.model.WeChatOauthResp;
import com.onyx.android.sdk.data.model.WeChatUserInfo;
import com.onyx.android.sdk.data.request.cloud.AccountGetOAuthCodeRequest;
import com.onyx.android.sdk.data.request.cloud.AccountOAuthRequest;
import com.onyx.android.sdk.data.request.cloud.PushSavingProductRequest;
import com.onyx.android.sdk.data.request.cloud.WeChatOauthRequest;
import com.onyx.android.sdk.data.request.cloud.WeChatUserInfoRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.BuildConfig;
import com.onyx.kreader.R;
import com.onyx.kreader.ReaderApplication;
import com.onyx.kreader.ui.dialog.DialogProgressHolder;
import com.onyx.kreader.manager.PermissionManager;
import com.onyx.kreader.utils.TextUtils;
import com.onyx.kreader.utils.ToastUtils;
import com.onyx.kreader.utils.NetworkUtils;
import com.onyx.sdk.ebookservice.CoverWrapper;
import com.onyx.sdk.ebookservice.HtmlManager;
import com.onyx.sdk.ebookservice.request.HtmlParseEpubRequest;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by wang.suicheng on 2017/1/21.
 */
public class ShareFileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String PUSH_TYPE = "digital_content";

    @Bind(R.id.message_textView)
    TextView messageTextView;

    private DialogProgressHolder progressDialogHolder = new DialogProgressHolder();
    private Object dialogObject = new Object();
    private Object weChatOauthObject = new Object();
    private String uploadFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initData();
    }

    private int getLayoutId() {
        return R.layout.activity_share_file;
    }

    private void initData() {
        if (NetworkUtils.isWifiConnected(this)) {

        } else {
            showWifiDisconnectedDialog();
        }
    }

    private void showWifiDisconnectedDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.share_file_warning_as_wifi_disconnect)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .show();
    }

    private void processSendActionIntent() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (uri != null) {
                processFileShared(uri);
            } else {
                Bundle bundle = intent.getExtras();
                String url = bundle.getString(Constant.URL_TAG);
                if (StringUtils.isNotBlank(url)) {
                    processWebUrlShared(url);
                } else if (StringUtils.isNotBlank(bundle.getString(Intent.EXTRA_TEXT))) {
                    String text = bundle.getString(Intent.EXTRA_TEXT, "");
                    url = splitUrlString(text);
                    if (StringUtils.isNotBlank(url)) {
                        processWebUrlShared(url);
                    } else {
                        processTextShared(text);
                    }
                }
            }
        }
    }

    private String splitUrlString(String text) {
        String url = null;
        int httpIndex = text.indexOf(Constant.HTTP_TAG);
        if (httpIndex >= 0) {
            url = text.substring(httpIndex);
            int blankIndex = url.indexOf(" ");
            if (blankIndex > 0) {
                url = url.substring(0, blankIndex).trim();
            }
        }
        return url;
    }

    private void showSharedMessage(String message) {
        messageTextView.setText(message + getIntentInfo());
    }

    private String getIntentInfo() {
        if (BuildConfig.DEBUG) {
            return TextUtils.NEW_LINE_DOUBLE + getIntent().toString() +
                    TextUtils.NEW_LINE_DOUBLE + getBundleData(getIntent().getExtras());
        }
        return "";
    }

    private String getBundleData(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder("Bundle{\r\n");
        for (String key : bundle.keySet()) {
            sb.append(key).append(" => ").append(bundle.get(key)).append(";\r\n");
        }
        sb.append(" }Bundle");
        return sb.toString();
    }

    private void processFileShared(Uri uri) {
        showSharedMessage(uploadFilePath = FileUtils.getRealFilePathFromUri(this, uri));
        startToUploadFile(uploadFilePath);
    }

    private Bitmap getDefaultCoverBitmap() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.default_cover);
    }

    private CoverWrapper getDefaultCoverWrapper() {
        CoverWrapper coverWrapper = new CoverWrapper();
        Bitmap coverBitmap = getDefaultCoverBitmap();
        if (coverBitmap != null) {
            coverWrapper.bitmap = coverBitmap.copy(Bitmap.Config.RGB_565, true);
        }
        return coverWrapper;
    }

    private void processWebUrlShared(final String url) {
        showSharedMessage(url);
        final HtmlParseEpubRequest parseEpubRequest = new HtmlParseEpubRequest(
                getExternalCacheDir().getAbsolutePath(), url, getDefaultCoverWrapper());
        HtmlManager htmlManager = new HtmlManager();
        htmlManager.submitRequest(this, parseEpubRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                progressDialogHolder.dismissProgressDialog(request);
                if (e != null || StringUtils.isNullOrEmpty(parseEpubRequest.getOutputFilePath())) {
                    ToastUtils.showToast(ShareFileActivity.this, getString(R.string.generate_epub_file_failed),
                            Toast.LENGTH_SHORT);
                    return;
                }
                startToUploadFile(parseEpubRequest.getOutputFilePath());
            }
        });
        progressDialogHolder.showIndeterminateProgressDialog(this, parseEpubRequest, R.string.generating_epub_file);
    }

    private File getTextFile(String content) {
        String fileName;
        if (content.length() < 20) {
            fileName = content;
        } else {
            fileName = content.substring(0, 20);
        }
        return new File(getExternalCacheDir(), FileUtils.fixNotAllowFileName(fileName) + ".txt");
    }

    private void processTextShared(String text) {
        showSharedMessage(text);
        File file = getTextFile(text);
        boolean success = TextUtils.writeTextToFile(file, text);
        if (success) {
            startToUploadFile(uploadFilePath = file.getAbsolutePath());
        }
    }

    private void signUpWeChat() {
        getWeChatManager().sendWeChatAuthRequest(this);
        ToastUtils.showShortToast(this, R.string.waiting_for_wechat_oauth);
        registerReceiver(authReceiver, new IntentFilter(WeChatManager.WE_CHAT_LOGIN_OAUTH_ACTION));
    }

    private BroadcastReceiver authReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WeChatManager.WE_CHAT_LOGIN_OAUTH_ACTION.equals(intent.getAction())) {
                processWeChatOauthAction();
            }
        }
    };

    private void processWeChatOauthAction() {
        getWeChatManager().sendWeChatTokenRequest(getCloudStore(), this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                WeChatOauthResp resp;
                if (e != null || (resp = ((WeChatOauthRequest) request).getResp()) == null) {
                    processOAuthErrorResult(R.string.get_wx_token_failed);
                    printException(e);
                    return;
                }
                processWeChatUserInfo(resp);
            }
        });
        progressDialogHolder.showIndeterminateProgressDialog(this, weChatOauthObject, R.string.loading);
    }

    private void processWeChatUserInfo(WeChatOauthResp resp) {
        getWeChatManager().sendWeChatUserInfoRequest(getCloudStore(), this, resp.accessToken, resp.openId,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        WeChatUserInfoRequest userInfoRequest = (WeChatUserInfoRequest) request;
                        WeChatUserInfo userInfo = userInfoRequest.getUserInfo();
                        if (e != null || userInfo == null) {
                            processOAuthErrorResult(R.string.get_wx_user_info_failed);
                            printException(e);
                            return;
                        }
                        requestOnyxOauthCode(userInfo);
                    }
                });
    }

    private void requestOnyxOauthCode(WeChatUserInfo userInfo) {
        getWeChatManager().sendOnyxOAuthCodeRequest(getCloudStore(), this, userInfo, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                String code = ((AccountGetOAuthCodeRequest) request).getOauthCode();
                if (e != null || StringUtils.isNullOrEmpty(code)) {
                    processOAuthErrorResult(R.string.get_onyx_account_oauth_failed);
                    printException(e);
                    return;
                }
                requestOnyxAccount(code);
            }
        });
    }

    private void requestOnyxAccount(String code) {
        final AccountOAuthRequest oAuthRequest = new AccountOAuthRequest(code);
        getCloudStore().submitRequest(this, oAuthRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                progressDialogHolder.dismissProgressDialog(weChatOauthObject);
                OnyxAccount account = oAuthRequest.getOnyxAccount();
                if (e != null || account == null) {
                    processOAuthErrorResult(R.string.get_onyx_account_bound_failed);
                    printException(e);
                    return;
                }
                OnyxAccount.saveAccount(ShareFileActivity.this, account);
                processSendActionIntent();
            }
        });
    }

    private void processOAuthErrorResult(int errorResId) {
        ToastUtils.showLongToast(this, errorResId);
        progressDialogHolder.dismissProgressDialog(weChatOauthObject);
    }

    private void afterPermissionGranted() {
        if (loadOnyxAccount()) {
            processSendActionIntent();
        } else {
            signUpWeChat();
        }
    }

    private OssManager getPushOssManager() {
        return ReaderApplication.getPushOssManger(getApplicationContext());
    }

    private void startToUploadFile(final String uploadFilePath) {
        progressDialogHolder.showProgressDialog(this, dialogObject, R.string.upload_file);
        getPushOssManager().asyncUploadFile(this, uploadFilePath, new BaseCallback() {
            @Override
            public void progress(BaseRequest request, final ProgressInfo info) {
                if (info != null) {
                    int progress = (int) info.progress;
                    progressDialogHolder.setProgress(dialogObject, progress);
                }
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                progressDialogHolder.dismissProgressDialog(dialogObject);
                boolean success = e == null;
                String uploadResult = getString(R.string.upload_file) + (success ? getString(R.string.success) : getString(R.string.failed));
                ToastUtils.showShortToast(ShareFileActivity.this, uploadResult);
                if (!success) {
                    e.printStackTrace();
                    return;
                }

                if (request instanceof OssManager.OssWrapRequest) {
                    OssManager.OssWrapRequest wrapRequest = (OssManager.OssWrapRequest) request;
                    PutObjectRequest put = (PutObjectRequest) wrapRequest.getRequest();
                    saveAndPushToBooxDevice(uploadFilePath, put.getObjectKey());
                }
            }
        });
    }

    private void saveAndPushToBooxDevice(String filePath, String objectKey) {
        final PushSavingProductRequest productRequest = new PushSavingProductRequest(getPushOssProduct(filePath, objectKey));
        getCloudStore().submitRequest(this, productRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                boolean success = e == null;
                String pushResult = getString(R.string.push_to_device) + (success ? getString(R.string.success) : getString(R.string.failed));
                ToastUtils.showShortToast(ShareFileActivity.this, pushResult);
                if (!success) {
                    e.printStackTrace();
                }
            }
        });
    }

    private PushOssProduct getPushOssProduct(String filePath, String objectKey) {
        PushOssProduct pushOssProduct = new PushOssProduct();
        pushOssProduct.setResourceData(filePath, objectKey);
        pushOssProduct.type = PUSH_TYPE;
        return pushOssProduct;
    }

    private boolean loadOnyxAccount() {
        String token = OnyxAccount.loadAccountSessionToken(this);
        if (StringUtils.isNotBlank(token)) {
            return true;
        }
        return false;
    }

    private CloudStore getCloudStore() {
        return ReaderApplication.getCloudStore();
    }

    private WeChatManager getWeChatManager() {
        return WeChatManager.sharedInstance(this, ReaderApplication.WX_APP_ID, ReaderApplication.WX_APP_SECRETE);
    }

    private void printException(Throwable e) {
        if (e != null) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        try {
            unregisterReceiver(authReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        PermissionManager.processPermissionPermanentlyDenied(this, getString(R.string.tip_of_permissions_request),
                requestCode, perms);
    }
}
