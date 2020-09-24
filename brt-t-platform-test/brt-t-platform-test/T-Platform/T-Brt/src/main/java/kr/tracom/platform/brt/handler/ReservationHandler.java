package kr.tracom.platform.brt.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.tracom.platform.attribute.brt.AtFtpListNotifyRequest;
import kr.tracom.platform.attribute.brt.AtFtpListPathItem;
import kr.tracom.platform.brt.config.GlobalConstants;
import kr.tracom.platform.brt.dao.BrtMapper;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.net.config.TimsConfig;
import kr.tracom.platform.net.protocol.TimsMessage;
import kr.tracom.platform.net.protocol.TimsMessageBuilder;
import kr.tracom.platform.net.protocol.attribute.AtData;
import kr.tracom.platform.service.dao.ServiceDao;
import kr.tracom.platform.service.manager.ErrorManager;
import kr.tracom.platform.service.manager.FtpManager;
import kr.tracom.platform.service.manager.TransactionManager;
import kr.tracom.platform.tcp.manager.TcpSessionManager;
import kr.tracom.platform.tcp.model.TcpChannelMessage;
import kr.tracom.platform.tcp.model.TcpChannelSession;

public class ReservationHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final ReservationHandler instance = new ReservationHandler();
	
	private static final int IMP_ID_DIGIT = 10;
	private ServiceDao serviceDao;
	private final String platformId = AppConfig.get("platform.id");
	
	private ReservationHandler() {
		serviceDao = new ServiceDao();
	}
	
	public static ReservationHandler getInstance() {
		return instance;
	}
	
	public void process(String deviceId, TimsConfig timsConfig) {
		List<AtData> reservationList = new ArrayList<AtData>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		try {
			paramMap.put("MNG_ID", deviceId);
			
			logger.debug("================================================================================================================");
			step0(paramMap, reservationList); // 상시 sync 정보 전송
			step1(paramMap, reservationList); // 펌웨어 업데이트 예약
			step2(paramMap, reservationList); // 행선지 안내기 예약
			step3(paramMap, reservationList); // 음성 예약
			step4(paramMap, reservationList); // 영상 예약(승객용안내기)
			step5(paramMap, reservationList); // 화면 설정 예약(승객용안내기)
			step6(paramMap, reservationList); // 전자노선도 예약
			step7(paramMap, reservationList); // 로그파일 경로
			logger.debug("================================================================================================================");
			
			// 예약 메시지 전송(Action Request -> attrId: 3510)
			TcpChannelSession tcpChannelSession = TcpSessionManager.getTcpChannelSessionById(deviceId);
	        if (tcpChannelSession != null) {
	        	
	        	for(AtData data : reservationList) {
					/*
					 * AtFtpListNotifyRequest request = (AtFtpListNotifyRequest) data;
					 * logger.debug(request.toString());
					 */
	        		
	        		TimsMessageBuilder builder = new TimsMessageBuilder(timsConfig);
	                TimsMessage timsMessage = builder.actionRequest(data);
	    			
	    			write(tcpChannelSession, timsMessage);
	        	}
	        }
		} catch(Exception e) {
			ErrorManager.trace(platformId,
					this.getClass().getName(), Thread.currentThread().getStackTrace(),
					"PLATFORM PARAMETER", e.getMessage());
		}
	}
	
	// 상시 Sync 정보 전송
	private void step0(Map<String, Object> paramMap, List<AtData> reservationList) throws Exception {
		AtFtpListNotifyRequest request = FtpManager.createMessage();
		List<AtFtpListPathItem> pathList = request.getList();
		pathList.add(createNotifyItem(GlobalConstants.getInstance().getServerRootPath() + "/common/"));
		request.setCount((byte) pathList.size());
		
		logger.debug("step0(common) Sync 리스트에 추가: " + request);
		reservationList.add(request);
	}
	
	// 펌웨어 업데이트 예약
	@SuppressWarnings("unchecked")
	private void step1(Map<String, Object> paramMap, List<AtData> reservationList) throws Exception {
		List<Object> list = serviceDao.selectList(BrtMapper.SELECT_DEVICE_UPDATE_RESERVATION, paramMap);
		
		String defaultPath = GlobalConstants.getInstance().getServerRootPath() + "/vehicle";
		
		logger.debug("step1 Start!!: " + list);
		
		for(Object o : list) {
			Map<String, Object> item = (Map<String, Object>)o;
			AtFtpListNotifyRequest request = FtpManager.createMessage();
			String resultPath = "";
			
			String rsvId = item.get("RSV_ID").toString();
			String mngId = item.get("MNG_ID").toString();
			
			request.setDeviceId(mngId);
			request.setReservationId(rsvId);
			
			// 경로 셋팅
			if(mngId.length() == 10) {
				resultPath = defaultPath + "/" + mngId + "/firmware/";
			} else {
				resultPath = defaultPath + "/" + mngId.substring(0, IMP_ID_DIGIT) + "/device/" + mngId.substring(IMP_ID_DIGIT) + "/firmware/"; 
			}
			
			List<AtFtpListPathItem> pathList = request.getList();
			pathList.add(createNotifyItem(resultPath));
			request.setCount((byte) pathList.size());
			
			logger.debug("step1(장치 업데이트) Sync 리스트에 추가: " + request);
			reservationList.add(request);
		}
	}
	
	// 행선지 안내기 예약
	@SuppressWarnings("unchecked")
	private void step2(Map<String, Object> paramMap, List<AtData> reservationList) throws Exception {
		List<Object> list = serviceDao.selectList(BrtMapper.SELECT_DESTINATION_RESERVATION, paramMap);
		
		logger.debug("step2 Start!!: " + list);
		
		for(Object o : list) {
			Map<String, Object> destiReservation = (Map<String, Object>)o;
			
			AtFtpListNotifyRequest request = FtpManager.createMessage();
			
			String rsvId = destiReservation.get("RSV_ID").toString();
			String mngId = destiReservation.get("MNG_ID").toString();
			
			request.setDeviceId(mngId);
			request.setReservationId(rsvId);
			
			List<AtFtpListPathItem> pathList = request.getList();
			pathList.add(createNotifyItem(GlobalConstants.getInstance().getServerRootPath() + "/destination/"));
			request.setCount((byte) pathList.size());
			
			logger.debug("step2(행선지안내기 예약) Sync 리스트에 추가: " + request);
			reservationList.add(request);
		}
	}
	
	// 음성 예약
	@SuppressWarnings("unchecked")
	private void step3(Map<String, Object> paramMap, List<AtData> reservationList) throws Exception {
		Map<String, Object> voiceReservation = (Map<String, Object>) serviceDao.select(BrtMapper.SELECT_VOICE_RESERVATION, paramMap);
		
		logger.debug("step3 Start!!: " + voiceReservation);
		
		if(voiceReservation != null) {
			AtFtpListNotifyRequest request = FtpManager.createMessage();
			
			String rsvId = voiceReservation.get("RSV_ID").toString();
			String mngId = voiceReservation.get("MNG_ID").toString();
			// String routId = voiceReservation.get("ROUT_ID").toString();
			
			request.setDeviceId(mngId);
			request.setReservationId(rsvId);
			
			// 경로 설정
			List<AtFtpListPathItem> pathList = request.getList();
			pathList.add(createNotifyItem(GlobalConstants.getInstance().getServerRootPath() + "/audio/"));
			pathList.add(createNotifyItem(GlobalConstants.getInstance().getServerRootPath() + "/route/"));
			request.setCount((byte) pathList.size());
			
			logger.debug("step3(음성,노선 예약) Sync 리스트에 추가: " + request);
			reservationList.add(request);
		}
	}
	
	// 영상 예약(승객용안내기)
	@SuppressWarnings("unchecked")
	private void step4(Map<String, Object> paramMap, List<AtData> reservationList) throws Exception {
		List<Object> list = serviceDao.selectList(BrtMapper.SELECT_VIDEO_RESERVATION, paramMap);
		
		String defaultPath = GlobalConstants.getInstance().getServerRootPath() + "/vehicle";
		
		logger.debug("step4 Start!!: " + list);
		
		for(Object o : list) {
			Map<String, Object> item = (Map<String, Object>)o;
			AtFtpListNotifyRequest request = FtpManager.createMessage();
			
			String rsvId = item.get("RSV_ID").toString();
			String mngId = item.get("MNG_ID").toString();
			
			request.setDeviceId(mngId);
			request.setReservationId(rsvId);
			
			List<AtFtpListPathItem> pathList = request.getList();
			pathList.add(createNotifyItem(defaultPath + "/" + mngId.substring(0, IMP_ID_DIGIT) + "/device/passenger/"));
			pathList.add(createNotifyItem(defaultPath + "/" + mngId.substring(0, IMP_ID_DIGIT) + "/device/" + mngId.substring(IMP_ID_DIGIT) + "/playlist/"));
			request.setCount((byte) pathList.size());
			
			logger.debug("step4(영상 예약) Sync 리스트에 추가: " + request);
			reservationList.add(request);
		}
	}
	
	// 화면 설정 예약(승객용안내기)
	@SuppressWarnings("unchecked")
	private void step5(Map<String, Object> paramMap, List<AtData> reservationList) throws Exception {
		List<Object> list = serviceDao.selectList(BrtMapper.SELECT_SCREEN_RESERVATION, paramMap);
		
		String defaultPath = GlobalConstants.getInstance().getServerRootPath() + "/vehicle";
		
		logger.debug("step5 Start!!: " + list);
		
		for(Object o : list) {
			Map<String, Object> item = (Map<String, Object>)o;
			AtFtpListNotifyRequest request = FtpManager.createMessage();
			
			String rsvId = item.get("RSV_ID").toString();
			String mngId = item.get("MNG_ID").toString();
			
			request.setDeviceId(mngId);
			request.setReservationId(rsvId);
			
			List<AtFtpListPathItem> pathList = request.getList();
			pathList.add(createNotifyItem(defaultPath + "/" + mngId.substring(0, IMP_ID_DIGIT) + "/device/" + mngId.substring(IMP_ID_DIGIT) + "/config/"));
			request.setCount((byte) pathList.size());
			
			logger.debug("step5(승객용안내기 화면설정 예약) Sync 리스트에 추가: " + request);
			reservationList.add(request);
		}
	}
	
	// 전자노선도 예약
	@SuppressWarnings("unchecked")
	private void step6(Map<String, Object> paramMap, List<AtData> reservationList) throws Exception {
		List<Object> list = serviceDao.selectList(BrtMapper.SELECT_ELECTRONIC_RESERVATION, paramMap);
		
		String defaultPath = GlobalConstants.getInstance().getServerRootPath() + "/vehicle";
		
		logger.debug("step6 Start!!: " + list);
		
		for(Object o : list) {
			Map<String, Object> item = (Map<String, Object>)o;
			AtFtpListNotifyRequest request = FtpManager.createMessage();
			
			String rsvId = item.get("RSV_ID").toString();
			String mngId = item.get("MNG_ID").toString();
			
			request.setDeviceId(mngId);
			request.setReservationId(rsvId);
			
			List<AtFtpListPathItem> pathList = request.getList();
			pathList.add(createNotifyItem(defaultPath + "/" + mngId.substring(0, IMP_ID_DIGIT) + "/device/" + mngId.substring(IMP_ID_DIGIT) + "/config/"));
			request.setCount((byte) pathList.size());
			
			logger.debug("step6(전자노선도 예약) Sync 리스트에 추가: " + request);
			reservationList.add(request);
		}
	}
	
	// 로그 저장 경로 예약
	private void step7(Map<String, Object> paramMap, List<AtData> reservationList) throws Exception {
		AtFtpListNotifyRequest request = FtpManager.createUploadMessage();
		
		String mngId = paramMap.get("MNG_ID").toString();
		List<AtFtpListPathItem> pathList = request.getList();
		pathList.add(createNotifyItem(GlobalConstants.getInstance().getServerRootPath() + "/vehicle/" + mngId + "/log/"));
		request.setCount((byte) pathList.size());
		
		logger.debug("step7(log) Sync 리스트에 추가: " + request);
		reservationList.add(request);
	}
	
	private AtFtpListPathItem createNotifyItem(String path) throws Exception {
		AtFtpListPathItem item = new AtFtpListPathItem();
		item.setSourcePath(path);
		return item;
	}
	
	private void write(TcpChannelSession tcpChannelSession, TimsMessage timsMessage) throws Exception {
		if(tcpChannelSession != null) {
			TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(
					tcpChannelSession.getChannel(),
					tcpChannelSession.getSession(),
					timsMessage);

			TransactionManager.write(tcpChannelMessage);
		}
	}
}
