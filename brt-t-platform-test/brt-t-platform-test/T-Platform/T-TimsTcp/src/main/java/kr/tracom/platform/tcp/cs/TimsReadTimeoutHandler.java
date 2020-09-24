package kr.tracom.platform.tcp.cs;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import kr.tracom.platform.net.config.TimsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimsReadTimeoutHandler extends ReadTimeoutHandler {
    private static final Logger logger = LoggerFactory.getLogger(TimsReadTimeoutHandler.class);

    private TimsConfig timsConfig;
    public TimsReadTimeoutHandler(TimsConfig timsConfig) {
        super(timsConfig.getReadTimeout());

        this.timsConfig = timsConfig;
    }

    @Override
    protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
        super.readTimedOut(ctx);

        /*
        TimsMessage timsMessage = new TimsMessage(this.timsConfig);
        TimsStackHandler.response(ctx.channel(), this.timsConfig, TimsStackHandler.ProtocolError.NoError.getValue(), timsMessage);
        */

        String clientIp = ctx.channel().remoteAddress().toString();
        logger.info("read timeout : " + clientIp);

        ctx.channel().close();
    }
}
