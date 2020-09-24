package kr.tracom.platform.service.manager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.tcp.model.TcpSession;

public class SessionManager {

    public static TcpSession select(String platformId, String sessionId) {
        PlatformDao platformDao = new PlatformDao();
        TcpSession tcpSession = (TcpSession) platformDao.select(PlatformMapper.SESSION_SELECT,
                platformDao.buildMap("PLF_ID", platformId, "SESSION_ID", sessionId));

        return tcpSession;
    }

    public static void login(TcpSession tcpSession) {
        PlatformDao platformDao = new PlatformDao();
        platformDao.update(PlatformMapper.SESSION_LOGIN, tcpSession.toMap());
    }

    public static void logout(TcpSession tcpSession) {
        PlatformDao platformDao = new PlatformDao();
        platformDao.update(PlatformMapper.SESSION_LOGOUT, tcpSession.toMap());
    }

    public static void forceClose(Channel channel, String sessionId, String log) {
        if(channel != null) {
            channel.close().addListener(new ChannelFutureListener() {
                //진짜 종료될 경우 호출될 콜백 함수를 작성
                public void operationComplete(ChannelFuture future) {
                    PacketLogManager.writeLog(PacketLogManager.FORCE_CLOSE, channel, log, sessionId);
                }
            });
        }
    }
}
