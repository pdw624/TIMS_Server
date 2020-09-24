package kr.tracom.platform.launcher.channel.handler;

import io.netty.channel.Channel;
import kr.tracom.platform.launcher.channel.PayloadHandler;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.payload.PlGetRequest;
import kr.tracom.platform.tcp.model.TcpSession;

import java.util.ArrayList;
import java.util.List;

public class GetRequest extends PayloadHandler {

    public GetRequest(TimsConfig timsConfig) {
        super(timsConfig);
    }

    @Override
    public List<Short> handle(Channel ch, TimsMessage timsMessage, TcpSession session) {
        List<Short> attributeList = new ArrayList<>();

        PlGetRequest payload = (PlGetRequest) timsMessage.getPayload();
        short attrId;
        for (int i = 0; i < payload.getAttrCount(); i++) {
            attrId = payload.getAttrList().get(i);

            attributeList.add(attrId);
        }

        return attributeList;
    }
}
