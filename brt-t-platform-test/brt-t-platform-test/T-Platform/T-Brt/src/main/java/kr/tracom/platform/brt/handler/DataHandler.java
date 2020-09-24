package kr.tracom.platform.brt.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.tracom.platform.attribute.BrtAtCode;
import kr.tracom.platform.attribute.brt.AtDeviceLocation;
import kr.tracom.platform.attribute.brt.AtDeviceStatus;
import kr.tracom.platform.attribute.brt.AtFtpListNotifyResponse;
import kr.tracom.platform.attribute.brt.AtFtpListResultReportRequest;
import kr.tracom.platform.attribute.brt.AtFtpListResultReportResponse;
import kr.tracom.platform.attribute.brt.AtRouteInfoRequest;
import kr.tracom.platform.attribute.brt.AtRouteInfoResponse;
import kr.tracom.platform.brt.dao.BrtMapper;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.util.StringUtil;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.net.protocol.attribute.AtMessage;
import kr.tracom.platform.net.protocol.attribute.AtResult;
import kr.tracom.platform.net.protocol.payload.PlActionRequest;
import kr.tracom.platform.net.protocol.payload.PlActionResponse;
import kr.tracom.platform.net.protocol.payload.PlCode;
import kr.tracom.platform.net.protocol.payload.PlGetResponse;
import kr.tracom.platform.net.protocol.payload.PlSetResponse;
import kr.tracom.platform.service.ServiceLauncher;
import kr.tracom.platform.service.dao.ServiceDao;
import kr.tracom.platform.service.manager.ErrorManager;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpChannelSession;


