package kr.tracom.platform.launcher.channel.handler;

import io.netty.channel.Channel;
import kr.tracom.platform.launcher.channel.PayloadHandler;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.protocol.payload.PlSetRequest;
import kr.tracom.platform.tcp.model.TcpSession;

import java.util.ArrayList;
import java.util.List;

public class SetRequest extends PayloadHandler {

    public SetRequest(TimsConfig timsConfig) {
        super(timsConfig);
    }

    @Override
    public List<Short> handle(Channel ch, TimsMessage timsMessage, TcpSession session) {
        List<Short> attributeList = new ArrayList<>();

        PlSetRequest payload = (PlSetRequest) timsMessage.getPayload();
        for (int i = 0; i < payload.getAttrCount(); i++) {
            AtMessage atMessage = payload.getAttrList().get(i);

            attributeList.add(atMessage.getAttrId());
        }

        return attributeList;
    }
}
