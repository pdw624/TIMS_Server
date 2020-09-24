package kr.tracom.platform.launcher.http.controller;

import kr.tracom.platform.launcher.http.builder.JsonBuilder;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.service.manager.SyncManager;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpChannelSession;

public abstract class ChannelController {
    protected String relayTimsMessage(String clientId, TimsMessage timsMessage, boolean bImmediately) {
        TcpChannelSession tcpChannelSession = TcpSessionManager.getTcpChannelSessionById(clientId);
        if(tcpChannelSession == null) {
            if(bImmediately) {
                return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_NOT_CONNECTED);
            } else {
                return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
            }
        } else {
            TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(
                    tcpChannelSession.getChannel(),
                    tcpChannelSession.getSession(),
                    timsMessage);

            tcpChannelMessage.setResponse(true);
            tcpChannelMessage.setBlocking(bImmediately);

            TransactionManager.write(tcpChannelMessage);

            boolean bSuccess = true;
            if(bImmediately) {
                bSuccess = TransactionManager.waitOn(3);
            }

            if(bSuccess) {
                return JsonBuilder.getJson(JsonBuilder.SUCS_CD, JsonBuilder.SUCS_MESSAGE);
            } else {
                if(SyncManager.syncObject.getSyncId() == tcpChannelMessage.getMessage().getHeader().getPacketId()) {
                    return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_TIMEOUT);
                } else {
                    return JsonBuilder.getJson(JsonBuilder.FAIL_CD, JsonBuilder.FAIL_OVER_CMD);
                }
            }
        }
    }

    protected boolean getAttributeData(String clientId, TimsMessage timsMessage) {
        TcpChannelSession tcpChannelSession = TcpSessionManager.getTcpChannelSessionById(clientId);

        if(tcpChannelSession == null) {
            return false;
        } else {
            TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(
                    tcpChannelSession.getChannel(),
                    tcpChannelSession.getSession(),
                    timsMessage);

            tcpChannelMessage.setResponse(true);
            tcpChannelMessage.setBlocking(true);

            TransactionManager.write(tcpChannelMessage);

            boolean bSuccess = TransactionManager.waitOn(3);

            return bSuccess;
        }
    }
}
