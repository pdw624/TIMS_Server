package kr.tracom.platform.launcher.channel;

import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import kr.tracom.platform.attribute.manager.AttributeManager;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.launcher.channel.handler.ActionRequest;
import kr.tracom.platform.launcher.channel.handler.ActionResponse;
import kr.tracom.platform.launcher.channel.handler.EventRequest;
import kr.tracom.platform.launcher.channel.handler.EventResponse;
import kr.tracom.platform.launcher.channel.handler.GetRequest;
import kr.tracom.platform.launcher.channel.handler.GetResponse;
import kr.tracom.platform.launcher.channel.handler.InitResponse;
import kr.tracom.platform.launcher.channel.handler.SetRequest;
import kr.tracom.platform.launcher.channel.handler.SetResponse;
import kr.tracom.platform.launcher.module.TServiceModule;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.payload.PlCode;
import kr.tracom.platform.service.ServiceLauncher;
import kr.tracom.platform.service.config.PlatformConfig;
import kr.tracom.platform.service.manager.ErrorManager;
import kr.tracom.platform.service.manager.PacketLogManager;
import kr.tracom.platform.service.manager.PacketManager;
import kr.tracom.platform.service.manager.SessionManager;
import kr.tracom.platform.service.model.ServiceArgs;
import kr.tracom.platform.tcp.cs.TimsStackHandler;
import kr.tracom.platform.tcp.cs.server.TimsServer;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpSession;

public class TChannelServer extends TimsServer {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String platformId = AppConfig.get("platform.id");

	private InitResponse initResponse;
	private GetRequest getRequest;
	private GetResponse getResponse;
	private SetRequest setRequest;
	private SetResponse setResponse;
	private ActionRequest actionRequest;
	private ActionResponse actionResponse;
	private EventRequest eventRequest;
	private EventResponse eventResponse;

	public TChannelServer(TimsConfig timsConfig) {
		super(timsConfig);

		this.initResponse = new InitResponse(timsConfig);
		this.getRequest = new GetRequest(timsConfig);
		this.getResponse = new GetResponse(timsConfig);
		this.setRequest = new SetRequest(timsConfig);
		this.setResponse = new SetResponse(timsConfig);
		this.actionRequest = new ActionRequest(timsConfig);
		this.actionResponse = new ActionResponse(timsConfig);
		this.eventRequest = new EventRequest(timsConfig);
		this.eventResponse = new EventResponse(timsConfig);

		AttributeManager.bind(AttributeManager.COMMON_ATTRIBUTE);
	}
	
	public void run() {
		super.run();
	}

	public void shutdown() {
		super.shutdown();
	}
	
	public void channelActive(Channel ch) {
		PacketManager.requestAuth(ch, timsConfig);
	}

	public void channelInactive(Channel ch) {
		TcpSession tcpSession = TcpSessionManager.getTcpSession(ch);

		if(tcpSession != null) {
			TcpSessionManager.removeSession(ch);

			//20171221 추가
			logoutToService(tcpSession.getSessionId(), tcpSession.getSessionIp());

			tcpSession.setSessionIp("-");
			tcpSession.setLoginDateTime("-");
			tcpSession.setLogoutDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

			SessionManager.logout(tcpSession);

			PacketLogManager.writeLog(PacketLogManager.CLOSE_CHANNEL, ch, null, tcpSession.getSessionId());
		}
	}

