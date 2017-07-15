package com.onyx.android.sdk.im.socket;

import com.onyx.android.sdk.im.IMConfig;
import com.onyx.android.sdk.utils.Debug;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
    private int reconnectCount = 0;
    private IMConfig config;
    private Timer timer = new Timer();

    public SocketIOClient(IMConfig c) {
        config = c;
        initSocketIOClient();
    }

    private void initSocketIOClient() {
        try {
            IO.Options options = new IO.Options();
            options.reconnection = true;
            options.reconnectionAttempts = config.getReconnectLimit();
            options.reconnectionDelayMax = config.getReconnectInterval();
            socket = IO.socket(config.getServerUri(), options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
        closed = false;
        on(Socket.EVENT_CONNECT,onConnect);
        on(Socket.EVENT_DISCONNECT,onDisconnect);
        on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        getSocket().connect();
        return this;
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Debug.d(getClass(), "onConnect");
            isConnected = true;
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Debug.d(getClass(), "onDisconnect");
            isConnected = false;
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Debug.d(getClass(), "onConnectError");
            isConnected = false;
        }
    };

    private void reconnect() {
        if (closed) {
            return;
        }
        if (!config.canReconnect(reconnectCount)) {
            return;
        }
        Debug.d(getClass(), "reconnect");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (closed) {
                    return;
                }
                reconnectCount++;
                getSocket().connect();
            }
        }, config.getReconnectInterval());
    }

    public void close() {
        closed = true;
        timer.cancel();
        removeListeners();
        getSocket().close();
        isConnected = false;
        reconnectCount = 0;
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
