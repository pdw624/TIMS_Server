package kr.tracom.platform.tcp.cs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import kr.tracom.platform.common.util.CrcUtil;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsTail;
import kr.tracom.platform.net.protocol.factory.TimsHeaderFactory;
import kr.tracom.platform.net.util.ByteHelper;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpSession;
import kr.tracom.platform.tcp.util.TcpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TimsDecoder extends ByteToMessageDecoder {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private TimsConfig timsConfig;

	public TimsDecoder(TimsConfig timsConfig) {
		this.timsConfig = timsConfig;
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
		Object obj = decode(ctx, buffer);
        if (obj != null) {
            out.add(obj);
            obj = null;
        }
	}
	
	private Object decode(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        try {
            int headerSize = TimsHeaderFactory.getHeaderSize(this.timsConfig);
            if(byteBuf.readableBytes() < headerSize) {
                return null;
            }

            byteBuf.markReaderIndex();

            byte[] headBuffer = new byte[headerSize];
            byteBuf.readBytes(headBuffer);

            if(headBuffer[0] != this.timsConfig.getIndicator()) {
                byteBuf.clear();
                return null;
            }

            int payloadSize = TimsMessage.getPayloadSize(timsConfig, headBuffer, headBuffer.length);
            int packetSize = payloadSize + TimsTail.SIZE;

            if(packetSize >= this.timsConfig.getMaxPacketSize()) {
                byteBuf.clear();
                return null;
            }

            if (byteBuf.readableBytes() < packetSize) {
                byteBuf.resetReaderIndex();
                return null;
            }

            byte[] dataBuffer = new byte[packetSize];
            byteBuf.readBytes(dataBuffer);

            byte[] totalBuffer = new byte[headerSize + packetSize];
            System.arraycopy(headBuffer, 0, totalBuffer, 0, headerSize);
            System.arraycopy(dataBuffer, 0, totalBuffer, headerSize, packetSize);

            // TCP 통신모듈에서 바이너리 패킷을 로깅한다.
            String remoteIp = TcpUtil.getRemoteKey(ctx.channel());
            String packetLog = "[RECV] (" + remoteIp + ") " + ByteHelper.toHex(totalBuffer, 0, totalBuffer.length);
            logger.debug(packetLog);

            TimsMessage timsMessage = new TimsMessage(timsConfig, totalBuffer, totalBuffer.length);
            if(timsMessage.parse() < 0) {
                logger.debug("channel packet parse error : " + timsMessage.getError());
                logger.debug(ByteHelper.toHex(totalBuffer, 0, totalBuffer.length));

                if(timsMessage.getError() == -10) {
                    TimsStackHandler.response(ctx.channel(), timsConfig, TimsStackHandler.ProtocolError.HeaderSize.getValue(), timsMessage);
                } else if(timsMessage.getError() == -20) {
                    TimsStackHandler.response(ctx.channel(), timsConfig, TimsStackHandler.ProtocolError.ApduSize.getValue(), timsMessage);
                }

                ctx.channel().close();
                byteBuf.clear();
                return null;
            }

            // 프로토콜 버전 체크
            if(timsConfig.getVersion() != timsMessage.getHeader().getProtocolVersion()) {
                TimsStackHandler.response(ctx.channel(), timsConfig, TimsStackHandler.ProtocolError.Version.getValue(), timsMessage);
                ctx.close();
            }

            // CRC 체크
            if(this.timsConfig.getCrcEnable() == 1) {
                int remoteCrc = ByteHelper.unsigned(timsMessage.getTail().getCrc());
                int localCrc = ByteHelper.unsigned(CrcUtil.makeCrc16(totalBuffer, 0, totalBuffer.length - TimsTail.SIZE));

                /*
                System.out.println(ctx.channel().remoteAddress() + " ---------------------------------------------------------");
                System.out.println("remote crc : " + remoteCrc);
                System.out.println("local  crc : " + localCrc);

                System.out.println("byte : " + ByteHelper.toHex(totalBuffer, 0, totalBuffer.length));
                */

                if (remoteCrc != localCrc) {
                    TimsStackHandler.response(ctx.channel(), timsConfig, TimsStackHandler.ProtocolError.Checksum.getValue(), timsMessage);

                    byteBuf.clear();
                    return null;
                }
            }

            // TCP 통신모듈에서 패킷 사용량을 계산한다.
            TcpSession session = TcpSessionManager.getTcpSession(ctx.channel());
            if(session != null) {
                session.setInPacketUsage(session.getInPacketUsage() + totalBuffer.length);
            }

            return timsMessage;

        } catch(Exception e) {
            logger.error("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            logger.error(e.getMessage());
            logger.error("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

            return null;
        }
    }
}

