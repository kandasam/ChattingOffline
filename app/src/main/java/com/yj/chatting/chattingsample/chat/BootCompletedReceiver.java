package com.yj.chatting.chattingsample.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yj.chatting.chattingsample.ChattingApp;

/**
 * Created by paulo on 6/9/2017.
 */

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ChattingApp app = (ChattingApp) context.getApplicationContext();
        app.startChatService();
    }
}