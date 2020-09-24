package kr.tracom.platform.tcp.model;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class TcpChannelSession {
	private Channel channel;
	private TcpSession session;
	
	public TcpChannelSession(Channel channel, TcpSession session) {
		this.channel = channel;
		this.session = session;
	}
}
