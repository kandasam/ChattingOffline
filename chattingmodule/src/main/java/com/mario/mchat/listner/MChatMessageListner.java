package com.mario.mchat.listner;

/**
 * Created by paulo on 5/23/2017.
 */

public interface MChatMessageListner {
    public void onMessageRecieve(String userXmppID, String body);
}
