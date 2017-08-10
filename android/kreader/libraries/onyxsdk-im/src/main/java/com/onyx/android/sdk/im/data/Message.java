package com.onyx.android.sdk.im.data;

/**
 * Created by ming on 2017/7/14.
 */

public class Message {
    private String channel;
    private String action;
    private String type;
    private String id;
    private String content;
    private String event;

    public Message() {
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Message(String channel, String action, String type, String id, String content) {
        this.type = type;
        this.channel = channel;
        this.action = action;
        this.id = id;
        this.content = content;
    }

    public Message(String action, String content) {
        this.action = action;
        this.content = content;
    }

    public Message(String action) {
        this.action = action;
    }

    public static Message create(String channel, String action, String type, String id, String content) {
        return new Message(channel, action, type, id, content);
    }

    public static Message create(String action, String content) {
        return new Message(action, content);
    }

    public static Message create(String action) {
        return new Message(action);
    }
}
