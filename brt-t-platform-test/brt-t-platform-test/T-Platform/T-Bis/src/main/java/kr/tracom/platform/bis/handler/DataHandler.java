package kr.tracom.platform.bis.handler;

import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.service.ServiceLauncher;
import kr.tracom.platform.service.manager.CodeManager;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpChannelSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private ServiceLauncher launcher;

	private BusHandler busHandler;
	private StationHandler stationHandler;

	public DataHandler(ServiceLauncher launcher) {
		this.launcher = launcher;
		this.busHandler = new BusHandler(this);
		this.stationHandler = new StationHandler(this);
	}

	public ServiceLauncher getLauncher() {
		return launcher;
	}

	public BusHandler getBusHandler() {
		return busHandler;
	}

	public StationHandler getStationHandler() {
		return stationHandler;
	}

	public void read(TcpChannelMessage tcpChannelMessage) {
		if(tcpChannelMessage.getSession().getRemoteType().equals(CodeManager.RoutingGroupName.BUS.getValue())) {
			busHandler.handle(tcpChannelMessage);
		} else if(tcpChannelMessage.getSession().getRemoteType().equals(CodeManager.RoutingGroupName.STN.getValue())) {
			stationHandler.handle(tcpChannelMessage);
		}
	}

	public void write(String deviceId, TimsMessage timsMessage) {
		TcpChannelSession tcpChannelSession = TcpSessionManager.getTcpChannelSessionById(deviceId);

		if(tcpChannelSession != null) {
			TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(
					tcpChannelSession.getChannel(),
					tcpChannelSession.getSession(),
					timsMessage);

			tcpChannelMessage.setResponse(true);

			TransactionManager.write(tcpChannelMessage);
		}
	}
}
