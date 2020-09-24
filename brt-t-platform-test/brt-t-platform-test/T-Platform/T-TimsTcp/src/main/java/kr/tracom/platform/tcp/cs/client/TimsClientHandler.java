package kr.tracom.platform.tcp.cs.client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.tcp.cs.TimsChannelEvent;

public class TimsClientHandler extends SimpleChannelInboundHandler<TimsMessage> {
	private TimsClient timsClient;	
	private TimsChannelEvent channelEvent;
	
	public TimsClientHandler(TimsClient timsClient, TimsChannelEvent channelRead) {
		this.timsClient = timsClient;
		this.channelEvent = channelRead;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {		
		this.channelEvent.channelActive(ctx.channel());
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) {			
		this.channelEvent.channelInactive(ctx.channel());
	}	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TimsMessage timsMessage) throws Exception {
		this.channelEvent.channelRead(ctx.channel(), timsMessage);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        t.printStackTrace();
        if (!ctx.channel().isActive()) {
            ctx.writeAndFlush("").addListener(ChannelFutureListener.CLOSE);
        }
    }
	
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        final EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(new Runnable() {
            public void run() {
                Bootstrap b = timsClient.configureBootstrap(new Bootstrap(), loop);
                ChannelFuture f = b.connect();
                try {
                    f.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 10, TimeUnit.SECONDS);
    }   	
}
