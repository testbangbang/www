package com.onyx.android.sdk.im.data;

/**
 * Created by lxm on 2017/8/8.
 */

public class JoinModel {

    private String room;
    private String name;
    private String mac;

    public JoinModel() {
    }

    public JoinModel(String room, String name) {
        this.room = room;
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public static JoinModel create(String room, String name) {
        return new JoinModel(room, name);
    }

    public JoinModel(String room, String name, String mac) {
        this.room = room;
        this.name = name;
        this.mac = mac;
    }

    public static JoinModel create(String room, String name, String mac) {
        return new JoinModel(room, name, mac);
    }
}
