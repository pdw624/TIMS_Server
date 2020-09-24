package kr.tracom.platform.launcher.channel.handler;

import io.netty.channel.Channel;
import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.attribute.common.AtDeviceAuth;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.launcher.channel.PayloadHandler;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsTail;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.protocol.factory.TimsHeaderFactory;
import kr.tracom.platform.net.protocol.payload.PlGetRequest;
import kr.tracom.platform.net.protocol.payload.PlGetResponse;
import kr.tracom.platform.net.protocol.payload.PlInitResponse;
import kr.tracom.platform.net.util.ByteHelper;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.manager.PacketLogManager;
import kr.tracom.platform.service.manager.PacketManager;
import kr.tracom.platform.service.manager.SessionManager;
import kr.tracom.platform.tcp.manager.AccessManager;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpSession;
import kr.tracom.platform.tcp.util.TcpUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class InitResponse extends PayloadHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public InitResponse(TimsConfig timsConfig) {
        super(timsConfig);
    }

    @Override
    public List<Short> handle(Channel ch, TimsMessage timsMessage, TcpSession session) {
        PlInitResponse payload = (PlInitResponse) timsMessage.getPayload();
        for (int i = 0; i < payload.getAttrCount(); i++) {
            AtMessage atMessage = payload.getAttrList().get(i);
            if (atMessage.getAttrId() == AtCode.DEVICE_AUTH) {
                AtDeviceAuth attribute = (AtDeviceAuth) atMessage.getAttrData();

                responseAuth(ch, attribute);
            }
        }

        return null;
    }

    private void responseAuth(Channel newChannel, AtDeviceAuth attribute) {
        String sessionId = attribute.getDeviceId();
        
        if(sessionId == null || sessionId.isEmpty()) {
            logger.debug("Session id is empty");
            SessionManager.forceClose(newChannel, sessionId, "Session ID Empty");
            return;
        }

        TcpSession tcpSession = SessionManager.select(timsConfig.getPlatformId(), sessionId);
        // 등록이 안된 세션이면
        if(tcpSession == null) {
            logger.debug("Unregistered ID : " + sessionId);
            SessionManager.forceClose(newChannel, sessionId, "Unregistered ID");
            return;
        }

        Channel oldChannel = TcpSessionManager.getChannelBySessionId(sessionId);
        if(oldChannel != null) {
            logger.debug("Already Connection : " + sessionId);
            SessionManager.forceClose(oldChannel, sessionId, "Already connection");
        }

        byte[] remoteKey = attribute.getAccessKey();
        byte[] localKey = AccessManager.getKey(sessionId);

        if("true".equalsIgnoreCase(AppConfig.get("platform.auth"))) {
            if (!Arrays.equals(remoteKey, localKey)) {
                logger.debug(sessionId + " : " + ByteHelper.toHex(remoteKey) + " : " + ByteHelper.toHex(localKey));

                SessionManager.forceClose(newChannel, sessionId, "Fail to login");
                return;
            }
        }

        int requestPacketSize = TimsHeaderFactory.getHeaderSize(timsConfig) + PlGetRequest.SIZE + TimsTail.SIZE;
        int responsePacketSize = TimsHeaderFactory.getHeaderSize(timsConfig) + PlGetResponse.SIZE + AtDeviceAuth.SIZE + TimsTail.SIZE;

        tcpSession.setPlatformId(timsConfig.getPlatformId());
        tcpSession.setSessionIp(TcpUtil.getRemoteKey(newChannel));
        tcpSession.setLoginDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));
        tcpSession.setLogoutDateTime("-");
        tcpSession.setLastDateTime("-");

        tcpSession.setInPacketUsage(tcpSession.getInPacketUsage() + requestPacketSize);
        tcpSession.setOutPacketUsage(tcpSession.getOutPacketUsage() + responsePacketSize);

        TcpSessionManager.createSession(newChannel, tcpSession);
        
        SessionManager.login(tcpSession);

        PacketLogManager.writeLog(PacketLogManager.OPEN_CHANNEL, newChannel, null, tcpSession.getSessionId());

        PacketManager.initPacket(newChannel, tcpSession, timsConfig);
    }
}
