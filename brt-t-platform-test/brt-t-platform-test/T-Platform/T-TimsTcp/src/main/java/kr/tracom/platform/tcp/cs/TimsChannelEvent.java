package kr.tracom.platform.tcp.cs;

import io.netty.channel.Channel;
import kr.tracom.platform.net.protocol.TimsMessage;

public interface TimsChannelEvent {
	void channelActive(Channel ch);
	void channelInactive(Channel ch);
	void channelRead(Channel ch, TimsMessage timsMessage);
}
