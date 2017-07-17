package com.onyx.android.sdk;

import android.app.Instrumentation;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SendCallback;
import com.onyx.android.sdk.im.Constant;
import com.onyx.android.sdk.im.IMConfig;
import com.onyx.android.sdk.im.IMManager;
import com.onyx.android.sdk.im.Message;
import com.onyx.android.sdk.im.test.IMTestActivity;
import com.onyx.android.sdk.utils.TestUtils;

import io.socket.emitter.Emitter;

/**
 * Created by zhuzeng on 08/01/2017.
 */

public class IMTest extends ActivityInstrumentationTestCase2<IMTestActivity> {

    private static final String TAG = "IMTest";

    public IMTest() {
        super(IMTestActivity.class);
    }

    public void testSocketIO() {
        IMConfig imInitArgs = new IMConfig("http://192.168.11.5:3000");
        IMManager.getInstance().
                init(imInitArgs).
                startSocketService(getActivity());
        IMManager.getInstance().getMessageIdSets().clear();
        TestUtils.sleep(2000);
        String messageId = TestUtils.randString();
        Message message = Message.create("Test", "Test", messageId, "Test");
        IMManager.getInstance().getSocketIOClient().emit(Constant.NEW_MESSAGE, JSONObject.toJSONString(message));
        TestUtils.sleep(2000);
        assertTrue(IMManager.getInstance().getMessageIdSets().contains(messageId));
    }

    public void testAVCloudPush() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final IMConfig imInitArgs = new IMConfig("qzQUFbupxkuCkw7qUNpzPbY3-gzGzoHsz",
                        "L01gnR0ph2TAPHYnOwJgtjv5");
                IMManager.getInstance().
                        init(imInitArgs).
                        startPushService(getActivity());
            }
        });


        IMManager.getInstance().getMessageIdSets().clear();
        TestUtils.sleep(2000);

        AVPush push = new AVPush();
        AVQuery<AVInstallation> query = AVInstallation.getQuery();
        query.whereEqualTo("installationId", AVInstallation.getCurrentInstallation()
                .getInstallationId());
        push.setQuery(query);

        String messageId = TestUtils.randString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.MESSAGE_ACTION, "com.avos.UPDATE_STATUS");
        jsonObject.put(Constant.MESSAGE_ID, messageId);
        jsonObject.put(Constant.MESSAGE_CONTENT, "test");

        push.setData(jsonObject);
        push.setPushToAndroid(true);
        push.sendInBackground();

        TestUtils.sleep(3000);

        assertTrue(IMManager.getInstance().getMessageIdSets().contains(messageId));


    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

}
