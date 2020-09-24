package kr.tracom.platform.tcp.cs.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.tcp.cs.TimsChannelEvent;
import kr.tracom.platform.tcp.cs.TimsDecoder;
import kr.tracom.platform.tcp.cs.TimsEncoder;
import kr.tracom.platform.tcp.cs.TimsReadTimeoutHandler;


public class TimsServerInitializer extends ChannelInitializer<SocketChannel> {
	private TimsChannelEvent channelEvent;
    private TimsConfig timsConfig;

    public TimsServerInitializer(TimsConfig timsConfig, TimsChannelEvent channelEvent) {
        this.timsConfig = timsConfig;
        this.channelEvent = channelEvent;
    }
    
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        if(timsConfig.getReadTimeout() > 0) {
            p.addLast("read_timeout", new TimsReadTimeoutHandler(timsConfig));
        }
        
        p.addLast("decoder", new TimsDecoder(this.timsConfig));
        p.addLast("encoder", new TimsEncoder(this.timsConfig));
        p.addLast("handler", new TimsServerHandler(this.timsConfig, this.channelEvent));
    }
}
