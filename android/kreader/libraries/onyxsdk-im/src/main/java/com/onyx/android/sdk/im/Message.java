package com.onyx.android.sdk.im;

/**
 * Created by ming on 2017/7/14.
 */

public class Message {

    private String channel;
    private String action;
    private String id;
    private String content;

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

    public Message(String channel, String action, String id, String content) {
        this.channel = channel;
        this.action = action;
        this.id = id;
        this.content = content;
    }

    public static Message create(String channel, String action, String id, String content) {
        return new Message(channel, action, id, content);
    }
}
