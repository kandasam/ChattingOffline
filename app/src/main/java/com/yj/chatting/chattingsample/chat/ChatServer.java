package com.yj.chatting.chattingsample.chat;

import android.content.Context;

import com.mario.mchat.MChatConnection;
import com.mario.mchat.listner.MChatConnectionListener;
import com.mario.mchat.listner.MChatMessageListner;
import com.yj.chatting.chattingsample.ChattingApp;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by paulo on 5/23/2017.
 */

public class ChatServer implements MChatConnectionListener, MChatMessageListner {

    public static final int CONNECTED = 0;
    public static final int CONNECT_FAILED = 1;
    public static final int CONNECT_CLOSED = 2;

    public interface MessageListner {
        public void onMessageReceived(ChatMessage message);
    }


    public static final String TAG = "ChatServer";

    public static final String DEV_LOCAL_OPENFIRE_SERVER_IP = "192.168.0.121";
    public static final String DEV_LOCAL_WEBRTC_SERVER_IP = "http://192.168.0.11:8080";

    public static final String GLOBAL_OPENFIRE_SERVER_IP = "175.126.38.49";
    public static final String GLOBAL_WEBRTC_SERVER_IP = "http://110.10.189.145:8080";


    public static final String TAG_CHAT = "voicetalk";
    public static final String TAG_CHATROOM = "voicetalk_room";
    public static final int OPENFIRE_SERVER_PORT = 5222;

    public static String getServerIp() {

        return GLOBAL_OPENFIRE_SERVER_IP;
    }

    public static String getWebRtcServerIp() {
        return GLOBAL_WEBRTC_SERVER_IP;
    }


    public static String getXMPPUserID(String userNo) {
        return String.format("%s%s", TAG_CHAT, userNo);
    }

    private String mServerIp = DEV_LOCAL_OPENFIRE_SERVER_IP;
    private int mPort = OPENFIRE_SERVER_PORT;

    public MChatConnection mChatConnection = null;
    public ArrayList<MessageListner> mArrMessageListener = new ArrayList<>();
    public Context mContext;
    private boolean mIsLoginned = false;
    private boolean mIsShowedErrorPopup = false;

    /*******************************************************
     * Public functions
     *******************************************************/

    public ChatServer(Context context, String ip, int port) {
        mServerIp = ip;
        mPort = port;
        mContext = context;
        mChatConnection = new MChatConnection(context, ip, port, this);
    }

    public boolean createUser(String userNo) {
        String userId = getXMPPUserID(userNo);
        return mChatConnection.createAccount(userId, userId);
    }

    public void reconnect() {
        mChatConnection = new MChatConnection(mContext, mServerIp, mPort, this);
    }

    public boolean loginUser(String userNo) {
        String userId = getXMPPUserID(userNo);
        boolean isSucess = mChatConnection.login(userId, userId);
        if (isSucess == true) {
            mIsLoginned = true;
            mChatConnection.setOneToOneChatListner(this);
        }
        return isSucess;
    }

    public boolean isConnected() {
        return mChatConnection.isConnected();
    }

    public boolean isLogined() {
        return mIsLoginned;
    }

    public void setLogout() {
        mIsLoginned = false;
    }

    public boolean sendMessage(String fromUserNo, ChatMessage message) {
        String userId = getXMPPUserID(fromUserNo);
        String messageID = mChatConnection.sendMessage(userId, message.toJSONString());

        if (messageID == null) {
            return false;
        }

        return true;
    }

    public void registerMessageListner(MessageListner listner) {
        mArrMessageListener.add(listner);
    }

    public void removeMessageListner(MessageListner listner) {
        mArrMessageListener.remove(listner);
    }

    public void getHistory(String fromID, int startIdx, int cnt) {
        mChatConnection.getHistoryFromOneToOne(getXMPPUserID(fromID));
    }

    /*******************************************************
     * Private functions
     *******************************************************/
    private void processChatMessage(final ChatMessage message) {

        if (message == null) {
            return;
        }

        ChattingApp app = (ChattingApp) mContext;
        //app.processChatMessage(message, false);
    }

    private void processInMessageListner(ChatMessage message) {
        int size = mArrMessageListener.size();
        for (int i = 0; i < size; i++) {
            mArrMessageListener.get(i).onMessageReceived(message);
        }
    }

    /*******************************************************
     * MChatConnection Listner
     *******************************************************/
    public void onConnected() {

    }

    public void onConnectFailed(Exception e) {

    }

    public void onInvalidIP() {

    }

    public void onClosed() {

    }

    /*******************************************************
     * MChatConnection Listner
     *******************************************************/
    public void onMessageRecieve(String userXmppID, String body) {
        try {
            JSONObject object = new JSONObject(body);

            ChatMessage message = ChatMessage.chatMessageFromJSON(object);
            processChatMessage(message);
        } catch (Exception e) {

        }
    }
}
