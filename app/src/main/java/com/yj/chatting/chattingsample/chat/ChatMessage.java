package com.yj.chatting.chattingsample.chat;

import org.json.JSONObject;

/**
 * Created by paulo on 5/23/2017.
 */

public class ChatMessage {

    public final static int TYPE_NORMAL_TEXT = 0;
    public final static int TYPE_REQUEST_CONSULTING = 1;
    public final static int TYPE_REQUEST_ACCEPT_CONSULTING = 2;
    public final static int TYPE_REQUEST_PRESENT = 3;
    public final static int TYPE_SEND_ENVELOP = 4;
    public final static int TYPE_ADD_FRIEND = 5;
    public final static int TYPE_SEND_PRESENT = 6;
    public final static int TYPE_CASH_QA = 7;
    public final static int TYPE_ADMIN_NORMAL_PUSH = 8;
    public final static int TYPE_REFUSE_IMAGE = 9;
    public final static int TYPE_SUCCESS_FREE_CHARGE = 10;
    public final static int TYPE_ADMIN_WARNING = 11;
    public final static int TYPE_ADMIN_APP_STOP = 12;
    public final static int TYPE_ADMIN_IMAGE_AGREE = 13;
    public final static int TYPE_ADMIN_VOICE_REFUSE = 14;
    public final static int TYPE_ADMIN_VOICE_AGREE = 15;
    public final static int TYPE_APP_STOP_REMOVE = 16;
    public final static int TYPE_REQUEST_CONSULTING_REFUSE = 17;
    public final static int TYPE_ADMIN_WITHDRAW_COMPLETE = 18;
    public final static int TYPE_COMPLETE_CONSULTING = 19;
    public final static int TYPE_ALARM_ENABLE = 20;
    public final static int TYPE_ALARM_DISABLE = 21;

    public static final int READED_UNREADED = -1;
    public static final int UNREADED = 0;
    public static final int READED = 1;

    public static final int SEARCH_ALL = -1;
    public static final int SEARCH_FRIEND = -2;
    public static final int SEARCH_CHAT = -3;


    public
    String mTitle = "";

    public
    String mContent = "";

    public
    int mType = ChatMessage.TYPE_NORMAL_TEXT;

    public
    int mIsRead = UNREADED;

    public
    int mFromUserNo;

    public
    int mToUserNo;

    public
    int mUnReadCnt = 0;


    public static ChatMessage chatMessageFromJSON(JSONObject jsonObject) {
        ChatMessage message = new ChatMessage();
        try {
            message.mType = jsonObject.getInt("type");
            String jsonUser = jsonObject.getString("user");
            message.mContent = jsonObject.getString("content");
            message.mTitle = jsonObject.getString("title");
        } catch (Exception e) {
        }

        return message;
    }

    public static JSONObject toJSON(ChatMessage message) {
        JSONObject object = new JSONObject();
        try {
            object.put("type", message.mType);
            object.put("content", message.mContent);
            object.put("title", message.mTitle);

        } catch (Exception e) {
        }
        return object;
    }

    public String toJSONString() {
        JSONObject object = toJSON(this);
        return object.toString();
    }

    public static String toJSONString(String roomID) {
        String ret = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("room_id", roomID);
            ret = jsonObject.toString();
        }
        catch (Exception e) {

        }

        return ret;
    }

    public static String toJSONString(int point) {
        String ret = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("point", point);
            ret = jsonObject.toString();
        }
        catch (Exception e) {

        }

        return ret;
    }
}
