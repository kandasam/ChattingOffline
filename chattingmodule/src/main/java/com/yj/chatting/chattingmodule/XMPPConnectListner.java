package com.yj.chatting.chattingmodule;

import android.content.Context;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by KCJ on 1/13/2016.
 */
class XMPPConnectListner implements ConnectionListener {

    ServerConnectListner mListener = null;

    XMPPConnectListner(ServerConnectListner listner) {
        super();
        mListener = listner;
    }

    @Override
    public void connected(XMPPConnection connection)
    {
        if(mListener != null) {
            mListener.onSuccess();
        }
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed)
    {

    }

    @Override
    public void connectionClosed()
    {

    }

    @Override
    public void connectionClosedOnError(Exception e)
    {

    }

    @Override
    public void reconnectionSuccessful()
    {

    }

    @Override
    public void reconnectingIn(int seconds)
    {

    }

    @Override
    public void reconnectionFailed(Exception e)
    {

    }

    public void onError(Exception e) {
        if(mListener != null) {
            mListener.onFailed(e);
        }
    }
}
