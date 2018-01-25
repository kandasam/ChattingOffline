package com.mario.mchat.IQ;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by paulo on 5/23/2017.
 */

public class MessageRetreiveIQ extends IQ {

    private String mJidId;
    public MessageRetreiveIQ(String userId) {
        super("list ", "urn:xmpp:archive");
        mJidId = userId;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("with",mJidId);
        xml.rightAngleBracket();
        xml.append("<set xmlns='http://jabber.org/protocol/rsm'><max>100</max></set>");
        xml.closeElement("list");
        return xml;
    }
}
