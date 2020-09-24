package kr.tracom.platform.service.manager;

import io.netty.channel.Channel;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.tcp.util.TcpUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PacketLogManager {
	private static final Logger logger = LoggerFactory.getLogger(PacketLogManager.class);

	public static final String OPEN_CHANNEL = "O";
	public static final String CLOSE_CHANNEL = "C";
	public static final String FORCE_CLOSE = "F";
	public static final String RECV_PACKET = "R";
	public static final String SEND_PACKET = "W";
	private static final String GBN_LINE1 = "-------------------------------------------------------";
	private static final String GBN_LINE2 = "=======================================================";

	// slf4j로 처리
	public static void writePacket(String serviceDir, String sessionId, String log) {
		String nowDate = DateTime.now().toString(PlatformConfig.PLF_DATE_FORMAT);
		String nowTIme = DateTime.now().toString(PlatformConfig.PLF_TIME_FORMAT);
		
		File nowDir = new File(serviceDir + "/" + nowDate);
		if(!nowDir.isDirectory()) {
			nowDir.mkdirs();
		}

		String logFileName = String.format("%s/%s/%s.log", serviceDir, nowDate, sessionId);		
		String logMessage = String.format("[%s] : %s", nowTIme, log);
				
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(logFileName, true));
			bw.write(logMessage);
			bw.newLine();
			bw.close();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public static void writeLog(String logType, Channel ch, String log, String sessionId) {
		String remoteIp = TcpUtil.getRemoteKey(ch);
		String packetLog;

		if(logType.equals(OPEN_CHANNEL)) {
			packetLog = String.format("[%s] %s channel open %s", logType, remoteIp, GBN_LINE1);
		} else if(logType.equals(CLOSE_CHANNEL)) {
			packetLog = String.format("[%s] %s client close %s", logType, remoteIp, GBN_LINE1);
		} else if(logType.equals(FORCE_CLOSE)) {
			packetLog = String.format("[%s] %s force close %s %s", logType, remoteIp, log, GBN_LINE2);
		} else {
			packetLog = String.format("[%s] %s", logType, log);
		}

		if (sessionId == null) {
			sessionId = "unauthorized";
		}

		MDC.put("sessionid", sessionId);
		logger.info(packetLog);
		MDC.clear();
	}
}