	public void channelRead(Channel ch, TimsMessage timsMessage) {
		TcpSession tcpSession = TcpSessionManager.getTcpSession(ch);
		String sessionId = tcpSession == null ? null : tcpSession.getSessionId();
		
		PacketLogManager.writeLog(PacketLogManager.RECV_PACKET, ch, timsMessage.getLog(), sessionId);
		PacketLogManager.writeLog("T", ch, timsMessage.forLog(), sessionId);
		
		byte opCode = timsMessage.getHeader().getOpCode();
		
		if (opCode == PlCode.OP_INIT_RES) {
			initResponse.handle(ch, timsMessage, tcpSession);
			tcpSession = TcpSessionManager.getTcpSession(ch);
			
			if(tcpSession != null) {
				//20171221 추가
				loginToService(tcpSession.getSessionId(), tcpSession.getSessionIp());
			}
		}
		else {
			if (tcpSession == null) {
				PacketManager.requestAuth(ch, timsConfig);
				return;
			} else {
				List<Short> attributeList;
				
				if (opCode == PlCode.OP_GET_REQ) { // 0x10
					attributeList = getRequest.handle(ch, timsMessage, tcpSession);
				} else if (opCode == PlCode.OP_GET_RES) { // 0x11
					attributeList = getResponse.handle(ch, timsMessage, tcpSession);
				}

				else if (opCode == PlCode.OP_SET_REQ) { // 0x20
					attributeList = setRequest.handle(ch, timsMessage, tcpSession);
				} else if (opCode == PlCode.OP_SET_RES) { // 0x21
					attributeList = setResponse.handle(ch, timsMessage, tcpSession);
				}

				else if (opCode == PlCode.OP_ACTION_REQ) { // 0x30
					attributeList = actionRequest.handle(ch, timsMessage, tcpSession);
				} else if (opCode == PlCode.OP_ACTION_RES) { // 0x31
					//command 보내면 대답이 이쪽으로옴 aaaaa
					attributeList = actionResponse.handle(ch, timsMessage, tcpSession);
				}

				else if (opCode == PlCode.OP_EVENT_REQ) { // 0x40
					attributeList = eventRequest.handle(ch, timsMessage, tcpSession);
				} else if (opCode == PlCode.OP_EVENT_RES) { // 0x41
					attributeList = eventResponse.handle(ch, timsMessage, tcpSession);
				}
				//jh
				
				else {
					TimsStackHandler.responseAndClose(ch, timsConfig, TimsStackHandler.ProtocolError.OpCode.getValue(), timsMessage);
					return;
				}

				tcpSession.setLastDateTime(DateTime.now().toString(PlatformConfig.PLF_DT_FORMAT));

				// 처리할 메시가 있으면
				if (attributeList != null && attributeList.size() > 0) {
					// 메시지를 전달한다.
					dispatch(new TcpChannelMessage(ch, tcpSession, timsMessage), attributeList);
				}
			}
		}
	}

	private void loginToService(String sessionId, String sessionIp) {
		Iterator<ServiceArgs> serviceList = TServiceModule.getInstance().getIterator();
		while(serviceList.hasNext()) {
			ServiceArgs item = serviceList.next();
			ServiceLauncher launcher = item.getServiceLauncher();

			launcher.login(sessionId, sessionIp);
		}
	}

	private void logoutToService(String sessionId, String sessionIp) {
		Iterator<ServiceArgs> serviceList = TServiceModule.getInstance().getIterator();
		while(serviceList.hasNext()) {
			ServiceArgs item = serviceList.next();
			ServiceLauncher launcher = item.getServiceLauncher();

			launcher.logout(sessionId, sessionIp);
		}
	}

	private void dispatch(TcpChannelMessage tcpChannelMessage, List<Short> attributeList) {		
		Iterator<ServiceArgs> serviceList = TServiceModule.getInstance().getIterator();
		
		while(serviceList.hasNext()) {
			ServiceArgs item = serviceList.next();
			ServiceLauncher launcher = item.getServiceLauncher();
			
			for(Short attributeId : attributeList) {
				if(item.getAttributeList().contains(attributeId)) {

					try {
						launcher.getServiceGetArgs().getReadQueue().add(tcpChannelMessage);

						logger.debug("dispatch message : " + tcpChannelMessage.toString());
					} catch(Exception e) {
						ErrorManager.trace(platformId,
								this.getClass().getName(), Thread.currentThread().getStackTrace(),
								"PLATFORM PARAMETER", e.getMessage());
					}
				}
			}
		}
	}
}