public class DataHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private ServiceLauncher launcher;
	private ServiceDao serviceDao;
	private final String platformId = AppConfig.get("platform.id");

	public DataHandler(ServiceLauncher launcher) {
		this.launcher = launcher;
		this.serviceDao = new ServiceDao();
	}

	public ServiceLauncher getLauncher() {
		return launcher;
	}

	public void read(TcpChannelMessage tcpChannelMessage) {
		try {
	        TimsMessage timsMessage = tcpChannelMessage.getMessage();
	        byte opCode = timsMessage.getHeader().getOpCode();
	
	        if(opCode == PlCode.OP_GET_RES) {
	        	PlGetResponse response = (PlGetResponse)timsMessage.getPayload();
	        	
	        	for(AtMessage message : response.getAttrList()) {
	        		Short attrId = message.getAttrId();
	        		
	        		if(attrId == BrtAtCode.DEVICE_LOCATION_INFO) {
	        			processDeviceLocation(message, tcpChannelMessage.getSession().getSessionId());
	        		} else if(attrId == BrtAtCode.DEVICE_STATUS_INFO) {
	        			processDeviceStatus(message);
	        		}
	        	}
	        } else if(opCode == PlCode.OP_SET_RES) { 
	        	PlSetResponse response = (PlSetResponse)timsMessage.getPayload();
	        	
	        	for(AtResult result : response.getResultList()) {
	        		Short attrId = result.getAtId();
	        		
	        		if(attrId == BrtAtCode.WEATHER_INFO || attrId == BrtAtCode.AIR_QUALITY_INFO || attrId == BrtAtCode.COOKIE_NEWS_INFO) {
	        			processLivingData(result, tcpChannelMessage.getSession().getSessionId());
	        		}
	        	}
	        } else if(opCode == PlCode.OP_ACTION_REQ) {
	            PlActionRequest request = (PlActionRequest)timsMessage.getPayload();
	            Short attrId = request.getAtMessage().getAttrId();
	            
	            if (attrId == BrtAtCode.ROUTE_INFO_REQUEST){
	            	processRouteInfoRequest(request.getAtMessage(), tcpChannelMessage.getSession().getSessionId());
	        	} else if(attrId == BrtAtCode.FTP_LIST_RESULT_REPORT_REQUEST) {
	            	processFtpListResultReportRequest(request.getAtMessage(), tcpChannelMessage.getSession().getSessionId());
	        	}
	        } else if(opCode == PlCode.OP_ACTION_RES) {
	        	PlActionResponse response = (PlActionResponse)timsMessage.getPayload();
	            Short attrId = response.getAtMessage().getAttrId();
	            
	        	if(attrId == BrtAtCode.FTP_LIST_NOTIFICATION_RESPONSE) {
	        		processFtpListNotificationResponse(response.getAtMessage());
	        	} else if(attrId == BrtAtCode.ROUTE_INFO_RESPONSE) {
	        		processRouteInfoResponse(response.getAtMessage());
	        	}
	        }
		} catch(Exception e) {
			ErrorManager.trace(platformId,
					this.getClass().getName(), Thread.currentThread().getStackTrace(),
					"PLATFORM PARAMETER", e.getMessage());
		}
	}
	
	private void processDeviceLocation(AtMessage message, String sessionId) throws Exception {
		AtDeviceLocation location = (AtDeviceLocation) message.getAttrData();
		logger.debug("Device Location Info Get Response: " + location);
		
		if(location.getDeviceId() == null || location.getDeviceId().equals("")) {
			location.setDeviceId(sessionId);
		}
		
		serviceDao.insert(BrtMapper.INSERT_DEVICE_LOCATION, location.toMap());
	}
	
	private void processDeviceStatus(AtMessage message) throws Exception {
		AtDeviceStatus status = (AtDeviceStatus) message.getAttrData();
		logger.debug("Device Status Info Get Response: " + status);
		
		if(status.getCount() != 0) {
			serviceDao.insert(BrtMapper.INSERT_DEVICE_CONDITION, status.toMap());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void processFtpListResultReportRequest(AtMessage message, String sessionId) throws Exception {
		AtFtpListResultReportRequest resultRequest = (AtFtpListResultReportRequest) message.getAttrData();
    	
    	String rsvId = resultRequest.getReservationId();
    	logger.debug("Ftp List Result Report Action Request: " + resultRequest);
    	
    	if(!StringUtil.isNullOrEmpty(rsvId)) {
	    	// 공통 코드 테이블에서 테이블 이름 조회
	    	String code = rsvId.substring(0, 2);
	    	Map<String, Object> paramMap = new HashMap<String, Object>();
	    	paramMap.put("CODE", code);
	    	String tableName = ((Map<String, Object>) serviceDao.select(BrtMapper.SELECT_RESERVATION_TABLE, paramMap)).get("TABLE_NAME").toString();
	    	
	    	Map<String, Object> map = resultRequest.toMap();
	    	map.put("TABLE_NAME", tableName);
	    	
	    	int updateResult = serviceDao.update(BrtMapper.UPDATE_RESERVATION_RESULT, map);
	    	
	    	logger.debug("FtpListResultReport ActionRequest Update Result: " + updateResult + ", params: " + map);
	    	
	    	AtFtpListResultReportResponse response = new AtFtpListResultReportResponse();
	    	response.setResultCode((byte)0x00);
	    	response.setReservationId(resultRequest.getReservationId());
	    	response.setDeviceId(resultRequest.getDeviceId());
	    	
	    	TimsMessageBuilder builder = new TimsMessageBuilder(launcher.getTimsConfig());
	        TimsMessage responseMessage = builder.actionResponse(response);
	    	write(sessionId, responseMessage);
    	}
	}
	
	private void processFtpListNotificationResponse(AtMessage message) throws Exception {
		// FTP Sync 요청을 OBE에 보낸 후 받은 응답 메시지 로깅만..
		AtFtpListNotifyResponse notifyResponse = (AtFtpListNotifyResponse) message.getAttrData();
		logger.debug("FtpListNotificationResponse: " + notifyResponse);
	}
	
	@SuppressWarnings("unchecked")
	public void processRouteInfoRequest(AtMessage message, String sessionId) throws Exception {
		AtRouteInfoRequest routeInfoRequest = (AtRouteInfoRequest) message.getAttrData();
		
		Map<String, Object> map = routeInfoRequest.toMap();
		Map<String, Object> routeInfo = (Map<String, Object>) serviceDao.select(BrtMapper.SELECT_ROUTE_INFO, map);
		
		logger.debug("RouteInfo Action Request: " + routeInfoRequest);
		
		AtRouteInfoResponse response = new AtRouteInfoResponse();
		if(routeInfo != null) {
			response.setParseMap(routeInfo);
		}
		
		logger.debug("RouteInfo Action Response: " + response);
			
		TimsMessageBuilder builder = new TimsMessageBuilder(launcher.getTimsConfig());
		TimsMessage responseMessage = builder.actionResponse(response);
		write(sessionId, responseMessage);
	}
	
	public void processRouteInfoResponse(AtMessage message) throws Exception {
		AtRouteInfoResponse routeResponse = (AtRouteInfoResponse) message.getAttrData();
		logger.debug("RouteInfo Action Response(ACK): " + routeResponse);
	}
	
	private void processLivingData(AtResult result, String sessionId) throws Exception {
		logger.debug("Living Data Response:" + result);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("MNG_ID", sessionId);
		paramMap.put("ATTR_ID", result.getAtId());
		
		serviceDao.insert(BrtMapper.INSERT_LIVING_LOG, paramMap);
	}

	public void write(String deviceId, TimsMessage timsMessage) throws Exception {
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
