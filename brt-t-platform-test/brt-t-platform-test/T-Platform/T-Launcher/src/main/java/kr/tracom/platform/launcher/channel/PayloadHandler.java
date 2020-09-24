package kr.tracom.platform.launcher.channel;

import io.netty.channel.Channel;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.tcp.model.TcpSession;

import java.util.List;

public abstract class PayloadHandler {
    protected TimsConfig timsConfig;

    public PayloadHandler(TimsConfig timsConfig) {
        this.timsConfig = timsConfig;
    }

    public abstract List<Short> handle(Channel ch, TimsMessage timsMessage, TcpSession session);
}
