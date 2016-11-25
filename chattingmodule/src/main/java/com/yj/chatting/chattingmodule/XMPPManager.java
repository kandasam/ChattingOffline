package com.yj.chatting.chattingmodule;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;

import java.io.IOException;

/**
 * Created by KCJ on 1/13/2016.
 */
class XMPPManager {
    private static final String TAG = "XMPPManager";
    private static XMPPManager sXmppManager;

    private XMPPTCPConnection mXmppConnection;
    private Context mContext;
    private Thread mXMPPConnectionThread;
    private XMPPTCPConnectionConfiguration mXMPPConnectionConfiguration;

    private XMPPConnectListner mXMPPConnectionListener;

    private XMPPManager(final Context context) {
        mContext = context;
    }

    public synchronized static XMPPManager getInstance(Context context) {

        if (sXmppManager == null) {
            sXmppManager = new XMPPManager(context);
            Log.d(TAG, "XMPP Manager creating new connection");
        }

        return sXmppManager;
    }

    public XMPPTCPConnection getConnection() {
        return mXmppConnection;
    }

    public void connect(String serverName, int serverPort, ServerConnectListner listner) {

        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setServiceName(serverName);
        configBuilder.setPort(serverPort);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setDebuggerEnabled(true);

        mXMPPConnectionConfiguration = configBuilder.build();
        mXMPPConnectionListener = new XMPPConnectListner(listner);
        mXmppConnection = new XMPPTCPConnection(mXMPPConnectionConfiguration);
        mXmppConnection.addConnectionListener(mXMPPConnectionListener);

        PingManager.setDefaultPingInterval(10);
        PingManager.getInstanceFor(mXmppConnection);
        DeliveryReceiptManager mDeliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(mXmppConnection);
        mDeliveryReceiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.disabled);
        mDeliveryReceiptManager.dontAutoAddDeliveryReceiptRequests();
        Roster.getInstanceFor(mXmppConnection).setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        ChatStateManager.getInstance(mXmppConnection);

        mXmppConnection.setUseStreamManagement(true);
        mXmppConnection.setUseStreamManagementResumption(true);

        Log.d(TAG, "connecction ->" + mXmppConnection);
        if (!isAlive()) {

            mXMPPConnectionThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mXmppConnection == null
                            || !mXmppConnection.isSocketClosed()) {
                        try {

                            mXmppConnection.connect();
                            ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mXmppConnection);
                            if (!reconnectionManager.isAutomaticReconnectEnabled()) {
                                reconnectionManager.enableAutomaticReconnection();
                            }
                            mXMPPConnectionListener.connected(mXmppConnection);
                        } catch (Exception e) {
                            e.printStackTrace();
                            mXMPPConnectionListener.onError(e);
                        }
                    }
                }
            });

            mXMPPConnectionThread.start();
        }
    }


    private void cleanUpConnection() {
        if (mXmppConnection != null && mXmppConnection.isConnected()) {
            mXmppConnection.instantShutdown();
            mXmppConnection = null;
        } else {
            mXmppConnection = null;
        }
    }


    // group chat functions end
    public boolean isAlive() {
        return (mXmppConnection != null
                && mXmppConnection.isAuthenticated()
                && mXmppConnection.isConnected()
                && !mXmppConnection.isSocketClosed());
    }


    public boolean createAccount(String p_strId, String p_strPassword) {

        if(mXmppConnection == null) {
            return false;
        }

        try {
            AccountManager mAccount = AccountManager.getInstance(mXmppConnection);

            if (mAccount.supportsAccountCreation()) {
                mAccount.createAccount(p_strId, p_strPassword);
            }
            return true;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            if (e.getXMPPError().getCondition() == XMPPError.Condition.conflict) {
                return true;
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean login(String p_strId, String p_strPassword) {
        if (p_strId != null && !p_strId.isEmpty() && p_strPassword != null && !p_strPassword.isEmpty()) {
            try {
                mXmppConnection.login(p_strId, p_strPassword);
                return true;
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        } else {
            return true;
        }
    }
}
