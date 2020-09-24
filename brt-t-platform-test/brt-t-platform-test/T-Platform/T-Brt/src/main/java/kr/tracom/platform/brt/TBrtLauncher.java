package kr.tracom.platform.brt;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import kr.tracom.platform.brt.handler.DataHandler;
import kr.tracom.platform.brt.handler.DeviceHandler;
import kr.tracom.platform.brt.handler.LivingHandler;
import kr.tracom.platform.brt.handler.ReservationHandler;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.db.factory.PlatformDbFactory;
import kr.tracom.platform.db.factory.ServiceDbFactory;
import kr.tracom.platform.db.util.DbHelper;
import kr.tracom.platform.service.ServiceLauncher;
import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.model.ServiceSetArgs;
import kr.tracom.platform.service.module.TScheduleModule;
import kr.tracom.platform.tcp.model.TcpChannelMessage;

public class TBrtLauncher extends ServiceLauncher {
	private DataHandler dataHandler;
	
    public TBrtLauncher() {
		super(64);
		
		this.ServiceId = "TA-BRT-01";
		this.ServiceName = "T-Brt";
		this.ServiceVersion = "1.0.0";
		
		this.dataHandler = new DataHandler(this);
	}
    
    public void initDatabase() {
        String serviceProfile = AppConfig.get("service.db.profile");
        DbHelper.addMapper(getServiceDb(),
                AppConfig.getClasspath(String.format("%s/brt/brt-sql-%s.xml", Constants.DB_PATH, serviceProfile)));
    }
    
    public void initSchedule() {
		PlatformDao platformDao = new PlatformDao();
		List<Object> scheduleList = platformDao.selectList(PlatformMapper.SCHEDULE_SELECT,
				platformDao.buildMap("APP_ID", ServiceId));

		TScheduleModule.getInstance().loadServiceJob(scheduleList, this);
	}
    
    @Override
	public void startup() {
		PlatformId = getPlatformId();

		initDatabase();
		initSchedule();
		
		// test();
		
		super.startup();
		
		String logMsg = "\r\n\r\n"; 
		logMsg += "===================================================================\r\n";
		logMsg += PlatformId + " - " + ServiceId + " Service Run !!!\r\n";
		logMsg += "===================================================================\r\n";
		System.out.println(logMsg);
	}

	@Override
	public void shutdown() {
		super.shutdown();
		
		String logMsg = "\r\n\r\n"; 
		logMsg += "===================================================================\r\n";
		logMsg += PlatformId + " - " + ServiceId + " Service Shutdown !!!\r\n";
		logMsg += "===================================================================\r\n";
		System.out.println(logMsg);
	}

	public void setServiceArgs(ServiceSetArgs args) {
		this.serviceSetArgs = args;
	}

	@Override
	public SqlSessionFactory getPlatformDb() {
		return PlatformDbFactory.getSqlSessionFactory();
	}

	@Override
	public SqlSessionFactory getServiceDb() {
		return ServiceDbFactory.getSqlSessionFactory();
	}

	@Override
	public void serviceWork(TcpChannelMessage tcpChannelMessage) {
		dataHandler.read(tcpChannelMessage);
	}

	@Override
	public void login(String sessionId, String sessionIp) {
		/********************************************************************************************/
		/* OBE 접속시 전송 데이터
		 * 1. 예약 알림(ActionRequest)
		 * 2. 기상/대기/뉴스 정보(SetRequest)
		 * 3. 장치 위치/상태 정보(GetRequest)
		 */
		ReservationHandler.getInstance().process(sessionId, getTimsConfig());
		LivingHandler.getInstance().sendAllMessageImp(sessionId, getTimsConfig());
		DeviceHandler.getInstance().sendAllMessageImp(sessionId, getTimsConfig());
		/********************************************************************************************/
	}

	@Override
	public void logout(String sessionId, String sessionIp) {
	}
	
	public DataHandler getDataHandler() {
		return this.dataHandler;
	}
	
	public void test() {
		/*
		String impId = "IMP0010000";
		
		// FTP 싱크 결과 테스트
		AtFtpListResultReportRequest result = new AtFtpListResultReportRequest();
		result.setResultCode((byte) 0x00);
		result.setOperation((byte) 0x10);
		result.setReservationId("AR00244");
		result.setDeviceId("IMP0010000");
		result.setCount((byte) 0x02);
		
		TimsMessageBuilder builder = new TimsMessageBuilder(getTimsConfig());
        TimsMessage timsMessage = builder.actionRequest(result);
        
        TcpSession tcpSession = new TcpSession();
        tcpSession.setSessionId(impId);
		
		TcpChannelMessage tcpChannelMessage = new TcpChannelMessage(
				null,
				tcpSession,
				timsMessage);

		tcpChannelMessage.setResponse(true);
		serviceWork(tcpChannelMessage);
		
		
		
		result = new AtFtpListResultReportRequest();
		result.setResultCode((byte) 0x10);
		result.setOperation((byte) 0x10);
		result.setReservationId("CR00001");
		result.setDeviceId("IMP0010000PD0001");
		result.setCount((byte) 0x01);
		
		atMessage = new AtMessage();
		atMessage.setAttrData(result);
		
		dataHandler.processFtpListResultReportRequest(atMessage, "IMP0010000");
		
		
		// 노선정보 테스트
		AtRouteInfoRequest result = new AtRouteInfoRequest();
		result.setRoutId("293000077");
		
		AtMessage atMessage = new AtMessage();
		atMessage.setAttrData(result);
		
		dataHandler.processRouteInfoRequest(atMessage, "IMP0010000");
		//*/
	}
}
