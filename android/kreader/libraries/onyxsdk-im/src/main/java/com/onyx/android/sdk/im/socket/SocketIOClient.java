package com.onyx.android.sdk.im.socket;

import com.onyx.android.sdk.im.Constant;
import com.onyx.android.sdk.im.IMConfig;
import com.onyx.android.sdk.im.IMManager;
import com.onyx.android.sdk.im.data.Message;
import com.onyx.android.sdk.utils.Debug;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by ming on 2017/7/11.
 */

public class SocketIOClient {

    private Socket socket;
    private Map<String,Emitter.Listener> listenerMap = new HashMap<>();
    private boolean isConnected = false;
    private boolean closed = false;
    private IMConfig config;
    private boolean isConnecting = false;
    private IMManager manager;

    public SocketIOClient(IMManager manager, IMConfig c) {
        config = c;
        this.manager = manager;
        initSocketIOClient();
    }

    private void initSocketIOClient() {
        try {
            IO.Options options = new IO.Options();
            options.reconnection = true;
            options.reconnectionAttempts = config.getReconnectLimit();
            options.reconnectionDelayMax = config.getReconnectInterval();
            options.path = config.getSocketIoPath();
            socket = IO.socket(config.getServerUri(), options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setConfig(IMConfig config) {
        this.config = config;
        initSocketIOClient();
    }

    public SocketIOClient on(String event, Emitter.Listener fn) {
        listenerMap.put(event, fn);
        getSocket().on(event, fn);
        return this;
    }

    public SocketIOClient off(String event, Emitter.Listener fn) {
        listenerMap.remove(event);
        getSocket().off(event, fn);
        return this;
    }

    public SocketIOClient connect() {
        if (isConnecting) {
            return this;
        }
        Debug.d(getClass(), "start connect");
        isConnecting = true;
        closed = false;
        on(Socket.EVENT_CONNECT,onConnect);
        on(Socket.EVENT_DISCONNECT,onDisconnect);
        on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        on(Constant.EVENT_NEW_USER, newUser);
        getSocket().connect();
        return this;
    }

    public SocketIOClient emit(String event, Object... args) {
        getSocket().emit(event, args);
        return this;
    }

    private Emitter.Listener newUser = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Debug.d(getClass(), "newUser");
            String data = (String) args[0];
            manager.onReceivedSocketMessage(Message.create(Constant.EVENT_NEW_USER, data));
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Debug.d(getClass(), "onConnected");
            isConnected = true;
            manager.onReceivedSocketMessage(Message.create(Socket.EVENT_CONNECT));
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Debug.d(getClass(), "onDisconnect");
            isConnected = false;
            manager.onReceivedSocketMessage(Message.create(Socket.EVENT_DISCONNECT));
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Debug.d(getClass(), "onConnectError");
            isConnecting = false;
            isConnected = false;
            manager.onReceivedSocketMessage(Message.create(Socket.EVENT_CONNECT_ERROR));
        }
    };

    public void close() {
        Debug.d(getClass(), "message: socket io close");
        closed = true;
        getSocket().close();
        removeListeners();
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isClosed() {
        return closed;
    }

    private void removeListeners() {
        for (Map.Entry<String, Emitter.Listener> entry : listenerMap.entrySet()) {
            getSocket().off(entry.getKey(), entry.getValue());
        }
        listenerMap.clear();
    }

    public Socket getSocket() {
        return socket;
    }
}
