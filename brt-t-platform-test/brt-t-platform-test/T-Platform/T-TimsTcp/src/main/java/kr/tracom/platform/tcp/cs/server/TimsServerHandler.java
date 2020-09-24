package kr.tracom.platform.tcp.cs.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.tcp.cs.TimsChannelEvent;
import kr.tracom.platform.tcp.util.TcpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimsServerHandler extends SimpleChannelInboundHandler<TimsMessage> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private TimsConfig timsConfig;
	private TimsChannelEvent channelEvent;
	
	public TimsServerHandler(TimsConfig timsConfig, TimsChannelEvent channelEvent) {
		this.timsConfig = timsConfig;
		this.channelEvent = channelEvent;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		String remoteIp = TcpUtil.getRemoteIp(ctx.channel());
    	String packetLog = "[ACTV] (" + remoteIp + ")"; 
		logger.debug(packetLog);

		this.channelEvent.channelActive(ctx.channel());

		//System.out.println(packetLog);
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) {
		String remoteIp = TcpUtil.getRemoteIp(ctx.channel());
    	String packetLog = "[INAT] (" + remoteIp + ")"; 
		logger.debug(packetLog);

		this.channelEvent.channelInactive(ctx.channel());

		//System.out.println(packetLog);
	}	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TimsMessage timsMessage) throws Exception {
		//여기서 다 읽어옴 aaaaa
		this.channelEvent.channelRead(ctx.channel(), timsMessage);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
		logger.error(t.getMessage());
		/*
        t.printStackTrace();
        if (!ctx.channel().isActive()) {
            ctx.writeAndFlush("").addListener(ChannelFutureListener.CLOSE);
        }
        */
    }
}
