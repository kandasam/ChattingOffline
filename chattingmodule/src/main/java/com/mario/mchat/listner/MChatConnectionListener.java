package com.mario.mchat.listner;

/**
 * Created by paulo on 5/22/2017.
 */

public interface MChatConnectionListener {
    public void onConnected();
    public void onConnectFailed(Exception e);
    public void onInvalidIP();
    public void onClosed();
}

