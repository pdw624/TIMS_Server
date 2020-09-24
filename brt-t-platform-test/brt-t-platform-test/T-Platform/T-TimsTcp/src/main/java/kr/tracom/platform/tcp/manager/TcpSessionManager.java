package kr.tracom.platform.tcp.manager;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import kr.tracom.platform.tcp.model.TcpChannelSession;
import kr.tracom.platform.tcp.model.TcpSession;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TcpSessionManager {	
	public static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	public static final AttributeKey<TcpSession> channelKey = AttributeKey.valueOf("TIMS-NET.KEY");

	public static TcpSession getTcpSession(Channel ch) {
		return ch.attr(channelKey).get();
	}

	public static int getSize() {
		return channelGroup.size();
	}

	public static void createSession(Channel ch, TcpSession session) {
		ch.attr(channelKey).set(session);
		channelGroup.add(ch);
	}

	public static void removeSession(Channel ch) {
		ch.attr(channelKey).remove();
		channelGroup.remove(ch);
	}

	/*
	public static void clear() {
		for (Channel ch : channelGroup) {
			removeSession(ch);
			ch.close();
		}
	}
	*/

	public static Iterator<TcpChannelSession> getSession() {
		List<TcpChannelSession> list = new ArrayList<>();
		for (Channel ch : channelGroup) {
			TcpSession session = ch.attr(channelKey).get();
			
			list.add(new TcpChannelSession(ch, session));
		}
		return list.iterator();
	}

	public static Channel getChannelBySessionId(String sessionId) {
		for(Channel ch : channelGroup) {
			TcpSession tcpSession = getTcpSession(ch);
			if(tcpSession.getSessionId().equals(sessionId)) {
				return ch;
			}
		}
		return null;
	}

	public static TcpChannelSession getTcpChannelSessionById(String sessionId) {
		TcpChannelSession tcpChannelSession = null;
		for (Channel channel : channelGroup) {
			TcpSession session = channel.attr(channelKey).get();

			if(session.getSessionId().equals(sessionId)) {
				tcpChannelSession = new TcpChannelSession(channel, session);
				break;
			}
		}
		return tcpChannelSession;
	}
	
	//test
	public static TcpChannelSession tcpTest() {
		TcpChannelSession tcpChannelSession = null;
		System.out.println("in TcpTest");
		System.out.println(channelGroup);
		System.out.println("-------------------");
		for(Channel channel : channelGroup) {
			TcpSession session = channel.attr(channelKey).get();
			System.out.println(session);
			tcpChannelSession = new TcpChannelSession(channel, session);
			System.out.println(tcpChannelSession);
			break;
		}
		return tcpChannelSession;
	}
	
	public static Iterator<TcpChannelSession> getTcpChannelSessionByRemoteType(String remoteType) {
		List<TcpChannelSession> list = new ArrayList<>();
		for (Channel channel : channelGroup) {
			TcpSession session = channel.attr(channelKey).get();
			
			if(session.getRemoteType().equals(remoteType)) {
				list.add(new TcpChannelSession(channel, session));
			}
		}
		return list.iterator();
	}
}
