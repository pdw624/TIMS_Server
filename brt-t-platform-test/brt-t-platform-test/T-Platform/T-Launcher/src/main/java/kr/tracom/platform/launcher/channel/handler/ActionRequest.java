package kr.tracom.platform.launcher.channel.handler;

import io.netty.channel.Channel;
import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.attribute.common.AtFtpResultRequest;
import kr.tracom.platform.launcher.channel.PayloadHandler;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.protocol.payload.PlActionRequest;
import kr.tracom.platform.service.manager.FtpManager;
import kr.tracom.platform.service.manager.PacketManager;
import kr.tracom.platform.tcp.model.TcpSession;

import java.util.ArrayList;
import java.util.List;

public class ActionRequest extends PayloadHandler {

    public ActionRequest(TimsConfig timsConfig) {
        super(timsConfig);
    }

    @Override
    public List<Short> handle(Channel ch, TimsMessage timsMessage, TcpSession session) {
        List<Short> attributeList = new ArrayList<Short>();

        PlActionRequest payload = (PlActionRequest) timsMessage.getPayload();
        AtMessage atMessage = payload.getAtMessage();

        if(atMessage.getAttrId() == AtCode.FTP_RESULT_REQ) {
            AtFtpResultRequest atFtpResultRequest = (AtFtpResultRequest)atMessage.getAttrData();

            PacketManager.responseAction(ch, session, timsConfig, timsMessage.getHeader().getPacketId(), (byte)0x00);

            FtpManager.completed(session.getSessionId(), atFtpResultRequest.getSourceFile());

            PacketManager.getRequest(ch, session, timsConfig, AtCode.VERSION);
        }

        attributeList.add(atMessage.getAttrId());

        return attributeList;
    }
}
