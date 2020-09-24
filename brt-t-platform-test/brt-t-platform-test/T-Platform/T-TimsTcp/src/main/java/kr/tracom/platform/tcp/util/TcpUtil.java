package kr.tracom.platform.tcp.util;

import io.netty.channel.Channel;

import java.io.IOException;
import java.net.Socket;

public class TcpUtil {

	public static boolean availablePort(int port) {
		try (Socket ignored = new Socket("localhost", port)) {
			return false;
		} catch (IOException ignored) {
			return true;
		}
	}
	public static String getRemoteKey(Channel ch) {
    	String clientIp = ch.remoteAddress().toString();    	
    	return clientIp.substring(1);
    }
	
	public static String getRemoteIp(Channel ch) {
		String clientKey = ch.remoteAddress().toString();
    	String remoteIp = clientKey.substring(1, clientKey.indexOf(':'));
    	return remoteIp;
	}
}
