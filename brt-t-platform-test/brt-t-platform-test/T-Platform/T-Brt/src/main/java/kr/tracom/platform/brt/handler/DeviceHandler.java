package kr.tracom.platform.brt.handler;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.tracom.platform.attribute.brt.AtDeviceLocation;
import kr.tracom.platform.attribute.brt.AtDeviceStatus;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.service.manager.ErrorManager;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpChannelSession;

public class DeviceHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String platformId = AppConfig.get("platform.id");
	
	private static final DeviceHandler instance = new DeviceHandler();
	
	public static DeviceHandler getInstance() {
		return instance;
	}
	
	public AtDeviceLocation getDeviceLocation() throws Exception {
		AtDeviceLocation location = new AtDeviceLocation();
		logger.debug("Device Location Get Request");
		
		return location;
	}
	
	public AtDeviceStatus getDeviceStatus() throws Exception {
		AtDeviceStatus status = new AtDeviceStatus();
		logger.debug("Device Status Get Request");
		
		return status;
	}
	
	public void sendAllMessageDeviceLocation(TimsConfig timsConfig) throws Exception {
		sendMessageAllImp(getDeviceLocation(), timsConfig);
	}
	
	public void sendAllMessageDeviceStatus(TimsConfig timsConfig) throws Exception {
		sendMessageAllImp(getDeviceStatus(), timsConfig);
	}
	
	private void sendMessageAllImp(AtData message, TimsConfig timsConfig) throws Exception {
		Iterator<TcpChannelSession> iterator = TcpSessionManager.getSession();
		
		while(iterator.hasNext()) {
			TcpChannelSession tcpChannelSession = iterator.next();

            if (tcpChannelSession != null) {
                TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
                TimsMessage timsMessage = builder.getRequest(message.getAttrId());
    			
    			write(tcpChannelSession, timsMessage);
            }
		}
	}
	
	public void sendMessageImp(String sessionId, AtData message, TimsConfig timsConfig) throws Exception {
		TcpChannelSession tcpChannelSession = TcpSessionManager.getTcpChannelSessionById(sessionId);
		
        if (tcpChannelSession != null) {
        	TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
            TimsMessage timsMessage = builder.getRequest(message.getAttrId());
			
			write(tcpChannelSession, timsMessage);
        }
	}
	
	public void sendAllMessageImp(String sessionId, TimsConfig timsConfig) {
		try {
			sendMessageImp(sessionId, getDeviceLocation(), timsConfig);
			sendMessageImp(sessionId, getDeviceStatus(), timsConfig);
		} catch(Exception e) {
			ErrorManager.trace(platformId,
					this.getClass().getName(), Thread.currentThread().getStackTrace(),
					"PLATFORM PARAMETER", e.getMessage());
		}
	}
	
	private void write(TcpChannelSession tcpChannelSession, TimsMessage timsMessage) throws Exception {
		if(tcpChannelSession != null) {
			TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(
					tcpChannelSession.getChannel(),
					tcpChannelSession.getSession(),
					timsMessage);
			
			tcpChannelMessage.setResponse(false);
			TransactionManager.write(tcpChannelMessage);
		}
	}
}
