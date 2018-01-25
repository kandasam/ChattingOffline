package com.yj.chatting.chattingsample.chat;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.yj.chatting.chattingsample.ChattingApp;

/**
 * Created by paulo on 6/9/2017.
 */

public class ChatService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent p_intent, int p_nFlags, int p_nServiceId) {
        super.onStartCommand(p_intent, p_nFlags, p_nServiceId);

        // auto login
        m_appLoginHandler.sendEmptyMessage(0);

        return START_STICKY;
    }

    Handler m_appLoginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ChattingApp app = (ChattingApp) getApplicationContext();
            String userNo = "";

            /*if (app.mPrefManager != null) {
                //userNo = String.format("%d", app.mLogginedUser.mNo);
                userNo = app.mPrefManager.getData(CURRENT_USER_NO);
            }

            if (userNo != null && userNo.isEmpty() == false) {
                if (app.mChatServer == null) {
                    LogUtils.file("ChatService", "Init again");
                    app.initChattingServer();
                    sendEmptyMessageDelayed(0, 1000);
                    return;
                }

                if (app.mChatServer.isConnected() == true) {
                    if (app.mChatServer.isLogined() == false) {
                        LogUtils.file("ChatService", "login");
                        app.mChatServer.loginUser(userNo);
                    }
                    else {
                        LogUtils.file("ChatService", "loginned");
                    }
                } else {
                    app.mChatServer.reconnect();
                    LogUtils.file("ChatService", "reconnect");
                }
            }
              */
            sendEmptyMessageDelayed(0, 1000);
        }
    };

    @Override
    public void onDestroy() {
    }
}
