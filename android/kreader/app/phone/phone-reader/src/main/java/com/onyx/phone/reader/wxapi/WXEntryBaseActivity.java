package com.onyx.phone.reader.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.onyx.android.sdk.data.manager.WeChatManager;
import com.onyx.phone.reader.BuildConfig;
import com.onyx.phone.reader.R;
import com.onyx.phone.reader.utils.ToastUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * Created by wang.suicheng on 2017/1/22.
 */
public class WXEntryBaseActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private static final String TAG = WXEntryBaseActivity.class.getSimpleName();
    private static final int DELAY_FINISH_ACTIVITY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean handleIntent = WeChatManager.sharedInstance(this).getWeChatApi().handleIntent(getIntent(), this);
        if (!handleIntent) {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WeChatManager.sharedInstance(this).getWeChatApi().handleIntent(intent, this);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        printLog(baseResp);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (baseResp instanceof SendAuth.Resp) {
                    processSendAuthResp((SendAuth.Resp) baseResp);
                } else if (baseResp instanceof SendMessageToWX.Resp) {
                    processSendMessageResp((SendMessageToWX.Resp) baseResp);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                processRespFailed(R.string.wx_cancel);
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                processRespFailed(R.string.wx_fail);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                processRespFailed(R.string.wx_denied);
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                processRespFailed(R.string.wx_nonsupport);
                break;
            default:
                processRespFailed(R.string.wx_back);
                break;
        }
    }

    private void processSendAuthResp(SendAuth.Resp resp) {
        String code = resp.code;
        WeChatManager.sharedInstance(this).setRespCode(code);
        ToastUtils.showShortToast(this, R.string.wx_success);
        processAuthRespSuccess();
    }

    private void processSendMessageResp(SendMessageToWX.Resp resp) {
        ToastUtils.showShortToast(this, R.string.wx_success);
        delayFinishActivity();
    }

    protected void processAuthRespSuccess() {
        Intent intent = new Intent();
        intent.setAction(WeChatManager.WE_CHAT_LOGIN_OAUTH_ACTION);
        sendBroadcast(intent);
        delayFinishActivity();
    }

    protected void processRespFailed(int resultStringRes) {
        showErrorResultAndFinish(resultStringRes);
    }

    private void showErrorResultAndFinish(int resultStringRes) {
        ToastUtils.showShortToast(this, resultStringRes);
        delayFinishActivity();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        if (BuildConfig.DEBUG) {
            Log.d("transaction", String.valueOf(baseReq.transaction));
            Log.d("openId", String.valueOf(baseReq.openId));
        }
    }

    private void printLog(BaseResp baseResp) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, ",errStr=" + baseResp.errStr + ",errCode=" + baseResp.errCode);
        }
    }

    private void delayFinishActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, DELAY_FINISH_ACTIVITY);
    }
}
