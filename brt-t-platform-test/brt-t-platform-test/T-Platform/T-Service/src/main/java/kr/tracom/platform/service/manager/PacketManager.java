package kr.tracom.platform.service.manager;

import org.joda.time.DateTime;

import io.netty.channel.Channel;
import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.attribute.common.AtFtpResultResponse;
import kr.tracom.platform.attribute.common.AtTimeStamp;
import kr.tracom.platform.attribute.common.AtTimeStampRequest;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpSession;

public class PacketManager {

    public static void requestAuth(Channel ch, TimsConfig timsConfig) {
        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsSendMessage = builder.initRequest(AtCode.DEVICE_AUTH);
        TransactionManager.write(new TcpChannelMessage(ch, null, timsSendMessage));
    }

    public static void initPacket(Channel ch, TcpSession tcpSession, TimsConfig timsConfig) {
        if(tcpSession.getRemoteType().equalsIgnoreCase(CodeManager.RoutingGroupName.STN.getValue())) {
            PacketManager.timeSyncAction(ch, tcpSession, timsConfig);

            PacketManager.getRequest(ch, tcpSession, timsConfig, AtCode.VERSION);
            PacketManager.getRequest(ch, tcpSession, timsConfig, AtCode.STATUS);

            FtpManager.checkFileTransfer(CodeManager.RoutingGroupId.STN.getValue(), timsConfig, tcpSession.getSessionId());

        } else if(tcpSession.getRemoteType().equalsIgnoreCase(CodeManager.RoutingGroupName.BUS.getValue())) {
            PacketManager.getRequest(ch, tcpSession, timsConfig, AtCode.VERSION);

            FtpManager.checkFileTransfer(CodeManager.RoutingGroupId.STN.getValue(), timsConfig, tcpSession.getSessionId());

        } else if(tcpSession.getRemoteType().startsWith("IMP")) {
        	PacketManager.timeSyncSet(ch, tcpSession, timsConfig);
            PacketManager.getRequest(ch, tcpSession, timsConfig, AtCode.IMP_PROCESS_LIST);
        }
    }

    public static void timeSyncAction(Channel ch, TcpSession tcpSession, TimsConfig timsConfig) {
        AtTimeStampRequest atData = new AtTimeStampRequest();
        atData.setTimeStamp(new AtTimeStamp(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT)));

        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.actionRequest(atData);

        TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(ch, tcpSession, timsMessage);
        tcpChannelMessage.setResponse(true);
        TransactionManager.write(tcpChannelMessage);
    }
    
    public static void timeSyncSet(Channel ch, TcpSession tcpSession, TimsConfig timsConfig) {
        AtTimeStamp atData = new AtTimeStamp(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.setRequest(atData);

        TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(ch, tcpSession, timsMessage);
        tcpChannelMessage.setResponse(true);
        TransactionManager.write(tcpChannelMessage);
    }

    public static void getRequest(Channel ch, TcpSession tcpSession, TimsConfig timsConfig, short attrId) {
        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsSendMessage = builder.getRequest(attrId);
        TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(ch, tcpSession, timsSendMessage);

        TransactionManager.write(tcpChannelMessage);
    }

    public static void responseAction(Channel ch, TcpSession tcpSession, TimsConfig timsConfig, byte packetId, byte result) {
        AtFtpResultResponse atResponse = new AtFtpResultResponse();
        atResponse.setResult(result);

        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage timsMessage = builder.actionResponse(atResponse);
        TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(ch, tcpSession, timsMessage);
        tcpChannelMessage.setPacketId(packetId);

        TransactionManager.write(tcpChannelMessage);
    }
}
