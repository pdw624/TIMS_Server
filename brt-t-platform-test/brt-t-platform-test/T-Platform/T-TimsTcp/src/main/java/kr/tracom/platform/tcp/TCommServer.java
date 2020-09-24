package kr.tracom.platform.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import kr.tracom.platform.attribute.AtCode;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.tcp.cs.server.TimsServer;


public class TCommServer {
	public static void main(String[] args) {
		/*
		TimsNet.setConfig((byte)0x01, "A", "EUC-KR", "big",
				(byte)3, (byte)3, (byte)0, (byte)0, (byte)0, (byte)0, 300);
		*/

		TimsConfig timsConfig = new TimsConfig();

		TimsServer timsServer = new TimsServer(timsConfig) {
			public void channelActive(Channel ch) {
				System.out.println(">>>" + ch.remoteAddress().toString());

				TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
				TimsMessage timsMessage = builder.getRequest(AtCode.DEVICE_AUTH);

				ch.writeAndFlush(timsMessage).addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) throws Exception {
						if (future.isSuccess()) {
							System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
						} else {
							System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
						}
					}
				});
			}

			public void channelInactive(Channel ch) {
				System.out.println("<<<" + ch.remoteAddress().toString());
			}

			public void channelRead(Channel ch, TimsMessage timsMessage) {
				System.out.println(timsMessage.getLog());
			}			
		};
		
		timsServer.run();
	}
}
