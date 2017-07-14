package com.onyx.android.sdk.im.socket;

import android.content.Context;

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

    private String serverUri;

    private Socket socket;
    private Map<String,Emitter.Listener> listenerMap = new HashMap<>();

    public SocketIOClient(String serverUri) {
        this.serverUri = serverUri;
        initSocketIOClient();
    }

    private void initSocketIOClient() {
        try {
            socket = IO.socket(serverUri);
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
        getSocket().connect();
        return this;
    }

    public void close() {
        removeListeners();
        getSocket().close();
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
