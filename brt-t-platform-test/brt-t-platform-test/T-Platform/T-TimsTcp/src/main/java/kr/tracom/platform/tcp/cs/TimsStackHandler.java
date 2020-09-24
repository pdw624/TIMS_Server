package kr.tracom.platform.tcp.cs;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import kr.tracom.platform.net.config.TimsCode;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimsStackHandler {
    private static final Logger logger = LoggerFactory.getLogger(TimsStackHandler.class);

    private static final byte SUCCESS = 0x01;
    private static final byte FAIL = 0x02;

    public enum ProtocolError {
        NoError((byte)0x00),
        Checksum((byte)0x21),
        Version((byte)0x22),
        HeaderSize((byte)0x23),
        ApduSize((byte)0x24),
        Timeout((byte)0x25),
        PacketId((byte)0x26),
        PacketIndex((byte)0x27),
        PacketOption((byte)0x28),
        OpCode((byte)0x29),
        SrcAddress((byte)0x2A),
        DstAddress((byte)0x2B),
        ApduBig((byte)0x2C);

        private byte value;

        ProtocolError(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }

    public static void response(Channel channel, TimsConfig timsConfig, final byte errorCode, TimsMessage recvMessage) {
        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage sendMessage = builder.protocolResponse(FAIL,
                recvMessage.getHeader().getOpCode(), recvMessage.getSrcAddress(),
                errorCode,
                recvMessage.getHeader().getPacketId());

        channel.writeAndFlush(sendMessage).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                String log = "Protocol Stack Response : " + TimsCode.getProtocolError(errorCode) + "\r\n";
                log += recvMessage.getLog();
                logger.debug(log);
            }
        });
    }

    public static void responseAndClose(Channel channel, TimsConfig timsConfig, final byte errorCode, TimsMessage recvMessage) {
        TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
        TimsMessage sendMessage = builder.protocolResponse(FAIL,
                recvMessage.getHeader().getOpCode(), recvMessage.getSrcAddress(),
                errorCode,
                recvMessage.getHeader().getPacketId());

        channel.writeAndFlush(sendMessage).addListener(ChannelFutureListener.CLOSE);
    }
}
