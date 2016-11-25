package com.yj.chatting.chattingmodule;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.ping.provider.PingProvider;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.pubsub.provider.EventProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.time.provider.TimeProvider;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;

import java.util.UUID;

/**
 * Created by KCJ on 1/13/2016.
 */
class XMPPUtils {
    public static final String EXTENSION_PROPERTIES = "properties";
    public static final String EXTENSION_PROPERTIES_NS = "urn:xmpp:softwarejoint:properties:1";
    public static final String EXTENSION_PROPERTIES_TAG_TYPE = "bodyType";

    public enum XMPP_MESSAGE_TYPE {
        DELETED("deleted"),
        READ("read"),
        TEXT("text"),
        AUDIO("audio"),
        VIDEO("video"),
        OTHER("other");

        private final String msgType;

        XMPP_MESSAGE_TYPE(String f) {
            msgType = f;
        }

        public static XMPP_MESSAGE_TYPE getMsgTypeByVal(String type) {
            for (XMPP_MESSAGE_TYPE t : XMPP_MESSAGE_TYPE.values()) {
                if (t.msgType.equalsIgnoreCase(type)) {
                    return t;
                }
            }

            return XMPP_MESSAGE_TYPE.OTHER;
        }

        public String toString() {
            return msgType;
        }
    }

    public static StanzaFilter INCOMING_DELIVERY_RECEIPT =
            new AndFilter(StanzaTypeFilter.MESSAGE,
                    new AndFilter(MessageTypeFilter.CHAT,
                            new StanzaExtensionFilter(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE)));

    public static String getUsernamePriv(String jid) {
        return jid.split("@")[0].trim();
    }

    public static String getUsernameFromGroupJID(String roomJID) {
        return roomJID.split("/")[1].trim();
    }

    public static String getGroupId(String roomJID) {
        return roomJID.split("@")[0].trim();
    }

    public static String getRoomJID(String roomName) {
        //return roomName + AppConstant.XMPP_MUC_DOMAIN_SUFFIX;
        return  "";
    }

    public static String getUserJID(String username) {
        //return username + AppConstant.XMPP_USER_SUFFIX;
        return  "";
    }

    public static void addMessageBodyType(Message message, XMPP_MESSAGE_TYPE bodyType)
    {
        DefaultExtensionElement extensionElement = new DefaultExtensionElement(EXTENSION_PROPERTIES, EXTENSION_PROPERTIES_NS);
        extensionElement.setValue(EXTENSION_PROPERTIES_TAG_TYPE, bodyType.toString());
        message.addExtension(extensionElement);
    }

    public static XMPP_MESSAGE_TYPE getMessageBodyType(Message message)
    {
        DefaultExtensionElement extensionElement =
                message.getExtension(XMPPUtils.EXTENSION_PROPERTIES, XMPPUtils.EXTENSION_PROPERTIES_NS);

        String bodyType = extensionElement.getValue(XMPPUtils.EXTENSION_PROPERTIES_TAG_TYPE);
        return XMPP_MESSAGE_TYPE.getMsgTypeByVal(bodyType);
    }

    public static String getThreadId(String userId){
        String myUserId = "";// MainApplication.getInstance().getAppPreferences().getUserName();
        String myJID = XMPPUtils.getUserJID(myUserId);
        String userJID = XMPPUtils.getUserJID(userId);
        return (myJID.compareTo(userJID) > 0) ? (userJID + "-" + myJID) : (myJID + "-" + userJID);
    }

    public static Chat getOrCreateChat(String userId, XMPPTCPConnection sXmppConnection){
        String jid = userId.contains("@") ?  userId : XMPPUtils.getUserJID(userId);
        String threadId = XMPPUtils.getThreadId(userId);
        ChatManager chatManager = ChatManager.getInstanceFor(sXmppConnection);
        Chat chat = chatManager.getThreadChat(threadId);
        if(chat == null){
            chat = chatManager.createChat(jid, threadId, null);
        }
        return chat;
    }

    public static MultiUserChat getMultiUserChat(XMPPTCPConnection xmpptcpConnection, String groupId){
        String roomJID = XMPPUtils.getRoomJID(groupId);
        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(xmpptcpConnection);
        return multiUserChatManager.getMultiUserChat(roomJID);
    }
}
