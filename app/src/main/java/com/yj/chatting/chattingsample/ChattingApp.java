package com.yj.chatting.chattingsample;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.yj.chatting.chattingsample.chat.ChatServer;
import com.yj.chatting.chattingsample.chat.ChatService;

/**
 * Created by Ralph on 8/5/2016.
 */
public class ChattingApp extends MultiDexApplication implements Constant{

    // Chatting Connection
    public ChatServer mChatServer = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void startChatService() {
        startService(new Intent(this, ChatService.class));
    }

    public void initChattingServer() {
        mChatServer = new ChatServer(this, ChatServer.getServerIp(), ChatServer.OPENFIRE_SERVER_PORT);
    }
}
