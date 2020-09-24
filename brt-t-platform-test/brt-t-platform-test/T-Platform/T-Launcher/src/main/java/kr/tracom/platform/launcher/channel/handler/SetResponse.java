package kr.tracom.platform.launcher.channel.handler;

import io.netty.channel.Channel;
import kr.tracom.platform.launcher.channel.PayloadHandler;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.net.protocol.attribute.AtResult;
import kr.tracom.platform.net.protocol.payload.PlSetResponse;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.model.TcpSession;

import java.util.ArrayList;
import java.util.List;

public class SetResponse extends PayloadHandler {

    public SetResponse(TimsConfig timsConfig) {
        super(timsConfig);
    }

    @Override
    public List<Short> handle(Channel ch, TimsMessage timsMessage, TcpSession session) {
        boolean bResponse = true;
        PlSetResponse payload = (PlSetResponse) timsMessage.getPayload();
        short attrId = 0;
        AtData attrData = null;
        List<Short> attributeList = new ArrayList<>();

        for (int i = 0; i < payload.getAttrCount(); i++) {
            AtResult atResult = payload.getResultList().get(i);
            if(0x00 != atResult.getResult()) {
                //TODO : 결과가 실패일 경우 어떻게 처리해야 할까?
                bResponse = false;
                break;
            } else {
            	attrId = payload.getResultList().get(i).getAtId();
                attributeList.add(attrId);
            }
        }

        // 요청에 대한 응답일 경우 패킷 대기 목록에서 제거한다.
        TransactionManager.responseOk(session.getSessionId(), timsMessage.getHeader().getPacketId(), bResponse,
                attrId, attrData);

        return attributeList;
    }
}
