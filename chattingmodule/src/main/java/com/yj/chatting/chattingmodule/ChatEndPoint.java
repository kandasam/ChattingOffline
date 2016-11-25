package com.yj.chatting.chattingmodule;

import android.content.Context;

/**
 * Created by KCJ on 1/9/2016.
 */
public class ChatEndPoint {

    /************************************************************
     *      Members
     * *************************************************************/

    // xmpp server information
    private String m_strServerName = null;
    private String m_strConferenceServiceName = null;
    private int    m_nServerPort = 0;

    private XMPPManager m_vXMPPManger = null;

    public ChatEndPoint(Context app) {
        m_vXMPPManger = XMPPManager.getInstance(app);
    }

    public void connect(String serverName, String conferServiceName, int serverPort, ServerConnectListner listner) {
        m_strServerName = serverName;
        m_strConferenceServiceName = conferServiceName;
        m_nServerPort = serverPort;

        m_vXMPPManger.connect(serverName, serverPort, listner);
    }

    public boolean registerUser(String userId, String password) {
        return m_vXMPPManger.createAccount(userId, password);
    }


}
