package kr.tracom.platform.tcp.model;

import io.netty.channel.Channel;
import kr.tracom.platform.net.protocol.TimsMessage;
import lombok.Data;
import org.joda.time.DateTime;

@Data
public class TcpChannelMessage {
	private Channel channel;
	private TcpSession session;
	private TimsMessage message;
	private int retry;
	private DateTime sendDateTime;
	private DateTime registerDateTime;
	private boolean isResponse;
	private boolean isBlocking;
	private byte packetId;
	
	public TcpChannelMessage(Channel channel, TcpSession session, TimsMessage message) {
		this.channel = channel;
		this.session = session;
		this.message = message;
		this.retry = 0;
		this.sendDateTime = new DateTime();
		this.registerDateTime = new DateTime();

		this.isResponse = false;
		this.isBlocking = false;
		this.packetId = 0;
	}

	public int nextRetry() {
		this.retry++;
		this.sendDateTime = new DateTime();

		return retry;
	}
}