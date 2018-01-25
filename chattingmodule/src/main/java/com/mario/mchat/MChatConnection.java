package com.mario.mchat;

import android.content.Context;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;

import com.mario.mchat.IQ.MessageRetreiveIQ;
import com.mario.mchat.listner.MChatConnectionListener;
import com.mario.mchat.listner.MChatMessageListner;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ExceptionCallback;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.PacketUtil;
import org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MucEnterConfiguration;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by paulo on 5/22/2017.
 */

public class MChatConnection implements ConnectionListener {

    private Context           mContext;

    // smack variables
    private XMPPTCPConnection mConnection;
    private XMPPTCPConnectionConfiguration mXmppConfiguration;

    // listners
    private MChatConnectionListener mConnectionListner;
    private MChatMessageListner     mOneToOneChatListner;

    public MChatConnection(Context context, String ip, int port, MChatConnectionListener connectionListener) {
        mContext = context;
        mConnectionListner = connectionListener;
        try {
            HostnameVerifier verifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return false;
                }
            };
            DomainBareJid serviceName = JidCreate.domainBareFrom(ip);
            XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
            configBuilder.setXmppDomain(serviceName);
            configBuilder.setHostAddress(InetAddress.getByName(ip));
            configBuilder.setPort(port);
            configBuilder.setHostnameVerifier(verifier);
            configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            configBuilder.setDebuggerEnabled(BuildConfig.DEBUG);
            mXmppConfiguration = configBuilder.build();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        doConnect();
                    }
                    catch (Exception e) {
                        if(mConnectionListner != null) {
                            mConnectionListner.onConnectFailed(e);
                        }

                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch (Exception e) {
            if(mConnectionListner != null) {
                mConnectionListner.onInvalidIP();
            }

            e.printStackTrace();
        }
    }

    private void doConnect() throws Exception {
        mConnection = new XMPPTCPConnection(mXmppConfiguration);
        mConnection.addConnectionListener(this);
        PingManager.getInstanceFor(mConnection).setPingInterval(30000);
        try {
            mConnection.connect();
            ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
            if (!reconnectionManager.isAutomaticReconnectEnabled()) {
                reconnectionManager.enableAutomaticReconnection();
            }
        } catch (Exception e) {
            if(e.getClass() == SmackException.AlreadyConnectedException.class) {
                if (mConnectionListner != null) {
                    mConnectionListner.onConnected();
                }
            }
            else {
                if (mConnectionListner != null) {
                    mConnectionListner.onConnectFailed(e);
                }
            }
        }
    }

    private void addOneToOneMessageListner() {
        mConnection.addAsyncStanzaListener(new StanzaListener() {
            @Override
            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
                if(packet.getClass() == Message.class) {
                    Message message = (Message)packet;
                    if(mOneToOneChatListner != null && message.getFrom() != null) {
                        mOneToOneChatListner.onMessageRecieve(message.getFrom().toString().replace(getServiceName(), ""), message.getBody());
                    }
                }
            }
        }, new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                return true;
            }
        });
    }

    private String getServiceName() {
        return  mXmppConfiguration.getXMPPServiceDomain().toString();
    }

    private String getJidName(String userXmppId) {
        return String.format("%s@%s", userXmppId, getServiceName());
    }


    public boolean createAccount(String p_strId, String p_strPassword) {
        try {
            AccountManager mAccount = AccountManager.getInstance(mConnection);

            if (mAccount.supportsAccountCreation()) {

                Localpart jidPart = Localpart.from(p_strId);
                mAccount.sensitiveOperationOverInsecureConnection(true);

                mAccount.createAccount(jidPart, p_strPassword);
            }
            return true;
        }
        catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            if (e.getXMPPError().getCondition() == XMPPError.Condition.conflict) {
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean login(String p_strId, String p_strPassword) {
        if (p_strId != null && !p_strId.isEmpty() && p_strPassword != null && !p_strPassword.isEmpty()) {
            try {
                mConnection.login(p_strId, p_strPassword);
                addOneToOneMessageListner();
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String sendMessage(String userXmppId, String message) {
        Message w_msg = new Message();
        w_msg.setType(Message.Type.chat);
        w_msg.setBody(message);

        try {
            String jidId = getJidName(userXmppId);
            w_msg.setTo(JidCreate.from(jidId));
            mConnection.sendStanza(w_msg);

            return w_msg.getStanzaId();
        } catch (Exception e) {
            return null;
        }
    }

    public void setOneToOneChatListner(MChatMessageListner listner) {
        this.mOneToOneChatListner = listner;
    }

    public void getHistoryFromOneToOne(String id) {
        try {
            // not working
            if(false) {
                String jidid = getJidName(id);
                MessageRetreiveIQ myIQ = new MessageRetreiveIQ(jidid);
                myIQ.setType(IQ.Type.set);
                myIQ.setFrom(JidCreate.from(jidid));
                myIQ.setTo(JidCreate.from(jidid));
                mConnection.sendIqWithResponseCallback(myIQ, new StanzaListener() {
                    @Override
                    public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
                        String id = packet.getStanzaId();
                        Log.d("Server", id);
                    }
                }, new ExceptionCallback() {
                    @Override
                    public void processException(Exception exception) {
                        Log.d("Server Exception", exception.toString());
                    }
                }, 5000);
            }
        }
        catch (Exception e) {

        }
    }

    public boolean isConnected() {
        if(mConnection == null) {
            return false;
        }
        return mConnection.isConnected();
    }

    /*********************************************************************
     * Connection Listners
    *********************************************************************/
    public void connected(XMPPConnection connection) {
        if(mConnectionListner != null) {
            mConnectionListner.onConnected();
        }
    }
    public void authenticated(XMPPConnection connection, boolean resumed) {

    }
    public void connectionClosed() {
        if(mConnectionListner != null) {
            mConnectionListner.onClosed();
        }
    }
    public void connectionClosedOnError(Exception e) {
        if(mConnectionListner != null) {
            mConnectionListner.onConnectFailed(e);
        }
    }
    public void reconnectionSuccessful() {

    }
    public void reconnectingIn(int seconds) {

    }
    public void reconnectionFailed(Exception e) {

    }
}
