package kr.tracom.platform.tcp.cs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.util.ByteHelper;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpSession;
import kr.tracom.platform.tcp.util.TcpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimsEncoder extends MessageToByteEncoder<TimsMessage> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private TimsConfig timsConfig;

	public TimsEncoder(TimsConfig timsConfig) {
		this.timsConfig = timsConfig;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, TimsMessage timsMessage, ByteBuf byteBuf) throws Exception {
		byte[] buffer = timsMessage.toByte();
		int size = buffer.length;

		byteBuf.writeBytes(buffer, 0, size);
		
		// TCP 통신모듈에서 패킷 사용량을 계산한다.
        TcpSession session = TcpSessionManager.getTcpSession(ctx.channel());
        if(session != null) {
        	session.setOutPacketUsage(session.getOutPacketUsage() + size);
        }
        
		// TCP 통신모듈에서 바이너리 패킷을 로깅한다.
    	String remoteIp = TcpUtil.getRemoteKey(ctx.channel());
    	String packetLog = "[SEND] (" + remoteIp + ") " + ByteHelper.toHex(buffer, 0, size);
		logger.debug(packetLog);
	}
}
