package kr.tracom.platform.tcp.cs.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import kr.tracom.platform.tcp.cs.TimsChannelEvent;
import kr.tracom.platform.net.config.TimsConfig;

public abstract class TimsServer implements TimsChannelEvent {
	private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    protected TimsConfig timsConfig;

    public TimsServer(TimsConfig timsConfig) {
        this.timsConfig = timsConfig;
    }
    
    public void run() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new TimsServerInitializer(timsConfig, this))
             //.option(ChannelOption.SO_BACKLOG, 128)
             //.option(ChannelOption.TCP_NODELAY, false)
             .childOption(ChannelOption.SO_KEEPALIVE, true);

            
            int port = timsConfig.getChannelPort();
            
            System.out.println("===================================================");
            System.out.println("Open Channel Port: " + port);
            System.out.println("===================================================");
            
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture();
        } 
        catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public void shutdown(){
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}