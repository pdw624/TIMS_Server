package kr.tracom.platform.launcher.channel.handler;

import io.netty.channel.Channel;
import kr.tracom.platform.launcher.channel.PayloadHandler;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.protocol.payload.PlEventRequest;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpSession;

import java.util.ArrayList;
import java.util.List;

public class EventRequest extends PayloadHandler {

    public EventRequest(TimsConfig timsConfig) {
        super(timsConfig);
    }

    @Override
    public List<Short> handle(Channel ch, TimsMessage timsMessage, TcpSession session) {
        List<Short> attributeList = new ArrayList<>();
        PlEventRequest payload = (PlEventRequest) timsMessage.getPayload();

        for (int i = 0; i < payload.getAttrCount(); i++) {
            AtMessage atMessage = payload.getAttrList().get(i);

            // 응답은 플랫폼에서 처리한다.
            responseEvent(ch, session, timsMessage.getHeader().getPacketId(), atMessage.getAttrId(), (byte)0x00);

            // 모든 이벤트는 서비스에서 처리한다.
            attributeList.add(atMessage.getAttrId());
        }

        return attributeList;
    }

    private void responseEvent(Channel ch, TcpSession tcpSession, byte packetId, short attributeId, byte result) {
        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.eventResponse(attributeId, result);
        TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(ch, tcpSession, timsMessage);
        tcpChannelMessage.setPacketId(packetId);

        TransactionManager.write(tcpChannelMessage);
    }
}
